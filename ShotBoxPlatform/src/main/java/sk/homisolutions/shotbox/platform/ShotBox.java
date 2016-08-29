package sk.homisolutions.shotbox.platform;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;
import sk.homisolutions.shotbox.librariesloader.classloading_system.NativeLibsLoaderFactory;
import sk.homisolutions.shotbox.platform.providers.TriggerProvider;
import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.external.trigger.ShootTrigger;
import sk.homisolutions.shotbox.tools.api.internal.trigger.TriggerPlatformProvider;

import java.util.List;

/**
 * Created by homi on 4/20/16.
 */
public class ShotBox {
    private static final Logger logger = Logger.getLogger(ShotBox.class);


    public static void main(String[] args){
        logger.info("Application start");

        ShotBox shotbox = new ShotBox();

        shotbox.initializeProviders();
        shotbox.initializeExternalModules();

        try {
            logger.fatal("infinite loop starts");
            while (true){
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Application ends");
    }

    private void initializeExternalModules() {
        ModulesManager.getInstance().initializeExternalModules();
    }

    private void initializeProviders() {
        ProvidersManager.getInstance().initializeProviders();
    }

    /*
            LibrariesLoader loader = NativeLibsLoaderFactory.getLoader();
        List<Class> loadedClasses = loader.getLoadedClasses();
        ShootTrigger trigger = null;
        Thread triggerThread = null;
        TriggerPlatformProvider triggerProvider = new TriggerProvider();

        logger.fatal("loaded classes: " +loadedClasses.size());

        for(Class c: loadedClasses){
            logger.fatal("name: " +c.getSimpleName());
            try {
                Object o = c.newInstance();

                if(o instanceof ShootTrigger){
                    trigger = (ShootTrigger) o;
                    trigger.setProvider(triggerProvider);
                    triggerThread = new Thread(trigger);
                    triggerThread.start();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

     */

    /*
    testing


    LibrariesLoader loader = NativeLibsLoaderFactory.getLoader();

        List<Class> loadedClasses = loader.getLoadedClasses();

//        File file = new File(".");
//
//        System.out.println(file.toURI());

        logger.info("Loaded classes:");
        loadedClasses.forEach(logger::info);


        Camera camera = null;
        Trigger trigger = null;

        for(Class c: loadedClasses){
            try {
                Object o = c.newInstance();

                if(o instanceof Camera){
                    camera = (Camera) o;
                }
                if (o instanceof Trigger){
                    trigger = (Trigger) o;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        camera.takeShot();
        trigger.run();
     */
}
