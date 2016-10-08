package sk.homisolutions.shotbox.snem.shoottrigger.keyboard;

import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

import java.io.PipedInputStream;
import java.util.Scanner;

/**
 * Created by homi on 8/20/16.
 */
public class TriggerRunner implements ShootTrigger{
    private TriggerPlatformProvider provider;
    Scanner scanner = new Scanner(System.in);

    public TriggerRunner(){
    }

    @Override
    public void setProvider(TriggerPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        System.out.println("------TRIGGER STARTS------");

        String line = "";
        try {
            do {
                line = scanner.nextLine();
//            if(line.equals("a")) {
                provider.takeShoot(this);
//            }
            } while (line != null);
        }catch (IllegalStateException e){
            System.out.println("scanner is closed");
        }

        System.out.println("------TRIGGER ENDS------");
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {
        if(message.isShutdown()){
            scanner.close();
        }
    }
}
