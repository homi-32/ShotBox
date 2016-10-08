package sk.homisolutions.shotbox.tools.api.internal.imageprocessor;

import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/30/16.
 */
public interface ImageHandlerPlatformProvider  extends ShotBoxPlatformProvider {
    void pictureIsAlreadyHandled(TakenPicture picture, ImageHandler handler);
}