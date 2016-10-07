package sk.homisolutions.shotbox.librariesloader.classloading_system;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.api.LibrariesLoader;
import sk.homisolutions.shotbox.librariesloader.exceptionhandling.ExceptionHandling;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;
import sk.homisolutions.shotbox.librariesloader.setup_loading.InitApplicationSetup;
import sk.homisolutions.shotbox.tools.models.ShotboxManifest;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * This class serve as tool for loading available .class files to application.
 *
 * Created by homi on 4/3/16.
 */
//TODO: add check, if the class is already loaded, or not, but now i am not sure, if it is possible
//TODO: review class, edit code logic, secure new parts, log new parts
public class LibsLoader implements LibrariesLoader {

    private static final Logger logger = Logger.getLogger(LibsLoader.class);
    private static final ExceptionHandling exceptionHandler = ExceptionHandling.createINSTANCE(LibsLoader.class);

    private FilesCrawler crawler;
    private FilesFilter filter;

    private List<String> relevantFiles = new ArrayList<>();
    private List<Class> allClasses = new ArrayList<>();
    private List<Class> relevantClasses = new ArrayList<>();
    private List<ShotboxManifest> manifests = new ArrayList<>();


    /**
     * This is constructor. While creating of LibsLoader object, it also
     * initialize setup for whole LibrariesLoaderSystem,
     * create FilesCrawler object,
     * create FilesFilter object
     * and perform loading libraries process - loads all libraries presented in libraries folder.
     */
    public LibsLoader() {
        logger.info("Object is being initialized.");

        logger.info("Loader is going to load LLS settings from config file.");
        InitApplicationSetup.getInstance().init();

        logger.info("Creating FilesCrawler object.");
        this.crawler = new FilesCrawler();

        logger.info("Creating FilesFilter object.");
        this.filter = new FilesFilter();

        logger.info("Loading classes (jars + standalone classes) process starts.");
        loadClasses();
        logger.info("Loading classes process ends.");

        logger.info("Initializing object ends.");
    }


    /**
     * This method reloads libraries presented in libraries folder. For more info, see:
     * @see LibrariesLoader#reloadPresentedClasses()
     *
     * @return filtered list with reloaded classes (standalone and packaged in jars) presented in libraries folder
     */
    @Override
    public List<Class> reloadPresentedClasses() {
        logger.info("***LLS*** Method called.");
        logger.info("***LLS*** Getting relevant files (Classes and Jars) from libraries directory.");

        logger.info("***LLS*** Relevant files (Classes and Jars) are recorded.");

        logger.info("***LLS*** Classes are going to be loaded.");
        List<Class> classes = loadClasses();
        logger.info("***LLS*** Classes are loaded: ");
        classes.forEach(logger::info);

        logger.info("***LLS*** Method ends.");
        return classes;
    }

    /**
     * This method returns list with filtered loaded classes (standalone and packaged in jars) which was presented in
     * libraries folder while loading process was performed. For more info see:
     * @see LibrariesLoader#getLoadedClasses()
     *
     * @return filtered list with loaded classes (standalone and packaged in jars) presented in libraries folder
     */
    @Override
    public List<Class> getLoadedClasses() {
        logger.info("***LLS*** Getting relevant classes:");
        relevantClasses.forEach(logger::info);
        return relevantClasses;
    }

    /**
     * This method returns list with all loaded classes  (standalone and packaged in jars) which was presented in
     * libraries folder while loading process was performed. For more info see:
     * @see LibrariesLoader#getAllLoadedClasses()
     *
     * @return list with all loaded classes (standalone and packaged in jars) presented in libraries folder
     */
    @Override
    public List<Class> getAllLoadedClasses() {
        logger.info("***LLS*** Getting all classes:");
        allClasses.forEach(logger::info);
        return allClasses;
    }

    @Override
    public List<ShotboxManifest> getShotboxManifests() {
        return manifests;
    }

