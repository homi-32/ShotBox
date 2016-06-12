package sk.homisolutions.shotbox.librariesloader.setup_loading;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;

/**
 * Class server as settings manager and settings consistency checker. Class tries to load settings from config file.
 * If file is missing or corrupted, class will generate new default one or use default settings. Class guarantees
 * that LibrariesLoaderSystem stars with correct settings, no matter in what state is config file.
 *
 * Created by homi on 5/14/16.
 */


public class InitApplicationSetup {

    private static InitApplicationSetup INSTANCE = null;

    private static final Logger logger = Logger.getLogger(InitApplicationSetup.class);
    private static final ExceptionHandling eh = ExceptionHandling.createINSTANCE(InitApplicationSetup.class);

    /**
     * Checking file consistency
     */
    private ConfigFileManagement configFM;

    /**
     * Loading settings from config file
     */
    private ConfigFileLoader confLoader;

    /**
     * Checking used configuration
     */
    private ConfigChecker configChecker;

    /**
     * Constructor. Initializes all parts of settings initializer.
     */
    private InitApplicationSetup(){
        //singleton

        logger.info("Initializing InitApplicationSetup object");
        configFM = new ConfigFileManagement();
        confLoader = new ConfigFileLoader();
        configChecker = new ConfigChecker();
        logger.info("InitApplicationSetup object initialization ends.");
    }

    /**
     * Returns object of InitApplicationSetup class. Singleton and lazy loading singleton is implemented.
     * @return object of InitApplicationSetup class
     */
    public static InitApplicationSetup getInstance(){
        logger.info("Method called");
        if(INSTANCE == null){
            logger.info("Object does not exist. Object will be created");
            INSTANCE = new InitApplicationSetup();
        }

        logger.info("Returning existing object. Method ends.");
        return INSTANCE;
    }

    /**
     * Method starts checking settings process. Method will load settings from config file.
     * If file or settings are not available, default values will be used.
     * After settings are set, this settings are checked once again.
     */
    public void init(){
        logger.info("******************Initializing setup for LibrariesLoaderSystem starts.******************");

        logger.info("Loading configuration file: " +Constants.CONFIG_FILE_PATH);
        if(configFM.checkConfigFileExistence()) {
            logger.info("Configuration file is loaded.");
            logger.info("Setup from configuration file is going to be loaded.");
            confLoader.loadConfigFile();
        } else {
            logger.fatal("Configuration file for LibrariesLoadedSystem could not be loaded, neither created.");
            logger.error("LSS will continue with default values.");
        }

        logger.info("Loaded setup for LLS is going to be checked.");
        configChecker.checkLoadedSetup();
        logger.info("******************Initializing setup for LibrariesLoaderSystem ends.******************");
    }

}
