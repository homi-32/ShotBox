package sk.homisolutions.shotbox.snem.scenecontroller.rpigpiocontroller;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by homi on 8/30/16.
 */
public class ShotboxGpioHelper {

    public Pin resolveGpioPin(String pin){
        return resolveGpioPin(Integer.valueOf(pin));
    }

    public Pin resolveGpioPin(Integer gpioPin){
        Pin pin;

        switch (gpioPin){
            case 2:
            pin = RaspiPin.GPIO_08;
            break;
            case 3:
            pin = RaspiPin.GPIO_09;
            break;
            case 4:
            pin = RaspiPin.GPIO_07;
            break;
            case 17:
            pin = RaspiPin.GPIO_00;
            break;
            case 27:
            pin = RaspiPin.GPIO_02;
            break;
            case 22:
            pin = RaspiPin.GPIO_03;
            break;
            case 10:
            pin = RaspiPin.GPIO_12;
            break;
            case 9:
            pin = RaspiPin.GPIO_13;
            break;
            case 11:
            pin = RaspiPin.GPIO_14;
            break;
            case 5:
            pin = RaspiPin.GPIO_21;
            break;
            case 6:
            pin = RaspiPin.GPIO_22;
            break;
            case 13:
            pin = RaspiPin.GPIO_23;
            break;
            case 19:
            pin = RaspiPin.GPIO_24;
            break;
            case 26:
            pin = RaspiPin.GPIO_25;
            break;
            case 14:
            pin = RaspiPin.GPIO_15;
            break;
            case 15:
            pin = RaspiPin.GPIO_16;
            break;
            case 18:
            pin = RaspiPin.GPIO_01;
            break;
            case 23:
            pin = RaspiPin.GPIO_04;
            break;
            case 24:
            pin = RaspiPin.GPIO_05;
            break;
            case 25:
            pin = RaspiPin.GPIO_06;
            break;
            case 8:
            pin = RaspiPin.GPIO_10;
            break;
            case 7:
            pin = RaspiPin.GPIO_11;
            break;
            case 12:
            pin = RaspiPin.GPIO_26;
            break;
            case 16:
            pin = RaspiPin.GPIO_27;
            break;
            case 20:
            pin = RaspiPin.GPIO_28;
            break;
            case 21:
            pin = RaspiPin.GPIO_29;
            break;
            default:
                pin = null;
                throw new SNEM_SceneController_RpiGpio17Controller_Exception("Defined GPIO pin is unknown. Use Broadcom GPIO pin numbering please: " +
                        "https://www.raspberrypi.org/documentation/usage/gpio-plus-and-raspi2/images/gpio-numbers-pi2.png");
        }

        return pin;
    }
}
