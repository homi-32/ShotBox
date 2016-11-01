package sk.homisolutions.shotbox.snem.simplecamera.gopro.multithread_solution_delete_it_later;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 8/22/16.
 */
//Thanks to KonradIT for this repo: https://github.com/KonradIT/goprowifihack
//without it this module could not be built
    //TODO: delete it
public class GoProRemoteToolOld implements SimpleCamera {
    private static final Logger logger = Logger.getLogger(GoProRemoteToolOld.class);

    private CameraPlatformProvider provider = null;

    public GoProRemoteToolOld(){
        System.out.println("GOPRO module initialized");
        //TODO create and call method "isGoproConnectedToWifi()"
//        try {
//            new GoProInitOld().connectToGoPro();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void setProvider(CameraPlatformProvider provider) {
        this.provider = provider;
    }

    //TODO: this can not be constructed as multi-thread solution. GoPro camera does not have performance to serve more than 1 thread - I founded it by black-box testing
    @Override
    public void takeShoot() {
        logger.fatal("Taking shot called");
        /*multithread solution:*//*
        try {
            GoProServiceOld service = new GoProServiceOld(provider, new TakenPicture());
            Thread thread = new Thread(service);
            thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        */

    }

    @Override
    public void run() {

    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }
}
