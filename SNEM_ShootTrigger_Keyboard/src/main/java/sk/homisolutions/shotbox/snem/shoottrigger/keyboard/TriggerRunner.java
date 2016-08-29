package sk.homisolutions.shotbox.snem.shoottrigger.keyboard;

import sk.homisolutions.shotbox.snem.shoottrigger.keyboard.notWorking.IsEnterPressed;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

import java.util.Scanner;

/**
 * Created by homi on 8/20/16.
 */
public class TriggerRunner implements ShootTrigger{
    TriggerPlatformProvider provider;

    public TriggerRunner(){
        Thread thread  = new Thread(this);
        thread.start();
    }

    @Override
    public void setProvider(TriggerPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        System.out.println("------TRIGGER STARTS------");

        Scanner scanner = new Scanner(System.in);

        while (scanner.nextLine() != null){
            provider.takeShoot();
        }

        System.out.println("------TRIGGER ENDS------");
    }
}
