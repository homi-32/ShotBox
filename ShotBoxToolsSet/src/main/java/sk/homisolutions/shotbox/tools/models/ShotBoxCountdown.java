package sk.homisolutions.shotbox.tools.models;

import sk.homisolutions.shotbox.tools.api.internal.countdown.CountdownPlatformProvider;

/**
 * Created by homi on 11/12/16.
 */
public class ShotBoxCountdown implements Runnable {

    CountdownPlatformProvider provider;

    private Long millis;

    private ShotBoxCountdown(Long secondsToTakingShot, CountdownPlatformProvider provider){

        millis = System.currentTimeMillis() + (secondsToTakingShot*1000);
        this.provider = provider;

        //singleton
    }

    public static Long createCountdown(Long secondsToTakingShot, CountdownPlatformProvider provider){
        ShotBoxCountdown instance = new ShotBoxCountdown(secondsToTakingShot, provider);
        Long millis = instance.getExecutionTime();
        Thread thread = new Thread(instance);
        thread.start();
        return millis;
    }

    @Override
    public void run() {
        while (millis>System.currentTimeMillis()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        provider.countdownIsOver();
    }

    public Long getExecutionTime(){
        return millis;
    }
}
