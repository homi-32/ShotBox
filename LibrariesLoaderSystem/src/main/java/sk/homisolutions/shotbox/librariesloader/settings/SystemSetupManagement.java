package sk.homisolutions.shotbox.librariesloader.settings;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * This class serve as tool for managing SystemSetup model (class).
 *
 * Created by homi on 6/12/16.
 */
public class SystemSetupManagement {

    private static Logger logger = Logger.getLogger(SystemSetupManagement.class);

    /**
     * Setting default values to SystemSetup model
     */
    public static void setDefaultValues(){
        logger.info("Method called.");
        logger.error("Using default settings for LibrariesLoaderSystem: ");
        SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;
        SystemSetup.PACKAGE_WITH_APIs = "";
        logger.error("Path to directory with external libraries: '" +SystemSetup.LIBRARY_FOLDER +"'");
        logger.error("Path to API: '" +SystemSetup.PACKAGE_WITH_APIs +"'");
        logger.info("Method ends.");
    }

    /**
     * Setting default values of SystemSetup to property object.
     * This method always set default values, no matters, which values are set in SystemSetup.
     *
     * @param props empty properties object, which will be fulfilled with default settings.
     */
    public static void setDefaultValuesToPropertyFile(Properties props){
        logger.info("Method called.");
        props.setProperty(Constants.PATH_TO_LIBRARIES_DIR_PROPERTY_NAME,Constants.PATH_TO_LIBRARIES_DIR);
        props.setProperty(Constants.PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME,"");
        logger.info("Method ends.");
    }

    /**
     * Setting actual values of SystemSetup to property object.
     * This method always set actual values, which are currently used by LibrariesLoaderSystem.
     *
     * @param props empty properties object, which will be fulfilled with actual settings.
     */
    public static void getSettingsFromSystemSetup(Properties props){
        logger.info("Method called.");
        props.setProperty(Constants.PATH_TO_LIBRARIES_DIR_PROPERTY_NAME,SystemSetup.LIBRARY_FOLDER);
        props.setProperty(Constants.PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME,SystemSetup.PACKAGE_WITH_APIs);
        logger.info("Method ends.");
    }

    /**
     * Setting properties as actual SystemSetup
     *
     * @param props properties, which should be set as SystemSetup
     */
    public static void setSettingsForSystemSetup(Properties props){
        logger.info("Method called.");
        logger.info("Starting to read properties.");
        logger.info("Reading path to directory with external libraries.");
        SystemSetup.LIBRARY_FOLDER = props.getProperty(Constants.PATH_TO_LIBRARIES_DIR_PROPERTY_NAME);
        logger.info("Red path: '" +SystemSetup.LIBRARY_FOLDER +"'");
        logger.info("Reading path to package with API.");
        SystemSetup.PACKAGE_WITH_APIs = props.getProperty(Constants.PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME);
        logger.info("Path to API loaded: " +SystemSetup.PACKAGE_WITH_APIs +"'");
        logger.info("Reading properties finished.");
        logger.info("Method ends.");
    }
}
