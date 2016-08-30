package sk.homisolutions.shotbox.snem.simplecamera.gopro;

import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.MimeTypes;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 * Created by homi on 8/23/16.
 */
//TODO: Investigate, if there is no issue with calling 1 object CameraPlatformProvider from lot of threads
public class GoProService implements Runnable{

    private CameraPlatformProvider provider;
    private TakenPicture picture;
    private Client client;

    //For now, if i can not obtain MAC address, object of GoProService does not have value, but only for now
    //I can (maybe) do it without need of MAC address otherwise
    public GoProService(CameraPlatformProvider provider, TakenPicture picture) throws UnknownHostException, SocketException {
        this.provider = provider;
        this.picture = picture;
        this.client = ClientBuilder.newClient();
    }

    @Override
    public void run() {
        try {
            new GoProInit().wakeUpGopro();
            takePicture();
            byte[] takenPhoto = getTakenPhoto();
            setupTakenPicture(takenPhoto);
            provider.provideTakenPicture(picture);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    private void setupTakenPicture(byte[] pictureAsByteArray) {
        picture.setPicture(pictureAsByteArray);
        picture.setType(MimeTypes.JPG);
    }

    private byte[] getTakenPhoto() {
        byte[] photo;

        //taking list of all files from gopro
        WebTarget target = client.target(Constants.GET_MEDIA_LIST);
        Response response = target.request().get();
        checkResponseStatus(response);
        Object mediaList = response.getEntity();
        String pathToLastTakenPhoto = "";

        //searching for last taken photo on gopro
        /*
        .....some magic, in which I get path to last taken photo on gopro camera
         */

        //requesting last taken photo from gopro
        target = client.target(Constants.MAIN_DIRECTORY_URL+pathToLastTakenPhoto);
        response = target.request().get();
        checkResponseStatus(response);
        Object receiverPhoto = response.getEntity();
        photo = serializePhoto(receiverPhoto);

        //resolving name of taken photo. This can be find by path to the photo or by header/metadata from last response
        String photoName = "";
        picture.setFilename(photoName);

        return photo;
//        throw new UnsupportedOperationException("Method is not implemented");
    }

    private byte[] serializePhoto(Object receiverPhoto) {
        byte[] serializedObject = new byte[0];

        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);

            objectOut.writeObject(receiverPhoto);
            serializedObject = byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serializedObject;
    }

    private void takePicture() {
        //TODO: do i need to set gopro before taking shoot??
        setGoproBeforeTakingPicture();

        WebTarget target = client.target(Constants.TRIGGER_PHOTO);
        Response response = target.request().get();
        checkResponseStatus(response);
    }

    private void setGoproBeforeTakingPicture() {
        //do i need to do it? probably yes, but now i don't know, what should i set
    }

    private void checkResponseStatus(Response response) {
        if(response.getStatus() != Response.Status.OK.getStatusCode())
            throw new SNEM_SimpleCamera_GoPro_Exception("Response code is not 200, but: " +response.getStatus());
    }
}
