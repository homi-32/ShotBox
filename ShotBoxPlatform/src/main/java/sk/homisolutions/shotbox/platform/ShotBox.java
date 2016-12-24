package sk.homisolutions.shotbox.platform;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.platform.logging.SetupLogger;
import sk.homisolutions.shotbox.platform.managers.ModulesManager;
import sk.homisolutions.shotbox.platform.managers.ProvidersManager;
import sk.homisolutions.shotbox.platform.managers.WorkflowManager;
import sk.homisolutions.shotbox.tools.models.ShotboxManifest;

/**
 * Created by homi on 4/20/16.
 */
public class ShotBox {
    private static final Logger logger = Logger.getLogger(ShotBox.class);
    public static ShotBox INSTANCE = null;


    public static void main(String[] args){
//        SetupLogger.getInstance().transferOutputToLog4j();
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        logger.info("Application start");

        ShotBox shotbox = new ShotBox();
        INSTANCE = shotbox;

        shotbox.setupOsEnviroment();
        shotbox.initializeProviders();
        shotbox.initializeExternalModules();

        shotbox.checkNecessaryParts();

        shotbox.startApplication();
    }

    private void setupOsEnviroment() {
        //TODO: check, if this is called from SUDO ??
        //TODO: redirect all errors and standard output to log4j
        //TODO: check, if all directories are created
        //TODO: check, if all necessary configuration files are created and set
        //TODO: if needed, check all PATH variables
    }

    private void checkNecessaryParts() {
        boolean triggersAvailable = ModulesManager.getInstance().getShootTriggerModules().size() >0;
        boolean camerasAvailable = ModulesManager.getInstance().getSimpleCameraModules().size() >0;
        boolean imageHandlersAvailable = ModulesManager.getInstance().getImageHandlerModules().size() >0;
        
        if(!(triggersAvailable && camerasAvailable && imageHandlersAvailable)){
            logger.fatal("Necessary modules to run are missing:");
            if(!triggersAvailable){
                logger.fatal("Missing any trigger");
            }
            if(!camerasAvailable){
                logger.fatal("Missing any camera module");
            }
            if(!imageHandlersAvailable){
                logger.fatal("Missing any image handler");
            }
            endApplication();
        }
    }

    public static ShotBox getInstance(){
        return INSTANCE;
    }

    private ShotBox(){

    }

    private void startApplication() {
        new Thread(){
            @Override
            public void run() {
                WorkflowManager.getInstance().startWorkflow();
            }
        }.start();
    }

    private void initializeExternalModules() {
        ModulesManager.getInstance().initializeExternalModules();
    }

    private void initializeProviders() {
        ProvidersManager.getInstance().initializeProviders();
    }

    public void endApplication(){
        WorkflowManager.getInstance().endWorkflow();
        ModulesManager.getInstance().sendShutdownMessageToModules();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.fatal("exit");
        System.exit(0);
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
