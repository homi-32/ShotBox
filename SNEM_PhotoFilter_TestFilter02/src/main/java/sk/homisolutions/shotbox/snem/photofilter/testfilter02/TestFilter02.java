package sk.homisolutions.shotbox.snem.photofilter.testfilter02;

import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by homi on 11/23/16.
 */
public class TestFilter02 implements sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter {

    private ImageFilterPlatformProvider provider;

    public TestFilter02(){
        System.out.println("Instantiate TestFilter02");
    }

    @Override
    public void setProvider(ImageFilterPlatformProvider provider) {
        this.provider = provider;
        System.out.println("TestFilter02 is fully initialized.");
    }

    @Override
    public List<TakenPicture> applyFilter(List<TakenPicture> pictures) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"TestFilter02 filter received pictures");
        for (TakenPicture pic: pictures) {
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "TestFilter02 filter: applying to " + pic.getFilename());
            pic.setFilename(pic.getFilename()+"_t-filter02");
        }
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"TEstFilter02 filter: returning pictures.");
        return pictures;
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
