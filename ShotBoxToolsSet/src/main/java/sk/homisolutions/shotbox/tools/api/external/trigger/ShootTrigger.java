package sk.homisolutions.shotbox.tools.api.external.trigger;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

/**
 * Created by homi on 4/20/16.
 */
public interface ShootTrigger extends ShotBoxExternalModule{

    void setProvider(TriggerPlatformProvider provider);
}
