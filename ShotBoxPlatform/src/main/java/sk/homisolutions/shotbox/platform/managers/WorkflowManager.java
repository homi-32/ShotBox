package sk.homisolutions.shotbox.platform.managers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.Constants;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.external.userinterface.GraphicalInterface;
import sk.homisolutions.shotbox.tools.models.TakenPicture;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

import java.util.*;

/**
 * Created by homi on 10/7/16.
 */
//TODO: Divide WorkflowManager to: Workflow with gui and Workflow without gui
public class WorkflowManager {

    //TODO: this values should be loaded from config file
    /** System Setup **/
    private Long countdownTimeInSeconds = 7l;

    /** Self managements **/
    private static final Logger logger = Logger.getLogger(WorkflowManager.class);
    private static final WorkflowManager INSTANCE = new WorkflowManager();

    private boolean workflowState;
    /** Self management ends **/

    /** Safety Catch state variables **/
    private Thread safetyCatch;
    private boolean safetyCatchInUse;

    private boolean lockedTriggers;
    private boolean countdownIsSet;
    private boolean countdownIsOver;
    private boolean sceneSetupProcessIsEnabled;
    private boolean shotCanBeTaken;
    private boolean sceneCanBeTurnedOff;
    private boolean scenesAreNotTurnedOffYet;
    private boolean allowToProvidePicture;
    private boolean userCanSendPhotoDecision;
    private boolean allowToHandlingPictures;

//    private Map<SceneController, Boolean> isSceneTurnedOn;
    private Integer setScenes;

    private Map<SimpleCamera, Boolean> doesCameraAlreadyTookPicture;
    private Map<SimpleCamera, Boolean> doesCameraAlreadyProvidekPicture;
    private List<TakenPicture> photosSentToGui;
    private Map<TakenPicture, Boolean> whatDecisionDidUserMadeForPhoto;
    private Map<TakenPicture, Boolean> isPictureFromCameraAlreadyHandled;

    private Boolean allCamerasProvidedPhotos;

    /** end of Safety Catch variables **/

    /** Self managements and Safety catch methods **/
    private WorkflowManager(){
        //singleton
        resetWorkflow();
    }

    public static WorkflowManager getInstance(){
        return INSTANCE;
    }

