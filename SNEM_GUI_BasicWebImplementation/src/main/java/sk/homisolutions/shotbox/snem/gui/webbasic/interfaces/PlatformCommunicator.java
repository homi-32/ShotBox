package sk.homisolutions.shotbox.snem.gui.webbasic.interfaces;

import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 11/12/16.
 */
public interface PlatformCommunicator {
    void triggerTakingShot();

    Long getMillisToTakingShot();

    void userWantPicture(boolean decision, TakenPicture picture);
}
