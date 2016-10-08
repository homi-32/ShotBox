package sk.homisolutions.shotbox.tools.api.internal.scene;

import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;

/**
 * Created by homi on 10/7/16.
 */
public interface ScenePlatformProvider  extends ShotBoxPlatformProvider {

    void sceneIsSetUp(SceneController controller);

    void sceneIsReleased(SceneController controller);
}
