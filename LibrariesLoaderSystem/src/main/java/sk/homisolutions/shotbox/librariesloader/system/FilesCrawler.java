package sk.homisolutions.shotbox.librariesloader.system;

import sk.homisolutions.shotbox.librariesloader.constants.Constants;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by homi on 4/14/16.
 */
//TODO: protect all calls for null occurrence
class FilesCrawler {
    private static final Logger logger = Logger.getLogger(FilesCrawler.class);

    private List<String> allFilesInDir = null;
    private List<String> relevantFiles = null;

    public FilesCrawler(){
        logger.info("Object is being initialized.");
        logger.info("Initializing object ends.");
    }

    public List<String> getRelevantFilesFromDir(){
        logger.info("Method called.");

        logger.info("Getting files' list from folder.");
        getAllFilesFromDir();
        if(this.allFilesInDir == null){
            logger.fatal("Files' list from directory is null. There is some big issue, which needs to be solved. " +
                    "For application consistency, empty list will be created.");
            this.allFilesInDir = new ArrayList<>();
        }
        if(this.allFilesInDir.size() == 0){
            logger.warn("All files' list is loaded. This list is empty. There are no files in directory. " +
                    "Nothing will be processed or loaded.");
        }
        logger.info("Files's list is loaded. Files count is: " +allFilesInDir.size());

        logger.info("Filtering loaded files' list.");
        filterFiles();

        filesListsCheck();

        logger.info("Files filtered:");
        relevantFiles.forEach(logger::info);
        logger.info("Method ends.");
        return this.relevantFiles;
    }

    private void filesListsCheck() {
        logger.info("Checking files' lists begin.");

        if(this.relevantFiles == null){
            logger.fatal("Files list is null. There is some really ugly issue here. For application consistency, " +
                    "empty list will be generated.");
            this.relevantFiles = new ArrayList<>();
        }
        if(this.relevantFiles.size() == 0){
            logger.warn("Files list is consist, but is empty. Nothing will be loaded.");
        }

        if(this.allFilesInDir == null){
            logger.error("For some reason, all files list is null, but it should not be." +
                    "Application will not crash, list will not be used, but something is wrong here.");
        }

        logger.info("Checking files' lists finished. Data are consistent.");
        return;
    }

