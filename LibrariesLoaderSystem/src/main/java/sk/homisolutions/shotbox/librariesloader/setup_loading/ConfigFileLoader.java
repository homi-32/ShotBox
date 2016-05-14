package sk.homisolutions.shotbox.librariesloader.setup_loading;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by homi on 5/14/16.
 */
class ConfigFileLoader {

    private static final Logger logger = Logger.getLogger(ConfigFileLoader.class);
    private static final ExceptionHandling eh = ExceptionHandling.getINSTANCE(ConfigFileLoader.class);


    /*
   TODO: create logs for this methods
   TODO: comment steps
    */
    public void loadConfigFile() {
        Properties props = null;
        File f = new File(Constants.CONFIG_FILE_PATH);
        InputStream in = null;

        try {
            props = new Properties();
            in = new FileInputStream(Constants.CONFIG_FILE_PATH);
            props.load(in);
            in.close();

            SystemSetup.LIBRARY_FOLDER = props.getProperty(Constants.PATH_TO_LIBRARIES_DIR_PROPERTY_NAME);
            SystemSetup.PACKAGE_WITH_APIs = props.getProperty(Constants.PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME);

        } catch (IOException e) {
            eh.handle(e);

            //TODO: print some bad error

            SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;
            SystemSetup.PACKAGE_WITH_APIs = "";
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

}
