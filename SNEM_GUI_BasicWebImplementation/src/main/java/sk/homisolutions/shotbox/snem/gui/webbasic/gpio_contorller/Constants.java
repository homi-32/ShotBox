package sk.homisolutions.shotbox.snem.gui.webbasic.gpio_contorller;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by homi on 4/2/17.
 */
public class Constants {

    //GPIO22 - RaspiPin.GPIO_03 pi4j representation
    public static Pin YES_BUTTON = RaspiPin.GPIO_03;

    //GPIO23 - RaspiPin.GPIO_04 pi4j representation
    public static Pin NO_BUTTON = RaspiPin.GPIO_04;

    //GPIO27 - RaspiPin.GPIO_02 pi4j representation
    public static Pin SWITCH_SCREEN_BUTTON = RaspiPin.GPIO_02;
}
