package sk.homisolutions.shotbox.snem.gui.webbasic.gpio_contorller;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.gui.webbasic.GuiState;
import sk.homisolutions.shotbox.snem.gui.webbasic.StateManager;

/**
 * Created by homi on 3/28/17.
 */
public class Gpio {
    private static Gpio INSTANCE;
    private boolean cameraScreenSelected = true;
    private static final Logger logger = Logger.getLogger(Gpio.class);

    private StateManager stateManager = StateManager.getInstance();
    private ShowScreenBackTimer timer;
    private GpioController gpio;
    private GpioPinDigitalOutput relay;

    private Gpio(){
        //GPIO needs to be controlled just few number of instances
        setupRaspberryGpio();
        setYesButtonTrigger();
        setNoButtonTrigger();
    }

    public static Gpio getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Gpio();
        }
        return INSTANCE;
    }

    private void setupRaspberryGpio(){
        //in documentation is written, that getInstance is not thread safe, so, maybe will this help?
        synchronized (GpioFactory.class){
            gpio = GpioFactory.getInstance();
            logger.fatal("..............................................Initializing relay state");
            relay = gpio.provisionDigitalOutputPin(Constants.SWITCH_SCREEN_BUTTON, PinState.LOW);
            relay.low();
            //setting pin state, which should be set, if application become terminated
            relay.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        }
    }

    private void switchScreen(){
//        logger.fatal("4??????????????????????????????????????????????-device called");
        cameraScreenSelected = !cameraScreenSelected;

        synchronized (GpioFactory.class){
//            logger.fatal("5??????????????????????????????????????????????-device is used");
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    logger.fatal("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHIGH");
                    logger.fatal(relay.getState());
                    relay.high();
                    logger.fatal(relay.getState());
                    try {
                        //5000 when it was working
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    logger.fatal("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOWWWWWWWWWWWWWW");
                    logger.fatal(relay.getState());
                    relay.low();
                    logger.fatal(relay.getState());
                }
            }).start();
//            logger.fatal("6??????????????????????????????????????????????-device already used");
        }
    }

    private void noUserActionPrevention(){
        timer = new ShowScreenBackTimer(this);
        new Thread(timer).run();
    }

    public void showCameraScreen(){
//        logger.fatal("1######################################################## show camera called" +cameraScreenSelected);
        if(!cameraScreenSelected){
            switchScreen();
            timer.dissableTimer();
        }
    }

    public void showGuiScreen(){
//        logger.fatal("2??????????????????????????????????????????????-switch is called" +cameraScreenSelected);
        if(cameraScreenSelected){
//            logger.fatal("3??????????????????????????????????????????????-switch switched screen");
            switchScreen();
            noUserActionPrevention();
        }
    }


    private void setYesButtonTrigger(){
        Pin pin = Constants.YES_BUTTON;

        synchronized (GpioFactory.class) {
            //push logic is set to pull down circuit
            GpioPinDigitalInput button = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
            //setting pin state, which should be set, if application become terminated
            button.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
            //registering listener for button
            button.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                    //if pin is receiving signal (pull down logic), button is pushed
                    if (gpioPinDigitalStateChangeEvent.getState().isHigh() && stateManager.getState() == GuiState.PHOTO_PROVIDED) {
                        stateManager.userWantPicture(true);
                        showCameraScreen();
                    }
                }
            });
        }
    }

    private void setNoButtonTrigger(){
        Pin pin = Constants.NO_BUTTON;

        synchronized (GpioFactory.class) {
            //push logic is set to pull down circuit
            GpioPinDigitalInput button = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
            //setting pin state, which should be set, if application become terminated
            button.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
            //registering listener for button
            button.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                    //if pin is receiving signal (pull down logic), button is pushed
                    if (gpioPinDigitalStateChangeEvent.getState().isHigh() && stateManager.getState() == GuiState.PHOTO_PROVIDED) {
                        stateManager.userWantPicture(false);
                        showCameraScreen();
                    }
                }
            });
        }
    }
}
