package sk.homisolutions.shotbox.tools.api.external.userinterface;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.userinterface.GuiPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 11/1/16.
 */
public interface GraphicalInterface extends ShotBoxExternalModule {

    void setProvider(GuiPlatformProvider provider);

    void showPicture(TakenPicture picture);

    void platformIsBusy(String busyMessage);

    void platformIsReady();

    void countdownStarts();

    void allPicturesAreTaken();

//    void showMessageToUser();
}
