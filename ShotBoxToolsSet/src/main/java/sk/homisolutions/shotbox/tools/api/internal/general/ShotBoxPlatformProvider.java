package sk.homisolutions.shotbox.tools.api.internal.general;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 10/8/16.
 */
public interface ShotBoxPlatformProvider {

    void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module);
}
