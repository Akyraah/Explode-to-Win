package GamePackage;
import Util.*;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PlayerClass {

    // Tableaux :
    public Item[] items = new Item[4];
    public ArrayList<Bullet> bullets;
    public ArrayList<PlaceableObject>placeableObjects;
	
    public int maxHealth;
    public int maxStamina;
    public int jumpingHeight;

    public PlayerClass(String item1, String item2, String item3, String item4) {
        if (item1 != null){
            items[0] = new Item(item1, 0);
        }
        if (item2 != null){
            items[1] = new Item(item2, 0);
        }
        if (item3 != null){
            items[2] = new Item(item3, 0);
        }
        if (item4 != null){
            items[3] = new Item(item4, 0);
        }
    }
    
    public void initProperties(){
    	jumpingHeight = 15;
        maxHealth = 100;
        maxStamina = 100;
    }
    
}