    /*
    TODO: rewrite this

    Method is actually badly broken. I need to rewrite it
     */
    public List<String> getPresentedInterfaces(){
        logger.info("Method called.");

        //TODO: clean this mess:

        //-------------------------------------------------------------------------------------------------//
        //TODO: tu je tento problem, uz absolutne netusim, ako to vyriesit
        /*
        Issue je take, ze nasledujuce objekty "pathToInterfacesFolder" nie su null, ked si spustim debug mod v Idei
         aaaale, ked si buildnem jarko, spustim ho, tak mi null davaju
         je jedno ci dam argument ".", alebo "./" alebo ""; vzdy mi vracaju null

         Co tymto chcel dosiahnut: potrebujem sa dostat do jarka, ktore spusta.
         Chcem citat obsah tohto jarka, aby som si nasiel svoju dependency kniznicu
         s interface-mi, aby som ich mohol nacitat a pracovat s nim
         */

//        URL pathToInterfacesFolder = this.getClass().getClassLoader().getResource("."/*"sk.homisolutions.shotbox"*//*Constants.PACKAGE_NAME_WITH_INTERFACES*/);
        /*
        URL pathToInterfacesFolder1 = ClassLoader.getSystemClassLoader().getResource(".");
        URL pathToInterfacesFolder2 = ClassLoader.getSystemResource(".");
        URL pathToInterfacesFolder3 = this.getClass().getClassLoader().getResource(".");
        URL pathToInterfacesFolder4 = this.getClass().getResource(".");
        System.out.println("FilesCrawler: getPresentedInterfaces: URL to interfaces folder: \""
                +pathToInterfacesFolder1 +"\"--\""
                +pathToInterfacesFolder2 +"\"--\""
                +pathToInterfacesFolder3 +"\"--\""
                +pathToInterfacesFolder4);

        nakoniec som toto riesenie zavrhol, lebo nefunguje po vytvoreni binarky (jarka)
        a uz fakt netusim precooooo!!!!!!!!!!

        TODO: bud to zmazat, alebo tie komentare aspon prelozit
        */
        //-------------------------------------------------------------------------------------------------//


        //-------------------------------------------------------------------------------------------------//
        /*
        This solution is from :
        http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
         */
        /*
        URL urlToExecutedJarFile = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        */


        /*
        Purpose is: find included interfaces (in maven as dependency), which are used as API for ShotBox modules
        I need to load this interfaces and compare them with interfaces implemented by loaded classes
        This comparison is used to filter loaded classes and saves references only to "main" classes,
        which i can instantiate (   Interface variable = new LoadedClass()   )
        and use them (   variable.useMethodDefinedByInterface()  )

        So I need to calculate some path to this interfaces, and this is only working solution, I could wrote:
         */
        /*
        String pathToInterfacesFolder = urlToExecutedJarFile.getPath()
                +File.separator
                +(Constants.PACKAGE_NAME_WITH_INTERFACES).replace(".",File.separator)
                ;
        */
        //-------------------------------------------------------------------------------------------------//


        //-------------------------------------------------------------------------------------------------//
        /* ten zvysok, ale som zistil, ze mi to asi netreba takto komplikovane riesit




        System.out.println("FilesCrawler: getPresentedInterfaces: parameter for finding URL of interfaces folder: " +pathToInterfacesFolder);

        File packageFolder = new File(pathToInterfacesFolder);

        List<String> interfacesPaths = crawlingDirectory(packageFolder);

        List<String> interfacesNames = getNameFromFilePath(interfacesPaths);

        ClassLoader cl = this.getClass().getClassLoader();
            interfacesNames = new ArrayList<>(interfacesNames.stream().filter(s -> {
                try {
                    return cl.loadClass(s).isInterface();
                } catch (ClassNotFoundException e) {
                    System.out.println("FilesCrawler: getPresentedInterfaces: ClassNotFoundException");
                    e.printStackTrace();
                }
                    return false;

            }).collect(Collectors.toList()));

        System.out.println("FilesCrawler: getPresentedInterfaces: loaded interfaces:");
        interfacesNames.forEach(System.out::println);
       */
        //-------------------------------------------------------------------------------------------------//


        /*
http://stackoverflow.com/questions/14314789/using-google-reflections-to-get-a-list-of-all-classes-but-java-seems-to-be

Google Reflections can be used to get all classes, including java.*, although it's not its primarily use.

Reflections reflections = new Reflections(
    ClasspathHelper.forClass(Object.class),
    new SubTypesScanner(false));
And than:

Set<String> allClasses =
    reflections.getStore().getSubTypesOf(Object.class.getName());
         */

        /*
        ------------------------------------------------------------------------------------------------------------
            This solutions is from:
            http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
        */
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(Constants.PACKAGE_NAME_WITH_INTERFACES))));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        /* and it looks awesome
        --------------------------------------------------------------------------------------------------------*/

        logger.debug("Loaded interfaces from: " +Constants.PACKAGE_NAME_WITH_INTERFACES);
        logger.debug("Number of interfaces: " +classes.size());
        logger.debug("Loaded interfaces list: ");
        classes.forEach(logger::debug);

        List<String> interfacesNames = new ArrayList<>();
        classes.forEach(x->{interfacesNames.add(x.getName());});


        logger.info("Resolved interfaces from: " +Constants.PACKAGE_NAME_WITH_INTERFACES);
        logger.info("Number of interfaces: " +interfacesNames.size());
        logger.info("Resolved interfaces list: ");
        interfacesNames.forEach(logger::info);

        logger.info("Method ends.");
        return interfacesNames;
    }

    private List<String> getNameFromFilePath(List<String> interfacesPaths) {
        logger.info("Method called.");

        List<String> ifacesNames = new ArrayList<>();

        for (String path: interfacesPaths) {
            logger.info("Analyzed path: " +path);

            String name = Constants.PACKAGE_NAME_WITH_INTERFACES + ""
                    +
                    path.substring(
                            path.lastIndexOf(Constants.PACKAGE_NAME_WITH_INTERFACES) + Constants.PACKAGE_NAME_WITH_INTERFACES.length() + 1,
                            path.lastIndexOf(".class")
                    ).replace(File.separator, ".");

            logger.info("Transformed to name: " +name);
            ifacesNames.add(name);
        }

        logger.debug("Files in list:");
        ifacesNames.forEach(logger::debug);

        logger.info("Method ends.");
        return ifacesNames;
    }

    private void filterFiles() {
        logger.info("Method called.");
        this.relevantFiles = new ArrayList<>();

        if(this.allFilesInDir == null){
            logger.fatal("For some crazy reason, files' list from directory is null. " +
                    "Something went really badly wrong. So, there is nothing to filter. " +
                    "Application will break this process and create empty files' list for consistency.");
            return;
        }

        logger.info("Files are being filtered.");
        this.relevantFiles.addAll(this.allFilesInDir.stream()
                .filter(s -> s.endsWith(".class") || s.endsWith(".jar"))
                .collect(Collectors.toList()));
        logger.info("Files are now filtered.");

        if(relevantFiles.size() == 0){
            logger.warn("After filtering, there are no relevant files presented in directory. " +
                    "There is no library or class file, that can be loaded by application.");
        }

        logger.debug("Files' list:");
        this.relevantFiles.forEach(logger::debug);

        logger.info("Method ends.");
    }

    private void getAllFilesFromDir(){
        logger.info("Method called.");
        logger.info("Scanning files in \"" + Constants.PATH_TO_CLASSES_DIR +"\" started.");

        File folder = new File(Constants.PATH_TO_CLASSES_DIR);
        if(folder == null){
            logger.fatal("Directory path is wrong or null. Application could not open the directory and read files. " +
                    "Application will now interrupt scanning process");
            return;
        }


        logger.info("Crawling directory: " +folder.getAbsolutePath());

        this.allFilesInDir = crawlingDirectory(folder);
        if(this.allFilesInDir == null){
            logger.error("Something went wrong, while scanning files in directory. " +
                    "Returned list with files' path is null. For application consistency, empty list will be created, " +
                    "but nothing will be processed, or loaded.");
            this.allFilesInDir = new ArrayList<>();
        }
        if(this.allFilesInDir.size() == 0){
            logger.warn("Returned files list is empty. There are no files, " +
                    "which can be processed or loaded by application. Directory with libraries is probably empty");
        }

        logger.info("Found " +this.allFilesInDir.size() +" files: ");
        allFilesInDir.forEach(logger::info);

        logger.info("Method ends.");
    }

    private List<String> crawlingDirectory(File folder){
        logger.info("Method called.");

        List<String> fileNames = new ArrayList<>();

        if(folder == null){
            logger.error("Input File object is null. There is some issue in application. " +
                    "Empty list will be returned for application consistency.");
            return fileNames;
        }

        logger.info("Crawling directory:" +folder.getAbsolutePath());

        logger.info("Founded " +folder.listFiles().length +" files." );
        Arrays.stream(folder.listFiles()).forEach(x -> logger.debug(x.getName()));

        for (File fileEntry: folder.listFiles()){
            //String prefix = folder.getAbsolutePath() + File.separator ; //"." + File.separator;

            logger.info("Analyzing '" +fileEntry +"' file.");
            if(fileEntry.isDirectory()){
                //crawlingDirectory(folder).forEach(x -> {fileNames.add(prefix +fileEntry.getName() +File.separator +x);});
                logger.info("File is a directory. Directory is being crawled.");
                crawlingDirectory(fileEntry).forEach(x -> fileNames.add(x));
            }else {
                logger.info("File's path is added to list.");
                fileNames.add(fileEntry.getAbsolutePath());
            }
        }

        logger.debug("Founded files in application for directory '" +folder.getAbsolutePath() +"': ");
        fileNames.forEach(logger::debug);

        logger.info("Method ends.");
        return fileNames;
    }
}

