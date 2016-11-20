package sk.homisolutions.shotbox.snem.gui.webbasic;

/**
 * Created by homi on 11/12/16.
 */
public enum GuiState {
    READY("ready"),
    COUNTDOWN("countdown"),
    TAKING_PICTURE("taking-picture"),
    PHOTO_IS_TAKEN("photo-is-taken"),
    PHOTO_PROVIDED("photo-provided-waiting-for-decision"),
    BUSY("platform-is-busy");

    private String state;
    GuiState(String web_representation){
        state = web_representation;
    }

    public String getString() {
        return state;
    }
}
