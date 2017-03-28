package sk.homisolutions.shotbox.snem.photofilter.sharpen;

import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by homi on 11/23/16.
 */
public class SharpenFilter implements ImageFilter {

    private ImageFilterPlatformProvider provider;

    public SharpenFilter(){
        System.out.println("Instantiate SharpenFilter");
    }

    @Override
    public void setProvider(ImageFilterPlatformProvider provider) {
        this.provider = provider;
        System.out.println("SharpenFilter is fully initialized.");
    }

    @Override
    public List<TakenPicture> applyFilter(List<TakenPicture> pictures) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"Sharpen filter received pictures");
        for (TakenPicture pic: pictures){
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"Sharpen filter: applying to " +pic.getFilename());
            try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(pic.getPicture()));

                //from https://examples.javacodegeeks.com/desktop-java/awt/image/sharpening-a-buffered-image/

                // A 3x3 kernel that sharpens an image
                Kernel kernel = new Kernel(3, 3, new float[]{
                        -1, -1, -1,
                        -1, 9, -1,
                        -1, -1, -1
                });
                BufferedImageOp op = new ConvolveOp(kernel);
                img = op.filter(img, null);


                ImageIO.write(img,"jpg",byteOut);
                byteOut.flush();
                pic.setPicture(byteOut.toByteArray());
                pic.setFilename(pic.getFilename()+"_filtered-sharpen");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"Sharpen filter: returning pictures.");
        return pictures;
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
