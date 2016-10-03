package sk.homisolutions.shotbox.snem.simplecamera.gopro.multithread_solution_delete_it_later;

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
import sk.homisolutions.shotbox.snem.simplecamera.gopro.Constants;
import sk.homisolutions.shotbox.snem.simplecamera.gopro.SNEM_SimpleCamera_GoPro_Exception;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.MimeTypes;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Created by homi on 8/23/16.
 */
//TODO: Investigate, if there is no issue with calling 1 object CameraPlatformProvider from lot of threads - Done: GoPro server cannot handle more threads so.... but synchronize is used when accessing providers
public class GoProServiceOld implements Runnable{
    private static final Logger logger = Logger.getLogger(GoProServiceOld.class);

    private CameraPlatformProvider provider;
    private TakenPicture picture;
    private Client client;

    //For now, if i can not obtain MAC address, object of GoProServiceOld does not have value, but only for now
    //I can (maybe) do it without need of MAC address otherwise
    public GoProServiceOld(CameraPlatformProvider provider, TakenPicture picture) {
        this.provider = provider;
        this.picture = picture;
        this.client = ClientBuilder.newClient();
    }

    @Override
    public void run() {
        try {
//            new GoProInitOld().wakeUpGopro();
            logger.fatal("new thread GoProServiceOld started");
            setPhotoMode();
            setupGoproBeforeTakingPicture();
            takePicture();
            byte[] takenPhoto = getTakenPhoto();
            setupTakenPicture(takenPhoto);
            provider.provideTakenPicture(picture);
        } catch (Exception e){
            e.printStackTrace();
        }
//        catch (SocketException e) {
//            e.printStackTrace();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

    }

    private void setPhotoMode() {
        logger.fatal("setting photo mode for gopro");
        WebTarget target = client.target(Constants.TURN_ON_PHOTO);
        logger.fatal("1");
        Response response = target.request().get();
        logger.fatal("2");
        checkResponseStatus(response);
        logger.fatal("3");
    }

    private void setupTakenPicture(byte[] pictureAsByteArray) {
        logger.fatal("setting get photo");
        picture.setPicture(pictureAsByteArray);
        picture.setType(MimeTypes.JPG);
    }

    private byte[] getTakenPhoto() {
        logger.fatal("getting last photo1");
        byte[] photo;
        String photoName =  getNameLastTakenPhoto();
        String photoUrl = Constants.MAIN_DIRECTORY_URL + photoName;

        logger.fatal("getting last photo2");
        logger.fatal("url: " +photoUrl);
        WebTarget target = client.target(photoUrl);
        Response response = target.request().get();
        response.bufferEntity();
        response.getEntity();
        checkResponseStatus(response);
        InputStream receiverPhoto = (InputStream) response.getEntity();
        photo = serializePhoto(receiverPhoto);

        picture.setFilename(photoName);
        logger.fatal("getting last photo3");

        return photo;
    }

