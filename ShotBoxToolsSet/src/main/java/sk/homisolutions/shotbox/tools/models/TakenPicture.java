package sk.homisolutions.shotbox.tools.models;

import javax.activation.MimeType;
import java.io.File;
import java.io.Serializable;

/**
 * Created by homi on 8/20/16.
 */
public class TakenPicture{

    private String filename;
    private MimeTypes type;
    //what is better, Object, or byte array byte[] ?????
    private byte[] picture;

    public TakenPicture(){
        this.filename = "";
        this.type = MimeTypes.NOT_DEFINED;
        this.picture = new byte[0]; //"".getBytes();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MimeTypes getType() {
        return type;
    }

    public void setType(MimeTypes type) {
        this.type = type;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
