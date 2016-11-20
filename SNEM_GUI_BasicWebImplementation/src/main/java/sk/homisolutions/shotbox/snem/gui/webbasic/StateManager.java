package sk.homisolutions.shotbox.snem.gui.webbasic;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.gui.webbasic.interfaces.PlatformCommunicator;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

/**
 * Created by homi on 11/12/16.
 */
public class StateManager {
    private static final Logger logger = Logger.getLogger(StateManager.class);
    private static final StateManager INSTANCE = new StateManager();

    private PlatformCommunicator communicator = GuiAdapter.getINSTANCE();
    private GuiState state = GuiState.READY;
    private TakenPicture picture;
    private TemporaryPicture tempPicture;
    private String busyMessage = "";

    private StateManager() {/*singleton*/}
    public static StateManager getInstance(){return INSTANCE;}

    //get state method
    public GuiState getState() {synchronized (StateManager.class){return state;}}
    //Setting state methods
    public void setStateReady(){synchronized (StateManager.class){state = GuiState.READY;}}
    public void setStateCountdown(){synchronized (StateManager.class){state = GuiState.COUNTDOWN;}}
    public void setStateTakingPicture(){synchronized (StateManager.class){state = GuiState.TAKING_PICTURE; communicator.triggerTakingShot();}}
    public void allPicturesAreTaken() {synchronized (StateManager.class){state = GuiState.PHOTO_IS_TAKEN;}}
    public void setStatePhotoProvided(TakenPicture picture, String pathToResources){synchronized (StateManager.class){tempPicture = new TemporaryPicture(picture, pathToResources); state = GuiState.PHOTO_PROVIDED;this.picture=picture;}}
    public void setStatePlatformIsBusy(String message){synchronized (StateManager.class){state = GuiState.BUSY; busyMessage = message;}}

    //helper methods
    public Long getMillisToTakingShot(){synchronized (StateManager.class){return communicator.getMillisToTakingShot();}}
    public String getBusyMessage() {synchronized (StateManager.class){return busyMessage;}}
    public void userWantPicture(Boolean userDecision) {synchronized (StateManager.class){communicator.userWantPicture(userDecision, picture);}}
    public String getTempFilePath(){synchronized (StateManager.class){return tempPicture.getLocalPathToPicture();}};
}
