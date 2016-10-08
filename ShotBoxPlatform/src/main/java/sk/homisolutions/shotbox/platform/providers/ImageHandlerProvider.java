package sk.homisolutions.shotbox.platform.providers;

import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.platform.managers.WorkflowManager;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageHandlerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/30/16.
 */
public class ImageHandlerProvider implements ImageHandlerPlatformProvider {
    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (ImageHandlerProvider.class){
            ModulesManager.getInstance().propagateMessage(message, module);
        }
    }

    @Override
    public void pictureIsAlreadyHandled(TakenPicture picture, ImageHandler handler) {
        synchronized (ImageHandlerProvider.class){
            WorkflowManager.getInstance().photoIsHandled(picture, handler);
        }
    }

   /*
   lately:

   void providePhotosList(String[] list);

   //provided ImageHandler object will be excluded from sharing
   void sharePhoto(TakenPicture photo, ImageHandler processor);
    */
}
