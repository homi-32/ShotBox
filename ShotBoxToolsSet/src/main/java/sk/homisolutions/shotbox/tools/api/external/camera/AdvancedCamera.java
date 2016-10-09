package sk.homisolutions.shotbox.tools.api.external.camera;

import sk.homisolutions.shotbox.tools.models.ShotBoxConnection;

/**
 * Created by homi on 10/8/16.
 */
public interface AdvancedCamera extends SimpleCamera {
    ShotBoxConnection provideVideoStreamConnection();
    void closeVideoStream();
}
