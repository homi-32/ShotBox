package loader;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.LibrariesLoader;

/**
 * Created by homi on 4/27/16.
 */
public class NativeLibsLoaderFactory {
    private static final Logger logger = Logger.getLogger(NativeLibsLoaderFactory.class);


    private static LibsLoader INSTANCE = null;

    private NativeLibsLoaderFactory(){
        //should not be instantiate
    }

    public static LibrariesLoader getLoader(){
        logger.info("Method called.");

        if(INSTANCE == null) {
            logger.info("Loader object does not exist. Object is going to be created.");
            INSTANCE = new LibsLoader();
        }

        logger.info("Loader object is created and it will be returned. Method ends.");
        return INSTANCE;
    }
}
