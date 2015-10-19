package GamePackage;

import Util.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Block {
	
    public String imageName = "block.png";
    public Image image;
    public int x = 0;
    public int y = 0;
    public int arrayX ;
    public int arrayY ;
    public boolean visible;
    public int width;
    public int height;
    public String type;
    public int factor;
    
    public int dimX;
    public int dimY;

    public static String[] blockNames ={
        "Brick",
        "Dirt",
        "Grass",
        "Pink",
        "Wood",
        "Barricade",
        "Platform",
        "Void",
        "WoodenCase1",
        "WoodenCase2",
        "WoodenCase3",
        "RedPylon1",
        "RedPylon2",
        "RedPylon3",
        "RedTile",
        "BlueTile",
        "GreyTile",
        "DarkWood",
        "RedWood",
        "Smoke"
    };

     public Block(String type) {

            //ImageIcon ii = new ImageIcon(imageName);
        //Image imageTemp = ii.getImage();
        //BufferedImage imageTempB = Transparency.ImageToBufferedImage(imageTemp, imageTemp.getWidth(null), imageTemp.getHeight(null));
        //BufferedImage imageTempB = ImageManager.LoadBufferedImage(imageName, true);
        this.type = type;
        factor = getFactor(type);
        //image = imageTempB.getSubimage(0,10*factor, 10, 10); //Transparency.makeColorTransparent(imageTempB.getSubimage(0,10*blocktype, 10, 10), Color.black);
        dimX = 1;
        dimY = 1;
        
        if (type.startsWith("WoodenCase")){
            dimX = 2;
            dimY = 2;
        }
        width = 10;
        height = 10;
        //missiles = new ArrayList<Missile>();
        visible = true;
        x = 0;
        y = 0;

    }
	 
    public Rectangle getBounds() {
        return new Rectangle(x+1, y+1, width - 2, height - 2);
    }
            
    public int getFactor(String name){
        for (int i = 0; i < blockNames.length; i++){
            if (name.equalsIgnoreCase(blockNames[i])){
                factor = i;
            }
        }
        return factor;
    }
}