    //in case that workflow gets stuck, there will be prevention, which release resources after some timeout
    private void setSafetyCatch(){
        new Thread(){
            @Override
            public void run() {
                safetyCatchInUse = true;
                long executionTime = System.currentTimeMillis() + Constants.SAFETY_CATCH_TIMEOUT_MILLIS + countdownTimeInSeconds;

                while (safetyCatchInUse && workflowState){
                    if(executionTime < System.currentTimeMillis()){
                        safetyCatchHandler();
                        resetWorkflow();
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void safetyCatchHandler(){
        logger.info("safety catch executing");
    }
    public void resetWorkflow(){
        logger.info("Workflow is recovering");
        safetyCatchInUse = false;

        lockedTriggers = false;
        countdownIsSet = false;
        countdownIsOver = true;
        sceneSetupProcessIsEnabled = false;
        shotCanBeTaken = false;
        sceneCanBeTurnedOff = false;
        scenesAreNotTurnedOffYet = false;
        allowToProvidePicture = false;
        userCanSendPhotoDecision = false;
        allowToHandlingPictures = false;

        setScenes = 0;

        doesCameraAlreadyTookPicture = new HashMap<>();
        doesCameraAlreadyProvidekPicture = new HashMap<>();
        isPictureFromCameraAlreadyHandled = new HashMap<>();
        photosSentToGui = new LinkedList<>();
        whatDecisionDidUserMadeForPhoto = new HashMap<>();

        allCamerasProvidedPhotos = false;

        propagatePlatformIsReady();
        logger.info("Workflow is recovered");
    }

    //TODO: implement propagating for every module
    private void propagatePlatformIsReady() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //inform GUI modules
                for(GraphicalInterface gui: ModulesManager.getInstance().getGuiModules()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gui.platformIsReady();
                        }
                    }).start();
                }
            }
        }).start();
    }


    public void startWorkflow(){
        workflowState = true;

        while (workflowState){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void endWorkflow(){
        workflowState = false;
    }
    /** Self management and Safety Catch ends **/

    /** Standard Workflow starts here **/

    /*
    if gui and scene controller are not available:
    - shot is triggered

    if gui is available, but scene modules not:
    - countdown is set
    - countdown starts triggering

    if gui is not available, but scene module is
    - scene setup started
    - after that, shot is triggered

    if gui and scene modules is available
    - countdown starts
    - parallel: scene is set
     */
    public void shotWasTriggered(ShootTrigger trigger){
        if(!lockedTriggers) {
//            logger.fatal("Triggered shot timestamp: " +System.currentTimeMillis());
            lockAllTriggers();
            if(ModulesManager.getInstance().isAnyGraphicalInterfaceAvailable()) {
                setCountdown();
                propagateCountdown();
            }
            if(ModulesManager.getInstance().isAnySceneControllerAvailable()) {
                prepareDevicesToTakingShot();
            }else{
                if(!ModulesManager.getInstance().isAnyGraphicalInterfaceAvailable()){
                    triggerCameraModules();
                }
            }
        }else {
            logger.error("Denial access recorded");
        }
    }

    private void lockAllTriggers() {
        lockedTriggers = true;
        setSafetyCatch();
    }

    private void setCountdown() {
        countdownIsSet = true;
        CountdownManager.getInstance().setupCountdown(countdownTimeInSeconds);
        countdownIsOver = false;
    }

    //TODO: implement propagating for every module
    private void propagateCountdown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //inform GUI modules
                for(GraphicalInterface gui: ModulesManager.getInstance().getGuiModules()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gui.countdownStarts();
                        }
                    }).start();
                }
            }
        }).start();
    }

    public void countdownIsOver(){
        if(countdownIsSet){
            countdownIsOver = true;
            logger.info("Countdown is over.");
            if(ModulesManager.getInstance().isAnySceneControllerAvailable()){
                devicesArePreparedToTakingShot(null);
            }else {
                triggerCameraModules();
            }
        }else {
            logger.fatal("ShotBox Countdown System violate the workflow process. Something very bad happens.");
        }
    }

    //order every scene controller to setup and register it
    private void prepareDevicesToTakingShot() {
        sceneSetupProcessIsEnabled = true;
        scenesAreNotTurnedOffYet = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(SceneController sceneController: ModulesManager.getInstance().getSceneModules()){
                    new Thread(){
                        @Override
                        public void run() {
                            sceneController.setupScene();
                        }
                    }.start();
                }
            }
        }).start();
