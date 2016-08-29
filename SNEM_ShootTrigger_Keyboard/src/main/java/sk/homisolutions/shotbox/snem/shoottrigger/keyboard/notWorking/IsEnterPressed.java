package sk.homisolutions.shotbox.snem.shoottrigger.keyboard.notWorking;

/**
 * Created by homi on 8/20/16.
 */
/*
Used solution from:
stackoverflow.com/questions/18037576/how-do-i-check-if-the-user-is-pressing-a-key
 */
public class IsEnterPressed {
    private static boolean enterPressed = false;

    public static boolean isPressed(){
        synchronized (IsEnterPressed.class){
            return enterPressed;
        }
    }

    public static void setPressed(boolean pressed){
        synchronized (IsEnterPressed.class){
            enterPressed = pressed;
        }
    }
}
