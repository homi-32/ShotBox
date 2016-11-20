package sk.homisolutions.shotbox.snem.gui.webbasic.services;

import org.apache.log4j.Logger;
import sk.homisolutions.shotbox.snem.gui.webbasic.EmbeddedServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created by homi on 11/7/16.
 */
@Path("/")
public class RedirectService {

    private static final Logger logger = Logger.getLogger(RedirectService.class);

    @GET
    public Response redirectToIndexHtml(){
        logger.info("User called server root URL address '"+EmbeddedServer.getDomain()
                +"'. User is redirected to GUI: " +EmbeddedServer.getUrlToGui());
        return Response.seeOther(URI.create(EmbeddedServer.getUrlToGui())).build();
    }
}
