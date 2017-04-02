package sk.homisolutions.shotbox.snem.gui.webbasic.gpio_contorller;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import sk.homisolutions.shotbox.snem.gui.webbasic.GuiState;
import sk.homisolutions.shotbox.snem.gui.webbasic.StateManager;

/**
 * Created by homi on 3/28/17.
 */
public class Gpio {
    private static Gpio INSTANCE;
    private boolean cameraScreenSelected = true;

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
            relay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02/*GPIO27*/, PinState.LOW);
            //setting pin state, which should be set, if application become terminated
            relay.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        }
    }

    //GPIO27 - RaspiPin.GPIO_02 pi4j representation
    private void switchScreen(){
        cameraScreenSelected = !cameraScreenSelected;

        synchronized (GpioFactory.class){
            relay.pulse(200, true);
        }
    }

    private void noUserActionPrevention(){
        timer = new ShowScreenBackTimer(this);
        new Thread(timer).run();
    }

    public void showCameraScreen(){
        if(!cameraScreenSelected){
            switchScreen();
            timer.dissableTimer();
        }
    }

    public void showGuiScreen(){
        if(cameraScreenSelected){
            switchScreen();
            noUserActionPrevention();
        }
    }

    //GPIO22 - RaspiPin.GPIO_03 pi4j representation
    private void setYesButtonTrigger(){
        Pin pin = RaspiPin.GPIO_03;

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
                    }
                }
            });
        }

        this.showCameraScreen();
    }

    //GPIO23 - RaspiPin.GPIO_04 pi4j representation
    private void setNoButtonTrigger(){
        Pin pin = RaspiPin.GPIO_04;

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
                        stateManager.blockAllPictures();
                    }
                }
            });
        }

        this.showCameraScreen();
    }
}
