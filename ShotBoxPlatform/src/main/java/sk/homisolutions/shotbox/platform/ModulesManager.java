package sk.homisolutions.shotbox.platform;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;
import sk.homisolutions.shotbox.librariesloader.classloading_system.NativeLibsLoaderFactory;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;

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

    private List<SimpleCamera> simpleCameraModules;
    private List<ShootTrigger> shootTriggerModules;

    //TODO: do i need this???
    private List<Thread> threads;

    private ProvidersManager providers = ProvidersManager.getInstance();

    private ModulesManager(){
        //singleton

        simpleCameraModules = new ArrayList<>();
        shootTriggerModules = new ArrayList<>();
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
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
}

