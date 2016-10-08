package sk.homisolutions.shotbox.platform.providers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.platform.managers.WorkflowManager;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.internal.scene.ScenePlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 10/7/16.
 */
public class SceneProvider implements ScenePlatformProvider {

    private Logger logger = Logger.getLogger(SceneProvider.class);

    @Override
    public void sceneIsSetUp(SceneController controller) {
        synchronized (SceneProvider.class) {
            WorkflowManager.getInstance().devicesArePreparedToTakingShot(controller);
        }
    }

    @Override
    public void sceneIsReleased(SceneController controller) {
        synchronized (SceneProvider.class) {
            logger.info("All scenes are released");
        }
    }

    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (SceneProvider.class){
            ModulesManager.getInstance().propagateMessage(message, module);
        }
    }
}
