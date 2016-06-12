package sk.homisolutions.shotbox.librariesloader.settings;

import java.io.File;

/**
 * Class serve as constants container for LibrariesLoaderSystem.
 *
 * Created by homi on 4/3/16.
 */
public class Constants {

    /********* functional values **********/

    /**
     * This is default path for libraries to load. It will be used, if no settings could be loaded.
     * This setting should not be used as basic setting in application. Data should be
     * !!!always!!!
     * red from settings file.
     * This is default emergency way, if settings file could not be loaded for any reason. Loader will use this
     * setting automatically is this emergency state happens.
     *
     * So please, do not use this, if you will be changing or adding to code anything.
     */
    @Deprecated
    public static final String PATH_TO_LIBRARIES_DIR = "libs";//"JARSorCLASSES"; //"." + File.separator +"JARSorCLASSES";

    /**
     * Path to configuration file for LibrariesLoaderSystem. This file stores settings for LLSystem.
     * If file does not exist, default file is generated.
     */
    public static final String CONFIG_FILE_PATH = "settings" + File.separator +"libs_loader.config";

    /********* textkey values **********/

    /**
     * Property name used in configuration file for LibrariesLoaderSystem,
     * which is used for storing users/custom path to directory,
     * which will contain all external libraries (.class and .jar files).
     */
    public static final String PATH_TO_LIBRARIES_DIR_PROPERTY_NAME = "path_to_libraries_folder";

    /**
     * Property name used in configuration file for LibrariesLoaderSystem,
     * which is used for storing canonical package name (full path to package),
     * where are stored API (interfaces) for loaded libraries for filtering purpose.
     */
    public static final String PACKAGE_NAME_WITH_INTERFACES_PROPERTY_NAME = "package_with_external_api";
}