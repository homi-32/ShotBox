package sk.homisolutions.shotbox.platform.providers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.platform.managers.WorkflowManager;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

import java.util.List;

/**
 * Created by homi on 8/20/16.
 */
public class TriggerProvider implements TriggerPlatformProvider {

    private static final Logger logger = Logger.getLogger(TriggerProvider.class);

    @Override
    public void takeShoot(ShootTrigger trigger) {
        synchronized (TriggerProvider.class) {
            //TODO delete log
            logger.fatal("TAKING SHOT");

            WorkflowManager.getInstance().shotWasTriggered(trigger);
        }
    }

    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (TriggerProvider.class){
            ModulesManager.getInstance().propagateMessage(message, module);
        }
    }
}
