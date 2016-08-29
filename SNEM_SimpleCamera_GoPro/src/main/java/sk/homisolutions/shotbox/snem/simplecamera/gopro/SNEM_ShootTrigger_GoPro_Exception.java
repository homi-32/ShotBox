package sk.homisolutions.shotbox.snem.simplecamera.gopro;

/**
 * Created by homi on 8/29/16.
 */
public class SNEM_ShootTrigger_GoPro_Exception extends RuntimeException{

    public SNEM_ShootTrigger_GoPro_Exception() {
        super();
    }

    public SNEM_ShootTrigger_GoPro_Exception(String message) {
        super(message);
    }

    public SNEM_ShootTrigger_GoPro_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public SNEM_ShootTrigger_GoPro_Exception(Throwable cause) {
        super(cause);
    }

    protected SNEM_ShootTrigger_GoPro_Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
