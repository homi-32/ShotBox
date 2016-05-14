package sk.homisolutions.shotbox.librariesloader.system;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.settings.Setup;

import java.io.*;
import java.util.*;

/**
 * Created by homi on 5/14/16.
 */


//TODO: check this class, add more logs, fix some situations (if branches) and comment methods and their parts
public class InitApplicationSetup {

    private static final Logger logger = Logger.getLogger(InitApplicationSetup.class);
    private static final ExceptionHandling eh = ExceptionHandling.getINSTANCE(InitApplicationSetup.class);

    public static void init(){
        logger.info("Initializing setup for LibrariesLoaderSystem starts.");

        logger.info("Loading configuration file: " +Constants.CONFIG_FILE_PATH);
        if(createConfigFile()) {
            logger.info("Configuration file is loaded.");
            logger.info("Setup from configuration file is going to be loaded.");
            loadConfigFile();
        } else {
            logger.error("Configuration file for LibrariesLoadedSystem could not be loaded, neither created.");
            logger.error("LSS will continue with default values.");
            //TODO: dunno, but this path is very bad way, so do something
        }

        logger.info("Loaded setup for LLS is going to be checked.");
        checkLoadedSetup();
        logger.info("Initializing setup for LibrariesLoaderSystem ends.");
    }

    //TODO: write some powerfull logs
    private static void checkLoadedSetup() {
        //checking folder with libraries:
        if(Setup.LIBRARY_FOLDER == null || Setup.LIBRARY_FOLDER.equals("")){
            Setup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;

            logger.warn("Path to folder with libraries was not defined." +
                    "Default one will be used: " +Setup.LIBRARY_FOLDER);

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

                Setup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;

                if(checkIfDirectoryExists()){
                    logger.info("Directory exists.");
                }else{
                    logger.fatal("No directory available");
                    //TODO: should I exith applicationi right now ??
                }

            }

        }

        //checking package name with interfaces:
        if(Setup.PACKAGE_WITH_APIs == null){
            Setup.PACKAGE_WITH_APIs = "";
        }else if(!Setup.PACKAGE_WITH_APIs.equals("")){
            //TODO: check, if this package exist for sure
            /*
            if package exists, good (it is god to check, if package contains interfaces)
            if package does not exist, empty string should be set ""
             */

            if(checkIfDefinedPackageExists()){
                logger.info("Defined package exists and contains classes: " +Setup.PACKAGE_WITH_APIs);
            }else{
                logger.error("Defined package does not exists, or contain no classes: " +Setup.PACKAGE_WITH_APIs);
            }

        }
    }

    private static boolean checkIfDefinedPackageExists() {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(Setup.PACKAGE_WITH_APIs))));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        if(classes.size() == 0){
            return false;
        }else{
            return true;
        }
    }


    private static boolean checkIfDirectoryExists() {
        File libFolder = new File(Setup.LIBRARY_FOLDER);

        if(!libFolder.exists() || !libFolder.isDirectory()) {
            logger.error("Folder does not exist, or it is not directory, but file. " +
                    "This folder will be created, but there is nothing to load.");
            if(libFolder.mkdir()){
                logger.info("Directory was created: " +Setup.LIBRARY_FOLDER);
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


    /*
    TODO: create logs for this methods
    TODO: comment steps
     */
    private static void loadConfigFile() {
        Properties props = null;
        File f = new File(Constants.CONFIG_FILE_PATH);
        InputStream in = null;

        try {
            props = new Properties();
            in = new FileInputStream(Constants.CONFIG_FILE_PATH);
            props.load(in);
            in.close();

            Setup.LIBRARY_FOLDER = props.getProperty(Constants.PATH_TO_LIBRARIES_DIR_PROPERTY_NAME);
            Setup.PACKAGE_WITH_APIs = props.getProperty(Constants.PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME);

        } catch (IOException e) {
            eh.handle(e);

            //TODO: print some bad error

            Setup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;
            Setup.PACKAGE_WITH_APIs = "";
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    eh.handle(e);
                    //TODO: print some fatal error
                }
            }
        }
    }

    /*
    TODO: create logs for this method
    TODO: comment steps
     */
    private static boolean createConfigFile() {
        Properties props = null;
        File f = new File(Constants.CONFIG_FILE_PATH);
        OutputStream out = null;

        if(!f.exists() || f.isDirectory()) {
            try {
                if(f.createNewFile()){
                    props = new Properties();
                    props.setProperty(Constants.PATH_TO_LIBRARIES_DIR_PROPERTY_NAME,Constants.PATH_TO_LIBRARIES_DIR);
                    props.setProperty(Constants.PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME,"");

                    out = new FileOutputStream(Constants.CONFIG_FILE_PATH);
                    /*
                    Dunno, what should I put as 2nd parameter
                    TODO: change parameter
                     */
                    props.store(out, null);
                    out.close();

                    return true;
                }else{
                    //TODO: print some very bad error too
                    logger.error("Configuration file for LLS does not exist, " +
                            "but it cloud not be created neither.");
                    return false;
                }
            } catch (IOException e) {
                //too lazy to write some meaningful error right now
                logger.error("Bad error occurred. Somebody did some bad voodoo.");
                eh.handle(e);

                //TODO: print some very bad error

                Setup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;
                Setup.PACKAGE_WITH_APIs = "";

                if(out!=null){
                    try {
                        out.close();
                    } catch (IOException e1) {
                        eh.handle(e1);
                        //TODO: print some fatal error
                        logger.fatal("Well, shit happens.");
                    }
                }

                return false;
            }
        }

        return true;
    }
}
