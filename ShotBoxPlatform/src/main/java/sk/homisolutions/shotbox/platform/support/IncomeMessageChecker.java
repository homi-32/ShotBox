package sk.homisolutions.shotbox.platform.support;

import sk.homisolutions.shotbox.tools.api.external.general.ShotBoxExternalModule;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 10/8/16.
 */
public class IncomeMessageChecker {
    private static final IncomeMessageChecker INSTANCE = new IncomeMessageChecker();

    private IncomeMessageChecker(){
        //singleton
    }

    public static IncomeMessageChecker getInstance(){
        return  INSTANCE;
    }

    public ShotBoxMessage checkMessage(ShotBoxMessage message, ShotBoxExternalModule module){

        message.setShutdown(false);

        return message;
    }

}
