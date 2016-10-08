package sk.homisolutions.shotbox.tools.api.external.general;

import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 10/8/16.
 */
public interface ShotBoxExternalModule extends Runnable{

    void receiveGlobalMessage(ShotBoxMessage message);
}