    //not mine: https://github.com/DomDomHaas/gopro-wifi-API/blob/master/src/org/gopro/core/GoProHelper.java
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
    //not mine: https://github.com/DomDomHaas/gopro-wifi-API/blob/master/src/org/gopro/core/GoProHelper.java
    private byte[] sendGET(String paramString)
            throws Exception {
        DefaultHttpClient paramDefaultHttpClient = newInstance();
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        HttpResponse localHttpResponse;
        try {
            System.out.println("Cmd :" + paramString);

            System.setProperty("http.keepAlive", "true");

            int statusCode = 0;
            int counter = 0;
            do {
                Thread.sleep(200);
                logger.fatal("requesting media list iteration: " +counter);
                HttpGet localHttpGet = new HttpGet(paramString);
                localHttpResponse = paramDefaultHttpClient.execute(localHttpGet);
                statusCode = localHttpResponse.getStatusLine().getStatusCode();
                if (statusCode >= 400) {
                    localHttpGet.abort();
//                    throw new IOException("Fail to send GET - HTTP error code = [" + statusCode + "]");
                }
                counter++;
            }while(statusCode >= 400 && counter < 50);
            if(statusCode >= 400){
                throw new IOException("Fail to send GET - HTTP error code = [" + statusCode + "]");
            }
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

    /*not mine method: http://helpdesk.objects.com.au/java/how-do-i-copy-one-stream-to-another-using-java*/
//    private void copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
//        byte[] buf = new byte[bufferSize];
//        int len = 0;
//        while ((len = in.read(buf)) >= 0){
//            out.write(buf, 0, len);
//        }
//    }


    /*
    if i could not find way, how to download media list, i have to use this:
    https://github.com/KonradIT/gopro.java/blob/master/GoProDownloadPics/gopro.java
     */

    private String getNameLastTakenPhoto() {
        logger.fatal("getting name of last picture");
        //taking list of all files from gopro
        logger.fatal("getting request from: " +Constants.GET_MEDIA_LIST);


        String json = "";
        try {
            byte[] byteAr = sendGET(Constants.GET_MEDIA_LIST);
            json = new String(byteAr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.fatal("getted response: " +json);
        if (json.length() > 0){
            String photoName;

            /***start of name resolving***/
            String postfix = ".JPG";
            int index = json.lastIndexOf(postfix);
            if(index == -1){
                postfix =".jpg";
                index = json.lastIndexOf(postfix);
            }if(index == -1){
                postfix =".PNG";
                index = json.lastIndexOf(postfix);
            }if(index == -1){
                postfix =".png";
                index = json.lastIndexOf(postfix);
            }if(index == -1){
                throw new SNEM_SimpleCamera_GoPro_Exception("There are no photos in camera or " +
                        "\nmethod for retrieving/resolving photo name is wrong implemented or " +
                        "\nsomething is wrong with GoPro camera.");
            }
            json = json.substring(0, index);
            index = json.lastIndexOf("\"");
            index+=1;
            json = json.substring(index);
            /***end of name resolving***/
//            logger.fatal(json);
//            logger.fatal(json +postfix);
            photoName = json + postfix;
//        return "GOPR0837.JPG";
            return photoName;

        }else{
            throw new SNEM_SimpleCamera_GoPro_Exception("Media list from GoPro could not be retrieved.");
        }

////from http://stackoverflow.com/questions/5737945/java-how-to-download-chunked-content-correctly
//        try {
//            URLConnection connection = new URL(Constants.GET_MEDIA_LIST).openConnection();
//
//            ByteArrayOutputStream sink = new ByteArrayOutputStream();
//            copy(connection.getInputStream(), sink, 1024);
//            byte[] downloadedFile = sink.toByteArray();
//            String json = new String(downloadedFile);
//            logger.fatal(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        /*
        int requestCounts = 0;
        Response response;
        do {
            //there is problem, that sometimes i cannot get media lists
            logger.info("Requesting media list.");
            waitToContinue();
            WebTarget target = client.target(Constants.GET_MEDIA_LIST);
            response = target.request().get();
            requestCounts++;
        }while ((response.getStatus() < 200 || response.getStatus() >= 300)&&(requestCounts < 50));
        checkResponseStatus(response);


        // how to read chunks in jersey: https://jersey.java.net/documentation/latest/async.html#d0e10687
        ChunkedInput<Object> chunkedInput = response.readEntity(new GenericType<ChunkedInput<Object>>(){});
        String string = "";
        Object chunk = "";
        do{
            string += chunk;
            chunk = chunkedInput.read();
        }while (chunk != null);

        logger.fatal("received media list: " +string);
        */

//        String json = ClientBuilder.newClient().target(Constants.GET_MEDIA_LIST).request().accept(MediaType.APPLICATION_JSON).get(String.class);
//        logger.fatal(json);

//        String jsonMediaList = response.readEntity(String.class);
//        Object object = response.getEntity();

//        logger.fatal(response.readEntity(String.class));
//        logger.fatal("");
//        GoproMediaList mediaList = null;
//        GoproMedia media = null;
//        for (GoproMedia m: mediaList.getMedia()){
//            if (m.getD().equals(Constants.GOPRO_FOLDER_WITH_PHOTOS)){
//                media = m;
//            }
//        }
//        if(media != null) {
//            String takenPicturesName = media.getFs().get(media.getFs().size() - 1).getN();
//            return takenPicturesName;
//        }else{
//            throw new SNEM_SimpleCamera_GoPro_Exception("No directory with photos found.");
//        }


//        return "GOPR0837.JPG";
    }

    private void waitToContinue() {
        //i dont know why, when i run the code, 500 response is returned, but while debugging, 200 is returned. So, maybe i need to wait a while
        //working with 700ms, 500ms is too short moment
        //TODO: find out, why is this happening
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void takePicture() {
        logger.fatal("taking picture");
        WebTarget target = client.target(Constants.TRIGGER_PHOTO);
        Response response = target.request().get();
        checkResponseStatus(response);
    }

    private void setupGoproBeforeTakingPicture() {
        //do i need to do it? probably yes, but now i don't know, what should i set
        //TODO: consult this with Dominik
    }

    private void checkResponseStatus(Response response) {
        logger.fatal("check response");
        if(response.getStatus() >= 200 && response.getStatus() < 300 /*!= Response.Status.OK.getStatusCode()*/) {
            logger.fatal("OK");
        }else{
            logger.fatal("NOT OK");
            throw new SNEM_SimpleCamera_GoPro_Exception("Response code is not 200, but: " + response.getStatus());
        }
    }
}
