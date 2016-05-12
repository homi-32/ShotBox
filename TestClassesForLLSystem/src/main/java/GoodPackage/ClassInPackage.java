package GoodPackage;

import sk.homisolutions.shotbox.api.external.trigger.Trigger;

/**
 * Created by homi on 4/20/16.
 */
public class ClassInPackage implements Trigger {
    public void run() {
        System.out.println("triggered......");
    }
}
