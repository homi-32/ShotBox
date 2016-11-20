package sk.homisolutions.shotbox.snem.simplecamera.webcamera;

import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 11/12/16.
 */
public class WebCamRemoteTool implements SimpleCamera {
    CameraPlatformProvider provider;
    PictureTaker taker;

    @Override
    public void setProvider(CameraPlatformProvider provider) {
        this.provider = provider;
        taker = new PictureTaker();
    }

    @Override
    public void takeShoot() {
        TakenPicture picture = taker.takePicture(this);
        provider.provideTakenPicture(picture, this);
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }

    public void pictureIsTaken(){
        provider.notifyPictureIsTaken(this);
    }
}
