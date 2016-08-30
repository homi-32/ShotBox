package sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem;

import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageProcessor;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageProcessorPlatformProvider;
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
public class StoringService implements ImageProcessor {

    private ImageProcessorPlatformProvider provider;

    @Override
    public void setProvider(ImageProcessorPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void processImage(TakenPicture picture) {
        String filepath = resolveFilePath(picture);

        OutputStream out = null;
        try {
            out = new FileOutputStream(filepath);
            out.write(picture.getPicture());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}
