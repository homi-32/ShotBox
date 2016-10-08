package sk.homisolutions.shotbox.tools.api.external.imageprocessing;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageHandlerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/30/16.
 */
/*
There is need for split ImageHandler to ImageStoring, ImageSharing,
 ImageEditor (for example: applying filters to image) and ScreenShower
 (send taken screen to GUI, to show it),
but it will be applied lately.
 */
//Temporary module api
public interface ImageHandler extends ShotBoxExternalModule {

    void setProvider(ImageHandlerPlatformProvider provider);

    void handleImage(TakenPicture picture);

//    void requestPicture(String photoFilePath);
}
