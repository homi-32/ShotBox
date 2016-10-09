package sk.homisolutions.shotbox.snem.advancedcamera.gopro;

/**
 * Created by homi on 8/23/16.
 */
public class Constants {

    public static final String GOPRO_HERO4_IP="10.5.5.9";
    public static final String GOPRO_HERO4_SUBNETMASK="255.255.255.0";
    public static final String GOPRO_HERO4_MAC="D6:D9:19:9A:8C:10";
    public static final Integer GOPRO_HERO4_PORT_FOR_WoL=9;

    //pairing process
    public static final String GOPRO_PAIRING_PIN="";
    public static final String GOPRO_PAIRING_START="https://10.5.5.9/gpPair?c=start&pin="+GOPRO_PAIRING_PIN+"&mode=0";
    public static final String GOPRO_PAIRING_FINISH="https://10.5.5.9/gpPair?c=finish&pin="+GOPRO_PAIRING_PIN+"&mode=0";

    /*
    GOPRO HERO4 BLACK/SILVER COMMANDS:
     */

    // (may only apply for HERO2/HERO3/HERO3+), HERO4 do not need any password
    public static final String GET_PASSWORD="http://10.5.5.9/camera/sd";

    //for all cameras
    public static final String GET_MEDIA_LIST="http://10.5.5.9:8080/gp/gpMediaList/";

    public static final String GOPRO_FOLDER_WITH_PHOTOS = "100GOPRO";
    public static String MAIN_DIRECTORY_URL="http://10.5.5.9:8080/videos/DCIM/"+GOPRO_FOLDER_WITH_PHOTOS+"/";

    /*
    more info here: https://github.com/KonradIT/goprowifihack/blob/master/HERO4/CameraStatus.md
     */
    public static final String GET_CAMERA_STATUS="http://10.5.5.9/gp/gpControl/status";

    // commands for hero4 black/silver are available here:
    // https://github.com/KonradIT/goprowifihack/blob/master/HERO4/WifiCommands.md
    // most values: 1-yes, 0-no
    // also available Multishot, instead of photo
    public static final String TURN_ON_PHOTO="http://10.5.5.9/gp/gpControl/command/mode?p=1";
    public static final String SET_NATIVE_WHITE_BALANCE_FOR_PHOTO="http://10.5.5.9/gp/gpControl/setting/22/4";
    //GOPRO color for photo https://i.ytimg.com/vi/q4CQZrBfk3w/maxresdefault.jpg
    public static final String SET_COLOR_TO_GOPRO_FOR_PHOTO="http://10.5.5.9/gp/gpControl/setting/23/0";
    //values 0 - 3: 0-800, 1-400, 2-200, 3-100
    public static final String SET_ISO_LIMIT_TO_200_FOR_PHOTO="http://10.5.5.9/gp/gpControl/setting/24/2";
    public static final String SET_SHARPNESS_TO_MEDIUM_FOR_PHOTO="http://10.5.5.9/gp/gpControl/setting/25/1";
    public static final String SET_PHOTO_AS_PRIMARY_MODE="http://10.5.5.9/gp/gpControl/command/mode?p=1";
    public static final String SET_PHOTO_RESOLUTION_TO_MEDIUM_7MP="http://10.5.5.9/gp/gpControl/setting/17/2";
    public static final String TURN_LCD_DISPLAY_ON="http://10.5.5.9/gp/gpControl/setting/72/1";
    public static final String TURN_LCD_DISPLAY_OFF="http://10.5.5.9/gp/gpControl/setting/72/0";
    //for video/timelapse there is need for stop command: http://10.5.5.9/gp/gpControl/command/shutter?p=0
    public static final String TRIGGER_PHOTO="http://10.5.5.9/gp/gpControl/command/shutter?p=1";
    public static final String GYRO_BASED_ORIENTATION="http://10.5.5.9/gp/gpControl/setting/52/0";
    public static final String QUICK_CAPTURE_ON="http://10.5.5.9/gp/gpControl/setting/54/1";
    public static final String LED_BLINK_4TIMES="http://10.5.5.9/gp/gpControl/setting/55/2";

    //video streaming
    public static final String ENABLE_VIDEO_STREAM="http://10.5.5.9/gp/gpControl/execute?p1=gpStream&c1=restart";
    public static final String DISABLE_VIDEO_STREAM="http://10.5.5.9:8080/gp/gpControl/execute?p1=gpStream&c1=stop";
    public static final String SET_VIDEO_STREAM_BITRATE_TO_2point4Mbps = "http://10.5.5.9/gp/gpControl/setting/59/2400000";
    public static final String SET_VIDEO_STREAM_WINDOWS_SIZE_TO_480_3to4 = "http://10.5.5.9/gp/gpControl/setting/64/5";
    public static final String VIDEO_STREAM_CONNECTION_URL_WITH_PORT = "10.5.5.9:8554";
    public static final String VIDEO_STREAM_CONNECTION_PROTOCOL = "rtp";

    //delete file: http://10.5.5.9/gp/gpControl/command/storage/delete?p=file (eg. /100GOPRO/G0010124.JPG)
    //Delete Last media taken: http://10.5.5.9/gp/gpControl/command/storage/delete/last
}
