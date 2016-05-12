import sk.homisolutions.shotbox.librariesloader.system.NativeLibsLoaderFactory;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;

import java.util.List;

/**
 * Created by homi on 4/20/16.
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args){
        logger.info("Application start");

        LibrariesLoader loader = NativeLibsLoaderFactory.getLoader();

        List<Class> loadedClasses = loader.getLoadedClasses();

//        File file = new File(".");
//
//        System.out.println(file.toURI());

        logger.info("Loaded classes:");
        loadedClasses.forEach(logger::info);

        logger.info("Application ends");
    }
}
