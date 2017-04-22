package sk.homisolutions.shotbox.snem.gui.webbasic;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.gui.webbasic.gpio_contorller.Gpio;
import sk.homisolutions.shotbox.snem.gui.webbasic.interfaces.PlatformCommunicator;
import sk.homisolutions.shotbox.tools.api.external.userinterface.GraphicalInterface;
import sk.homisolutions.shotbox.tools.api.internal.userinterface.GuiPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private Gpio gpioController;
    private StateManager stateManager;
    private List<TakenPicture> bufferedPhotos;

    public GuiAdapter(){
        logger.info("GUI module instantiated");
        INSTANCE = this;
        stateManager = StateManager.getInstance();
        bufferedPhotos = Collections.synchronizedList(new LinkedList<>());
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
        synchronized (GuiAdapter.class) {
            if (stateManager.getState() != GuiState.PHOTO_PROVIDED) {
                stateManager.setStatePhotoProvided(picture, pathToWebApplication);
                logger.info("Photo is sent to GUI: " +picture.getFilename());
            } else {
                //if there is too much pictures to show, some type of buffer is needed, like this one:
                bufferPicture(picture);
            }
        }
    }

    private void bufferPicture(TakenPicture picture) {
        logger.info("Photo is buffered: " + picture.getFilename());
        bufferedPhotos.add(picture);
        if(bufferedPhotos.size()==1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (bufferedPhotos.size() > 0) {
                        while (stateManager.getState() != GuiState.DECISION_PROVIDED) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!stateManager.arePicturesBlocked()) {
                            showPicture(bufferedPhotos.get(0));
                            bufferedPhotos.remove(0);
                        } else {
                            while (bufferedPhotos.size() > 0) {
                                logger.info("Buffered photo is thrown: " + bufferedPhotos.get(0).getFilename());
                                userWantPicture(false, bufferedPhotos.get(0));
                                bufferedPhotos.remove(0);
                            }
                        }
                    }
                }
            }).start();
        }
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
//        logger.fatal("1??????????????????????????????????????????????-switch should be called");
        if(gpioController != null){
            gpioController.showGuiScreen();
        }
    }

    @Override
    public void allPicturesAreTaken() {
        logger.info("notification from platform: all pictures are provided");
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
        synchronized (GuiAdapter.class) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    provider.pictureIsApproved(decision, picture);
                }
            }).start();
        }
    }

    private void secondSetup() {
        pathToFileSpace = provider.getPathToResourcesWorkingDirectory(this);
        createTempFileSpace();
        extractGuiToFileSpace();
        initializeServer();
        initializeGpioController();
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

    private void initializeGpioController(){
        try{
            gpioController = Gpio.getInstance();
        }catch (Throwable e){
            logger.error("Raspberry Pi GPIO is not available.");
            gpioController = null;
        }
    }
}
