package sk.homisolutions.shotbox.snem.advancedcamera.gopro;

import org.apache.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by homi on 10/9/16.
 */
public class GoProSetupHelper {

    private static final GoProSetupHelper INSTANCE = new GoProSetupHelper();
    private static final Logger logger = Logger.getLogger(GoProSetupHelper.class);

    private Client client;

    private GoProSetupHelper(){
        //singleton
        this.client = ClientBuilder.newClient();
    }

    public static GoProSetupHelper getInstance(){
        return INSTANCE;
    }

    public void initializeSetup(){
        synchronized (GoProSetupHelper.class) {
            WebTarget target;
            Response response;
            GoProConnectionHelper connection = GoProConnectionHelper.getInstance();

            logger.info("Initial module setup");

            //turning on quick capture feature
            logger.info("Setting quick capture feature");
            target = client.target(Constants.QUICK_CAPTURE_ON);
            response = target.request().get();
            connection.checkResponseStatus(response);

            //turning on gyro-based photo orientation
            logger.info("Setting gyro-based orientation");
            target = client.target(Constants.GYRO_BASED_ORIENTATION);
            response = target.request().get();
            connection.checkResponseStatus(response);

            //turning on white balance feature
            logger.info("enabling native white balance");
            target = client.target(Constants.SET_NATIVE_WHITE_BALANCE_FOR_PHOTO);
            response = target.request().get();
            connection.checkResponseStatus(response);

            //turn on gopro color protune feature
            logger.info("enabling gopro color coloration");
            target = client.target(Constants.SET_COLOR_TO_GOPRO_FOR_PHOTO);
            response = target.request().get();
            connection.checkResponseStatus(response);

            //setting gopro resolution
            logger.info("setting medium photo resolution");
            target = client.target(Constants.SET_PHOTO_RESOLUTION_TO_MEDIUM_7MP);
            response = target.request().get();
            connection.checkResponseStatus(response);

            setPhotoModeOnGoPro();
        }
    }

    public void setPhotoModeOnGoPro() {
        synchronized (GoProSetupHelper.class) {
            logger.info("setting photo mode for gopro");
            WebTarget target = client.target(Constants.TURN_ON_PHOTO);
            Response response = target.request().get();
            GoProConnectionHelper.getInstance().checkResponseStatus(response);
        }
    }

    public void setupVideoStreaming(){
        WebTarget target;
        Response response;
        GoProConnectionHelper connection = GoProConnectionHelper.getInstance();

        //set bitrate to 2.4Mbps
        target = client.target(Constants.SET_VIDEO_STREAM_BITRATE_TO_2point4Mbps);
        response = target.request().get();
        connection.checkResponseStatus(response);

        //set screen resolution to 480p 3:4
        target = client.target(Constants.SET_VIDEO_STREAM_WINDOWS_SIZE_TO_480_3to4);
        response = target.request().get();
        connection.checkResponseStatus(response);
    }

    public void enableVideoStreaming(){
        synchronized (GoProSetupHelper.class) {
            logger.fatal("VIDEO RESEARCH - ENABLE VIDEO STREAM");
            WebTarget target = client.target(Constants.ENABLE_VIDEO_STREAM);
            Response response = target.request().get();
            GoProConnectionHelper.getInstance().checkResponseStatus(response);
        }
    }

    public void disableVideoStreaming(){
        synchronized (GoProSetupHelper.class) {
            logger.fatal("VIDEO RESEARCH - DISABLE VIDEO STREAM");
            WebTarget target = client.target(Constants.DISABLE_VIDEO_STREAM);
            Response response = target.request().get();
            GoProConnectionHelper.getInstance().checkResponseStatus(response);
        }
    }
}
