package sk.homisolutions.shotbox.snem.photofilter.median;

import sk.homisolutions.shotbox.tools.api.external.imageprocessing.ImageFilter;
import sk.homisolutions.shotbox.tools.api.internal.imageprocessor.ImageFilterPlatformProvider;
import sk.homisolutions.shotbox.tools.models.ShotBoxMessage;
import sk.homisolutions.shotbox.tools.models.TakenPicture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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
public class MedianFilter implements ImageFilter {

    private ImageFilterPlatformProvider provider;

    public MedianFilter(){
        System.out.println("Instantiate MedianFilter");
    }

    @Override
    public void setProvider(ImageFilterPlatformProvider provider) {
        this.provider = provider;
        System.out.println("MedianFilter is fully initialized.");
    }

    @Override
    public List<TakenPicture> applyFilter(List<TakenPicture> pictures) {
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"Median filter received pictures");
        for (TakenPicture pic: pictures){
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"Median filter: applying to " +pic.getFilename());
            try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(pic.getPicture()));

                //filter from https://github.com/praserocking/MedianFilter/blob/master/MedianFilter.java
                Color[] pixel=new Color[9];
                int[] R=new int[9];
                int[] B=new int[9];
                int[] G=new int[9];
                for(int i=1;i<img.getWidth()-1;i++)
                    for(int j=1;j<img.getHeight()-1;j++) {
                        pixel[0]=new Color(img.getRGB(i-1,j-1));
                        pixel[1]=new Color(img.getRGB(i-1,j));
                        pixel[2]=new Color(img.getRGB(i-1,j+1));
                        pixel[3]=new Color(img.getRGB(i,j+1));
                        pixel[4]=new Color(img.getRGB(i+1,j+1));
                        pixel[5]=new Color(img.getRGB(i+1,j));
                        pixel[6]=new Color(img.getRGB(i+1,j-1));
                        pixel[7]=new Color(img.getRGB(i,j-1));
                        pixel[8]=new Color(img.getRGB(i,j));
                        for(int k=0;k<9;k++){
                            R[k]=pixel[k].getRed();
                            B[k]=pixel[k].getBlue();
                            G[k]=pixel[k].getGreen();
                        }
                        Arrays.sort(R);
                        Arrays.sort(G);
                        Arrays.sort(B);
                        img.setRGB(i,j,new Color(R[4],B[4],G[4]).getRGB());
                    }
                ImageIO.write(img,"jpg",byteOut);
                byteOut.flush();
                pic.setPicture(byteOut.toByteArray());
                pic.setFilename(pic.getFilename()+"_filtered with median");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())+"Median filter: returning pictures.");
        return pictures;
    }

    @Override
    public void receiveGlobalMessage(ShotBoxMessage message) {

    }

    @Override
    public void run() {

    }
}
