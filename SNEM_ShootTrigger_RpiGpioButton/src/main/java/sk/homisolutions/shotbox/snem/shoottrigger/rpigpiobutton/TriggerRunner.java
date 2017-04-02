package sk.homisolutions.shotbox.snem.shoottrigger.rpigpiobutton;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 8/29/16.
 */
//TODO: draw circuit scheme
    /*
    See comments in POM, this module is little bit tricky.
     */
public class TriggerRunner implements ShootTrigger {


    private static final Logger logger = Logger.getLogger(TriggerRunner.class);
    private TriggerPlatformProvider provider;

    private GpioController gpio;
    private GpioPinDigitalInput button;
    private ShootTrigger INSTANCE;

    private boolean runningState = true;

    public TriggerRunner(){

        logger.info("Instantiate GPIO Trigger");

        INSTANCE = this;

        try {
            //requesting access to GPIO 4 / Physical pin 7 / PI4J pin 07
            Pin pin = new ShotboxGpioHelper().resolveGpioPin(Constants.GPIO_PIN);

            //in documentation is written, that getInstance is not thread safe, so, maybe will this help?
            //TODO: will this help?
            synchronized (GpioFactory.class) {
                gpio = GpioFactory.getInstance();

                //push logic is set to pull down circuit
                button = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
                //setting pin state, which should be set, if application become terminated
                button.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
                //registering listener for button
                button.addListener(new GpioPinListenerDigital() {
                    @Override
                    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                        //if pin is receiving signal (pull down logic), button is pushed
                        if (gpioPinDigitalStateChangeEvent.getState().isHigh()) {
                            logger.info("Trigger pushed");
                            provider.takeShoot(INSTANCE);
                        }
                    }
                });
            }
        }catch (Throwable e){
            logger.fatal("Error occurs during initializing Raspberry GPIO devices: "+e.getMessage()+". Is GPIO available on Raspberry?");
            e.printStackTrace();
        }
    }

    @Override
    public void setProvider(TriggerPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        while (runningState){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {
        if(message.isShutdown()){
            runningState = false;
        }
    }

    /*
    GPIO CODE:


        GpioController gpio = GpioFactory.getInstance();

        //GPIO 4, Physical pin 7, PI4J pin 07
        //initialization
        GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);

        //setting pin state, which happens when application is shutted down or terminated by user
        button.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

        button.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
                if(gpdsce.getState().isHigh()){
                    System.out.println("pushed");
                }
            }
        });

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     */
}
