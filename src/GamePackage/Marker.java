
package GamePackage;

import Util.ImageManager;
import java.awt.Image;
import java.awt.Rectangle;

public class Marker {
    
    public int posX;
    public int posY;
    public String name;
    public String imageName;
    public Image image;
    public static String[] markerNames = {"","redSpawn","blueSpawn","blueSafe","redSafe"};
    
    
    public Marker(int posX, int posY, String name){
        this.posX = posX;
        this.posY = posY;
        this.name = name;
        
        switch (name){
            case "redSpawn":
                imageName = "RedMarker.png";
                image = ImageManager.LoadBufferedImage(imageName, true);
                break;
            case "blueSpawn":
                imageName = "BlueMarker.png";
                image = ImageManager.LoadBufferedImage(imageName, true);
                break;
            case "blueSafe":
                imageName = "BlueSafeMarker.png";
                image = ImageManager.LoadBufferedImage(imageName, true);
                break;
            case "redSafe":
                imageName = "RedSafeMarker.png";
                image = ImageManager.LoadBufferedImage(imageName, true);
                break;
        }
        
    }
    
    public static boolean markerExists (String markerName){
        for (String name:markerNames){
            if (markerName.equalsIgnoreCase(name) && !markerName.equalsIgnoreCase("")){
                return true;
            }
        }
        return false;
    }
    
    public Rectangle getBounds(){
        return new Rectangle(posX, posY, image.getWidth(null), image.getHeight(null));
    }
}
