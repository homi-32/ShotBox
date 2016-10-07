package sk.homisolutions.shotbox.librariesloader.exceptionhandling;

import org.apache.log4j.Logger;

/**
 * This class server as tool to standardize exception handling in LibrariesLoader.
 * For now, it is just logging exceptions, but point is, it is standardizing this output.
 * Purpose is to make exception easier to find and read in log files.
 *
 * Created by homi on 5/14/16.
 */
public class ExceptionHandling {
    private static final Logger logger = Logger.getLogger(ExceptionHandling.class);
    private String className = "";

    private ExceptionHandling(Class cl){
        logger.info("ExceptionHandling constructor called, creating object for class: " +cl.getName());
        //singleton
        this.className = cl.getName();
        logger.info("ExceptionHandling object created.");
    }

    /**
     * Factory for ExceptionHandling tool. It creates exception handler for concrete class.
     *
     * @param cl class, which will use this handler
     * @return ExceptionHandling object for concrete class
     */
    public static ExceptionHandling createINSTANCE(Class cl)
    {
        logger.info("Method called. Creating handler for class: " +cl.getName());
        return new ExceptionHandling(cl);
    }

    /**
     * Method for correct exception handling way in LibrariesLoaderSystem.
     * For now, it just create standardized output with logger, to to make finding and reading exceptions easier
     * in logs.
     *
     * @param e exception, which should be handled
     */
    public void handle(Throwable t){
        if(t instanceof Exception) {
            logger.info("Method called, \nfor class: " + className + ", \nexception: " + t);
            logger.error("Exception trowed: " +t.getClass().getSimpleName());
        }else if (t instanceof Error){
            logger.fatal("Method called, \nfor class: " + className + ", \nerror: " + t);
            logger.fatal("Error trowed: " +t.getClass().getSimpleName());
        }else {
            logger.fatal("Method called, \nfor class: " + className + ", \n issue: " + t);
            logger.fatal("Trowed: " +t.getClass().getSimpleName());
        }
        logger.fatal("Class: " +className);
        logger.fatal("Message: " +t.getMessage());
        logger.fatal("Cause: " + t.getCause());
        t.printStackTrace();
    }
}
