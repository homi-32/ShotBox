package sk.homisolutions.shotbox.platform.managers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;
import sk.homisolutions.shotbox.librariesloader.classloading_system.NativeLibsLoaderFactory;
import sk.homisolutions.shotbox.platform.ShotBox;
import sk.homisolutions.shotbox.platform.support.IncomeMessageChecker;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by homi on 8/20/16.
 */
//TODO: consider, if all modules should implements Runabble and all modules automatically starts in new thread, or it will be better to let implementer consider, if he want something like that and manage threads by himself
public class ModulesManager {

    private static final Logger logger = Logger.getLogger(ModulesManager.class);
    private static final ModulesManager INSTANCE = new ModulesManager();

    private List<ShotBoxExternalModule> modules;

    private List<SimpleCamera> simpleCameraModules;
    private List<ShootTrigger> shootTriggerModules;
    private List<ImageHandler> imageHandlerModules;
    private List<SceneController> sceneModules;
    private List<ImageFilter> filterModules;

    //TODO: do i need this??? Maybe it will be helpful lately
    private List<Thread> threads;

    private ProvidersManager providers = ProvidersManager.getInstance();

    private ModulesManager(){
        //singleton

        modules = new ArrayList<>();
        simpleCameraModules = new ArrayList<>();
        shootTriggerModules = new ArrayList<>();
        imageHandlerModules = new ArrayList<>();
        sceneModules = new ArrayList<>();
        filterModules = new ArrayList<>();
        threads = new LinkedList<>();
    }

    public static ModulesManager getInstance(){
        return INSTANCE;
    }

    public List<SimpleCamera> getSimpleCameraModules() {
        return simpleCameraModules;
    }

    public List<ShootTrigger> getShootTriggerModules() {
        return shootTriggerModules;
    }

    public List<ImageHandler> getImageHandlerModules() {
        return imageHandlerModules;
    }

    public List<SceneController> getSceneModules() {
        return sceneModules;
    }

    public List<ImageFilter> getImageFilterModules() {
        return filterModules;
    }

    public List<Thread> getAllThreadsWithModules() {
        return threads;
    }

    public void initializeExternalModules(){
        if(!ProvidersManager.getInstance().isInitialized()) {
            logger.error("Providers are not initialized (they are not created). " +
                    "External modules will not be able to communicate with platform and each other.");
        }

        LibrariesLoader loader = NativeLibsLoaderFactory.getLoader();
        List<Class> loadedClasses = loader.getLoadedClasses();

        for(Class c: loadedClasses){
            try {
                Object o = c.newInstance();

                if(o instanceof ShootTrigger){
                    initShootTrigger((ShootTrigger) o);
                }
                if (o instanceof SimpleCamera){
                    initSimpleCamera((SimpleCamera) o);
                }
                if (o instanceof ImageHandler){
                    initImageHandler((ImageHandler) o);
                }
                if (o instanceof SceneController){
                    initSceneController((SceneController) o);
                }
                if (o instanceof ImageFilter){
                    initImageFilter((ImageFilter) o);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void initImageFilter(ImageFilter imageFilter) {
        imageFilter.setProvider(providers.getImageFilterProvider());
        this.filterModules.add(imageFilter);
        Thread thread = new Thread(imageFilter);
        threads.add(thread);
        thread.start();
    }

    private void initSceneController(SceneController sceneController) {
        sceneController.setProvider(providers.getSceneProvider());
        this.sceneModules.add(sceneController);
        Thread thread = new Thread(sceneController);
        threads.add(thread);
        thread.start();
    }

    private void initImageHandler(ImageHandler imageHandler) {
        imageHandler.setProvider(providers.getImageHandlerProvider());
        this.imageHandlerModules.add(imageHandler);
        Thread thread = new Thread(imageHandler);
        threads.add(thread);
        thread.start();
    }

    private void initSimpleCamera(SimpleCamera camera){
        camera.setProvider(providers.getCameraProvider());
        this.simpleCameraModules.add(camera);
        Thread thread =new Thread(camera);
        threads.add(thread);
        thread.start();
    }

    private void initShootTrigger(ShootTrigger trigger){
        trigger.setProvider(providers.getTriggerProvider());
        this.shootTriggerModules.add(trigger);
        Thread thread = new Thread(trigger);
        threads.add(thread);
        thread.start();
    }

    public List<ShotBoxExternalModule> getAllModules(){
        if(modules.size() != (
                shootTriggerModules.size()
                + imageHandlerModules.size()
                + sceneModules.size()
                + filterModules.size()
                + simpleCameraModules.size()
        )) {
            modules.clear();
            modules.addAll(shootTriggerModules);
            modules.addAll(imageHandlerModules);
            modules.addAll(sceneModules);
            modules.addAll(filterModules);
            modules.addAll(simpleCameraModules);
        }

        return modules;
    }

    public void propagateMessage(ShotBoxMessage message, ShotBoxExternalModule module){
        if(message == null || module == null){
            logger.warn("Invalid message received.");
            return;
        }
        message = IncomeMessageChecker.getInstance().checkMessage(message, module);
        for(ShotBoxExternalModule sem: getAllModules()){
            if(sem != module){
                sem.receiveGlobalMessage(message);
            }
        }
    }

    public void sendShutdownMessageToModules(){
        ShotBoxMessage message = ShotBoxMessage.newMessage();
        message.setShutdown(true);
        for(ShotBoxExternalModule sem: getAllModules()){
            sem.receiveGlobalMessage(message);
        }
    }

    public boolean isAnySceneControllerAvailable(){
        return (this.sceneModules.size() > 0);
    }

    public boolean isAnyImageFilterAvailable() {
        return (this.filterModules.size() >0);
    }
}

