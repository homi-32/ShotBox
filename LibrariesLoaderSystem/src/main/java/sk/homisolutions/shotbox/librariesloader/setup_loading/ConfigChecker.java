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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by homi on 5/14/16.
 */
class ConfigChecker {

    private static final Logger logger = Logger.getLogger(ConfigChecker.class);

    /**
     * Method for checking SystemSetup after loading settings from config file
     */
    public void checkLoadedSetup() {
        logger.info("Method starts.");

        logger.info("Checking path to folder with external libraries.");
        //checking folder with libraries setting:
        if(SystemSetup.LIBRARY_FOLDER == null || SystemSetup.LIBRARY_FOLDER.equals("")){
            SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;

            logger.warn("Path to folder with libraries was not defined." +
                    "Default one will be used: " + SystemSetup.LIBRARY_FOLDER);

            logger.info("Checking default path to folder with libraries.");
            if(checkIfDirectoryExists()){
                logger.info("Directory exists.");
            }else{
                logger.error("No directory available. New one will be created.");
                createDefaulthDirectoryWithLibraries();
            }

        }else {
            logger.info("Path is defined.");

            logger.info("Checking path to folder with libraries.");
            if(checkIfDirectoryExists()){
                logger.info("Directory exists.");
            }else{
                logger.error("No directory available. Wrong path defined. Used default one.");

                SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;

                if(checkIfDirectoryExists()){
                    logger.info("Directory exists.");
                }else{
                    logger.error("No directory available. New one will be created.");
                    createDefaulthDirectoryWithLibraries();
                }

            }

        }

        logger.info("Checking package name definition.");
        //checking package name with interfaces:
        if(SystemSetup.PACKAGE_WITH_APIs == null){
            logger.warn("Package name is not defined.");
            SystemSetup.PACKAGE_WITH_APIs = "";
            logger.warn("Default package setting is set. No filter will be used.");
        }else if(!SystemSetup.PACKAGE_WITH_APIs.equals("")){
            /*
            if package exists, good (it is god to check, if package contains interfaces)
            if package does not exist, empty string should be set ""
             */

            logger.info("Package name is defined.");
            logger.info("Checking if package does exists.");
            if(checkIfDefinedPackageExists()){
                logger.info("Defined package exists and contains classes: " + SystemSetup.PACKAGE_WITH_APIs);
            }else{
                logger.error("Defined package does not exists, or contain no classes: " + SystemSetup.PACKAGE_WITH_APIs);
                logger.warn("Defined package does not exists, default one will be set. No filter will be used.");
                SystemSetup.PACKAGE_WITH_APIs = "";
            }

        } else {
            logger.warn("Default package setting is set. No filter will be used.");
        }

        logger.info("Method ends.");
    }

    private void createDefaulthDirectoryWithLibraries() {
        logger.info("Method starts.");

        File f = new File(SystemSetup.LIBRARY_FOLDER);

        logger.info("Creating default directory for storing external libraries.");
        if(f.mkdir()){
            logger.error("Default directory for storing libraries was created, but it is empty. " +
                    "No libraries will be loaded.");
        } else {
            logger.fatal("Default directory for storing libraries could not be created and no custom is defined. " +
                    "Application exits now.");
            System.exit(0);
        }

        logger.info("Method ends.");
    }

    private boolean checkIfDefinedPackageExists() {
        logger.info("Method starts.");
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(SystemSetup.PACKAGE_WITH_APIs))));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        logger.info("Method ends.");

        if(classes.size() == 0){
            logger.info("Returning false.");
            return false;
        }else{
            logger.info("Returning true.");
            return true;
        }
    }


    private boolean checkIfDirectoryExists() {
        logger.info("Method starts.");

        File libFolder = new File(SystemSetup.LIBRARY_FOLDER);

        if(!libFolder.exists() || !libFolder.isDirectory()) {
            logger.error("Folder does not exist, or it is not directory, but file. " +
                    "This folder will be created, but there is nothing to load.");
            if(libFolder.mkdir()){
                logger.info("Directory was created: " + SystemSetup.LIBRARY_FOLDER);
            }else{
                logger.fatal("Directory could not be created.");
                logger.info("Returning false. Method ends.");
                return false;
            }
        }

        if(libFolder.list().length == 0){
            logger.warn("Directory exists, but it is empty. There are no libraries to load.");
        }


            /*
            if it does exist, it is good (will be better, if i could check, if this contain some classes/jars,
            but who cares right now)
            If it does not exist, it is good to create one, but empty

            This situation should be handled out of this method
             */

        logger.info("Returning true. Method ends.");
        return true;
    }
}
