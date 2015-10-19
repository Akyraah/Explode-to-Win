package GamePackage;

import Util.*;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class PlaceableObject {
	
	public int x;
	public int y;
	public int dx;
	public int dy;
	
	public Block[][] posBlocks;
	
	public String name;
	public String[] args;
	
	public int delayTimer;
	public int radius;
        public int detectionRadius;
	public int timerCount;
	public int damage;
        public int orientation;
        public int offsetX;
        public int offsetY;
        public String team;
	private String objectSheetName;
        public Image objectImage;
        public BufferedImage objectSheet;
        
        public int  numPlayer; // Pour la dynamite
        
        public int xSup; // Pour la tyrolienne, normalement...
        public int ySup;
        public boolean set;

        public boolean exists;
        public boolean visible;

	public PlaceableObject(String name, int x, int y, String[] args){
		this.name = name;
		this.args = args;
		this.x = x;
		this.y = y;
		orientation = 1;
                InitObject();
                
	}
	
	public void InitObject()
	{
            exists = true;
            switch(name){
		case "Dynamite":
                    objectSheetName = "dynamite.png";
                    objectImage = ImageManager.LoadBufferedImage(objectSheetName, true);
                    //delayTimer = 0;
                    delayTimer = Integer.parseInt(args[0]);
                    radius = Integer.parseInt(args[1]);
                    damage = Integer.parseInt(args[2]);
                    timerCount = 0;
                    visible = true;
                    offsetX = 3;
                    offsetY = 12;
                    break;
		case "Shovel":
                    visible = false;
                    radius = Integer.parseInt(args[0]);
                    delayTimer = 0;
                    timerCount = 0;
                    break;
                case "Camera":
                    objectSheetName = "cameraSheet.png";
                    objectImage = ImageManager.LoadBufferedImage(objectSheetName, true).getSubimage(50, 0, 20, 20);
                    team = args[0];
                    orientation = Integer.parseInt(args[1]);
                    delayTimer = 0; 
                    offsetX = 0;
                    offsetY = 0;
                    visible = true;
                    break;
                case "Pylon":
                    objectSheetName = "pylon.png";
                    objectImage = ImageManager.LoadBufferedImage(objectSheetName, true);
                    delayTimer = 0; 
                    offsetX = 0;
                    offsetY = 0;
                    visible = true;
                    xSup = Integer.parseInt(args[0]);
                    ySup = Integer.parseInt(args[1]);
                    set = false;
                        break;
                case "LandMine":
                    objectSheetName = "landMineSheet.png";
                    team = args[0];
                    String playerTeam = args[1];
                    radius = Integer.parseInt(args[2]);
                    damage = Integer.parseInt(args[4]);
                    detectionRadius = Integer.parseInt(args[3]);
                    if (playerTeam.equalsIgnoreCase(team)){
                        objectImage = ImageManager.LoadBufferedImage(objectSheetName, true).getSubimage(0, 0, 10, 10);
                     
                    }
                    else {
                        objectImage = ImageManager.LoadBufferedImage(objectSheetName, true).getSubimage(0, 10, 10, 10);
                     
                    }
                    delayTimer = 0; 
                    offsetX = 0;
                    offsetY = 0;
                    visible = true;
                    break;
            }
	}
	
	public void objectAction()
	{
		switch(name){
		case "Dynamite":
			timerCount ++;
			if (timerCount > delayTimer){
				exists = false;
			}
			break;
		case "Shovel":
			timerCount ++;
			if (timerCount > delayTimer){
				exists = false;
			}
			break;
                case "Camera":
                        if (orientation == 1){
                            objectImage = ImageManager.LoadBufferedImage(objectSheetName, true).getSubimage(50, 0, 20, 20);
                        }
                        else {
                            objectImage = ImageManager.LoadBufferedImage(objectSheetName, true).getSubimage(30, 0, 20, 20);
                        }
                        break;
                }
	}
        
    public Rectangle getBounds() {
        return new Rectangle(x, y, objectImage.getWidth(null), objectImage.getHeight(null));
    	//return new Rectangle(x+3, y+8, 30-8, 41-10);
        //return new Rectangle(x+3, y+10, width-8, height-12);
        
    }
    
    public void setPos(Rectangle r){
        double distance = Math.sqrt(Math.pow(x+10 - r.getCenterX(),2) + Math.pow(y - r.getCenterY(),2));
        double distance2 = Math.sqrt(Math.pow(x+10 - xSup,2) + Math.pow(y - ySup,2));
        if (!set || distance2 > distance){
            xSup = (int) r.getCenterX();
            ySup = (int) r.getCenterY();
            set = true;
        }
    }
}
