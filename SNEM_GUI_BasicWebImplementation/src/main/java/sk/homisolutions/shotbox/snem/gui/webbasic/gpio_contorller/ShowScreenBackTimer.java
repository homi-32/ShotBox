package sk.homisolutions.shotbox.snem.gui.webbasic.gpio_contorller;

import org.apache.log4j.Logger;

/**
 * Created by homi on 3/28/17.
 */
public class ShowScreenBackTimer implements Runnable {

    private static final Logger logger = Logger.getLogger(ShowScreenBackTimer.class);
    private Gpio guiGpioController;
    private boolean active;

    public ShowScreenBackTimer(Gpio controller){
        this.guiGpioController = controller;
        this.active = true;
    }

    public void dissableTimer(){
        logger.fatal("2######################################################## timer is disabled");
        this.active = false;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(55000l);
            if(active){
                guiGpioController.showCameraScreen();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
