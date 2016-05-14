package sk.homisolutions.shotbox.librariesloader.classloading_system;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import sk.homisolutions.shotbox.librariesloader.settings.SystemSetup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by homi on 5/14/16.
 */
class FilesFilter {

    private static final Logger logger = Logger.getLogger(FilesFilter.class);

    public FilesFilter(){
        logger.info("Creating object FilesFilter");
        logger.info("Object FilesFilter is created");
    }

    public void filterPresentedFiles(List<String> allFiles, List<String> jars, List<String> classes) {
        logger.info("Method called.");

        if(jars == null || classes == null){
            logger.error("Some programmer did something wrong. Input arrays are null: " +
                    "jars-" +jars +";" +
                    "classes-" +classes + " " +
                    "Without this lists, method is useless, so application will terminate this process.");
            return;
        }

        if(allFiles == null){
            logger.error("Files' list is null. This should not happens. " +
                    "There is nothing to filter, so application will terminate this process.");
            return;
        }

        logger.debug("This files' paths are going to be filtered: ");
        allFiles.forEach(logger::debug);

        for (String filePath: allFiles){
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


    /*
    Until the getPresentedInterface will not be repaired, this method is useless too.
     */
    public List<Class> filterClasses(List<Class> allClasses) {
        logger.info("Method called.");

        List<Class> relevantClasses = new ArrayList<>();

        logger.info("Getting presented interfaces (api for external modules).");
        List<String> interfacesFullNames = getPresentedInterfaces();

        if(interfacesFullNames==null){
            logger.error("No interfaces (api) could be loaded. List is null. " +
                    "All classes will be marked as relevant. Filter process is quitting.");
            relevantClasses.addAll(allClasses);
            return relevantClasses;
        }
        if(interfacesFullNames.size()==0){
            logger.error("No interfaces (api) could be loaded. List is empty. " +
                    "All classes will be marked as relevant. Filter process is quitting.");
            relevantClasses.addAll(allClasses);
            return relevantClasses;
        }

        logger.info("Interfaces (api) are loaded. All loaded classes are going to be filtered. " +
                "Relevant classes list will be created.");

        logger.debug("All classes are going to be filtered by loaded interfaces. " +
                "Relevant classes list will be created.");

        /*
        Process of filtering classes by interfaces:

        For every loaded class, list of implemented interfaces is get.
        Every interface for every class is compared with every loaded interface from library, if there will be match.
        If yes, class implements expected interface and class is relevant. If class does not implement any of
        loaded interface from library, class is not relevant.

        So..... From All classes list is created stream and this stream is filtered. Filter decide,
        if class is relevant or not. If yes, class is added to collection, if no, class is not important.
        Created collection is added to relevant classes list.
        What filter does is described above.
         */

        relevantClasses.addAll(allClasses.stream()
                .filter(c -> {
                    logger.debug("Class " +c.getName() +" is going to be analyzed.");
                    logger.debug("Reading interfaces of this class (If class implements any interface) : ");
                    for(Class iface: c.getInterfaces()){
                        logger.debug("Analyzed interface: " +iface.getName());
                        if(interfacesFullNames.contains(iface.getName())){
                            logger.debug("Interface match with loaded interfaces list. " +
                                    "Class is relevant and will be added to relevant classes list.");
                            return true;
                        }else{
                            logger.debug("Interface did not match with loaded interfaces list.");
                        }
                    }
                    logger.debug("Analyzing class " +c.getName() +" finished.");
                    return false;
                } )
                .collect(Collectors.toList())
        );
        /*
        Filtering ends.
         */

        logger.info("Creating relevant loaded classes list is finished:");

        if(relevantClasses.size() == 0){
            logger.warn("Relevant classes list is empty. " +
                    "There is no class, which can be used and empty list will be returned.");
        }

        relevantClasses.forEach(logger::debug);

        logger.info("Method ends.");
        return relevantClasses;
    }

    private List<String> getPresentedInterfaces(){
        logger.info("Method called.");


        if(SystemSetup.PACKAGE_WITH_APIs == null || SystemSetup.PACKAGE_WITH_APIs.equals("")){
            logger.error("Path to package with interfaces is null or not defined. " +
                    "No interfaces will be loaded. Empty list will be returned.");
            return new ArrayList<>();
        }
        String packageWithInterfaces = SystemSetup.PACKAGE_WITH_APIs;

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
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageWithInterfaces))));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        /* and it looks awesome
        --------------------------------------------------------------------------------------------------------*/

        logger.debug("Loaded interfaces from: " + SystemSetup.PACKAGE_WITH_APIs);
        logger.debug("Number of interfaces: " +classes.size());
        logger.debug("Loaded interfaces list: ");
        classes.forEach(logger::debug);

        List<String> interfacesNames = new ArrayList<>();
        classes.forEach(x->{interfacesNames.add(x.getName());});


        logger.info("Resolved interfaces from: " + SystemSetup.PACKAGE_WITH_APIs);
        logger.info("Number of interfaces: " +interfacesNames.size());
        logger.info("Resolved interfaces list: ");
        interfacesNames.forEach(logger::info);

        logger.info("Method ends.");
        return interfacesNames;
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
