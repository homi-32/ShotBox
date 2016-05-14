package sk.homisolutions.shotbox.librariesloader.classloading_system;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;
import sk.homisolutions.shotbox.librariesloader.setup_loading.InitApplicationSetup;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by homi on 4/3/16.
 */
public class LibsLoader implements LibrariesLoader {

    private static final Logger logger = Logger.getLogger(LibsLoader.class);
    private static final ExceptionHandling exceptionHandler = ExceptionHandling.getINSTANCE(LibsLoader.class);

    private FilesCrawler crawler;
    private FilesFilter filter;

    private List<String> relevantFiles = new ArrayList<>();
    private List<Class> allClasses = new ArrayList<>();
    private List<Class> relevantClasses = new ArrayList<>();

    public LibsLoader() {
        logger.info("Object is being initialized.");

        logger.info("Loader is going to load LLS settings from config file.");
        InitApplicationSetup.getINSTANCE().init();

        logger.info("Creating FilesCrawler object.");
        this.crawler = new FilesCrawler();

        logger.info("Creating FilesFilter object.");
        this.filter = new FilesFilter();

        logger.info("Loading classes (jars + standalone classes) process starts.");
        loadClasses();
        logger.info("Loading classes process ends.");
        logger.info("Initializing object ends.");
    }


    @Override
    public List<Class> reloadPresentedClasses() {
        logger.info("Method called.");
        logger.info("Getting relevant files (Classes and Jars) from libraries directory.");

        logger.info("Relevant files (Classes and Jars) are recorded.");

        logger.info("Classes are going to be loaded.");
        List<Class> classes = loadClasses();
        logger.info("Classes are loaded: ");
        classes.forEach(logger::info);

        logger.info("Method ends.");
        return classes;
    }

    @Override
    public List<Class> getLoadedClasses() {
        logger.info("Getting relevant classes:");
        relevantClasses.forEach(logger::info);
        return relevantClasses;
    }

    @Override
    public List<Class> getAllLoadedClasses() {
        logger.info("Getting all classes:");
        allClasses.forEach(logger::info);
        return allClasses;
    }

    private List<Class> loadClasses() {
        logger.info("Method called.");

        logger.debug("Libraries from directory '"+ SystemSetup.LIBRARY_FOLDER+"' are going to be indexed.");
        relevantFiles = this.crawler.gettingRelevantFiles();

        List<String> jarFiles = new LinkedList<>();
        List<String> classFiles = new LinkedList<>();

        logger.info("Presented files are going to be filtered.");
        filter.filterPresentedFiles(relevantFiles, jarFiles, classFiles);
        logger.info("Presented files are now filtered");

        if (jarFiles.size() == 0) {
            logger.warn("List of jars is empty. There are libraries presented in library folder to load.");
        }
        if (classFiles.size() == 0) {
            logger.warn("List of classes is empty. There are standalone classes presented in library folder to load.");
        }


        logger.info("Loading presented jars will now start.");
        this.loadClassesFromJar(jarFiles);
        logger.info("All presented jars are now loaded in application.");
        logger.info("Loading standalone classes will now start.");
        this.loadStandaloneClass(classFiles);
        logger.info("All standalone classes are now loaded in application.");

        if (allClasses.size() == 0) {
            logger.warn("List with loaded classes is empty. Nothing was loaded. " +
                    "This empty list will be returned.");
            this.relevantClasses.addAll(this.allClasses);
            return this.relevantClasses;
        }

        logger.info("All loaded classes:");
        allClasses.forEach(x -> logger.info(x.getName()));

        logger.info("All loaded classes are going to be filtered.");
        relevantClasses = filter.filterClasses(allClasses);
        logger.info("Loaded classes are now filtered.");

        if (relevantClasses.size() == 0) {
            logger.warn("List with loaded relevant classes is empty. There are no loaded classes, which could be " +
                    "used by application.");
        } else {
            relevantClasses.forEach(x -> logger.info(x.getName()));
        }

        logger.info("Method ends.");

        return this.relevantClasses;
    }


