package sk.homisolutions.shotbox.tools.api.external.scene;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.scene.ScenePlatformProvider;

/**
 * Created by homi on 10/7/16.
 */
public interface SceneController extends ShotBoxExternalModule {

    void setProvider(ScenePlatformProvider provider);

    void setupScene();

    void releaseScene();
}
