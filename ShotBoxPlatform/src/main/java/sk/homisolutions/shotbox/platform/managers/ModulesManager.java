package sk.homisolutions.shotbox.platform.managers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;
import sk.homisolutions.shotbox.librariesloader.classloading_system.NativeLibsLoaderFactory;
import sk.homisolutions.shotbox.platform.ShotBox;
import sk.homisolutions.shotbox.platform.support.IncomeMessageChecker;
import sk.homisolutions.shotbox.tools.api.external.camera.AdvancedCamera;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.external.userinterface.GraphicalInterface;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    private List<AdvancedCamera> advancedCameraModules;
    private List<GraphicalInterface> guiModules;

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
        advancedCameraModules = new ArrayList<>();
        guiModules = new ArrayList<>();
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

    public List<AdvancedCamera> getAdvancedCameraModules() {
        return advancedCameraModules;
    }

    public List<GraphicalInterface> getGuiModules() {
        return guiModules;
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
            logger.info("*Loading Class: " +c.getName());

            //I should avoid to this approach, but I need to analyze suggested approach, before I will use it.
            try {
                Object o = c.newInstance();
                resolveObjectOrigin(o);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Throwable t){
                logger.fatal("An unpredictable fatal error occurred: "+t.getMessage()+". Class cloud not be instantiated.");
                t.printStackTrace();
            }

            //I should instantiate every object by separate thread, becouse I really don't know, what can be executed in Construcotr,
            // if it can, or can not stop process of loading, or it can create huge delay
            //okey, I found out that this is stupid, little bit, it just not working, becouse platform become shutdown, this is the reason 1
            //reason 2: every object will be in separate thread, so EVERY access to platform MUST be synchronized
            //soo....
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    //some tips, how to instantiate objects:
//                    //http://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime
//                    //http://stackoverflow.com/questions/194698/how-to-load-a-jar-file-at-runtime
//                    //http://stackoverflow.com/questions/195321/why-is-class-newinstance-evil
//
//
//                    /*
//                    Class<?> clazz = Class.forName("mypackage.MyClass", true, loader);
//                    Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
//                    // Avoid Class.newInstance, for it is evil.
//                    Constructor<? extends Runnable> ctor = runClass.getConstructor();
//                    Runnable doRun = ctor.newInstance();
//                    doRun.run();
//                     */
//                    try {
//                        Object o = c.newInstance();
//                        resolveObjectOrigin(o);
//                    } catch (InstantiationException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//
//                    Class<? extends  Runnable> runableClass = c.asSubclass(Runnable.class);
//                    try {
//                        Constructor<? extends Runnable> constructor = runableClass.getConstructor();
//                        Runnable run = constructor.newInstance();
//                        run.run();
//                        resolveObjectOrigin(run);
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InstantiationException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
        }
    }

    private void resolveObjectOrigin(Object o){
        synchronized (ModulesManager.class){
            boolean objectIsLoaded = false;

            if(o instanceof ShootTrigger){
                initShootTrigger((ShootTrigger) o, objectIsLoaded);
                objectIsLoaded = true;
            }
            if (o instanceof SimpleCamera){
                initSimpleCamera((SimpleCamera) o, objectIsLoaded);
                objectIsLoaded = true;
            }
            if (o instanceof ImageHandler){
                initImageHandler((ImageHandler) o, objectIsLoaded);
                objectIsLoaded = true;
            }
            if (o instanceof SceneController){
                initSceneController((SceneController) o, objectIsLoaded);
                objectIsLoaded = true;
            }
            if (o instanceof ImageFilter){
                initImageFilter((ImageFilter) o, objectIsLoaded);
                objectIsLoaded = true;
            }
            if (o instanceof AdvancedCamera){
                initAdvancedCamera((AdvancedCamera) o, objectIsLoaded);
                objectIsLoaded = true;
            }
            if (o instanceof GraphicalInterface){
                initGraphicalInterface((GraphicalInterface) o, objectIsLoaded);
                objectIsLoaded = true;
            }
        }
    }

    private void initAdvancedCamera(AdvancedCamera camera, boolean objectIsLoaded) {
        camera.setProvider(providers.getCameraProvider());
        advancedCameraModules.add(camera);
        if(!objectIsLoaded){
            Thread thread = new Thread(camera);
            threads.add(thread);
            thread.start();
        }
    }

    private void initImageFilter(ImageFilter imageFilter, boolean objectIsLoaded) {
        imageFilter.setProvider(providers.getImageFilterProvider());
        this.filterModules.add(imageFilter);
        if(!objectIsLoaded) {
            Thread thread = new Thread(imageFilter);
            threads.add(thread);
            thread.start();
        }
    }

    private void initSceneController(SceneController sceneController, boolean objectIsLoaded) {
        sceneController.setProvider(providers.getSceneProvider());
        this.sceneModules.add(sceneController);
        if(!objectIsLoaded) {
            Thread thread = new Thread(sceneController);
            threads.add(thread);
            thread.start();
        }
    }

    private void initImageHandler(ImageHandler imageHandler, boolean objectIsLoaded) {
        imageHandler.setProvider(providers.getImageHandlerProvider());
        this.imageHandlerModules.add(imageHandler);
        if(!objectIsLoaded) {
            Thread thread = new Thread(imageHandler);
            threads.add(thread);
            thread.start();
        }
    }

    private void initSimpleCamera(SimpleCamera camera, boolean objectIsLoaded){
        camera.setProvider(providers.getCameraProvider());
        this.simpleCameraModules.add(camera);
        if(!objectIsLoaded) {
            Thread thread = new Thread(camera);
            threads.add(thread);
            thread.start();
        }
    }

    private void initShootTrigger(ShootTrigger trigger, boolean objectIsLoaded){
        trigger.setProvider(providers.getTriggerProvider());
        this.shootTriggerModules.add(trigger);
        if(!objectIsLoaded) {
            Thread thread = new Thread(trigger);
            threads.add(thread);
            thread.start();
        }
    }

    private void initGraphicalInterface(GraphicalInterface gui, boolean objectIsLoaded){
        gui.setProvider(providers.getGuiProvider());
        this.guiModules.add(gui);
        if(!objectIsLoaded){
            Thread thread = new Thread(gui);
            threads.add(thread);
            thread.start();
        }
    }

    public List<ShotBoxExternalModule> getAllModules(){
        if(modules.size() == 0) {
            modules.addAll(shootTriggerModules);
            modules.addAll(imageHandlerModules);
            modules.addAll(sceneModules);
            modules.addAll(filterModules);
            modules.addAll(simpleCameraModules);
            modules.addAll(advancedCameraModules);
            modules.addAll(guiModules);
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

    public boolean isAnyGraphicalInterfaceAvailable() { return (this.guiModules.size() >0); }
}

