package sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem.gallery.EmbeddedServer;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageHandlerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by homi on 8/30/16.
 */
public class StoringService implements ImageHandler {
    private static final Logger logger = Logger.getLogger(StoringService.class);

    private ImageHandlerPlatformProvider provider;
    private EmbeddedServer server;
    private boolean serverIsSetUp = false;

    @Override
    public void setProvider(ImageHandlerPlatformProvider provider) {
        this.provider = provider;
        if(!serverIsSetUp){
            String pathToFileSpace = provider.getPathToResourcesWorkingDirectory(this);
            createTempFileSpace(pathToFileSpace);
            String pathToWebApplication = extractGuiToFileSpace(pathToFileSpace);
            initializeServer(pathToWebApplication);
        }
    }

    @Override
    public void handleImage(TakenPicture picture) {
        logger.info("storing picture");
        String filepath = resolveFilePath(picture);

        OutputStream out = null;
        try {
            logger.info("Storing picture to: " +filepath);
            out = new FileOutputStream(filepath);
            out.write(picture.getPicture());
            out.flush();
            logger.info("picture stored");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            provider.pictureIsAlreadyHandled(picture, this);
        }
    }

    private String resolveFilePath(TakenPicture picture) {
        String filename;
        if (picture.getFilename()==null || picture.getFilename().equals(""))
            filename = "photo_" + new SimpleDateFormat().format(new Date());
        else{
            filename = picture.getFilename();
        }

        filename += "."+picture.getType().getSuffix();
        filename = Constants.PathToDirectory + filename;

        return filename;
    }

    @Override
    public void run() {

    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    private void createTempFileSpace(String pathToFileSpace) {
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

    private String extractGuiToFileSpace(String pathToFileSpace) {
        //locate packaged gui
        InputStream jarInput = getClass().getResourceAsStream("/webapp.zip");
        if(jarInput == null){
            throw new RuntimeException("GUI was not bundled with package." +
                    "Please, contact author of this module.");
        }

        //copy package with gui to file space
        try {
            Files.copy(jarInput, Paths.get(pathToFileSpace+"/webapp.zip"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("GUI could not be extracted from module. " +
                    "Maybe there is no free space on disk?");
        }

        //update path to folder with unzipped gui
        String pathToWebApplication = pathToFileSpace+"/webapp";

        //unzip gui
        try {
            ZipFile zip = new ZipFile(pathToFileSpace+"/webapp.zip");
            zip.extractAll(pathToWebApplication);
        } catch (ZipException e) {
            e.printStackTrace();
            throw new RuntimeException("GUI could not be unpacked.");
        }

        //delete zip
        File zipFile = new File(pathToFileSpace+"/webapp.zip");
        zipFile.delete();

        return pathToWebApplication;
    }

    private void initializeServer(String pathToWebApplication){
        //create server
        server = new EmbeddedServer(pathToWebApplication);
        serverIsSetUp = true;
    }
}
