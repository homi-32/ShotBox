package sk.homisolutions.shotbox.snem.simplecamera.gopro;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.MimeTypes;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by homi on 10/3/16.
 */
public class GoProService {
    private static final Logger logger = Logger.getLogger(GoProService.class);

    private CameraPlatformProvider provider;
    private SimpleCamera camera;
    private Client client;

    public GoProService(CameraPlatformProvider provider, SimpleCamera camera) {
        this.provider = provider;
        this.camera = camera;
        this.client = ClientBuilder.newClient();
    }

    public void takePhotoViaGoPro(TakenPicture pic){
        logger.info("Taking picture started");

        TakenPicture picture;
        if(pic == null){
            logger.warn("No provided object of picture. Creating new one");
            picture = new TakenPicture();
        }else {
            picture = pic;
        }

        setPhotoModeOnGoPro();
        setupGoproBeforeTakingPicture();
        takePicture();
        provider.notifyPictureIsTaken(camera);
        processTakenPicture(picture);
        provider.provideTakenPicture(picture, camera);

    }

    private void setPhotoModeOnGoPro() {
        logger.fatal("setting photo mode for gopro");
        WebTarget target = client.target(Constants.TURN_ON_PHOTO);
        Response response = target.request().get();
        checkResponseStatus(response);
    }

    private void setupGoproBeforeTakingPicture() {
        //do i need to do it? probably yes, but now i don't know, what should i set
        //TODO: consult this with Dominik
    }

    private void takePicture() {
        logger.info("taking picture");
        WebTarget target = client.target(Constants.TRIGGER_PHOTO);
        Response response = target.request().get();
        checkResponseStatus(response);
    }

    private void processTakenPicture(TakenPicture picture) {
        String photoFileName = getNameLastTakenPhoto();
        byte[] takenPhoto = getTakenPhoto(photoFileName);
        setupTakenPicture(picture, takenPhoto, photoFileName);
    }

    private byte[] getTakenPhoto(String photoFileName) {
        logger.info("getting last photo");
        byte[] photo;
        String photoName =  photoFileName;
        String photoUrl = Constants.MAIN_DIRECTORY_URL + photoName;

        logger.info("url: " +photoUrl);
        boolean responseStatus = false;
        int counter = 0;
        Response response = null;
        while (!responseStatus) {
            counter += 1;
            WebTarget target = client.target(photoUrl);
            response = target.request().get();
            response.bufferEntity();
            response.getEntity();

            /* this is chceck.
            GoPro sometimes throw 500 internal error response.
            if it occurs few times, it is OK.
            if it occurs 50 times in a row, it is not OK and something is wrong
             */
            try {
                responseStatus = checkResponseStatus(response);
            }catch (SNEM_SimpleCamera_GoPro_Exception e){
                if(counter > 50){
                    throw e;
                }else {
                    responseStatus = false;
                    sleepThread();
                }

            }
        }


        logger.info("request was successful. reading retrieved picture");
        InputStream receiverPhoto = (InputStream) response.getEntity();
        photo = serializePhoto(receiverPhoto);

        logger.info("picture was red successfully");
        return photo;
    }

    private void setupTakenPicture(TakenPicture picture, byte[] pictureAsByteArray, String photoName) {
        logger.info("setting get photo");
        picture.setPicture(pictureAsByteArray);
        picture.setType(MimeTypes.JPG);
        picture.setFilename(photoName);
    }

    //TODO check the llicence for common utils dependency and find better solution for serialize content from input reader, if it is needed
    private byte[] serializePhoto(InputStream receiverPhoto) {
        byte[] serializedObject = new byte[0];
        //used http://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
        //in combination with this http://stackoverflow.com/questions/20336235/how-to-save-a-file-from-jersey-response
        try {
            serializedObject = IOUtils.toByteArray(receiverPhoto);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }

//        This works, when I can serialize an object, but i am not sure, if I can use it with InputStream
//        try {
//            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
//
//            objectOut.writeObject(receiverPhoto);
//            serializedObject = byteOut.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return serializedObject;
    }

    private boolean checkResponseStatus(Response response) {
        logger.info("check response");
        if(response.getStatus() >= 200 && response.getStatus() < 300 /*!= Response.Status.OK.getStatusCode()*/) {
            logger.info("OK");
            return true;
        }else{
            logger.error("NOT OK");
            logger.error("Response code is not 200, but: " + response.getStatus());
            throw new SNEM_SimpleCamera_GoPro_Exception("Response code is not 200, but: " + response.getStatus());
//            return false;
        }
    }

