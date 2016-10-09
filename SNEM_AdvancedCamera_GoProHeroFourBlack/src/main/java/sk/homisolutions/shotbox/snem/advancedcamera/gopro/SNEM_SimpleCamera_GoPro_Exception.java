package sk.homisolutions.shotbox.snem.advancedcamera.gopro;

/**
 * Created by homi on 8/29/16.
 */
public class SNEM_SimpleCamera_GoPro_Exception extends RuntimeException{

    public SNEM_SimpleCamera_GoPro_Exception() {
        super();
    }

    public SNEM_SimpleCamera_GoPro_Exception(String message) {
        super(message);
    }

    public SNEM_SimpleCamera_GoPro_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public SNEM_SimpleCamera_GoPro_Exception(Throwable cause) {
        super(cause);
    }

    protected SNEM_SimpleCamera_GoPro_Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
