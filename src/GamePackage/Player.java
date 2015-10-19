package GamePackage;
import Util.*;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player {
	
	
    public final int RIGHT = 1;
    public final int LEFT = 0;
	
    
    public boolean playable;
    public boolean visible;
    
    public String name;
    public int num;
    
    // D�placement et Coordonn�es :
    public int x;
    public int y;
   
    public int dx;
    public int dy;
    
    public int mouseX;
    public int mouseY;
    
    // Images :
    
    private String playerSheetName = "playerAnimationSheet.png";

    public Image playerImage;
    public BufferedImage animationSheet;
    
    
    public int width;
    public int height;
    
    // Propri�t�s physiques :
    private int walkingAnimationNumber = 14;
    public boolean isWalking;
    public boolean isRunning;
    public boolean isInAir;
    public boolean isJumping;
    public boolean isFiring;
    public boolean isCarryingTool;
    public boolean isDead;
    public boolean isCarryingBag;
    public boolean isFollowingLine;
    public boolean isPassingTrough;
    public boolean isRestablishing;
    //public boolean isReloading;
    public int orientation;
    public int itemNumber;
    public String team;
    public Board board;
    
    // Tableaux :
    public Item[] items = new Item[4];
    public ArrayList<Bullet> bullets;	
    
    // Carract�ristiques du perso :
    //public int magazine;
    public int health;
    public int maxHealth;
    public int stamina;
    public int maxStamina;
    public int jumpingHeight;
    public int spawnX;
    public int spawnY;
    public PlayerClass c;
    
    // Compteurs:
    public int animationCount;
    //public int fireCount;
    //public int reloadingCount;
    public int animationCollumn;
    public int jumpingCount;
    
    public boolean teamSet;
    
    public PlaceableObject lineFollowed;

    public Player(Board board, int num) {
        animationSheet = ImageManager.LoadBufferedImage(playerSheetName, true);  
        initProperties(); 
        drawPlayer();
        width = playerImage.getWidth(null);
        height = playerImage.getHeight(null); 
        initPlayerCharacteristics();
        this.board = board;
        this.num = num;
        visible = true;
        x = 0;
        y = 0;
    }
    
    public void initProperties(){
    	animationCount = 0;
        isCarryingBag = false;
    	itemNumber = 0;
    	//reloadingCount = 0;
    	//fireCount = 0;
        playable = false;
        isWalking = false;
        isRunning = false;
        isInAir = false;
        isJumping = false;
        isFiring = false;
        isDead = true;
        isFollowingLine = false;
        isPassingTrough = false;
        isCarryingTool = false;
        isRestablishing = false;
        //isReloading = false;
        orientation = RIGHT;
        
        teamSet = false;
        team = "";
        c = new PlayerClass("Gun","Gun","Gun","Gun");
        bullets = new ArrayList<Bullet>();
    }
    
    public void initPlayerCharacteristics(){
    	jumpingHeight = 15;
        items[0] = new Item("Sniper",width);
        items[1] = new Item("Pomp", width);
        items[2] = new Item("Dynamite", width);
        items[3] = new Item("Ak", width);
        
        items[0].drawItem(orientation);
        items[1].drawItem(orientation);       
        items[2].drawItem(orientation);
        items[3].drawItem(orientation);
        
        //magazine = items[0].magazineCapacity;
        maxHealth = 100;
        maxStamina = 100;
        health = maxHealth;
        stamina = maxStamina;
        
       
    }
    
    public void respawn(PlayerClass c){
        isDead = false;
        health = maxHealth;
        stamina = maxStamina;
        isCarryingBag = false;
        for (int i = 0; i < 4; i++){
            if (c.items[i] != null){
                items[i] = new Item(c.items[i].name, width);
                items[i].drawItem(orientation);
            }
            else{
                items[i] = null;
            }
        }
        
        x = spawnX;
        y = spawnY;
        this.c = c;
    }
    //
    // MOVEMENT FUNCTIONS :
    //
    
    public void moving() {
    	
    	if (isInAir){
            if (isJumping) {
            	if (jumpingCount < jumpingHeight) {
                	jumpingCount += 1;
                }
                else {
                	isJumping = false;
                        isRestablishing = true;
                	dy = 0;
                }
            }
        	else {
                dy = 3;
            }
        }
        else{
        	dy = 0;
        }
    	
        if (!isRunning && stamina < maxStamina){
        	stamina += 1;
        }
        if (isRunning && !isInAir){
        	if (dx !=0){
        		//x += dx;
        		stamina -= 1;
        		if (stamina <= 0){
            		stamina = 0;
            		isRunning = false;
            	}
        	}
        }
        
        drawPlayer();
        if (isFiring){
            items[itemNumber].drawItem(orientation);
        }
        checkBordure();
        
    }
    
    public void checkBordure() {
    	if (x < 1) {
            x = 1;
        }

        if (y < -20) {
            y = -20;
        }
        
        if (x > 2380) {
        	x = 2380;
        }
        if (y > 600 - height) {
        	y = 600 - height;
        	isInAir = false;
                //isJumping = false;
        }
    }
    
    
    //
    // EVENEMENTS :
    //
    
    public void changeItem(int number){
    	itemNumber = number;
    	items[itemNumber].drawItem(orientation);
    	if (items[itemNumber].type.equalsIgnoreCase("tool")){
    		isCarryingTool = true;
    	}
    	else{
    		isCarryingTool = false;
    	}
    }
    
    public void loseHealth(int damage){
    	health -= damage;
        if (health <= 0){
            health = 0;
            isDead = true;
        }
    }
    
    public void actionTiming(){
    	Item w = items[itemNumber];
    	if (w.fireCount != 0){
			w.fireCount ++;
			if (w.fireCount == w.fireRate)
			{
				w.fireCount =0;
			}
		}
		if (w.isReloading){
	    		w.reloadingCount ++;
	    		if (w.reloadingCount > w.reloadTime){
	    			w.reloadingCount = 0;
	    			w.isReloading = false;
	    			w.magazine = w.magazineCapacity;
	    		}
		}
    }
           
    public boolean fire() {
    	
       // missiles.add(new Missile(x + width, y + height/2));
    	//fireCount ++;
    	Item w = items[itemNumber];
    	if (isFiring){
    		if (w.fireCount == 0 && !w.isReloading){
    			if (w.magazine > 0){   		
        			return true;
        		}
        	}
    	}
    	
    	return false;
    	
		
    }
    
    public void launchBullet(double angleVariation, int numItem){
    	Item w = items[numItem];
        if (w != null){
                System.out.println("itemNumber: " + itemNumber);
                double factor = -1 + 2*orientation;
                double angle = w.rotationAngle + Math.toRadians(angleVariation);
                double startX = x + w.adjustOffSetX(orientation) + (w.rotationPointX + (w.firePointRelativeDistance)*Math.cos(angle));//*factor;
                double startY = y + w.offSetY + w.rotationPointY + (w.firePointRelativeDistance)*Math.sin(angle)*factor;
                bullets.add(new Bullet((int) startX, (int) startY, (int) (w.bulletSpeed*factor), angle, w, "normal"));	
		w.magazine -= 1;
		if (w.magazine == 0){
			w.isReloading = true;
		}
		w.fireCount = 1;
        }

    }
    
    public void toolAction(int eventX, int eventY){
    	board.toolAction(eventX, eventY, this);

    	
    }

    //
    // ANIMATION FUNCTIONS :
    //
    
    
    public void drawPlayer() {
    	if (!teamSet && !team.equalsIgnoreCase("")){
            if (team.equalsIgnoreCase("red")){
                playerSheetName = "playerAnimationSheetRed.png";
                animationSheet = ImageManager.LoadBufferedImage(playerSheetName, true);
                teamSet = true;
            }
            else if (team.equalsIgnoreCase("blue")){
                playerSheetName = "playerAnimationSheetBlue.png";
                animationSheet = ImageManager.LoadBufferedImage(playerSheetName, true);
                teamSet = true;
            }
        }
    	animationCollumn = orientation;
    	if (isFiring || isCarryingTool){
    		animationCollumn += 2;
    	}
    	
    	int i = animationSheetNumber();
    	//Image imageTemp = Transparency.makeColorTransparent(animationSheet.getSubimage(20*orientation, 28*i, 20, 27), Color.black);
        
    	BufferedImage imageTemp = animationSheet.getSubimage(20*animationCollumn, 28*i, 20, 27);
        playerImage = imageTemp.getScaledInstance((int) Math.round(imageTemp.getWidth(null)*1.5), (int) Math.round(imageTemp.getHeight(null)*1.5), java.awt.Image.SCALE_SMOOTH);
        
        
        
       /* if (isFiring){
        	
    		BufferedImage rifleSheet = ImageManager.LoadBufferedImage(itemsheetName, true);
    		BufferedImage rifleImage = rifleSheet.getSubimage(40*orientation, 0, 40, 10);
    		
    		BufferedImage playerImage = imageTemp;
    		int dx;
    		int dy;
    		if (orientation == RIGHT)
    		{
    			dx = 8;
    			dy = 11;
    		}
    		else
    		{
    			dx = -29;
    			dy = 11;
    		}
        	imageTemp = ImageManager.mergeSubImage(playerImage, rifleImage, dx, dy);
        }*/
    	
    	
    }
    
    public int animationSheetNumber () {
    	if (isInAir) {
    		return 5;
    	}
    	else if (isWalking && !isInAir){
    		animationCount += 1;
    		if (animationCount > walkingAnimationNumber) {
    			animationCount = 1;
    		}
    		return (5 + animationCount);
    	}
    	else {
    		return 0;
    	}
    }

    //
    // GET FUNCTIONS :
    //
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return playerImage;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public boolean isPlayable() {
        return playable;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x+3, y+6, 21, 35);
    	//return new Rectangle(x+3, y+8, 30-8, 41-10);
        //return new Rectangle(x+3, y+10, width-8, height-12);
        
    }
    
    //
    // SET FUNCTIONS :
    //
    
    public void setX(int x){
    	this.x = x;
    }
    
    public void setY(int y){
    	this.y = y;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setPlayable(boolean playable){
    	this.playable = playable;
    }
    
    public void setOrientation(int eventX){
        if (x < eventX){
            orientation = 1;
        }
        else{
            orientation = 0;
        }
    }
    
    //
    // EVENT FUNCTIONS :
    // 
    
    public void mouseEvent(int eventID, int eventX, int eventY){
        if (!isDead){
            switch (eventID){
            case MouseEvent.MOUSE_PRESSED:
                    mouseX = eventX;
                    mouseY = eventY;
                    if (mouseY < 600){
                        setOrientation(eventX);
                        if (items[itemNumber] != null){
                            items[itemNumber].calculateAngle(orientation, eventX, eventY, x, y);
                            isFiring = true;
                        }
                        
                        
                    }
                    if (items[itemNumber] != null){
                        if (items[itemNumber].type.equalsIgnoreCase("tool")){
                            toolAction(eventX,eventY);
                        } 
                    }

                    break;
            case MouseEvent.MOUSE_RELEASED:
                    isFiring = false;
                    break;
            case MouseEvent.MOUSE_DRAGGED:
                    if (isFiring){
                            items[itemNumber].calculateAngle(orientation, eventX, eventY, x, y);
                    }
                    break;
            }
        }

    }
    
    public void keyEvent(String eventName){
        if (!isDead){
            switch (eventName){
            case "keyQPressed":
                dx = -2;
                isWalking = true;
                if (!isFiring){
                    orientation = LEFT;
                    if (items[itemNumber] != null){
                        items[itemNumber].drawItem(orientation);
                    }
                }
                    break;
            case "keyQReleased":
                dx = 0;
                animationCount = 0;
                isWalking = false;
                    break;
            case "keyDPressed":
                    dx = 2;
                isWalking = true;
                if (!isFiring){
                    orientation = RIGHT;
                    if (items[itemNumber] != null){
                        items[itemNumber].drawItem(orientation);
                    }
                    
                }
                    break;
            case "keyDReleased":
                dx = 0;
                animationCount = 0;
                isWalking = false;
                    break;
            case "keySpacePressed":
                    if (!isInAir){
                            dy = -5;
                    jumpingCount = 0;
                    isJumping = true;
                    isInAir = true;
                    isPassingTrough = true;
                    }
                    isFollowingLine = false;
                    break;
            case "keySpaceReleased":
                dy = 0;
                isJumping = false;
                isRestablishing = true;
                    break;
            case "keySPressed":
                isPassingTrough = true;
                    break;
            case "keySReleased":
                isRestablishing = true;
                //isPassingTrough = false;
                    break;
            case "keyShiftPressed":
                    if (!isJumping){
                        isRunning = true;
                    }
                    
                    break;
            case "keyShiftReleased":
                    isRunning = false;
                    break;
            case "keyRPressed":
                    items[itemNumber].isReloading = true;
                    break;
            case "key1Pressed":
                    if (items[0] != null){
                            changeItem(0);
                            //itemNumber = 0;
                    }
                    break;
            case "key2Pressed":
                    if (items[1] != null){
                            changeItem(1);
                            //itemNumber = 1;
                    }
                    break;
            case "key3Pressed":
                    if (items[2] != null){
                            changeItem(2);
                            //itemNumber = 2;
                    }
                    break;
            case "key4Pressed":
                    if (items[3] != null){
                            changeItem(3);
                            //itemNumber = 3;
                    }
                    break;

            }
        }
    }
    
    
}