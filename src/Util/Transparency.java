package Util;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class Transparency {
	 public static Image makeColorTransparent
	    (Image im, final Color color) {
		BufferedImage imb = ImageManager.ImageToBufferedImage(im,im.getWidth(null),im.getHeight(null));
	    ImageFilter filter = new RGBImageFilter() {
	      public int markerRGB = color.getRGB() | 0xFF000000;

	      public final int filterRGB(int x, int y, int rgb) {
	        if ( ( rgb | 0xFF000000 ) == markerRGB ) {
	          return 0x00FFFFFF & rgb;
	          }
	        else {
	          return rgb;
	          }
	        }
	      }; 

	    ImageProducer ip = new FilteredImageSource(imb.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(ip);
	}

}