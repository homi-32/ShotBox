package GoodPackage;

import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

/**
 * Created by homi on 4/20/16.
 */
public class ClassInPackage implements ShootTrigger {
    public void run() {
        System.out.println("triggered......");
    }

    @Override
    public void setProvider(TriggerPlatformProvider provider) {

    }
}
