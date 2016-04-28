package loader;

import constants.Constants;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.librariesloader.LibrariesLoader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Created by homi on 4/3/16.
 */
//TODO: protect all calls for null occurrence
class LibsLoader implements LibrariesLoader {

    private static final Logger logger = Logger.getLogger(LibsLoader.class);

    private FilesCrawler filesCrawler;
    private List<String> relevantFiles = new ArrayList<>();
    private List<Class> allClasses = new ArrayList<>();
    private List<Class> relevantClasses = new ArrayList<>();

    public LibsLoader(){
        logger.info("Object is being initialized.");

        logger.info("Creating FilesCrawler object.");
        this.filesCrawler = new FilesCrawler();

        logger.debug("Libraries from directory are going to be indexed.");
        gettingRelevantFiles();

        logger.info("Classes and jars in "+ Constants.PATH_TO_CLASSES_DIR +" are going to be loaded.");
        loadClasses();
        logger.info("Classes and jars are loaded.");
        logger.info("Initializing object ends.");
    }

    private void gettingRelevantFiles() {
        logger.info("Method called.");
        logger.info("Files in directory '" + Constants.PATH_TO_CLASSES_DIR +"' are going to be scanned.");
        this.relevantFiles = this.filesCrawler.getRelevantFilesFromDir();
        if(this.relevantFiles == null){
            logger.fatal("Returned list with files' paths is null. There is some big issue, wich need to be resolved. "+
                    "For application consistency, empty array is created.");
            this.relevantFiles = new ArrayList<>();
        }
        if(this.relevantFiles.size() == 0){
            logger.warn("Returned list with relevant files' paths is empty. " +
                    "There are no files, which can be loaded from directory.");
        }
        logger.info("Files in '" + Constants.PATH_TO_CLASSES_DIR +"' are now scanned." +
                "Relevant files number is: " +this.relevantFiles.size());
        logger.info("Method ends.");
    }

