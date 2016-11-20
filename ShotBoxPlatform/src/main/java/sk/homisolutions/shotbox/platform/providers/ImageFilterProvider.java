package sk.homisolutions.shotbox.platform.providers;

import sk.homisolutions.shotbox.platform.managers.CountdownManager;
import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 10/8/16.
 */
public class ImageFilterProvider implements ImageFilterPlatformProvider {

//    @Override
//    public void returnFilteredPicture(TakenPicture picture, ImageFilter filter) {
//        synchronized (ImageFilterProvider.class){
//
//        }
//    }

    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (ImageFilterProvider.class){
            ModulesManager.getInstance().propagateMessage(message, module);
        }
    }

    @Override
    public Long getMillisToTakingShot() {
        return CountdownManager.getInstance().getMillisToTakingShot();
    }
}
