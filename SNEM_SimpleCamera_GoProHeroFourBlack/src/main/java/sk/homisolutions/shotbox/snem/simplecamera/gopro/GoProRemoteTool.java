package sk.homisolutions.shotbox.snem.simplecamera.gopro;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 10/3/16.
 * new implementation of old concept, but not multi-thread
 */
//Thanks to KonradIT for this repo: https://github.com/KonradIT/goprowifihack
//without it this module could not be built
public class GoProRemoteTool implements SimpleCamera {
    private static final Logger logger = Logger.getLogger(GoProRemoteTool.class);

    private CameraPlatformProvider provider = null;
    private GoProService service = null;

    public GoProRemoteTool(){
        logger.info("GOPRO module initialized");
        //TODO create and call method "isGoproConnectedToWifi()"
    }
    @Override
    public void setProvider(CameraPlatformProvider provider) {
        this.provider = provider;
        setupGoProService();
    }

    @Override
    public void takeShoot() {
        //lazy initialization for service, because of object provider, which is not available in time of constructing RemoteTool object
        setupGoProService();
        this.service.takePhotoViaGoPro(new TakenPicture());
    }

    @Override
    public void run() {
    }

    private void setupGoProService() {
        if(service == null){
            if(provider == null){
                throw new SNEM_SimpleCamera_GoPro_Exception("Camera Platform Provider is not available");
            }
            service = new GoProService(provider);
        }
    }
}