    private void loadStandaloneClass(List<String> classPaths) {

        logger.info("Method called.");

        if (classPaths == null) {
            logger.error("Input list with classes' path is null. Something is wrong and needs to debug. " +
                    "There is nothing to load, so application is going to terminate this process.");
            return;
        }
        if (classPaths.size() == 0) {
            logger.warn("Input list with classes' path is empty. There is nothing to load." +
                    "Application will end this process.");
            return;
        }

        URLClassLoader classLoader = null;
        String actualDirPath = "";

        for (String filePath : classPaths) {
            logger.info("Raw class file path: " + filePath);

        /*
        this class path calculating is good, when i have classes without any package and without any structure
        but when i need to load structured classloading_system, structured class classloading_system, this is very bad path calculating

        String directoryPath = filePath.substring(0, filePath.lastIndexOf(File.separator)+1);
        String className = filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.lastIndexOf("."));
        */

        /*
        correct way to calculate class path :
         */

            String directoryPath = filePath.substring(0, filePath.indexOf(SystemSetup.LIBRARY_FOLDER) + SystemSetup.LIBRARY_FOLDER.length());
            String className = filePath.substring(filePath.indexOf(SystemSetup.LIBRARY_FOLDER) + SystemSetup.LIBRARY_FOLDER.length() + 1);
            className = className.substring(0, className.indexOf(""));
            className = className.replace(File.separator, "");

            logger.info("Calcualted class path: " +
                    directoryPath + " : " +
                    className + " -");
            try {
                if (!actualDirPath.equals(directoryPath)) {
                    logger.info("New dir path is calculated for ClassLoader: '" + directoryPath
                            + "' old one: '" + actualDirPath + "'.");
                    actualDirPath = directoryPath;

                    logger.info("Classloader is going to be initialized.");
                    classLoader = initClassLoader(directoryPath);
                    if (classLoader == null) {
                        logger.error("Classloader is null -> could not be initialized. Nothing can be loaded.");
                        continue;
                    } else {
                        logger.info("Classloader is initialized.");
                    }
                }

                if (classLoader == null) {
                    logger.error("Classloader is not initialized. Probable reason is wrong path to directory " +
                            "with class. Without classloader and correct path to class, nothing can be loaded. " +
                            "This class will be skipped.");
                    continue;
                }

                if (actualDirPath == null || actualDirPath.equals("")) {
                    logger.error("Path to directory with classes is not valid: " + actualDirPath
                            + ". Nothing will be loaded. This class is skipped.");
                    continue;
                }

                logger.info("Actual dir path for Classloader: " + actualDirPath);

                logger.info("Class is going to be loaded: " + className);


                Class c = classLoader.loadClass(className);
                logger.info("Class is loaded: " + c.getName());

                if (c == null) {
                    logger.error("Class could not be loaded. Probable reason is wrong class name. " +
                            "Class is skipped.");
                    continue;
                }

                allClasses.add(c);
                logger.info("Class is added to list");
            } catch (MalformedURLException e) {
                exceptionHandler.handle(e);
            } catch (ClassNotFoundException e) {
                exceptionHandler.handle(e);
            }
        }

        logger.debug("Classes in list:");

        allClasses.forEach(logger::debug);

        logger.info("Method ends.");
    }

    private URLClassLoader initClassLoader(String path) throws MalformedURLException {
        logger.info("Method called.");

        if (path == null) {
            logger.error("Input path is null. No Classloader will be initialized. Quitting this action." +
                    "Null will be returned.");
            return null;
        }
        if (path.equals("")) {
            logger.error("Input path is empty. No Classloader will be initialized. Quitting this action." +
                    "Null will be returned.");
            return null;
        }

        logger.info("Path for classloader: " + path);

        URL[] urls = {null};

        if (path.endsWith(".jar")) {
            urls[0] = new URL("jar:file:" + path + "!/");
            logger.info("Will be created class loader for jar file.");
        } else {
            File file = new File(path);
            urls[0] = file.toURI().toURL();
            logger.info("Will be created class loader for directory.");
        }

        if (urls[0] == null) {
            logger.error("Path is not valid for creating classloader. Null will be returned.");
            return null;
        }

        logger.info("Classloader is going to be created.");
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        if (loader == null) {
            logger.error("Classloader is null. Address was probably wrong. Method is returning null");
            return null;
        }
        logger.info("Classloader created.");

        logger.info("Method ends.");
        return loader;
    }


    private void loadClassesFromJar(List<String> jarPaths) {
        logger.info("Method called");

        if (jarPaths == null) {
            logger.error("Input list with jars' path is null. Something is wrong and needs to debug. " +
                    "There is nothing to load, so application is going to terminate this process.");
            return;
        }
        if (jarPaths.size() == 0) {
            logger.warn("Input list with jars' path is empty. There is nothing to load." +
                    "Application will end this process.");
            return;
        }

        for (String filePath : jarPaths) {
            logger.info("Loaded jar: " + filePath);
            try {

                logger.info("Jar file is going to be loaded.");
                //load jar file
                JarFile jar = new JarFile(filePath);
                if (jar == null) {
                    logger.error("Jar could not be loaded. Path to jar is probably wrong. " +
                            "This jar is skipping.");
                    continue;
                }
                logger.info("Jar file is loaded.");

                //get jar content as enumeration
                Enumeration e = jar.entries();

                logger.info("Getting classloader for jar file.");
                //create classloader for loaded jar
                URLClassLoader cl = initClassLoader(filePath);
                if (cl == null) {
                    logger.error("Returned classloader is null. CL could not be created." +
                            "Path to jar is probably wrong. This jar is skipping.");
                    continue;
                }
                logger.info("Classloader for jar is created.");

                //enumerating jar content
                while (e.hasMoreElements()) {

                    //getting file from jar
                    JarEntry je = (JarEntry) e.nextElement();

                    //check, if file is directory, or something else, but not class
                    logger.info("File in jar: " + je.getName());
                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        logger.info("File is not class.");
                        continue;
                    }

                    logger.info("File is class");

                    //concating class path to get class name for UrlClassLoader
                    String className = je.getName().substring(0, (je.getName().length() - 6));
                    className = className.replace('/', '.');
                    logger.info("Class name for UrlClassLoader: " + className);

                    //loading class
                    logger.info("Loading class");
                    Class c = cl.loadClass(className);
                    logger.info("Class is loaded: " + c.getName());

                    //adding class to class list for all loaded classes
                    logger.info("Class will be added to list with all classes.");
                    allClasses.add(c);
                }

            } catch (IOException e) {
                exceptionHandler.handle(e);
            } catch (ClassNotFoundException e) {
                exceptionHandler.handle(e);
            }
        }

        logger.debug("Loaded classes: ");
        allClasses.forEach(logger::debug);

        logger.info("Method ends.");
    }
}

