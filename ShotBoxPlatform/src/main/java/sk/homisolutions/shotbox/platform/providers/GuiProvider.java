package sk.homisolutions.shotbox.platform.providers;

import sk.homisolutions.shotbox.platform.managers.CountdownManager;
import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.platform.managers.WorkflowManager;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.userinterface.GraphicalInterface;
import sk.homisolutions.shotbox.tools.api.internal.userinterface.GuiPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 11/12/16.
 */
public class GuiProvider implements GuiPlatformProvider {
    @Override
    public void pictureIsApproved(boolean approval, TakenPicture picture) {
        synchronized (GuiProvider.class){
                WorkflowManager.getInstance().userChosePhotoToBePropagated(picture, approval);
        }
    }

    @Override
    public void takeShoot(GraphicalInterface trigger) {
        synchronized (GuiProvider.class){
            //TODO: resolve this conflict
            WorkflowManager.getInstance().shotWasTriggered(null);
        }
    }

    @Override
    public void makePlatformReadyNow(GraphicalInterface gui) {
        synchronized (GuiProvider.class){
            //this method should not be used by programmers, but....
            WorkflowManager.getInstance().resetWorkflow();
        }
    }

    @Override
    public void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module) {
        synchronized (GuiProvider.class){
            ModulesManager.getInstance().propagateMessage(message, module);
        }
    }

    @Override
    public Long getMillisToTakingShot() {
        synchronized (GuiProvider.class){
            return CountdownManager.getInstance().getMillisToTakingShot();
        }
    }
}
