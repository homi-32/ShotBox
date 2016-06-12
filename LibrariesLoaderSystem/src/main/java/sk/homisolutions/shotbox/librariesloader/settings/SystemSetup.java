package sk.homisolutions.shotbox.librariesloader.settings;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * This class serve as model for storing settings for LibrariesLoaderSystem, which are actually using.
 *
 * Created by homi on 5/12/16.
 */
public class SystemSetup {

    /**
     * Storing path to folder, which contains external libraries.
     */
    public static String LIBRARY_FOLDER = null;

    /**
     * Storing path (package full name) to API for external libraries.
     */
    public static String PACKAGE_WITH_APIs = null;
}
