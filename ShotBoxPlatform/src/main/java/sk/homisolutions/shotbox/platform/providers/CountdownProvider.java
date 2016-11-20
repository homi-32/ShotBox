package sk.homisolutions.shotbox.platform.providers;

import sk.homisolutions.shotbox.platform.managers.CountdownManager;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.countdown.CountdownPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 11/12/16.
 */
public class CountdownProvider implements CountdownPlatformProvider {

    @Override
    public void countdownIsOver() {
        synchronized (CountdownProvider.this) {
            CountdownManager.getInstance().countdownIsOver();
        }
    }

    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (CountdownProvider.this) {
            throw new RuntimeException("Countdown object can not send any message. Something wrong happens.");
        }
    }

    @Override
    public Long getMillisToTakingShot() {
        synchronized (CountdownProvider.this) {
            throw new RuntimeException("Countdown object should never ask platform for countdown. " +
                    "Something wrong happens.");
        }
    }
}
