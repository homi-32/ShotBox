package sk.homisolutions.shotbox.snem.shoottrigger.keyboard.notWorking;

import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by homi on 8/20/16.
 */
/*
Used solution from:
stackoverflow.com/questions/18037576/how-do-i-check-if-the-user-is-pressing-a-key
 */
public class TriggerRunner  {
    private TriggerPlatformProvider provider;

//    @Override
    public void setProvider(TriggerPlatformProvider provider) {
        this.provider = provider;
    }
//    @Override
    public void run() {
        System.out.println("----------TRIGGER STARTED--------------");
        registerKeyListenner();
        readListener();
    }

    private void readListener() {
        while (true){
            if(IsEnterPressed.isPressed()){
                provider.takeShoot();
                while(IsEnterPressed.isPressed()){
                    sleep();
                }
            }
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerKeyListenner(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        System.out.println("dispatchKeyEventMethod started");
                        switch (e.getID()){
                            case KeyEvent.KEY_PRESSED:
                                System.out.println("key pressed");
                                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                                    System.out.println("enter pressed");
                                    IsEnterPressed.setPressed(true);
                                }
                                break;
                            case KeyEvent.KEY_RELEASED:
                                System.out.println("key released");
                                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                                    System.out.println("enter released");
                                    IsEnterPressed.setPressed(false);
                                }
                                break;
                        }
                        System.out.println("after switch");
                        return false;
                    }
                });
    }
}
