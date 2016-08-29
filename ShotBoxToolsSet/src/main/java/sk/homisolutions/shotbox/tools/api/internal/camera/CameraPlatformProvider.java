package sk.homisolutions.shotbox.tools.api.internal.camera;

import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/20/16.
 */
public interface CameraPlatformProvider{

    void provideTakenPicture(TakenPicture picture);
}
