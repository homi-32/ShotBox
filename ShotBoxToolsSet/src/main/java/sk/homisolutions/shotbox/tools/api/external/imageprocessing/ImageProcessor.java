package sk.homisolutions.shotbox.tools.api.external.imageprocessing;

import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageProcessorPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.Serializable;

/**
 * Created by homi on 8/30/16.
 */
/*
There is need for split ImageProcessor to ImageStoring, ImageSharing
and ImageEditor (for example: applying filters to image),
but it will be applied lately.
 */
//Temporary module api
public interface ImageProcessor extends Runnable {

    void setProvider(ImageProcessorPlatformProvider provider);

    void processImage(TakenPicture picture);

//    void requestPicture(String photoFilePath);
}
