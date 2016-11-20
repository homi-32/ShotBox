package sk.homisolutions.shotbox.snem.gui.webbasic;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.*;

/**
 * Created by homi on 11/12/16.
 */
public class TemporaryPicture {
    private static final Logger logger = Logger.getLogger(TemporaryPicture.class);
    private String localPathToPicture = "temp-picture";
    private String absolutePathToPicture;

    public TemporaryPicture(TakenPicture picture, String pathToResources){
        logger.info("New temporary picture will be created.");

        //setting file path for temporary picture
        if(!pathToResources.endsWith(File.separator)){
            pathToResources += File.separator;
        }
        absolutePathToPicture = pathToResources + localPathToPicture;
        logger.info("Path for temporary picture: '" +absolutePathToPicture +"'");

        //creating new temporary picture
        File file = new File(absolutePathToPicture);
        if(file.exists()){
            file.delete();
        }
        try(OutputStream out = new FileOutputStream(absolutePathToPicture)) {
            logger.info("Writing temporary picture on disk.");
            //writing new picture to file
            out.write(picture.getPicture());
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Temporary picture is now stored.");
    }

    public String getLocalPathToPicture() {
        return localPathToPicture;
    }

    public String getAbsolutePathToPicture() {
        return absolutePathToPicture;
    }
}
