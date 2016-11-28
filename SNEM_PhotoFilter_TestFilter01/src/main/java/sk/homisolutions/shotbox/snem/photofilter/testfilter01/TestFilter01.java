package sk.homisolutions.shotbox.snem.photofilter.testfilter01;

import sk.homisolutions.shotbox.tools.api.external.imageprocessing.*;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by homi on 11/23/16.
 */
public class TestFilter01 implements sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter {

    private ImageFilterPlatformProvider provider;

    public TestFilter01(){
        System.out.println("Instantiate TestFilter01");
    }

    @Override
    public void setProvider(ImageFilterPlatformProvider provider) {
        this.provider = provider;
        System.out.println("TestFilter01 is fully initialized.");
    }

    @Override
    public List<TakenPicture> applyFilter(List<TakenPicture> pictures) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"TestFilter01 filter received pictures");
        for (TakenPicture pic: pictures) {
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "TestFilter01 filter: applying to " + pic.getFilename());
            pic.setFilename(pic.getFilename()+"_t-filter01");
        }
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"TEstFilter01 filter: returning pictures.");
        return pictures;
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