    private void sleepThread() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    /*
    if i could not find way, how to download media list, i have to use this:
    https://github.com/KonradIT/gopro.java/blob/master/GoProDownloadPics/gopro.java
     */
    private String getNameLastTakenPhoto() {
        logger.info("getting name of last picture");

        //taking list of all files from gopro
        logger.info("getting request from: " +Constants.GET_MEDIA_LIST);
        String json = "";
        try {
            byte[] byteAr = sendGET(Constants.GET_MEDIA_LIST);
            json = new String(byteAr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("getted response: " +json);
        if (json.length() > 0){
            logger.info("resolving photo name from response");
            String photoName = resolveLatestPhoto(json);

            logger.info("name is resolved: " +photoName);
            return photoName;

        }else{
            throw new SNEM_SimpleCamera_GoPro_Exception("Media list from GoPro could not be retrieved.");
        }
    }

    //TODO: while using method 'sendGET' I need to use this parser, but later, it should be rewritten
    private String resolveLatestPhoto(String json) {
        String postfix = ".JPG";
        String name;
        List<Long> photos = new LinkedList<>();

        json = json.substring(0, json.lastIndexOf("JPG"));

        while (json.contains(postfix)){
            int indexONE = json.lastIndexOf("GOPR");
            int indexTWO = json.lastIndexOf(".");
            if(indexONE<indexTWO){
                String number = json.substring((indexONE+4), indexTWO);
                if(number.length() == 4) {
                    photos.add(Long.parseLong(number));
                }
            }
            json = json.substring(0, indexONE);
        }
        Collections.sort(photos);
        photos.forEach(logger::info);
        name = String.valueOf(photos.get(photos.size()-1));
        while(name.length() < 4){
            name = "0"+name;
        }
        return "GOPR"+name + postfix;
    }

    //not mine: https://github.com/DomDomHaas/gopro-wifi-API/blob/master/src/org/gopro/core/GoProHelper.java
    //TODO: after long time testing i found out, there is problem with receiving packets. some data (characters from json) can lost and it break all structure and some filenames, so this needs to be replaced
    @Deprecated
    private byte[] sendGET(String paramString)
            throws Exception {
        DefaultHttpClient paramDefaultHttpClient = newInstance();
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        HttpResponse localHttpResponse;
        try {
            logger.info("Cmd :" + paramString);

            System.setProperty("http.keepAlive", "true");

            int statusCode = 0;
            int counter = 0;
            boolean repeatRequest = true;
            do {
                counter++;
                logger.info("requesting media list iteration: " +counter);
                HttpGet localHttpGet = new HttpGet(paramString);
                localHttpResponse = paramDefaultHttpClient.execute(localHttpGet);
                statusCode = localHttpResponse.getStatusLine().getStatusCode();

                if (statusCode >= 400) {
                    localHttpGet.abort();
                    if(counter > 50){
                        throw new IOException("Fail to send GET - HTTP error code = [" + statusCode + "]");
                    }else {
                        sleepThread();
                    }
                }else {
                    repeatRequest = false;
                }
            }while(repeatRequest);

        } catch (Exception localException) {
            throw localException;
        }
        int j = (int) localHttpResponse.getEntity().getContentLength();
        if (j <= 0)
            j = 128;
        InputStream localInputStream = localHttpResponse.getEntity().getContent();
        byte[] arrayOfByte = new byte[j];
        while (true) {
            if (localInputStream.read(arrayOfByte, 0, arrayOfByte.length) == -1) {
                localByteArrayOutputStream.flush();
                return localByteArrayOutputStream.toByteArray();
            }
            localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
        }
    }

    //not mine: https://github.com/DomDomHaas/gopro-wifi-API/blob/master/src/org/gopro/core/GoProHelper.java
    @Deprecated
    private DefaultHttpClient newInstance() {
        BasicHttpParams localBasicHttpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(localBasicHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(localBasicHttpParams, "ISO-8859-1");
        HttpProtocolParams.setUseExpectContinue(localBasicHttpParams, true);
        HttpConnectionParams.setStaleCheckingEnabled(localBasicHttpParams, false);
        HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 10000);
        HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
        HttpConnectionParams.setSocketBufferSize(localBasicHttpParams, 8192);
        SchemeRegistry localSchemeRegistry = new SchemeRegistry();
        localSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ConnManagerParams.setMaxTotalConnections(localBasicHttpParams, 1);
        return new DefaultHttpClient(new ThreadSafeClientConnManager(localBasicHttpParams,
                localSchemeRegistry), localBasicHttpParams);
    }
}
