package sk.homisolutions.shotbox.tools.api.internal.userinterface;

import sk.homisolutions.shotbox.tools.api.external.userinterface.GraphicalInterface;
import sk.homisolutions.shotbox.tools.api.internal.general.ShotBoxPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 11/1/16.
 */
public interface GuiPlatformProvider extends ShotBoxPlatformProvider {

    void pictureIsApproved(boolean approval, TakenPicture picture);

    void takeShoot(GraphicalInterface trigger);

    void makePlatformReadyNow(GraphicalInterface gui);
}

