import sk.homisolutions.shotbox.camera.Camera;

/**
 * Created by homi on 4/20/16.
 */
public class Class1 implements Camera{

    Class2 cl2 = new Class2();

    public void takeShot() {
        System.out.println("shotTaken with " +cl2.getBrand());
    }
}
