package GamePackage;
import java.awt.Image;
import java.awt.image.BufferedImage;
import Util.*;


public class Item {
	
	// Graphismes :
    public int offSetX;
    public int offSetY;
    public int rotationPointX;
    public int rotationPointY;
    public int largeImageOffSet;
    public int playerWidth;
    public int width;
    public int height;
    public int firePointRelativeDistance;

    // Caractristiques de l'arme:
    public String type;
    public String name;
    
    public double maxAngle;
    public double angleVariation;
    public int fireRate;
    public int bulletSpeed;
    public int bulletDamage;
    public int magazineCapacity;
    public int reloadTime;
    public int bulletNumber;
    
    public int fireCount;
    public int reloadingCount;
    public int magazine;
    public boolean isReloading;
    
    
    // Caracristiques spcifiques aux tools :
    public boolean consummable = false;
    public int delayTimer;
    public int range;
    public boolean rotationAdjustable;
    public int radius;
    public int detectionRadius;
    public int damage;
    
    // Proprits :
    public double rotationAngle;
    
    // Images :
    public String itemSheetName;
    public Image itemImage;
    public BufferedImage itemSheet;
    
	public Item(String name, int playerWidth){
		this.playerWidth = playerWidth;
		this.name = name;
		initProperties();
		itemSheet = ImageManager.LoadBufferedImage(itemSheetName, true);
	}
	
	public void initProperties(){
        largeImageOffSet = 60;
		offSetX = 12;
		offSetY = 16;
		
        rotationPointX = 8;
        rotationPointY = 3;
        firePointRelativeDistance = 27;
        rotationAngle = 0;
        
        fireCount = 0;
        reloadingCount = 0;
        
        switch(name){
        case "Sniper":
            offSetX -= 10;
            offSetY += 3;
            maxAngle = Math.PI/2;
            angleVariation = 0;
            fireRate = 25;
            bulletSpeed = 45;
            magazineCapacity = 5;
            reloadTime = 90;
            bulletDamage = 80;
            itemSheetName = "SniperSheet.png";
            type = "weapon";
            width = 60;
            height = 15;
            bulletNumber = 1;
        	break;
        case "Ak":
            offSetX -= 4;
            offSetY += 2;
            maxAngle = Math.PI/2;
            angleVariation = 5;
            fireRate = 5;
            bulletSpeed = 25;
            magazineCapacity = 20;
            reloadTime = 70;
            bulletDamage = 20;
            itemSheetName = "AkSheet.png";
            type = "weapon";
            width = 60;
            height = 15;
            bulletNumber = 1;
        	break;
        case "Gun":
            maxAngle = Math.PI/2;
            angleVariation = 10;
            fireRate = 8;
            bulletSpeed = 15;
            magazineCapacity = 7;
            reloadTime = 80;
            bulletDamage = 30;
            itemSheetName = "gunSheet.png";
            type = "weapon";
            width = 60;
            height = 15;
            bulletNumber = 1;
        	break;
        case "Pomp":
            offSetX -= 4;
            offSetY +=2;
            maxAngle = Math.PI/2;
            angleVariation = 17;
            fireRate = 30;
            bulletSpeed = 15;
            magazineCapacity = 16;
            reloadTime = 80;
            bulletDamage = 15;
            itemSheetName = "pompSheet.png";
            type = "weapon";
            width = 60;
            height = 15;
            bulletNumber = 8;
        	break;
        case "Uzi":
            offSetX -= 4;
            offSetY += 2;
            maxAngle = Math.PI/2;
            angleVariation = 17;
            fireRate = 5;
            bulletSpeed = 25;
            magazineCapacity = 120;
            reloadTime = 70;
            bulletDamage = 10;
            itemSheetName = "uziSheet.png";
            type = "weapon";
            width = 60;
            height = 23;
            bulletNumber = 2;
        	break;
            
            
        case "Dynamite":
            type = "tool";
            maxAngle = Math.PI/2;
            offSetX += 8;
            offSetY +=4;
            
            itemSheetName = "dynamite.png";
            rotationAdjustable = false;
            width = 6;
            height = 12;
            
            magazineCapacity = 4;
            reloadTime = 0;
            fireRate = 30;
            consummable = true;
            
            
            range = 60;
            
            delayTimer = 100;
            damage = 100;
            radius = 40;


        	break;
            
        case "Shovel":
        	itemSheetName = "shovel.png";
        	//offSetX = 8;
        	offSetY +=4;
        	maxAngle = Math.PI/3;
        	type = "tool";
        	magazineCapacity = 1;
        	fireRate = 30;
        	consummable = false;
        	range = 60;
        	radius = 10;
        	reloadTime = 0;
        	rotationAdjustable = true;
        	width = 60;
                height = 15;
                break;
            
        case "Camera":
        	itemSheetName = "cameraSheet.png";
        	offSetX += 8;
                offSetY += 4;
        	maxAngle = Math.PI/3;
        	type = "tool";
        	magazineCapacity = 1;
        	fireRate = 30;
        	consummable = true;
        	range = 60;
        	reloadTime = 0;
        	rotationAdjustable = true;
        	width = 50;
                height = 15;
                break;
        	
        case "Barricade":
                itemSheetName = "barricade.png";
                maxAngle = Math.PI/2;
        	offSetX += 8;
        	offSetY +=4;
        	type = "tool";
        	magazineCapacity = 4;
        	reloadTime = 0;
        	fireRate = 5;
        	consummable = true;
                range = 60;
        	rotationAdjustable = false;
        	width = 10;
                height = 10;
                break;
        case "Pylon" :
                itemSheetName = "pylon.png";
                maxAngle = Math.PI/2;
        	offSetX +=1;
        	offSetY -=16;
        	type = "tool";
        	magazineCapacity = 1;
        	reloadTime = 0;
        	fireRate = 5;
        	consummable = true;
                range = 1000;
        	rotationAdjustable = false;
        	width = 20;
                height = 40;
                break;
        case "LandMine" :
            type = "tool";
            maxAngle = Math.PI/2;
            offSetX += 8;
            offSetY +=4;
            
            itemSheetName = "landMineSheet.png";
            rotationAdjustable = false;
            width = 10;
            height = 10;
            
            magazineCapacity = 3;
            reloadTime = 0;
            fireRate = 30;
            consummable = true;
            
            
            range = 60;
            
            delayTimer = 100;
            damage = 100;
            radius = 30;
            detectionRadius = 20;

                break;
            
        }
        magazine = magazineCapacity;
        isReloading = false;

        
	}
	