    private List<Class> loadClasses() {
        logger.info("Method called.");
        logger.info("********* LibrariesLoaderSystem Analyse starts *********");

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

        logger.info("********* LibrariesLoaderSystem Analyse ends *********");
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
            className = className.substring(0, className.indexOf("."));
            className = className.replace(File.separator, ".");

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


                Class c = null;
                try {
                    c = loadClass(classLoader, className);
                    if(c != null) {
                        logger.info("Class is loaded: " + c.getName());
                    }
                }catch (Exception exc){
                    logger.error("Exception occurs while loading class: '" +className +"'.");
                    exceptionHandler.handle(exc);
                }


                if (c == null) {
                    logger.error("Class could not be loaded. Probable reason is wrong class name, " +
                            "or class is already loaded" +
                            "Class is skipped.");
                    continue;
                }

                allClasses.add(c);
                logger.info("Class is added to list");
            } catch (MalformedURLException e) {
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
            logger.info("Loading jar: " + filePath);

            //solution from: http://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime
            /*
            Works. Even with dependencies to other classes inside the jar. The first line was incomplete.
            I used URLClassLoader child = new URLClassLoader (new URL[] {new URL("file://./my.jar")}, Main.class.getClassLoader());
            assuming that the jar file is called my.jar and is located in the same directory. – jaw Feb 4 '15 at 14:04
             */
//            try {
//                URLClassLoader child = new URLClassLoader( (new URL[]{new URL(filePath)}, ))
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }


            //this variables are declared here, so i can print it in catch block;
            String className ="";
            String jarsPath = "";
            try {

                logger.info("Jar file is going to be loaded.");
                //load jar file
                JarFile jar = new JarFile(filePath);
                jarsPath = filePath;
                if (jar == null) {
                    logger.error("Jar could not be loaded. Path to jar is probably wrong. " +
                            "This jar is skipping.");
                    continue;
                }
                logger.info("Jar file is loaded.");

                logger.info("reading shotbox manifest from jar");
                ShotboxManifest manifest = loadManifest(jar);
                if(manifest == null) {
                    logger.error("some error log");
                    continue;
                }
                manifests.add(manifest);
                logger.info("manifest was red successfully");

                logger.info("resolving relevant classes to read from jar");
                List<String> classPaths = getRelevantClassPaths(manifest);


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
                    className = je.getName().substring(0, (je.getName().length() - 6));
                    className = className.replace('/', '.');
                    logger.info("Class name for UrlClassLoader: " + className);

                    if(isClassRelevant(className, classPaths)) {
                        //loading class
                        logger.info("Loading class");
                        Class c = null;
                        try {
                            c = this.loadClass(cl, className);
//                        logger.info("Class is loaded: " + c.getName());
                        } catch (Exception exc) {
                            logger.error("Exception occurs for class:'" + className + "' in jar:'" + jarsPath + "'.");
                            exceptionHandler.handle(exc);
                        }


                        //adding class to class list for all loaded classes
                        if (c != null) {
                            logger.info("Class is loaded and will be added to list with all classes.");
                            allClasses.add(c);
                        } else {
                            logger.warn("Class " + className + " in jar " + jarsPath + " could not be loaded. Class is maybe already loaded (see previous check log).");
                        }
                    }else{
                        logger.info("not important to load");
                        continue;
                    }
                }

            } catch (IOException e) {
                logger.error("IOException for class '" + className +"' in jar: '"+jarsPath+"'.");
                exceptionHandler.handle(e);
            }

        }

        logger.debug("Loaded classes: ");
        allClasses.forEach(logger::debug);

        logger.info("Method ends.");
    }

    private boolean isClassRelevant(String className, List<String> classPaths) {
        logger.info("Analyzing, if class '" +className +"' is relevant to load");

        for(String s: classPaths){
            if(className.startsWith(s)){
                logger.info("Class is relevant to load.");
                return true;
            }
        }
        logger.info("Class is not relevant to load");
        return false;
    }

    private List<String> getRelevantClassPaths(ShotboxManifest manifest) {
        return manifest.getImplementation_packages();
    }

    private ShotboxManifest loadManifest(JarFile jar) {
        ShotboxManifest manifest = null;
        Gson gson = new Gson();

        //analyzing, if jar contain required manifest
        ZipEntry entry = jar.getEntry("manifest.json");
        if(entry==null){
            logger.error("Jar file "+jar.getName()+" does not contain any manifest for ShotBox.");
            return null;
        }
        logger.info("Manifest is present.");

        try {
            InputStream in = jar.getInputStream(entry);
            int character;
            String json="";
            while ((character = in.read()) != -1){
                json += (char) character;
            }
            logger.info("manufest: " +json);
            manifest = gson.fromJson(json, ShotboxManifest.class);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("cannot read manifest.json: " +e.getMessage());
            return null;
        }

        return manifest;
    }

    private Class loadClass(ClassLoader cl, String className) throws ClassNotFoundException{
        Class c = null;

        logger.info("Loading class: '" +className +"'");
        try{
            c = cl.loadClass(className);
        }catch (Throwable e){
            logger.fatal("!!!! Class '"+className+"' could not be loaded");
            exceptionHandler.handle(e);
        }

        return c;
    }
}

