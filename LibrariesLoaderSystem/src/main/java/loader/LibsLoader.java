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

        
        
        logger.info("Loading presented jars will now start.");
        this.loadClassesFromJar(jarFiles);
        logger.info("All presented jars are now loaded in application.");
        logger.info("Loading standalone classes will now start.");
        this.loadStandaloneClass(classFiles);
        logger.info("All standalone classes are now loaded in application.");

        if(allClasses.size() == 0){
            logger.warn("List with loaded classes is empty. Nothing was loaded. " +
                    "This empty list will be returned.");
            this.relevantClasses.addAll(this.allClasses);
            return this.relevantClasses;
        }

        logger.info("All loaded classes:");
        allClasses.forEach(x-> logger.info(x.getName()));

        logger.info("All loaded classes are going to be filtered.");
        filterClasses();
        logger.info("Loaded classes are now filtered.");

        if(relevantClasses.size() == 0){
            logger.warn("List with loaded relevant classes is empty. There are no loaded classes, which could be " +
                    "used by application.");
        }else {
            relevantClasses.forEach(x -> logger.info(x.getName()));
        }

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

        if(interfacesFullNames==null){
            logger.error("No interfaces (specification) could be loaded. List is null. " +
                    "All classes will be marked as relevant. Filter process is quitting.");
            this.relevantClasses.addAll(allClasses);
            return;
        }
        if(interfacesFullNames.size()==0){
            logger.error("No interfaces (specification) could be loaded. List is empty. " +
                    "All classes will be marked as relevant. Filter process is quitting.");
            this.relevantClasses.addAll(allClasses);
            return;
        }

        logger.info("Interfaces (specification) are loaded. All loaded classes are going to be filtered. " +
                "Relevant classes list will be created.");
        this.relevantClasses.addAll(allClasses.stream()
                    .filter(c -> interfacesFullNames.contains(c.getName()))
                    .collect(Collectors.toList())
        );


        logger.info("Creating relevant loaded classes list finished.");
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

        if(classPaths == null){
            logger.error("Input list with classes' path is null. Something is wrong and needs to debug. " +
                    "There is nothing to load, so application is going to terminate this process.");
            return;
        }
        if(classPaths.size() == 0){
            logger.warn("Input list with classes' path is empty. There is nothing to load." +
                    "Application will end this process.");
            return;
        }

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
                        logger.error("Classloader is null -> could not be initialized. Nothing can be loaded.");
                        continue;
                    }else {
                        logger.info("Classloader is initialized.");
                    }
                }

                if(classLoader==null){
                    logger.error("Classloader is not initialized. Probable reason is wrong path to directory " +
                            "with class. Without classloader and correct path to class, nothing can be loaded. " +
                            "This class will be skipped.");
                    continue;
                }

                if(actualDirPath==null || actualDirPath.equals("")){
                    logger.error("Path to directory with classes is not valid: " +actualDirPath
                            +". Nothing will be loaded. This class is skipped.");
                    continue;
                }

                logger.info("Actual dir path for Classloader: " +actualDirPath);

                logger.info("Class is going to be loaded: " + className);


                Class c = classLoader.loadClass(className);
                logger.info("Class is loaded: " + c.getName());

                if(c == null){
                    logger.error("Class could not be loaded. Probable reason is wrong class name. " +
                            "Class is skipped.");
                    continue;
                }

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

        if(path == null){
            logger.error("Input path is null. No Classloader will be initialized. Quitting this action." +
                    "Null will be returned.");
            return null;
        }
        if (path.equals("")){
            logger.error("Input path is empty. No Classloader will be initialized. Quitting this action." +
                    "Null will be returned.");
            return null;
        }

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

        if(urls[0] == null){
            logger.error("Path is not valid for creating classloader. Null will be returned.");
            return null;
        }

        logger.info("Classloader is going to be created.");
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        if(loader == null){
            logger.error("Classloader is null. Address was probably wrong. Method is returning null");
            return null;
        }
        logger.info("Classloader created.");

        logger.info("Method ends.");
        return loader;
    }


    private void loadClassesFromJar(List<String> jarPaths) {
        logger.info("Method called");

        if(jarPaths == null){
            logger.error("Input list with jars' path is null. Something is wrong and needs to debug. " +
                    "There is nothing to load, so application is going to terminate this process.");
            return;
        }
        if(jarPaths.size() == 0){
            logger.warn("Input list with jars' path is empty. There is nothing to load." +
                    "Application will end this process.");
            return;
        }

        for(String filePath: jarPaths) {
            logger.info("Loaded jar: " + filePath);
            try {

                logger.info("Jar file is going to be loaded.");
                //load jar file
                JarFile jar = new JarFile(filePath);
                if(jar == null){
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
                if(cl==null){
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
                    logger.info("Class is loaded: " +c.getName());

                    //adding class to class list for all loaded classes
                    logger.info("Class will be added to list with all classes.");
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
        if(filePath == null || filePath.equals("")){
            logger.error("Input filepath is not valid: " +filePath);
            return false;
        }
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
        if(filePath == null || filePath.equals("")){
            logger.error("Input filepath is not valid: " +filePath);
            return false;
        }
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
