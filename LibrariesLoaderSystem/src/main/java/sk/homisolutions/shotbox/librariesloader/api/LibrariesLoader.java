package sk.homisolutions.shotbox.librariesloader.api;

import java.util.List;

/**
 * Created by homi on 4/27/16.
 */
public interface LibrariesLoader {

    List<Class> reloadPresentedClasses();

    List<Class> getLoadedClasses();

    List<Class> getAllLoadedClasses();
}