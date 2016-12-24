package sk.homisolutions.shotbox.platform.logging;

import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Created by homi on 12/14/16.
 */
public class SetupLogger {

    private static final SetupLogger INSTANCE = new SetupLogger();
    private static final Logger logger = Logger.getLogger(SetupLogger.class);

    private SetupLogger(){
        //singleton
    }

    public static SetupLogger getInstance(){
        return INSTANCE;
    }

    public void transferOutputToLog4j(){
        System.setOut(new PrintStream(System.out){
            public void print(final String string) {
                logger.info(string);
            }
        });
        System.setErr(new PrintStream(System.out){
            public void print(final String string) {
                logger.error(string);
            }
        });
    }
}
