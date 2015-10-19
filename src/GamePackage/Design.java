
package GamePackage;

import Util.ImageManager;
import java.awt.Image;
import java.awt.Rectangle;

public class Design {
    
    public int posX;
    public int posY;
    public int width;
    public int height;
    public String name;
    public String imageName;
    public Image image;
    public int factor;
    public static String[] designNames = {
        "",
        "concreteWall",
        "sky",
        "brickWall",
        "brickWallBig",
        "brickWindow",
        "brickWindow2",
        "dirtWallBig",
        "dirtWall",
        "woodenPlankWall",
        "woodenPlankWallBig",
        "woodenWindow",
        "lampPost",
        "treeGreen",
        "treeBrown",
        "treeCherry",
        "safe",
        "woodenCaseWall1",
        "woodenCaseWall2",
        "woodenCaseWall3",
        "katana",
        "paperWall",
        "paperDoor",
        "paperWindow",
        "oldMannequin",
        "woodenShelves",
        "redPylonWall"};
    
    
    public Design(int posX, int posY, String name){
        this.posX = posX;
        this.posY = posY;
        this.name = name;
        
        switch (name){
            case "concreteWall":
                imageName = "concreteWall.png";
                height = 30;
                width = 30;
                
                break;
            case "brickWall":
                height = 10;
                width = 10;
                imageName = "brickWall.png";
                
                break;
            case "brickWallBig":
                imageName = "brickWallBig.png";
                height = 50;
                width = 50;
                
                break;
            case "woodenPlankWall":
                height = 10;
                width = 10;
                imageName = "woodenPlankWall.png";
                
                break;
            case "woodenPlankWallBig":
                imageName = "woodenPlankWallBig.png";
                height = 50;
                width = 50;
                
                break;
            case "woodenWindow":
                imageName = "woodenWindow.png";
                
                break;
            case "brickWindow":
                imageName = "brickWindow.png";
                height = 40;
                width = 40;
                
                break;
            case "brickWindow2":
                imageName = "brickWindow2.png";
                height = 30;
                width = 20;
                
                break;
            case "dirtWallBig":
                imageName = "dirtWallBig.png";
                height = 30;
                width = 30;
                
                break;
             case "dirtWall":
                imageName = "dirtWall.png";
                height = 10;
                width = 10;
                
                
                break;
            case "lampPost":
                imageName = "lampPost.png";
                height = 100;
                width = 20;
                
                break;
            case "treeGreen":
                imageName = "treeGreen.png";
                height = 110;
                width = 70;
                
                break;
            case "treeBrown":
                imageName = "treeBrown.png";
                height = 110;
                width = 70;
                
                break;
            case "sky":
                imageName = "sky.png";
                height = 600;
                width = 1200;
                break;
            case "safe":
                imageName = "safe.png";
                height = 30;
                width = 30;
                break;
            case "woodenCaseWall1":
                imageName = "woodenCaseWall1.png";
                height = 20;
                width = 20;
                break;
            case "woodenCaseWall2":
                imageName = "woodenCaseWall2.png";
                height = 20;
                width = 20;
                break;
            case "woodenCaseWall3":
                imageName = "woodenCaseWall3.png";
                height = 20;
                width = 20;
                break;
            case "treeCherry":
                imageName = "treeCherry.png";
                height = 110;
                width = 70;
                break;
            case "katana":
                imageName = "katana.png";
                height = 20;
                width = 30;
                break;
            case "paperWall":
                imageName = "paperWall.png";
                height = 30;
                width = 30;
                break;
            case "paperDoor":
                imageName = "paperDoor.png";
                height = 40;
                width = 20;
                break;
            case "paperWindow":
                imageName = "paperWindow.png";
                height = 30;
                width = 30;
                break;
            case "woodenShelves":
                imageName = "woodenShelves.png";
                height = 30;
                width = 20;
                break;
            case "oldMannequin":
                imageName = "oldMannequin.png";
                height = 40;
                width = 20;
                break;
            case "redPylonWall":
                imageName = "redPylonWall.png";
                height = 10;
                width = 10;
                break;
                
        }
        
        factor = getFactor(name);
        
    }
    
    public static boolean designExists (String designName){
        for (String name:designNames){
            if (designName.equalsIgnoreCase(name) && !designName.equalsIgnoreCase("")){
                return true;
            }
        }
        return false;
    }
    
    public Rectangle getBounds(){
        return new Rectangle(posX, posY, width, height);
    }
    
    public int getFactor(String name){
        int factor = 0;
        for (int i = 0; i < designNames.length; i++){
            if (name.equalsIgnoreCase(designNames[i])){
                factor = i;
            }
        }
        return factor;
    }
}
