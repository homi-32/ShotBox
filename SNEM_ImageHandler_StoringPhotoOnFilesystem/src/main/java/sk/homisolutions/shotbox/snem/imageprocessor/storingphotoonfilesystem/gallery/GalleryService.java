package sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem.gallery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by homi on 3/20/17.
 */
@Path("/api/")
public class GalleryService {
    private static final Logger logger = Logger.getLogger(GalleryService.class);
    private String prefix = "photos/";

    @GET
    @Path("getPhotoPaths")
    @Produces({MediaType.APPLICATION_JSON})
    public String getPhotoPaths(){
        File photosDirectory = new File("./photos");
        List<String> paths = new ArrayList<>();
//        logger.fatal("get photos called");
        String domain = EmbeddedServer.getDomain() + prefix;
        if(photosDirectory.isDirectory()){
            for(int i = 0; i<photosDirectory.list().length; i++){
                String url = domain + photosDirectory.list()[i];
//                logger.fatal(url);
                paths.add(url);
            }
        }else{
//            logger.error("No Photos are present.");
        }

        try {
            return new ObjectMapper().writeValueAsString(paths);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while parsing List<String> to json");
        }
    }
}
