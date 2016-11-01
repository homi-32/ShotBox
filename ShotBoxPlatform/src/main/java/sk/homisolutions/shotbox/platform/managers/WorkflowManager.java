package sk.homisolutions.shotbox.platform.managers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.Constants;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.util.*;

/**
 * Created by homi on 10/7/16.
 */
public class WorkflowManager {

    /** Self managements **/
    private static final Logger logger = Logger.getLogger(WorkflowManager.class);
    private static final WorkflowManager INSTANCE = new WorkflowManager();

    private boolean workflowState;
    /** Self management ends **/

    /** Safety Catch state variables **/
    private Thread safetyCatch;
    private boolean safetyCatchInUse;

    private boolean lockedTriggers;
    private boolean sceneSetupProcessIsEnabled;
    private boolean shotCanBeTaken;
    private boolean sceneCanBeTurnedOff;
    private boolean allowToProvidePicture;
    private boolean allowToHandlingPictures;

//    private Map<SceneController, Boolean> isSceneTurnedOn;
    private Integer setScenes;

    private Map<SimpleCamera, Boolean> doesCameraAlreadyTookPicture;
    private Map<SimpleCamera, Boolean> doesCameraAlreadyProvidekPicture;
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
                long executionTime = System.currentTimeMillis() + Constants.SAFETY_CATCH_TIMEOUT_MILLIS;

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
        logger.fatal("safety catch executing");
    }
    private void resetWorkflow(){
        logger.fatal("called reset");
        safetyCatchInUse = false;

        lockedTriggers = false;
        sceneSetupProcessIsEnabled = false;
        shotCanBeTaken = false;
        sceneCanBeTurnedOff = false;
        allowToProvidePicture = false;
        allowToHandlingPictures = false;

        setScenes = 0;

        doesCameraAlreadyTookPicture = new HashMap<>();
        doesCameraAlreadyProvidekPicture = new HashMap<>();
        isPictureFromCameraAlreadyHandled = new HashMap<>();

        allCamerasProvidedPhotos = false;
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

    public void shotWasTriggered(ShootTrigger trigger){
        if(!lockedTriggers) {
            logger.fatal("Triggered shot timestamp: " +System.currentTimeMillis());
            lockAllTriggers();
            if(ModulesManager.getInstance().isAnySceneControllerAvailable()) {
                prepareDevicesToTakingShot();
            }else{
                triggerCameraModules();
            }
        }else {
            logger.error("Denial access recorded");
        }
    }

    private void lockAllTriggers() {
        lockedTriggers = true;
        setSafetyCatch();
    }

    //order every scene controller to setup and register it
    private void prepareDevicesToTakingShot() {
        sceneSetupProcessIsEnabled = true;
        for(SceneController sceneController: ModulesManager.getInstance().getSceneModules()){
//            isSceneTurnedOn.put(sceneController, false);
            new Thread(){
                @Override
                public void run() {
                    sceneController.setupScene();
                }
            }.start();
        }
//        shotCanBeTaken = true;
    }

    //control, if every controller is setup. if yes, shot can be taken
    public void devicesArePreparedToTakingShot(SceneController controller){
        if(sceneSetupProcessIsEnabled) {
            //TODO: this for can be removed (not that one uncomment line!!!), due to performance issue, is is jush more recure check, is all scenes controller are setup
            //register, that scene is already setup
//        for(Map.Entry<SceneController, Boolean> entry : isSceneTurnedOn.entrySet()){
//            if(entry.getKey() == controller){
//                entry.setValue(true);
            setScenes += 1;
//                break;
//            }
//        }


            if (setScenes == ModulesManager.getInstance().getSceneModules().size()) {
                //TODO: due to performance issue, this for could be removed in future (it is just sme check, which is maybe not necessary)
                //check, if every scene is setup
//            for (Map.Entry<SceneController, Boolean> entry : isSceneTurnedOn.entrySet()) {
//            //if just one controller is not setup, no picture can be taken
//                if (entry.getValue() == false){
//                    return;
//                }
//            }
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
            //TODO: Here can be generating some event
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
                if (ModulesManager.getInstance().isAnySceneControllerAvailable()) {
                    releaseDeviceAfterTakingShot();
                }
            }

            logger.fatal("shot taken timestamp: " +System.currentTimeMillis());
        }else {
            logger.error("Denial access recorded");
        }
    }

    public void provideTakenPicture(TakenPicture picture, SimpleCamera camera){
        if(allowToProvidePicture) {
            shotIsTaken(camera);

            cameraAlreadyProvidedPicture(camera);

            if (ModulesManager.getInstance().isAnyImageFilterAvailable()) {
                applyPhotoFilters(picture);
            } else {
                //TODO: Should be there way to propagate raw photo, without filters?
                List<TakenPicture> pictures = new ArrayList<>();
                pictures.add(picture);
                new Thread() {
                    @Override
                    public void run() {
                        propagatePhoto(pictures);
                    }
                }.start();
            }

            logger.fatal("picture provided timestamp: " +System.currentTimeMillis());
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

    //TODO: start in own thread
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
                List<TakenPicture> pictures = new LinkedList<>();
                pictures.add(picture);
                for(ImageFilter filter: ModulesManager.getInstance().getImageFilterModules()){
                    pictures = filter.applyFilter(pictures);
                }
                propagatePhoto(pictures);
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

    private void propagatePhoto(List<TakenPicture> pictures){
        synchronized (WorkflowManager.class) {
            allowToHandlingPictures = true;
            registerUnhandledPhotos(pictures);
            if(!allCamerasProvidedPhotos){
                checkIfAllCamerasProvidedPhoto();
            }
            for(ImageHandler handler: ModulesManager.getInstance().getImageHandlerModules()){
                new Thread(){
                    @Override
                    public void run() {
                        for(TakenPicture picture: pictures){
                            handler.handleImage(picture);
                        }
                    }
                }.start();
            }
        }
    }

    public void photoIsHandled(TakenPicture picture, ImageHandler handler){
        if(allowToHandlingPictures){
            markPictureAsHandled(picture);

            if(allCamerasProvidedPhotos){
                //check if all photos are handled
                for (Map.Entry<TakenPicture, Boolean> entry: isPictureFromCameraAlreadyHandled.entrySet()){
                    if(!entry.getValue())
                        return;
                }
                logger.fatal("workflow ends timestamp: " +System.currentTimeMillis());
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
