package sk.homisolutions.shotbox.platform.providers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 8/20/16.
 */
public class CameraProvider implements CameraPlatformProvider{
    private static final Logger logger = Logger.getLogger(CameraProvider.class);

    //every thread can has own property with: private ThreadLocal<String> name; http://tutorials.jenkov.com/java-concurrency/threadlocal.html

    @Override
    public void provideTakenPicture(TakenPicture picture) {
        synchronized (CameraProvider.class) {
            logger.fatal("picture is taken");
        }
    }
}