//        shotCanBeTaken = true;
    }

    //control, if every controller is setup. if yes, shot can be taken
    public void devicesArePreparedToTakingShot(SceneController controller){
        if(sceneSetupProcessIsEnabled) {

            //countdown can call this method too, but with null argument
            if(controller != null){
                setScenes += 1;
            }

            //if GUI does not exists, variable countdownIsOver is always true
            if (setScenes == ModulesManager.getInstance().getSceneModules().size() && countdownIsOver) {
                logger.info("All scenes with countdown are prepared to taking shot.");
                triggerCameraModules();
            }
        }else {
            logger.error("Denial access recorded");
        }
    }

    private void triggerCameraModules() {
        shotCanBeTaken = true;
        allowToProvidePicture = true;

        List<Thread> threads = new LinkedList<>();

        for(SimpleCamera camera: ModulesManager.getInstance().getSimpleCameraModules()){
            doesCameraAlreadyTookPicture.put(camera, false);
            doesCameraAlreadyProvidekPicture.put(camera, false);
            Thread t = new Thread(){
                @Override
                public void run() {
                    camera.takeShoot();
                }
            };
            threads.add(t);
        }

        threads.forEach(Thread::start);
        sceneCanBeTurnedOff = true;
    }

    public void shotIsTaken(SimpleCamera camera){
        if(shotCanBeTaken) {
            //register, that camera already took picture
            for (Map.Entry<SimpleCamera, Boolean> entry : doesCameraAlreadyTookPicture.entrySet()) {
                if (entry.getKey() == camera) {
                    entry.setValue(true);
                    break;
                }
            }

            //check if every camera took picture
            if (doesCameraAlreadyTookPicture.size() == ModulesManager.getInstance().getSimpleCameraModules().size()) {
                for (Map.Entry<SimpleCamera, Boolean> entry : doesCameraAlreadyTookPicture.entrySet()) {
                    //if any camera did not take picture yet, process can not continue
                    if (!entry.getValue()) {
                        return;
                    }
                }


                //if every camera took picture, GUIs are notified
                notifyGuiThatShotIsAlreadyTaken();

                //scenes can be released
                if (ModulesManager.getInstance().isAnySceneControllerAvailable() && scenesAreNotTurnedOffYet) {
                    scenesAreNotTurnedOffYet = false;
                    releaseDeviceAfterTakingShot();
                }
            }

//            logger.fatal("shot taken timestamp: " +System.currentTimeMillis());
        }else {
            logger.error("Denial access recorded");
        }
    }

    //TODO: implement for all modules??
    private void notifyGuiThatShotIsAlreadyTaken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(GraphicalInterface gui: ModulesManager.getInstance().getGuiModules()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gui.allPicturesAreTaken();
                        }
                    }).start();
                }
            }
        }).start();
    }

    public void provideTakenPicture(TakenPicture picture, SimpleCamera camera){
        if(allowToProvidePicture) {
            logger.info("Picture is provided: " +picture.getFilename());

            shotIsTaken(camera);

            cameraAlreadyProvidedPicture(camera);

            /*
            if gui and filters are not available:
             - photo is propagated

             if gui is available, but filters are not
             - photo is showed to user, if photo can be propagated

             if filters are available
             - if filters are available, there is no matter, is gui is available,
               because photo has to be filtered first
             */
            if (ModulesManager.getInstance().isAnyImageFilterAvailable()) {
                //no matter if gui is available, photo must be filtered first
                logger.info("filters will be applied for: " +picture.getFilename());
                applyPhotoFilters(picture);
            } else {
                logger.info("no filter available");
                //filters are not available
                if(ModulesManager.getInstance().isAnyGraphicalInterfaceAvailable()){
                    logger.info("showing picture to user: " +picture.getFilename());
                    //gui is available, but filters not: photo is showed to user
                    userCanSendPhotoDecision = true;
                    showPictureToUserAndWaitForUserDecision(picture);
                }else {
                    logger.info("no GUI, propagating picture : " +picture.getFilename());
                    //gui is not available, nor filters: photo is automatically propagated
                    List<TakenPicture> pictures = new ArrayList<>();
                    pictures.add(picture);
                    new Thread() {
                        @Override
                        public void run() {
                            propagatePhoto(pictures);
                        }
                    }.start();
                }
            }

//            logger.fatal("picture provided timestamp: " +System.currentTimeMillis());
        }else {
            logger.error("Denial access recorded");
        }
    }

    private void cameraAlreadyProvidedPicture(SimpleCamera camera) {
        for(Map.Entry<SimpleCamera, Boolean> entry: doesCameraAlreadyProvidekPicture.entrySet()){
            if(entry.getKey() == camera){
                entry.setValue(true);
                break;
            }
        }
    }

    private void releaseDeviceAfterTakingShot() {
        if(sceneCanBeTurnedOff){
            new Thread(){
                @Override
                public void run() {
                    for(SceneController controller: ModulesManager.getInstance().getSceneModules()){
                        new Thread(){
                            @Override
                            public void run() {
                                controller.releaseScene();
                            }
                        }.start();
                    }
                }
            }.start();
        }
    }

    //for every photo should be applied set of filters (this approach is implementer) - or - for every photo should be applied just one filter, but more times ?
    //one filter can produfe more photos, but all photos are passed to another filters - filters permutation
    private void applyPhotoFilters(TakenPicture picture) {
        //TODO: I'm not sure, if this can be divided to more threads, or it should be handled by 1 thread with synchronize block, but synchronize block is already in filterProvider
        new Thread(){
            @Override
            public void run() {
                logger.info("start applying filters for photo: " +picture.getFilename());
                List<TakenPicture> pictures = new LinkedList<>();
                pictures.add(picture);
                for(ImageFilter filter: ModulesManager.getInstance().getImageFilterModules()){
                    logger.info("using filter: "+filter.toString()+" for photo: " +picture.getFilename());
                    pictures = filter.applyFilter(pictures);
                }
                if(ModulesManager.getInstance().isAnyGraphicalInterfaceAvailable()){
                    logger.info("showing photo to user: " +picture.getFilename());
                    userCanSendPhotoDecision = true;
                    showPicturesToUserAndWaitForUserDecision(pictures);
                }else {
                    logger.info("no GUI available, propagating photo: " +picture.getFilename());
                    propagatePhoto(pictures);
                }
            }
        }.start();
    }

    private void registerUnhandledPhotos(List<TakenPicture> pictures){
        for(TakenPicture pic: pictures){
            isPictureFromCameraAlreadyHandled.put(pic, false);
        }
    }

    private void checkIfAllCamerasProvidedPhoto() {
        for(Map.Entry<SimpleCamera, Boolean> entry: doesCameraAlreadyProvidekPicture.entrySet()){
            if(!entry.getValue()){
                return;
            }
        }
        allCamerasProvidedPhotos = true;
    }

    private void showPicturesToUserAndWaitForUserDecision(List<TakenPicture> pictures){
        synchronized (WorkflowManager.class) {
            for (TakenPicture p : pictures) {
                logger.info("showing photo to user: " +p.getFilename());
                showPictureToUserAndWaitForUserDecision(p);
            }
        }
    }

    private void showPictureToUserAndWaitForUserDecision(TakenPicture picture) {
        photosSentToGui.add(picture);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (GraphicalInterface gui: ModulesManager.getInstance().getGuiModules()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            gui.showPicture(picture);
                        }
                    }).start();
                }
            }
        }).start();
    }

    public void userChosePhotoToBePropagated(TakenPicture picture, boolean decision){
        if(userCanSendPhotoDecision) {
            logger.info("User made decision: " +decision +" for photo: " +picture.getFilename());
            whatDecisionDidUserMadeForPhoto.put(picture, decision);

            //when every photo is returned
            if(photosSentToGui.size() == whatDecisionDidUserMadeForPhoto.size()) {
                logger.info("all photos are returned from gui, may workflow end?");
                if(checkIfWorkflowMayEnd()){
                    logger.info("yes");
                    resetWorkflow();
                }else {
                    logger.info("no");
                    List<TakenPicture> pictureList = new LinkedList<>();
                    for(Map.Entry<TakenPicture, Boolean> e: whatDecisionDidUserMadeForPhoto.entrySet()){
                        if(e.getValue()){
                            pictureList.add((e.getKey()));
                        }
                    }
                    propagatePhoto(pictureList);
                }
            }
        }else {
            logger.error("Denial access recorded");
        }
    }

    private boolean checkIfWorkflowMayEnd() {
        //if all photos are not allowed by user, workflow may end
        for(Map.Entry<TakenPicture, Boolean> e: whatDecisionDidUserMadeForPhoto.entrySet()){
            if(e.getValue()){
                return false;
            }
        }
        return true;
    }

    private void propagatePhoto(List<TakenPicture> pictures){
        synchronized (WorkflowManager.class) {
            logger.info("calling propagate");
            allowToHandlingPictures = true;
            registerUnhandledPhotos(pictures);
            if(!allCamerasProvidedPhotos){
                checkIfAllCamerasProvidedPhoto();
            }
            for(ImageHandler handler: ModulesManager.getInstance().getImageHandlerModules()){
                logger.info("photo propagator: " +handler.toString());
                new Thread(){
                    @Override
                    public void run() {
                        for(TakenPicture picture: pictures){
                            logger.info("propagating photo: " +picture.getFilename());
                            handler.handleImage(picture);
                        }
                    }
                }.start();
            }
        }
    }

    public void photoIsHandled(TakenPicture picture, ImageHandler handler){
        if(allowToHandlingPictures){
            logger.info("photo is handled: " +picture.getFilename());
            markPictureAsHandled(picture);

            if(allCamerasProvidedPhotos){
                logger.info("check, if all photos are handled");
                //check if all photos are handled
                for (Map.Entry<TakenPicture, Boolean> entry: isPictureFromCameraAlreadyHandled.entrySet()){
                    if(!entry.getValue()) {
                        logger.info("one photo is not handled");
                        return;
                    }
                }
//                logger.fatal("workflow ends timestamp: " +System.currentTimeMillis());
                resetWorkflow();
            }
        }else {
            logger.error("Denial access recorded");
        }
    }

    private void markPictureAsHandled(TakenPicture picture) {
        for (Map.Entry<TakenPicture, Boolean> entry: isPictureFromCameraAlreadyHandled.entrySet()){
            if(entry.getKey() == picture){
                entry.setValue(true);
            }
        }
    }

    /** Standard Workflow ends here **/

}
