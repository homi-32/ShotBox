package sk.homisolutions.shotbox.librariesloader.api;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * This interface represent standard for LibrariesLoader used in ShotBox project, but it is not necessary to use it outside of this project.
 *
 * Created by Homi on 4/27/16.
 */
public interface LibrariesLoader {

    /**
     * This method reload files presented in libraries folder, load all classes and jars, filter all load classes and return filtered list with classes.
     * If you do nod need to reload filed in directory with libraries, call method getLoadedClasses().
     *
     * @see #getLoadedClasses()
     *
     * @return filtered list with loaded classes actually presented in directory with libraries
     */
    List<Class> reloadPresentedClasses();

    /**
     * This method return filtered list with loaded classes. This list represent classes, which was presented in directory, when this directory was scanned last time.
     * When you call this method, it just return loaded classes. If you need to rescan and reload all classes again from directory, use method:
     * reloadPresentedClasses().
     * If you need to get all loaded classes, use method: getAllLoadedClasses().
     *
     * @see #getAllLoadedClasses()
     * @see #reloadPresentedClasses()
     *
     * @return filtered list with loaded classes, which was presented in directory with libraries when this directory was scanned last time.
     */
    List<Class> getLoadedClasses();

    /**
     * This method return non-filtered list with all loaded classes. This list represent classes, which was presented in directory, when this directory was scanned last time.
     *
     * When you call this method, it just return loaded classes. If you need to rescan and reload all classes from directory and then get all this classes, you need to call first:
     * reloadPresentedClasses()
     * and then use this method:
     * getAllLoadedClasses().
     *
     * @see #getAllLoadedClasses()
     * @see #reloadPresentedClasses()
     *
     * @return non-filtered list with all loaded classes, which was presented in directory with libraries when this directory was scanned last time.
     */
    List<Class> getAllLoadedClasses();
}
