package sk.homisolutions.shotbox.snem.scenecontroller.rpigpiocontroller;

import com.pi4j.io.gpio.*;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.internal.scene.ScenePlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 11/23/16.
 */
public class RaspberryPiGpio17Controller implements SceneController{
    private static final Logger logger = Logger.getLogger(RaspberryPiGpio17Controller.class);

    private ScenePlatformProvider provider;

    private static final Integer gpioPinNumber = 17;
    private Pin pin;
    private GpioController gpio;
    private GpioPinDigitalOutput gpio17;

    public RaspberryPiGpio17Controller(){

        logger.info("Instantiate SceneController");
        try{
            //in documentation is written, that getInstance is not thread safe, so, maybe will this help?
            synchronized (GpioFactory.class) {
                //requesting access to GPIO 4
                pin = new ShotboxGpioHelper().resolveGpioPin(gpioPinNumber);

                gpio = GpioFactory.getInstance();

                gpio17 = gpio.provisionDigitalOutputPin(pin);
            }

            logger.info("RPI GPIO17 SceneController instantiated.");
        }catch (Throwable e){
            logger.fatal("Error occurs during initializing Raspberry GPIO devices: "+e.getMessage()+". Is GPIO available on Raspberry?");
            e.printStackTrace();
        }
    }

    @Override
    public void setProvider(ScenePlatformProvider provider) {
        this.provider=provider;
        logger.info("RPI GPIO17 SceneController initialized.");
    }

    @Override
    public void setupScene() {
        logger.info("calling scene setup");
        gpio17.high();
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("scene is setup");
        provider.sceneIsSetUp(this);
    }

    @Override
    public void releaseScene() {
        logger.info("calling scene release");
        gpio17.low();
        logger.info("scene released");
        provider.sceneIsReleased(this);
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
