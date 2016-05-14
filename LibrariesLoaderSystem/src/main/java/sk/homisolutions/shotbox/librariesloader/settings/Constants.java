package sk.homisolutions.shotbox.librariesloader.settings;

import java.io.File;

/**
 * Created by homi on 4/3/16.
 */
public class Constants {

    //TODO: This is default values for now, but in future, this should not be used.

    @Deprecated
    public static final String PATH_TO_LIBRARIES_DIR = "libs";//"JARSorCLASSES"; //"." + File.separator +"JARSorCLASSES";

    /*
    Really, I need to delete this
    TODO: delete this
     */
//    @Deprecated
    /*IMPORTANT: there needs to be full java path to interfaces package; e.g.: com.mySoft.interfaces */
//    public static final String PACKAGE_NAME_WITH_INTERFACES = "sk.homisolutions.shotbox.api.external"; //"sk.homisolutions.shotbox";//"interfaces";

    public static final String CONFIG_FILE_PATH = "settings" + File.separator +"libs_loader.config";
    public static final String PATH_TO_LIBRARIES_DIR_PROPERTY_NAME = "path_to_libraries_folder";
    public static final String PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME = "package_with_external_api";
}