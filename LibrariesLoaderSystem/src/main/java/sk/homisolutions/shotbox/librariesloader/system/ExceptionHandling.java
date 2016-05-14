package sk.homisolutions.shotbox.librariesloader.system;

import org.apache.log4j.Logger;

/**
 * Created by homi on 5/14/16.
 */
public class ExceptionHandling {
    private static final Logger logger = Logger.getLogger(ExceptionHandling.class);
    private String className = "";

    private ExceptionHandling(Class cl){
        //singleton
        this.className = cl.getName();
    }

    //TODO: add logs
    public static ExceptionHandling getINSTANCE(Class cl){
        return new ExceptionHandling(cl);
    }

    public void handle(Exception e){
        logger.error("Class: " +className);
        logger.error("Exception throwed: " +e.getClass().getSimpleName());
        logger.error("Message: " +e.getMessage());
        logger.error("Cause: " + e.getCause());
        e.printStackTrace();
    }
}
