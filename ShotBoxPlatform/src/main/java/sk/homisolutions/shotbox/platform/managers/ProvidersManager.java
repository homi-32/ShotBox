package sk.homisolutions.shotbox.platform.managers;

import sk.homisolutions.shotbox.platform.providers.*;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageHandlerPlatformProvider;
import sk.homisolutions.shotbox.tools.api.internal.scene.ScenePlatformProvider;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by homi on 8/20/16.
 */
public class ProvidersManager {

    private static final ProvidersManager INSTANCE = new ProvidersManager();

    private boolean isInitialized = false;

    private CameraPlatformProvider cameraProvider;
    private TriggerPlatformProvider triggerProvider;
    private ImageHandlerPlatformProvider imageHandlerProvider;
    private ScenePlatformProvider sceneProvider;
    private ImageFilterPlatformProvider imageFilterProvider;

    private ProvidersManager(){
        //singleton
    }

    public static ProvidersManager getInstance() {
        return INSTANCE;
    }

    public void initializeProviders() {
        cameraProvider = new CameraProvider();
        triggerProvider = new TriggerProvider();
        imageHandlerProvider = new ImageHandlerProvider();
        sceneProvider = new SceneProvider();
        imageFilterProvider = new ImageFilterProvider();

        isInitialized = true;
    }

    public CameraPlatformProvider getCameraProvider() {
        return cameraProvider;
    }

    public TriggerPlatformProvider getTriggerProvider() {
        return triggerProvider;
    }

    public ImageHandlerPlatformProvider getImageHandlerProvider() {
        return imageHandlerProvider;
    }

    public ScenePlatformProvider getSceneProvider() {
        return sceneProvider;
    }

    public ImageFilterPlatformProvider getImageFilterProvider() {
        return imageFilterProvider;
    }

    public List<ShotBoxPlatformProvider> getAllProviders(){
        List<ShotBoxPlatformProvider> providers = new ArrayList<>();
        providers.add(cameraProvider);
        providers.add(triggerProvider);
        providers.add(imageHandlerProvider);
        providers.add(imageFilterProvider);
        providers.add(sceneProvider);
        return providers;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}