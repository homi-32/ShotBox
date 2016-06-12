package sk.homisolutions.shotbox.librariesloader.setup_loading;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;
import sk.homisolutions.shotbox.librariesloader.settings.Constants;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetupManagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * This class serve as tool for checking config file consistency
 *
 * Created by homi on 5/14/16.
 */
class ConfigFileManagement {

    private static final Logger logger = Logger.getLogger(ConfigFileManagement.class);
    private static final ExceptionHandling eh = ExceptionHandling.createINSTANCE(ConfigFileManagement.class);

    public ConfigFileManagement(){
        logger.info("Creating object of ConfigFileManagement");
    }

    /**
     * Method for checking, if file exits. If exists, method returns true.
     * If does not, method will try to create default one and returns true.
     * If file does not exists, neither could be created, method returns false, but this is very bad state.
     *
     * @return true file exists or was created defauld one and therefore can be used
     * @return false file does not exists, neither could be created and therefore can not be used
     */
    public boolean checkConfigFileExistence() {
        logger.info("Method called.");

        //initializing setup for method
        Properties props = null;
        File f = new File(Constants.CONFIG_FILE_PATH);
        OutputStream out = null;

        logger.info("Checking, if config file does exists: '" +Constants.CONFIG_FILE_PATH +"'");
        // checking, if file exists
        if(!f.exists() || f.isDirectory()) {
            //file does not exists branch
            logger.info("Config file does not exists.");

            //checking file path, if there should be created any directories
            if(f.getAbsolutePath().contains(File.separator)){
                logger.info("Creating path to config file");
                String direcories = f.getAbsolutePath();
                direcories = direcories.substring(0, direcories.lastIndexOf(File.separator));
                if(new File(direcories).mkdirs()){
                    logger.info("Creating directory was successful: '" +direcories +"'");
                }else {
                    logger.error("Creating path for config file was not successful. " +
                            "Used path: '" +direcories +"'. " +
                            "Creating file will probably fail too.");
                }
            }

            try {
                logger.info("New config file will be created.");
                //trying to create new config file with default settings
                if(f.createNewFile()){
                    //new file is successfully created branch
                    logger.info("New config file was created successfully: '" +f.getAbsolutePath() +"'");

                    logger.info("Creating default settings for config file");
                    props = new Properties();
                    SystemSetupManagement.setDefaultValuesToPropertyFile(props);
                    logger.info("Default settings created. Setting will be stored in config file.");

                    logger.info("Creating output stream to fulfilling config file with settings.");
                    out = new FileOutputStream(Constants.CONFIG_FILE_PATH);
                    logger.info("Output stream created.");

                    logger.info("Storing settings in config file through output stream");

                    props.store(out, "default file created automatically by application");
                    logger.info("Settings are stored.");

                    logger.info("Closing output stream.");
                    out.close();
                    logger.info("Output stream closed.");

                    logger.info("Returning true, method ends.");
                    return true;
                }else{
                    //new config file could not be created
                    logger.fatal("Configuration file for LLS does not exist, " +
                            "and could not be created neither.");
                    logger.info("Returning false. Method ends.");
                    return false;
                }
            } catch (IOException e) {
                //too lazy to write some meaningful error right now
                logger.error("IOException threw while creating or fulfilling new config file");
                eh.handle(e);

                logger.error("Bad error: So, it looks like no config file was created. This is not good situation. " +
                        "Default settings will be used:");

                SystemSetupManagement.setDefaultValues();

                logger.info("Returning false. Method ends.");
                return false;
            } finally {
                logger.info("Checking, if there is some opened stream to close.");
                if(out!=null){
                    try {
                        logger.info("Opened stream found.");
                        logger.info("OutputFileStream had been opened, before exception was threw. " +
                                "Trying to close this stream.");
                        out.close();
                        logger.info("Stream has been closed successfully.");
                    } catch (IOException e1) {
                        logger.fatal("Stream could not be closed. Another IOException has been thrown. " +
                                "So, it looks like stream stays opened. Bloody stream.");
                        eh.handle(e1);
//                        logger.fatal("Well, shit happens.");
                    }
                }else {
                    logger.info("No opened stream found.");
                }
            }
        }

        logger.info("Config file does exists. Returning true. Method ends.");
        //file does exists branch
        return true;
    }
}
