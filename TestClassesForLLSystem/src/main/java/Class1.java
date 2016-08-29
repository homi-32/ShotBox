import sk.homisolutions.shotbox.tools.api.external.camera.SimpleCamera;
import sk.homisolutions.shotbox.tools.api.internal.camera.CameraPlatformProvider;

/**
 * Created by homi on 4/20/16.
 */
public class Class1 implements SimpleCamera{

    Class2 cl2 = new Class2();

    @Override
    public void takeShoot() {
        System.out.println("shotTaken with " +cl2.getBrand());
    }

    @Override
    public void setProvider(CameraPlatformProvider provider) {

    }

    @Override
    public void run() {

    }
}
