package sk.homisolutions.shotbox.snem.gui.webbasic;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.gui.webbasic.interfaces.PlatformCommunicator;
import sk.homisolutions.shotbox.tools.api.external.userinterface.GraphicalInterface;
import sk.homisolutions.shotbox.tools.api.internal.userinterface.GuiPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by homi on 11/1/16.
 */
public class GuiAdapter implements GraphicalInterface, PlatformCommunicator {
    private static final Logger logger = Logger.getLogger(GuiAdapter.class);
    private static GuiAdapter INSTANCE = null;

    private String pathToFileSpace = "";
    private String pathToWebApplication = "";

    private GuiPlatformProvider provider;
    private EmbeddedServer server;
    private StateManager stateManager;

    public GuiAdapter(){
        logger.info("GUI module instantiated");
        INSTANCE = this;
        stateManager = StateManager.getInstance();
    }

    public static GuiAdapter getINSTANCE(){
        if(INSTANCE == null){
            throw new SNEM_GUI_BasicWebImplementation_Exception("GuiAdapter for platform is not initialized. " +
                    "User Interface can not communicate with platform right now.");
        }
        return INSTANCE;
    }

    @Override
    public void setProvider(GuiPlatformProvider provider) {
        this.provider = provider;
        secondSetup();
    }

    @Override
    public void showPicture(TakenPicture picture) {
        stateManager.setStatePhotoProvided(picture, pathToWebApplication);
    }

    @Override
    public void platformIsBusy(String busyMessage) {
        stateManager.setStatePlatformIsBusy(busyMessage);
    }

    @Override
    public void platformIsReady() {
        stateManager.setStateReady();
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {
        if(message.isShutdown()){
            server.shutdown();
        }
    }

    @Override
    public void countdownStarts() {
        stateManager.setStateCountdown();
    }

    @Override
    public void allPicturesAreTaken() {
        stateManager.allPicturesAreTaken();
    }

    @Override
    public void run() {
    }

    @Override
    public void triggerTakingShot(){
        provider.takeShoot(this);
    }

    @Override
    public Long getMillisToTakingShot() {
        return provider.getMillisToTakingShot();
    }

    @Override
    public void userWantPicture(boolean decision, TakenPicture picture) {
        provider.pictureIsApproved(decision, picture);
    }

    private void secondSetup() {
        pathToFileSpace = provider.getPathToResourcesWorkingDirectory(this);
        createTempFileSpace();
        extractGuiToFileSpace();
        initializeServer();
    }

    private void createTempFileSpace() {
        File directory = new File(pathToFileSpace);
        if(!directory.exists()) {
            directory.mkdirs();
        }else{
            for(File f: directory.listFiles()){
                f.delete();
            }
            directory.mkdirs();
        }
    }

    private void extractGuiToFileSpace() {
        //locate packaged gui
        InputStream jarInput = getClass().getResourceAsStream("/webapp.zip");
        if(jarInput == null){
            throw new SNEM_GUI_BasicWebImplementation_Exception("GUI was not bundled with package." +
                    "Please, contact author of this module.");
        }

        //copy package with gui to file space
        try {
            Files.copy(jarInput, Paths.get(pathToFileSpace+"/webapp.zip"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new SNEM_GUI_BasicWebImplementation_Exception("GUI could not be extracted from module. " +
                    "Maybe there is no free space on disk?");
        }

        //update path to folder with unzipped gui
        pathToWebApplication = pathToFileSpace+"/webapp";

        //unzip gui
        try {
            ZipFile zip = new ZipFile(pathToFileSpace+"/webapp.zip");
            zip.extractAll(pathToWebApplication);
        } catch (ZipException e) {
            e.printStackTrace();
            throw new SNEM_GUI_BasicWebImplementation_Exception("GUI could not be unpacked.");
        }

        //delete zip
        File zipFile = new File(pathToFileSpace+"/webapp.zip");
        zipFile.delete();
    }

    private void initializeServer(){
        //create server
        server = new EmbeddedServer(pathToWebApplication);
    }
}
