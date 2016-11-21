package sk.homisolutions.shotbox.snem.gui.webbasic.services;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.gui.webbasic.GuiState;
import sk.homisolutions.shotbox.snem.gui.webbasic.StateManager;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by homi on 11/2/16.
 */
@Path("/api/")
public class GuiService {
    private static final Logger logger = Logger.getLogger(GuiService.class);
    private StateManager stateManager = StateManager.getInstance();

    @GET
    @Path("trigger")
    public Boolean trigger(){
        logger.info("Triggering shot service is called.");
        stateManager.setStateTakingPicture();
        return true;
    }

    @GET
    @Path("getCountdown")
    public Long getMillisToTakingShot(){
        return stateManager.getMillisToTakingShot();
    }

    @GET
    @Path("getState")
    public String getPlatformState(){
        return stateManager.getState().getString();
    }

    @GET
    @Path("getBusyMessage")
    public String getBusyMessageFromPlatform(){
        return stateManager.getBusyMessage();
    }

    @GET
    @Path("keepPhoto")
    public boolean doesUserWantToKeepTakenPhoto(@QueryParam("decision")Boolean userDecision){
        logger.info("User decided to keep picture: " +userDecision);
        stateManager.userWantPicture(userDecision);
        return userDecision;
    }

    @GET
    @Path("blockPhotos")
    public boolean blockAllIncommingPhotos(){
        stateManager.blockAllPictures();
        return true;
    }

//    not using at this time
//    @GET
//    @Path("getPhotoPath")
//    public String getPathToPhotoWhichShouldBeShowed(){
//        logger.info("Getting path to picture, which should be showed on GUI.");
//        String path = stateManager.getTempFilePath();
//        logger.info("Photo path to show:" +path);
//        return path;
//    }
}
