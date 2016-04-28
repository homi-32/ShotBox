package constants;

import java.io.File;

/**
 * Created by homi on 4/3/16.
 */
public class Constants {

    @Deprecated
    public static String PATH_TO_CLASSES_DIR = "libs";//"JARSorCLASSES"; //"." + File.separator +"JARSorCLASSES";

    @Deprecated
    /*IMPORTANT: there needs to be full java path to interfaces package; e.g.: com.mySoft.interfaces */
    public static String PACKAGE_NAME_WITH_INTERFACES = "sk.homisolutions.shotbox";//"interfaces";

    public static String CONFIG_FILE_PATH = "settings" + File.separator +"libs_loader.config";
}