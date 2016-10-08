package sk.homisolutions.shotbox.tools.api.external.imageprocessing;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.util.List;

/**
 * Created by homi on 10/8/16.
 */
public interface ImageFilter extends ShotBoxExternalModule {

    void setProvider(ImageFilterPlatformProvider provider);

    List<TakenPicture> applyFilter(List<TakenPicture> pictures);
}
