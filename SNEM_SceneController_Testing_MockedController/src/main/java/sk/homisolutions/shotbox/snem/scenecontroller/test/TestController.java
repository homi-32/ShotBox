package sk.homisolutions.shotbox.snem.scenecontroller.test;

import sk.homisolutions.shotbox.tools.api.external.scene.SceneController;
import sk.homisolutions.shotbox.tools.api.internal.scene.ScenePlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;

/**
 * Created by homi on 11/23/16.
 */
public class TestController implements SceneController {

    private ScenePlatformProvider p;

    @Override
    public void setProvider(ScenePlatformProvider provider) {
        p=provider;
    }

    @Override
    public void setupScene() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("scene is set up");
        p.sceneIsSetUp(this);
    }

    @Override
    public void releaseScene() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("scene is released");
        p.sceneIsReleased(this);
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
