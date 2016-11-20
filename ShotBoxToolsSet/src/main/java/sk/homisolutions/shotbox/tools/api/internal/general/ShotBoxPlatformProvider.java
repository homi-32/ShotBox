package sk.homisolutions.shotbox.tools.api.internal.general;

import sk.homisolutions.shotbox.tools.Constants;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

import java.io.File;

/**
 * Created by homi on 10/8/16.
 */
public interface ShotBoxPlatformProvider {

    void sendGlobalMessage(ShotBoxMessage message, ShotBoxExternalModule module);

    default String getPathToResourcesWorkingDirectory(Object requester){
        File directory = new File(Constants.DIRECTORY_FOR_MODULES_RESOURCES+requester.getClass().getCanonicalName());
        directory.mkdir();
        return directory.getAbsolutePath();
    }

    Long getMillisToTakingShot();
}
