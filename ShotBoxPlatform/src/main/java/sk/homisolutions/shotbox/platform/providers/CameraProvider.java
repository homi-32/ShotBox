package sk.homisolutions.shotbox.platform.providers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.platform.managers.WorkflowManager;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/20/16.
 */
public class CameraProvider implements CameraPlatformProvider{
    private static final Logger logger = Logger.getLogger(CameraProvider.class);

    //every thread can has own property with: private ThreadLocal<String> name; http://tutorials.jenkov.com/java-concurrency/threadlocal.html

    @Override
    public void provideTakenPicture(TakenPicture picture, SimpleCamera camera) {
        synchronized (CameraProvider.class) {
            WorkflowManager.getInstance().provideTakenPicture(picture, camera);


            //TODO: don't forget this is commented
//            logger.fatal("picture is taken");
//
//            List<ImageHandler> imageProcessors = ModulesManager.getInstance().getImageHandlerModules();
//
//            for (ImageHandler im: imageProcessors) {
//                new Thread(){
//                    @Override
//                    public void run() {
//                        im.handleImage(picture);
//                    }
//                }.start();
//            }
        }
    }

    @Override
    public void notifyPictureIsTaken(SimpleCamera camera) {
        synchronized (CameraProvider.class){
            WorkflowManager.getInstance().shotIsTaken(camera);
        }
    }

    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (CameraProvider.class){
            ModulesManager.getInstance().propagateMessage(message, module);
        }
    }
}
