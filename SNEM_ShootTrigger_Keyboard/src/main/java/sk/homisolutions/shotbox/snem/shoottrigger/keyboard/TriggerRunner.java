package sk.homisolutions.shotbox.snem.shoottrigger.keyboard;

import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

import java.util.Scanner;

/**
 * Created by homi on 8/20/16.
 */
public class TriggerRunner implements ShootTrigger{
    TriggerPlatformProvider provider;

    public TriggerRunner(){
    }

    @Override
    public void setProvider(TriggerPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        System.out.println("------TRIGGER STARTS------");

        Scanner scanner = new Scanner(System.in);

        String line = "";
        do{
            line = scanner.nextLine();
//            if(line.equals("a")) {
                provider.takeShoot();
//            }
        }while (line != null);

        System.out.println("------TRIGGER ENDS------");
    }
}
