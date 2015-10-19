package GamePackage;

import GamePackage.Item;
import Util.*;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Bullet {
	public Item weapon;
	
    public int x;
    public int y;
    public int bulletSpeed;
    public double dx;
    public double dy;
    
    public int relativeOffSet;
    
    public double angle;
    
    private String bulletSheetName = "bulletSheet.png";
    public String name;
    public Image bulletImage;
    public BufferedImage bulletSheet;

    public int width;
    public int height;
    
    public PlaceableObject pylon;

    public Bullet(int startX, int startY, int bulletSpeed, double angle, Item weapon, String name){
        this.name = name;
        bulletSheet = ImageManager.LoadBufferedImage(bulletSheetName, true);
        this.weapon = weapon;
        initProperties(startX, startY, bulletSpeed,angle);
        drawBullet();

    }

    public void initProperties(int startX, int startY, int bulletSpeed, double angle){
        x = startX;
        y = startY;
        this.bulletSpeed = bulletSpeed;
        this.angle = angle;
        relativeOffSet = 29;
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, 5, 5);
    }

    public void drawBullet() {
        BufferedImage tempImage = bulletSheet;
        switch(name){
            case "normal":
                tempImage = bulletSheet.getSubimage(0, 0, 5, 5);
                width = tempImage.getWidth();
                height = tempImage.getHeight();
                break;
            case "pylon":
                tempImage = bulletSheet.getSubimage(6, 0, 5, 5);
                width = tempImage.getWidth();
                height = tempImage.getHeight();
                break;
        }


        bulletImage = tempImage;
        //bulletImage =ImageManager.rotateImage(tempImage, width, angle, relativeOffSet, 1);

    }
}
