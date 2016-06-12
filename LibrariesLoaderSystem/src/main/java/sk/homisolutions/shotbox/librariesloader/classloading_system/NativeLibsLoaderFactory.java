package sk.homisolutions.shotbox.librariesloader.classloading_system;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;

/**
 * Class serve as Factory for standard Library loader.
 *
 * Created by homi on 4/27/16.
 */
public class NativeLibsLoaderFactory {
    private static final Logger logger = Logger.getLogger(NativeLibsLoaderFactory.class);


    private static LibsLoader INSTANCE = null;

    private NativeLibsLoaderFactory(){
        //should not be instantiate
    }

    /**
     * Getter for standard libraries loader. Getter is implemented as singleton.
     *
     * @return object of LibsLoader, which is standard library loader for LibrariesLoaderSystem
     */
    public static LibrariesLoader getLoader(){
        logger.info("****LLS**** Method called.");

        if(INSTANCE == null) {
            logger.info("****LLS**** Loader object does not exist. Object is going to be created.");
            INSTANCE = new LibsLoader();
        }

        logger.info("****LLS**** Loader object is created and it will be returned. Method ends.");
        return INSTANCE;
    }
}
