package sk.homisolutions.shotbox.snem.advancedcamera.gopro;

import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import java.net.*;

/**
 * Created by homi on 10/9/16.
 */
public class GoProConnectionHelper {
    private static final Logger logger = Logger.getLogger(GoProSetupHelper.class);
    private static final GoProConnectionHelper INSTANCE = new GoProConnectionHelper();

    private GoProConnectionHelper(){
        //singleton
    }

    public static GoProConnectionHelper getInstance(){
        return INSTANCE;
    }

    public boolean checkResponseStatus(Response response) {
        logger.info("check response");
        if(response.getStatus() >= 200 && response.getStatus() < 300 /*!= Response.Status.OK.getStatusCode()*/) {
            logger.info("OK");
            return true;
        }else{
            logger.error("NOT OK");
            logger.error("Response code is not 200, but: " + response.getStatus());
            throw new SNEM_SimpleCamera_GoPro_Exception("Response code is not 200, but: " + response.getStatus());
//            return false;
        }
    }

    //not working....
    public void sendWakeUpPacket(){
        try {
            wakeUpGopro();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    //totaly coppied
    public void wakeUpGopro() throws SocketException, UnknownHostException {
        logger.info("Waking up GoPro camera");
        byte[] macInBytes = getMacBytes(Constants.GOPRO_HERO4_MAC);

        byte[] bytes = new byte[6 + 16 * macInBytes.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macInBytes.length) {
            System.arraycopy(macInBytes, 0, bytes, i, macInBytes.length);
        }

        DatagramSocket socket = null;
        try {
            InetAddress address = InetAddress.getByName(Constants.GOPRO_HERO4_IP);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, Constants.GOPRO_HERO4_PORT_FOR_WoL);
            socket = new DatagramSocket();
            socket.send(packet);
            logger.info("GoPro camera is wake up");
        } catch (Exception e) {
            logger.error("Waking GoPro up was not successfull: Exception occured");
            e.printStackTrace();
        } finally {
            if(socket!=null){
                socket.close();
            }
        }
    }

    //totaly coppied
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