    @Override
    public List<Class> reloadPresentedClasses(){
        logger.info("Method called.");
        logger.info("Getting relevant files (Classes and Jars) from libraries directory.");
        this.relevantFiles = this.filesCrawler.getRelevantFilesFromDir();
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

    private List<Class> loadClasses(){
        logger.info("Method called.");

        List<String> jarFiles = new LinkedList<>();
        List<String> classFiles = new LinkedList<>();

        logger.info("Presented files are going to be filtered.");
        filterPresentedFiles(jarFiles, classFiles);
        logger.info("Presented files are now filtered");

        if(jarFiles.size() == 0){
            logger.warn("List of jars is empty. There are libraries presented in library folder to load.");
        }
        if(classFiles.size() == 0){
            logger.warn("List of classes is empty. There are standalone classes presented in library folder to load.");
        }
        //TODO: Here i ended with checking code. 28.04.2016
        
        
        logger.info("Loading presented jars will now start.");
        this.loadClassesFromJar(jarFiles);
        logger.info("All presented jars are now loaded in application.");
        logger.info("Loading standalone classes will now start.");
        this.loadStandaloneClass(classFiles);
        logger.info("All standalone classes are now loaded in application.");

        logger.info("All loaded classes:");
        allClasses.forEach(x-> logger.info(x.getName()));

        logger.info("All loaded classes are going to be filtered.");
        filterClasses();
        logger.info("Loaded classes are now filtered.");
        relevantClasses.forEach(x-> logger.debug(x.getName()));

        logger.info("Method ends.");

        return this.relevantClasses;
    }

    /*
    Until the getPresentedInterface will not be repaired, this method is useless too.
     */
    @Deprecated
    private void filterClasses() {
        logger.info("Method called.");

        logger.info("Getting presented interfaces (specification for external modules).");
        List<String> interfacesFullNames = this.filesCrawler.getPresentedInterfaces();

        {
            if (interfacesFullNames == null) {
                logger.error("Interfaces (specification) could not be loaded: interfaces' list is null");
            } else if (interfacesFullNames.size() == 0) {
                logger.warn("Interfaces (specification) could not be loaded: interfaces' list size is "
                        + interfacesFullNames.size());
            }
        }

        if(interfacesFullNames == null || interfacesFullNames.size() == 0){
            logger.warn("Interfaces' list can not be used -> Classes' list can not be filtered. " +
                    "All classes are returned as relevant.");
            this.relevantClasses.addAll(allClasses);
        }else{
            logger.info("Interfaces (specification) are loaded.");
            this.relevantClasses.addAll(allClasses.stream()
                    .filter(c -> interfacesFullNames.contains(c.getName()))
                    .collect(Collectors.toList())
            );
        }

        logger.info("Relevant loaded classes.");
        this.relevantClasses.forEach(logger::debug);

        logger.info("Method ends.");
    }

    private void filterPresentedFiles(List<String> jars, List<String> classes) {
        logger.info("Method called.");

        if(jars == null || classes == null){
            //TODO: should be there some exception ??
            logger.error("Some programmer did something wrong. Input arrays are null: " +
                    "jars-" +jars +";" +
                    "classes-" +classes +". " +
                    "Without this lists, method is useless, so application will terminate this process.");
            return;
        }

        if(relevantFiles == null){
            logger.error("Files' list is null. This should not happens. " +
                    "There is nothing to filter, so application will terminate this process.");
            return;
        }

        logger.debug("This files' paths are going to be filtered: ");
        relevantFiles.forEach(logger::debug);

        for (String filePath: relevantFiles){
            logger.info("Analyzing file path: " +filePath);
            if (isJar(filePath)){
                logger.info("File is jar.");
                jars.add(filePath);
            } else if(isClass(filePath)){
                logger.info("File is class.");
                classes.add(filePath);
            } else {
                logger.info("File is not class, neither jar.");
            }
        }

        logger.debug("Jars:");
        jars.forEach(logger::debug);
        logger.debug("Classes:");
        classes.forEach(logger::debug);

        logger.info("Method ends.");
    }


    private void loadStandaloneClass(List<String> classPaths) {

        logger.info("Method called.");

        URLClassLoader classLoader = null;
        String actualDirPath = "";

        for(String filePath: classPaths) {
            logger.info("Raw class file path: " + filePath);

        /*
        this class path calculating is good, when i have classes without any package and without any structure
        but when i need to load structured system, structured class system, this is very bad path calculating

        String directoryPath = filePath.substring(0, filePath.lastIndexOf(File.separator)+1);
        String className = filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.lastIndexOf("."));
        */

        /*
        correct way to calculate class path :
         */

            String directoryPath = filePath.substring(0, filePath.indexOf(Constants.PATH_TO_CLASSES_DIR) + Constants.PATH_TO_CLASSES_DIR.length());
            String className = filePath.substring(filePath.indexOf(Constants.PATH_TO_CLASSES_DIR) + Constants.PATH_TO_CLASSES_DIR.length() + 1);
            className = className.substring(0, className.indexOf("."));
            className = className.replace(File.separator, ".");

            logger.info("Calcualted class path: " +
                    directoryPath +" : " +
                    className);
            try {
                if(!actualDirPath.equals(directoryPath)) {
                    logger.info("New dir path is calculated for ClassLoader: '" +directoryPath
                            +"' old one: '" +actualDirPath +"'.");
                    actualDirPath = directoryPath;

                    logger.info("Classloader is going to be initialized.");
                    classLoader = initClassLoader(directoryPath);
                    if(classLoader == null){
                        logger.fatal("Classloader is null -> could not be initialized. Nothing can be loaded.");
                        continue;
                    }else {
                        logger.info("Classloader is initialized.");
                    }
                }

                logger.info("Actual dir path for Classloader: " +directoryPath);

                logger.info("Class is going to be loaded: " + className);
                if(classLoader == null){
                    logger.fatal("Classloader is still null. Class can not be loaded.");
                    continue;
                }
                Class c = classLoader.loadClass(className);
                logger.info("Class is loaded: " + c.getName());

                allClasses.add(c);
                logger.info("Class is added to list");
            } catch (MalformedURLException e) {
                //TODO: rewrite this error message handling
                logger.error("MalformedURLException");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                //TODO: rewrite this error message handling
                logger.error("ClassNotFoundException");
                e.printStackTrace();
            }
        }

        logger.debug("Classes in list:");
        allClasses.forEach(logger::debug);

        logger.info("Method ends.");
    }

    private URLClassLoader initClassLoader(String path) throws MalformedURLException {
        logger.info("Method called.");
        logger.info("Path for classloader: " +path);

        URL[] urls = {null};

        if(path.endsWith(".jar")) {
            urls[0] = new URL("jar:file:" + path + "!/");
            logger.info("Will be created class loader for jar file.");
        }else{
            File file = new File(path);
            urls[0] = file.toURI().toURL();
            logger.info("Will be created class loader for directory.");
        }

        logger.info("Method ends.");
        return URLClassLoader.newInstance(urls);
    }


    private void loadClassesFromJar(List<String> jarPaths) {
        logger.info("Method called");

        for(String filePath: jarPaths) {
            logger.info("Loaded jar: " + filePath);
            try {

                //load jar file
                JarFile jar = new JarFile(filePath);

                //ged jar content as enumeration
                Enumeration e = jar.entries();

                //create classloader for loaded jar
                URLClassLoader cl = initClassLoader(filePath);

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
                    logger.info("Class is loaded: " +c.getName());

                    //adding class to class list for all loaded classes
                    allClasses.add(c);
                }

            } catch (IOException e) {
                //TODO: rewrite this error message handling
                logger.error("IOEXCEPTION");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                //TODO: rewrite this error message handling
                logger.error("CLASSNOTFOUNFEXCEPTION");
                e.printStackTrace();
            }
        }

        logger.debug("Loaded classes: ");
        allClasses.forEach(logger::debug);

        logger.info("Method ends.");
    }

    private boolean isClass(String filePath) {
        logger.info("Method called.");
        logger.info("Analyzed filename: " + filePath);
        if(filePath.endsWith(".class")) {
            logger.info("Result: true.");
            logger.info("Methods ends.");
            return true;
        }
        logger.info("Result: false.");
        logger.info("Methods ends.");
        return false;
    }

    private boolean isJar(String filePath) {
        logger.info("Method called.");
        logger.info("Analyzed filename: " + filePath);
        if(filePath.endsWith(".jar")) {
            logger.info("Result: true.");
            logger.info("Methods ends.");
            return true;
        }
        logger.info("Result: false.");
        logger.info("Methods ends.");
        return false;
    }
}
