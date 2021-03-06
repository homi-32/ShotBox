package sk.homisolutions.shotbox.tools.api.external.camera;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;

/**
 * Created by homi on 8/20/16.
 */
public interface SimpleCamera extends ShotBoxExternalModule{

    void setProvider(CameraPlatformProvider provider);

    void takeShoot();
}
