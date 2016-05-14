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

    //TODO: write some powerfull logs
    public void checkLoadedSetup() {
        //checking folder with libraries:
        if(SystemSetup.LIBRARY_FOLDER == null || SystemSetup.LIBRARY_FOLDER.equals("")){
            SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;

            logger.warn("Path to folder with libraries was not defined." +
                    "Default one will be used: " + SystemSetup.LIBRARY_FOLDER);

            if(checkIfDirectoryExists()){
                logger.info("Directory exists.");
            }else{
                logger.fatal("No directory available");
                //TODO: should I exith applicationi right now ??
            }

        }else {

            if(checkIfDirectoryExists()){
                logger.info("Directory exists.");
            }else{
                logger.error("No directory available. Wrong path defined. Used default one.");

                SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;

                if(checkIfDirectoryExists()){
                    logger.info("Directory exists.");
                }else{
                    logger.fatal("No directory available");
                    //TODO: should I exith applicationi right now ??
                }

            }

        }

        //checking package name with interfaces:
        if(SystemSetup.PACKAGE_WITH_APIs == null){
            SystemSetup.PACKAGE_WITH_APIs = "";
        }else if(!SystemSetup.PACKAGE_WITH_APIs.equals("")){
            //TODO: check, if this package exist for sure
            /*
            if package exists, good (it is god to check, if package contains interfaces)
            if package does not exist, empty string should be set ""
             */

            if(checkIfDefinedPackageExists()){
                logger.info("Defined package exists and contains classes: " + SystemSetup.PACKAGE_WITH_APIs);
            }else{
                logger.error("Defined package does not exists, or contain no classes: " + SystemSetup.PACKAGE_WITH_APIs);
            }

        }
    }

    private boolean checkIfDefinedPackageExists() {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(SystemSetup.PACKAGE_WITH_APIs))));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        if(classes.size() == 0){
            return false;
        }else{
            return true;
        }
    }


    private boolean checkIfDirectoryExists() {
        File libFolder = new File(SystemSetup.LIBRARY_FOLDER);

        if(!libFolder.exists() || !libFolder.isDirectory()) {
            logger.error("Folder does not exist, or it is not directory, but file. " +
                    "This folder will be created, but there is nothing to load.");
            if(libFolder.mkdir()){
                logger.info("Directory was created: " + SystemSetup.LIBRARY_FOLDER);
            }else{
                logger.fatal("Directory could not be created.");
                //TODO: should I exith applicationi right now ??
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
             */

        return true;
    }
}
