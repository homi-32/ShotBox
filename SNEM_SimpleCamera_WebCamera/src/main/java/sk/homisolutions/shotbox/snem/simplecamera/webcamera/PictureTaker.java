package sk.homisolutions.shotbox.snem.simplecamera.webcamera;


import com.github.sarxos.webcam.Webcam;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.models.MimeTypes;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by homi on 11/12/16.
 *
 * resources:
 * http://stackoverflow.com/questions/276292/capturing-image-from-webcam-in-java/4383406#4383406
 * http://stackoverflow.com/questions/8549577/simplest-way-to-take-a-webcam-picture-using-java
 */
public class PictureTaker {
    public TakenPicture takePicture(WebCamRemoteTool camera){
        TakenPicture picture = new TakenPicture();
        Webcam cam = Webcam.getDefault();
        cam.open();
        BufferedImage image = cam.getImage();
        camera.pictureIsTaken();
        cam.close();
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()){
            ImageIO.write(image, "jpg", byteOut);
            byteOut.flush();
            picture.setPicture(byteOut.toByteArray());
            picture.setType(MimeTypes.JPG);
            picture.setFilename();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picture;
    }
}
