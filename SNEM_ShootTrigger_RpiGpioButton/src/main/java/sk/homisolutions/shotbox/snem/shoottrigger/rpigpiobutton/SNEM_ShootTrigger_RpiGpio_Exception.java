package sk.homisolutions.shotbox.snem.shoottrigger.rpigpiobutton;

/**
 * Created by homi on 8/30/16.
 */
public class SNEM_ShootTrigger_RpiGpio_Exception extends RuntimeException {
    public SNEM_ShootTrigger_RpiGpio_Exception() {
        super();
    }

    public SNEM_ShootTrigger_RpiGpio_Exception(String message) {
        super(message);
    }

    public SNEM_ShootTrigger_RpiGpio_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public SNEM_ShootTrigger_RpiGpio_Exception(Throwable cause) {
        super(cause);
    }

    protected SNEM_ShootTrigger_RpiGpio_Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
