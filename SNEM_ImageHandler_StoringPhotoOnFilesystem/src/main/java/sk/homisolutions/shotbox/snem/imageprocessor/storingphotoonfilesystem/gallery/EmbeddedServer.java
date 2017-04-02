package sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem.gallery;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sk.homisolutions.shotbox.snem.imageprocessor.storingphotoonfilesystem.Constants;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by homi on 11/1/16.
 */
public class EmbeddedServer {
    private Logger logger = Logger.getLogger(EmbeddedServer.class);

    private static Integer port = 6414;
    private static String domain = "";
    private static String BASE_URI = "";
    private static final String GALLERY_SUFFIX = "/gallery/";
    private static final String PHOTOS_SUFFIX = "/photos/";
    private static String URL_TO_GALLERY = "";
    private String pathToWebApplication;

    private ResourceConfig config;
    private HttpServer server;

    public EmbeddedServer(String pathToWebApplication){
        this.pathToWebApplication = pathToWebApplication;

        determineHostIpAddress();
        chooseFreePort();
        createUrlAddress();
        adjustWebApplicationForDeployment();
        setupServerConfig();
        createServer();
        startServer();
        setupServer();
    }

    private void startServer() {
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createServer() {
        URI uri = URI.create(BASE_URI);
        server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
        logger.fatal("Gallery Server starts. Photos are available on address: \n" +uri.toString());
    }

    private void setupServerConfig() {
        config = new ResourceConfig();
        config.register(GalleryService.class);
    }

    private void createUrlAddress() {
        BASE_URI = "http://"+domain+":"+port+"/";
    }

    private void determineHostIpAddress() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(ifaces)){
                Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
//                    if(inetAddress.getHostName()){
//                        domain = inetAddress.getHostAddress();
//                        break;
//                    }
                    if(!inetAddress.getHostAddress().startsWith("127.") && isIpV4Address(inetAddress.getHostAddress())
                            //for GOPRO address
                            && !inetAddress.getHostAddress().startsWith("10.5.5.")
                            //for Shotbox Prototype
//                            && iface.getName().equals("wlan0")
                            ){
                        domain = inetAddress.getHostAddress();
                        break;
                    }
                }
                if(!domain.equals("")){
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    private boolean isIpV4Address(String hostAddress) {
        for(Character c: hostAddress.toCharArray()){
            if(Character.isLetter(c))
                return false;
            if(!Character.isDigit(c) && !c.equals('.'))
                return false;
        }
        return true;
    }

    private void adjustWebApplicationForDeployment() {
        File jsScript = new File(pathToWebApplication +File.separator +"src"+File.separator+"constants.js");
        if(!jsScript.exists()){
            throw new RuntimeException("Server can not locate GUI on file space");
        }
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(jsScript.getAbsolutePath()));
            String sourceCode = new String(encoded, StandardCharsets.UTF_8);
            sourceCode = sourceCode.replace("INSERT_IP_HERE", domain);
            sourceCode = sourceCode.replace("INSERT_PORT_HERE", String.valueOf(port));
            Files.write(Paths.get(jsScript.getAbsolutePath()), sourceCode.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseFreePort() {
        try {
            ServerSocket socket = new ServerSocket(port);
            port = socket.getLocalPort();
            socket.close();
            logger.info("Port " +port +" will be used.");
        } catch (BindException e){
            logger.error("Port "+port+" is already in use. New port will be chosen.");
            port = 0;
            chooseFreePort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        server.shutdown();
    }

    private void setupServer() {
        //setup index html to server as static handler
        StaticHttpHandler webAppHandler = new StaticHttpHandler(pathToWebApplication);
        //there is need to remove lock on static route for files
        //http://stackoverflow.com/questions/13307489/grizzly-locks-static-served-resources
        webAppHandler.setFileCacheEnabled(false);
        File photos = new File(Constants.PathToDirectory);
        StaticHttpHandler photosHandler = new StaticHttpHandler(photos.getAbsolutePath());
        server.getServerConfiguration().addHttpHandler(webAppHandler, GALLERY_SUFFIX);
        server.getServerConfiguration().addHttpHandler(photosHandler, PHOTOS_SUFFIX);
        URL_TO_GALLERY =BASE_URI+ GALLERY_SUFFIX;
    }

    public static String getDomain(){
        return BASE_URI;
    }
}
