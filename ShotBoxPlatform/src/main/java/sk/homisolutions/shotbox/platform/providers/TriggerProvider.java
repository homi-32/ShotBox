package sk.homisolutions.shotbox.platform.providers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.platform.ModulesManager;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

import java.util.List;

/**
 * Created by homi on 8/20/16.
 */
public class TriggerProvider implements TriggerPlatformProvider {

    private static final Logger logger = Logger.getLogger(TriggerProvider.class);

    @Override
    public void takeShoot() {
        synchronized (TriggerProvider.class) {
            //TODO delete log
            logger.fatal("TAKING SHOT");

            List<SimpleCamera> cameras = ModulesManager.getInstance().getSimpleCameraModules();

            for (SimpleCamera c : cameras) {
                new Thread() {
                    @Override
                    public void run() {
                        c.takeShoot();
                    }
                }.start();
            }
        }
    }
}
