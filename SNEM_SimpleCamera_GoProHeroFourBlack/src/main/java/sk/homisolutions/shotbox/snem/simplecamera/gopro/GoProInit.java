package sk.homisolutions.shotbox.snem.simplecamera.gopro;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.*;

/**
 * Created by homi on 8/23/16.
 */
public class GoProInit {

    private Client client;

    public GoProInit(){
        client = ClientBuilder.newClient();
    }

    //solution from: http://stackoverflow.com/questions/19291814/get-mac-address-in-java
    private byte[] getGoproMacAddress() throws UnknownHostException, SocketException {
        InetAddress ip = InetAddress.getByName(Constants.GOPRO_HERO4_IP);
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();
        return mac;
    }

    //method write with help from: http://www.jibble.org/wake-on-lan/
    //almost totally copied
    public void wakeUpGopro() throws SocketException, UnknownHostException {
        byte[] macAddress = getGoproMacAddress();
//        byte[] macInBytes = getMacBytes(this.macAddress);

        byte[] bytes = new byte[6 + 16 * macAddress.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macAddress.length) {
            System.arraycopy(macAddress, 0, bytes, i, macAddress.length);
        }

        DatagramSocket socket = null;
        try {
            InetAddress address = InetAddress.getByName(Constants.GOPRO_HERO4_IP);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, Constants.GOPRO_HERO4_PORT_FOR_WoL);
            socket = new DatagramSocket();
            socket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(socket!=null){
                socket.close();
            }
        }


    }

    //should be this process part of my application, or not ? i think, probably not, but who knews
    public void connectToGoPro() throws SocketException, UnknownHostException {
        wakeUpGopro();
        if(!isPlatformAutorizedToGopro())
            authorizeToGoPro();
    }

    private boolean isPlatformAutorizedToGopro() {
        WebTarget target = client.target(Constants.GET_CAMERA_STATUS);
        Response response = target.request().get();

        if(response.getStatus() == Response.Status.OK.getStatusCode())
            return true;
        else
            return false;
    }

    //do i need this?? answer is: yeah, i have to authorize device connecting to gopro
    //video tutorial: https://www.youtube.com/watch?v=4BH59qEeQUg  - this is only for iphones, android does not support this
    //okey, taakze taka vec, nebudem riesit pripojenie na gopro wifi, to nech si riesi pouzivatel sam, aale, je potrebne parovat, takze to sa bude riesit
    private void authorizeToGoPro(){
        WebTarget target = client.target(Constants.GOPRO_PAIRING_START);
        Response response = target.request().get();

        if (response.getStatus() != Response.Status.OK.getStatusCode())
            throw new SNEM_SimpleCamera_GoPro_Exception("Response code is not 200, but: " +response.getStatus());

        target = client.target(Constants.GOPRO_PAIRING_FINISH);
        response = target.request().get();

        if (response.getStatus() != Response.Status.OK.getStatusCode())
            throw new SNEM_SimpleCamera_GoPro_Exception("Response code is not 200, but: " +response.getStatus());
    }

    //maybe i dont need it now
    //totally copied from: http://www.jibble.org/wake-on-lan/WakeOnLan.java
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


