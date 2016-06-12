package sk.homisolutions.shotbox.librariesloader.setup_loading;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetupManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class serve as tool for loading settings from config files.
 *
 * Created by homi on 5/14/16.
 */
class ConfigFileLoader {

    private static final Logger logger = Logger.getLogger(ConfigFileLoader.class);
    private static final ExceptionHandling eh = ExceptionHandling.createINSTANCE(ConfigFileLoader.class);


    /**
     * Method for loading settings from config file and storing it to SystemSetup model.
     */
    public void loadConfigFile() {
        logger.info("Method starts.");

        Properties props = null;
        File f = new File(Constants.CONFIG_FILE_PATH);
        InputStream in = null;

        logger.info("Loading setting from config file: '" +f.getAbsolutePath() +"' is going to start.");
        //loading properties from config file
        try {
            props = new Properties();
            logger.info("Opening stream to load settings from file.");
            in = new FileInputStream(Constants.CONFIG_FILE_PATH);
            logger.info("Stream opened. Loading settings.");
            props.load(in);
            logger.info("Settings loaded. Closing stream.");
            in.close();
            logger.info("Stream closed.");
            logger.info("Loading settings finished.");
            SystemSetupManagement.setSettingsForSystemSetup(props);
            logger.info("Reading settings finished. Settings are now stored.");

        } catch (IOException e) {
            logger.error("IOException threw while reading settings from config file: '" +Constants.CONFIG_FILE_PATH +"'");
            eh.handle(e);

            logger.error("Reading settings from config file failed. Default settings will be used.");
            SystemSetupManagement.setDefaultValues();

        } finally {
            logger.info("Checking, if there is some opened stream to close.");
            if(in != null){
                try {
                    logger.info("Opened stream found.");
                    logger.error("InputFileStream had been opened, before exception was threw. " +
                            "Trying to close this stream.");
                    in.close();
                    logger.error("Stream has been closed successfully.");
                } catch (IOException e) {
                    logger.fatal("Stream could not be closed. Another IOException has been thrown. " +
                            "So, it looks like stream stays opened. Bloody stream.");
                    eh.handle(e);
                }
            }else {
                logger.info("No opened stream found.");
            }
        }

        logger.info("Method ends.");
    }

}
