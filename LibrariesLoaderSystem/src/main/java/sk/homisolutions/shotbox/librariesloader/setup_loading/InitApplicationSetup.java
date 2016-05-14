package sk.homisolutions.shotbox.librariesloader.setup_loading;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;

import java.io.*;
import java.util.*;

/**
 * Created by homi on 5/14/16.
 */


//TODO: check this class, add more logs, fix some situations (if branches) and comment methods and their parts
public class InitApplicationSetup {

    private static InitApplicationSetup INSTANCE = null;

    private static final Logger logger = Logger.getLogger(InitApplicationSetup.class);
    private static final ExceptionHandling eh = ExceptionHandling.getINSTANCE(InitApplicationSetup.class);

    private ConfigFileManagement configFM;
    private ConfigFileLoader confLoader;
    private ConfigChecker configChecker;

    private InitApplicationSetup(){
        //singleton

        configFM = new ConfigFileManagement();
        confLoader = new ConfigFileLoader();
        configChecker = new ConfigChecker();
    }

    public static InitApplicationSetup getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new InitApplicationSetup();
        }

        return INSTANCE;
    }

    public void init(){
        logger.info("Initializing setup for LibrariesLoaderSystem starts.");

        logger.info("Loading configuration file: " +Constants.CONFIG_FILE_PATH);
        if(configFM.checkConfigFileExistence()) {
            logger.info("Configuration file is loaded.");
            logger.info("Setup from configuration file is going to be loaded.");
            confLoader.loadConfigFile();
        } else {
            logger.error("Configuration file for LibrariesLoadedSystem could not be loaded, neither created.");
            logger.error("LSS will continue with default values.");
            //TODO: dunno, but this path is very bad way, so do something
        }

        logger.info("Loaded setup for LLS is going to be checked.");
        configChecker.checkLoadedSetup();
        logger.info("Initializing setup for LibrariesLoaderSystem ends.");
    }

}
