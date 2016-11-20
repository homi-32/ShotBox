package sk.homisolutions.shotbox.tools.api.internal.countdown;

import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxCountdown;

/**
 * Created by homi on 11/12/16.
 */
public interface CountdownPlatformProvider extends ShotBoxPlatformProvider {
    void countdownIsOver();
}
