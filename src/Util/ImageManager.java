package Util;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;

public class ImageManager {

    public static Image LoadImage (String imageName, Boolean Transparent){
        Image image;
        ImageIcon ii = new ImageIcon(imageName);
        Image imageTemp = ii.getImage();

        if (Transparent){
            image = Transparency.makeColorTransparent(imageTemp, Color.BLACK);

        }
        else{
            image = imageTemp;
        }
        return image;      
    }

    public static BufferedImage ImageToBufferedImage(Image image, int width, int height){
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }

    public static BufferedImage LoadBufferedImage(String imageName, Boolean Transparent){
        Image imageTemp = LoadImage(imageName,Transparent);
        BufferedImage bufferedImage = ImageToBufferedImage(imageTemp, imageTemp.getWidth(null), imageTemp.getHeight(null));
        return bufferedImage;
    }

    public static BufferedImage mergeImages(BufferedImage image, BufferedImage overlay, int x, int y){

        int w = Math.max(image.getWidth(), overlay.getWidth() + Math.abs(x));
        int h = Math.max(image.getHeight(), overlay.getHeight()+ Math.abs(y));
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(overlay, x, y, null);

        return combined;
    }

    public static BufferedImage rotateImage(BufferedImage image, int largeImageOffSet, double angle, int rotationPointX, int rotationPointY){

        BufferedImage largeImage = new BufferedImage(largeImageOffSet*2, largeImageOffSet*2, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = mergeImages(largeImage, image,largeImageOffSet,largeImageOffSet);
        AffineTransform transform = new AffineTransform();
        transform.rotate(angle, rotationPointX + largeImageOffSet, rotationPointY + largeImageOffSet);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        rotated = op.filter(rotated, null);

            //Graphics g = rotated.getGraphics();
            //g.drawImage(image, largeImageOffSet, largeImageOffSet,null);
        //g.rotate(Math.toRadians(angle),rotationPointX + largeImageOffSet, rotationPointY + largeImageOffSet);

        return rotated;
    }
    
    public static BufferedImage AddString(BufferedImage image, String string, int x, int y,int fontSize){
				
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.setFont(new Font("Serif",Font.BOLD,fontSize));

        g2.setColor(Color.WHITE);
        g2.drawString(string, x, y);
        g2.dispose();

        return newImage;
    }

}
