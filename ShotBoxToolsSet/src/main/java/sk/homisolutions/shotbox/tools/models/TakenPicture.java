package sk.homisolutions.shotbox.tools.models;

import javax.activation.MimeType;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by homi on 8/20/16.
 */
public class TakenPicture{

    private String filename;
    private MimeTypes type;
    //what is better, Object, or byte array byte[] ?????
    private byte[] picture;

    public TakenPicture(){
        this.type = MimeTypes.NOT_DEFINED;
        this.picture = new byte[0]; //"".getBytes();
        setFilename();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(){
        setFilename(null);
    }
    public void setFilename(String filename) {
        if(filename == null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss_a");
            String formattedTimestamp = sdf.format(new Date());
            this.filename = "Photo_"+formattedTimestamp+"."+type.getSuffix();
        }else {
            this.filename = filename;
        }
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
