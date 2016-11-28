package sk.homisolutions.shotbox.snem.photofilter.testfilter03;

import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by homi on 11/23/16.
 */
public class TestFilter03 implements sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter {

    private ImageFilterPlatformProvider provider;

    public TestFilter03(){
        System.out.println("Instantiate TestFilter03");
    }

    @Override
    public void setProvider(ImageFilterPlatformProvider provider) {
        this.provider = provider;
        System.out.println("TestFilter03 is fully initialized.");
    }

    @Override
    public List<TakenPicture> applyFilter(List<TakenPicture> pictures) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"TestFilter03 filter received pictures");
        for (TakenPicture pic: pictures) {
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "TestFilter03 filter: applying to " + pic.getFilename());
            pic.setFilename(pic.getFilename()+"_t-filter03");
        }
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"TEstFilter03 filter: returning pictures.");
        return pictures;
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
