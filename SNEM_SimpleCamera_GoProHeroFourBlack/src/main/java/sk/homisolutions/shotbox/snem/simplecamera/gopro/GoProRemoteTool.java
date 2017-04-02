package sk.homisolutions.shotbox.snem.simplecamera.gopro;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 10/3/16.
 * new implementation of old concept, but not multi-thread
 */
//Thanks to KonradIT for this repo: https://github.com/KonradIT/goprowifihack
//without it this module could not be built
public class GoProRemoteTool implements SimpleCamera {
    private static final Logger logger = Logger.getLogger(GoProRemoteTool.class);

    private CameraPlatformProvider provider = null;
    private GoProService service = null;

    private boolean streamingVideo = false;

    public GoProRemoteTool(){
        logger.info("GOPRO module initialized. First testing shot being executed.");

        //TODO create and call method "isGoproConnectedToWifi()"

//        executeInitializeCheck();
    }

    private void executeInitializeCheck() {
        new Thread(){
            @Override
            public void run() {
                logger.info("Initial check. One testing photo will be taken");

                long timestamp = System.currentTimeMillis() + 15500l;

                while(timestamp > System.currentTimeMillis()){
                    if(provider != null){
                        takeShoot();
                        return;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                throw new SNEM_SimpleCamera_GoPro_Exception("Initial setup check fails. " +
                        "Module will probably not working");
            }
        }.start();
    }

    @Override
    public void setProvider(CameraPlatformProvider provider) {
        this.provider = provider;
        setupGoProService();
    }

    @Override
    public void takeShoot() {
        //lazy initialization for service, because of object provider, which is not available in time of constructing RemoteTool object
        setupGoProService();
        this.service.takePhotoViaGoPro(new TakenPicture());
    }

    @Override
    public void run() {
    }

    private void setupGoProService() {
        if(service == null){
            if(provider == null){
                throw new SNEM_SimpleCamera_GoPro_Exception("Camera Platform Provider is not available");
            }
            service = new GoProService(provider, this);
        }
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

//    @Override
//    public ShotBoxConnection provideVideoStreamConnection() {
//        logger.info("Opening video stream");
//        ShotBoxConnection connection = new ShotBoxConnection();
//        streamingVideo = true;
//        GoProSetupHelper.getInstance().setupVideoStreaming();
//        new Thread(){
//            @Override
//            public void run() {
//                while (streamingVideo){
//                    GoProSetupHelper.getInstance().enableVideoStreaming();
//                    try {
//                        Thread.sleep(15000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                GoProSetupHelper.getInstance().disableVideoStreaming();
//            }
//        }.start();
//
//
//        connection.setAddress(Constants.VIDEO_STREAM_CONNECTION_URL_WITH_PORT);
//        connection.setProtocol(Constants.VIDEO_STREAM_CONNECTION_PROTOCOL);
//        connection.setModule(this);
//        return connection;
//    }
//
//    @Override
//    public void closeVideoStream() {
//        logger.info("Closing video stream");
//        streamingVideo = false;
//    }
}
