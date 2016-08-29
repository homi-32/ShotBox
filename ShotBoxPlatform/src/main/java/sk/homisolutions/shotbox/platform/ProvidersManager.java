package sk.homisolutions.shotbox.platform;

import sk.homisolutions.shotbox.platform.providers.CameraProvider;
import sk.homisolutions.shotbox.platform.providers.TriggerProvider;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

/**
 * Created by homi on 8/20/16.
 */
public class ProvidersManager {

    private static final ProvidersManager INSTANCE = new ProvidersManager();

    private boolean isInitialized = false;

    private CameraPlatformProvider cameraProvider;
    private TriggerPlatformProvider triggerProvider;

    private ProvidersManager(){
        //singleton
    }

    public static ProvidersManager getInstance() {
        return INSTANCE;
    }

    public void initializeProviders() {
        cameraProvider = new CameraProvider();
        triggerProvider = new TriggerProvider();

        isInitialized = true;
    }

    public CameraPlatformProvider getCameraProvider() {
        return cameraProvider;
    }

    public TriggerPlatformProvider getTriggerProvider() {
        return triggerProvider;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