	public void drawItem(int orientation) {
		
		switch (type){
		case "weapon":
			drawWeapon(orientation);
			break;
		case "tool":
			drawTool(orientation);
			break;
		}
	}
	
	public void drawTool(int orientation){
		
		if (!rotationAdjustable){
                itemImage = itemSheet.getSubimage(0, 0, width, height);
	    	itemImage =ImageManager.rotateImage(ImageManager.ImageToBufferedImage(itemImage, width, height), largeImageOffSet, 0, 0, 0);
	    	
		}
		else
		{
			BufferedImage tempImage = itemSheet.getSubimage(width*orientation, 0, width, height);
			int rotationX;
	    	int rotationY;
	    	
	    	if (orientation == 0){
	    		rotationX = width - rotationPointX;
	    	}
	    	else{
	    		rotationX = rotationPointX;
	    	}
			rotationY = rotationPointY;
	    	itemImage =ImageManager.rotateImage(tempImage, largeImageOffSet, rotationAngle, rotationX, rotationY);

		}
                
                //if (name.equalsIgnoreCase("Pylon")){
                //    itemImage = itemImage.getScaledInstance((int) (itemImage.getWidth(null)*0.5),(int) (itemImage.getHeight(null)*0.5), java.awt.Image.SCALE_SMOOTH);
                //}

         
         
	}
	
	public void drawWeapon(int orientation){
		
        BufferedImage tempImage = itemSheet.getSubimage(60*orientation, 0, width, height);

    	// Deux lignes en commentaires : Affichage de l'arme sans orientation de l'arme
        //BufferedImage largeImage = new BufferedImage(largeImageOffSet*2, largeImageOffSet*2, BufferedImage.TYPE_INT_ARGB);      
        //itemImage = ImageManager.mergeImages(largeImage, rifleImage, largeImageOffSet , largeImageOffSet);
        int rotationX;
    	int rotationY;
    	
    	if (orientation == 0){
    		rotationX = width - rotationPointX;
    	}
    	else{
    		rotationX = rotationPointX;
    	}
		rotationY = rotationPointY;
    	itemImage =ImageManager.rotateImage(tempImage, largeImageOffSet, rotationAngle, rotationX, rotationY);

	}
	
	public int adjustOffSetX (int orientation){
		int offSetXAdjusted;
		if (orientation == 0){
			offSetXAdjusted = playerWidth - offSetX - width;
		}
		else{
			offSetXAdjusted = offSetX;
		}
		
		return offSetXAdjusted;
	}
	
	public void calculateAngle( int orientation, double mouseX, double mouseY, double x, double y)
	{
		//Orientation :

    	double dX = (mouseX - ( x + rotationPointX + offSetX));    	
    	double dY = (mouseY - (y + rotationPointY + offSetY));
    	
    	rotationAngle = Math.atan(dY/dX);
    	//System.out.println(rotationAngle);
    	
    	if (dX * (-1 + 2 * orientation) < 0){
    		if (dY * (-1 + 2 * orientation) > 0){
    			rotationAngle = maxAngle;
    		}
    		else{
    			rotationAngle = -maxAngle;
    		}
    	}
    	if (Math.abs(rotationAngle)>maxAngle)
    	{
    		rotationAngle = Math.signum(rotationAngle)*maxAngle;
    	}
	}

}
