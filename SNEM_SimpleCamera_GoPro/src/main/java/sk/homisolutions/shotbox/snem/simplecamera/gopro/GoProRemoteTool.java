package sk.homisolutions.shotbox.snem.simplecamera.gopro;

import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by homi on 8/22/16.
 */
//Thanks to KonradIT for this repo: https://github.com/KonradIT/goprowifihack
//without it this module could not be built
public class GoProRemoteTool implements SimpleCamera {

    private CameraPlatformProvider provider = null;

    public GoProRemoteTool(){
        System.out.println("GOPRO module initialized");
        try {
            new GoProInit().connectToGoPro();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setProvider(CameraPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void takeShoot() {
        try {
            GoProService service = new GoProService(provider, new TakenPicture());
            Thread thread = new Thread(service);
            thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

    }
}
