package sk.homisolutions.shotbox.librariesloader.setup_loading;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by homi on 5/14/16.
 */
class ConfigFileManagement {

    private static final Logger logger = Logger.getLogger(ConfigFileManagement.class);
    private static final ExceptionHandling eh = ExceptionHandling.getINSTANCE(ConfigFileManagement.class);

    /*
    TODO: create logs for this method
    TODO: comment steps
     */
    public boolean checkConfigFileExistence() {
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

                SystemSetup.LIBRARY_FOLDER = Constants.PATH_TO_LIBRARIES_DIR;
                SystemSetup.PACKAGE_WITH_APIs = "";

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
