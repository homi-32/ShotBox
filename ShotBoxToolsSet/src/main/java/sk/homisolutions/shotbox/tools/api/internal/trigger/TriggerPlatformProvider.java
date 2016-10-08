package sk.homisolutions.shotbox.tools.api.internal.trigger;

import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;

/**
 * Created by homi on 8/20/16.
 */
public interface TriggerPlatformProvider  extends ShotBoxPlatformProvider {

    void takeShoot(ShootTrigger trigger);
}
