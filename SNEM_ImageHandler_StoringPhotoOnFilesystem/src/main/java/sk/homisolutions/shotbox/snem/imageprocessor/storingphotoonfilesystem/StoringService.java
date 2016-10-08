package sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageHandlerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by homi on 8/30/16.
 */
public class StoringService implements ImageHandler {
    private static final Logger logger = Logger.getLogger(StoringService.class);

    private ImageHandlerPlatformProvider provider;

    @Override
    public void setProvider(ImageHandlerPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void handleImage(TakenPicture picture) {
        logger.fatal("storing picture");
        String filepath = resolveFilePath(picture);

        OutputStream out = null;
        try {
            logger.fatal("Storing picture to: " +filepath);
            out = new FileOutputStream(filepath);
            out.write(picture.getPicture());
            out.flush();
            logger.fatal("picture stored");
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
}
