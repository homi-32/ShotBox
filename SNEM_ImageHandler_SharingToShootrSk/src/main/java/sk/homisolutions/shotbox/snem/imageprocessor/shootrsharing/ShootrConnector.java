package sk.homisolutions.shotbox.snem.imageprocessor.shootrsharing;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageHandler;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageHandlerPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.io.IOException;

/**
 * Created by homi on 3/31/17.
 */
public class ShootrConnector implements ImageHandler {

    private ImageHandlerPlatformProvider provider;
    private static Logger logger = Logger.getLogger(ShootrConnector.class);

    public ShootrConnector(){
        logger.info("Initializing ShootrModule");
    }

    @Override
    public void setProvider(ImageHandlerPlatformProvider provider) {
        this.provider = provider;
    }

    @Override
    public void handleImage(TakenPicture picture) {
        if(picture.getType().getSubtype().toLowerCase().equals("jpg")
                || picture.getType().getSubtype().toLowerCase().equals("jpeg")
                ||picture.getType().getSubtype().toLowerCase().equals("png")){
            sendToShootr(picture);
        }else {
            logger.error("Photo format is not supported by Shootr.sk: " +picture.getType().getMediaType() +"/" +picture.getType().getSubtype() +
                    ". Supported are only: image/jpg; image/jpeg; image/png");
        }
    }

    //using tutorial: http://www.baeldung.com/httpclient-multipart-upload
    private void sendToShootr(TakenPicture picture) {
        logger.fatal("*******************Sending request to Shootr.sk");
        logger.fatal(picture.getType().getSubtype());
        CloseableHttpClient client = HttpClients.createDefault();
        String shotBoxID = "qwertyuiopqwertyuisdfghjk";
        String url = "http://matuso77.myqnapcloud.com:8000/api/v1/shotbox/"+shotBoxID+"/photo";
        logger.fatal("Sending data to: " +url);
        HttpPost post = new HttpPost(url);//"http://api.shootr.sk/api/v1/shotbox/"+shotBoxID+"/photo");
        String message = "Sending photo by ShotBox Shootr module";
//
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file", picture.getPicture(), ContentType.create("image/"+picture.getType().getSubtype().toLowerCase()), picture.getFilename());
//        builder.addTextBody("uploading photo", message, ContentType.TEXT_PLAIN);
//
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try {
            HttpResponse response = client.execute(post);
            logger.error("Request na Shootr.sk nespadol: " +response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("chyba pri posielani na Shootr.sk");
        }

    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
