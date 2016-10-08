package sk.homisolutions.shotbox.tools.api.internal.camera;

import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/20/16.
 */
public interface CameraPlatformProvider extends ShotBoxPlatformProvider{

    void provideTakenPicture(TakenPicture picture, SimpleCamera camera);

    void notifyPictureIsTaken(SimpleCamera camera);
}
