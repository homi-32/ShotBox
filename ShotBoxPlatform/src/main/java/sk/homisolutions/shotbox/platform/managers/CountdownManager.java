package sk.homisolutions.shotbox.platform.managers;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.models.ShotBoxCountdown;

/**
 * Created by homi on 11/12/16.
 */
public class CountdownManager {
    private static final Logger logger = Logger.getLogger(CountdownManager.class);
    private static final CountdownManager INSTANCE = new CountdownManager();

    private Long executionTime = null;

    private CountdownManager(){
        //singleton
    }

    public static CountdownManager getInstance(){
        return INSTANCE;
    }

    public Long getMillisToTakingShot(){
        if(executionTime == null)
            return 0L;
        else {
            return executionTime - System.currentTimeMillis();
        }
    }

    public void setupCountdown(Long seconds){
        executionTime = ShotBoxCountdown.createCountdown(seconds, ProvidersManager.getInstance().getCountdownProvider());
        logger.info("Countdown is set to: " +executionTime);
    }

    public void countdownIsOver(){
            WorkflowManager.getInstance().countdownIsOver();
            executionTime = null;

    }
}
