package ExplodetoWin;

import GamePackage.Block;
import GamePackage.Design;
import GamePackage.ImagesLibrary;
import GamePackage.Marker;
import Util.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class MapMaker extends JPanel implements Runnable{
    private static final long serialVersionUID = 1L;

    public Button SaveButton;
    public Button OpenButton;
    public ArrayList<Button> buttons;

    Map map;
    String mapName = "defaultMap";
    
    String mouseType;
    Design movingDesign;
    Marker movingMarker;
    
    private final int DELAY = 25;
    private Thread animator;
    public int x = 0;
    public int dx = 0;
    
    
    ImagesLibrary imagesLibrary;

    boolean bottomLayerInitialized = false;
    boolean transparency = true;
    
    
    public MapMaker(){
	setFocusable(true);
        setSize(1206,728);
        this.requestFocus();
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMotionAdapter());
        setKeyBindings();
        
        
        setBackground(Color.white);
        setDoubleBuffered(true);    
        
        SaveButton = new Button("ButtonDisquette.png",50,30,610);
        OpenButton = new Button("ButtonDossier.png",50,30,660);

        buttons = new ArrayList<>();
        buttons.add(SaveButton);
        buttons.add(OpenButton);
    	mouseType = "Dirt";
        
        map = new Map(mapName);
        imagesLibrary = new ImagesLibrary(transparency);
	}
	
    @Override
    public void addNotify() {
        super.addNotify();
    	animator = new Thread(this);
    	animator.start();
    }
    
    @Override
    public void run() {
        long beforeTime, timeDiff, sleep ;
        beforeTime = System.currentTimeMillis();
    	while (true) {
            repaint ();
            x += dx;
            if (x < 0){
                x =0;
            }
            if (x > 1200){
                x = 1200;
            }
            
            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;
            if (sleep < 0) {
                    sleep = 2;
            }

            try {
                    Thread.sleep(sleep);
            } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
        }

            beforeTime = System.currentTimeMillis();
    	}
        

        
    }

    @Override
    public void paint(Graphics g) {
	
        super.paint(g);

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        drawDesigns(g,rh);
        drawBlocks(g,rh);
        drawMarkers(g,rh);

        drawBottomLayer(g, rh);
 
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
        
        
    }
    public void drawDesigns(Graphics g, RenderingHints rh){
        Image designImage;
        Graphics2D g2d = (Graphics2D)g; 
        g2d.setRenderingHints(rh);

        for (Design design: map.backgroundDesigns){
            designImage = imagesLibrary.designImages[design.factor];
            if (design.posX - x+ designImage.getWidth(this) > 0 && design.posX - x < 1206){
                g2d.drawImage(designImage, design.posX - x , design.posY, this);
            }
            
        }
    }
    
    public void drawBlocks (Graphics g, RenderingHints rh){
        Image BlockImage;
    	Graphics2D g2d = (Graphics2D)g; 
        g2d.setRenderingHints(rh);
        
        for (int i = 0; i<map.blocks.length; i++){
            for (int j=0; j<map.blocks[0].length; j ++){
                if (map.blocks[i][j] != null){
                BlockImage = imagesLibrary.blockImages[map.blocks[i][j].factor];//imageTempB.getSubimage(0,10*mapWithFog.blocks[i][j].factor, 10, 10); //Transparency.makeColorTransparent(imageTempB.getSubimage(0,10*blocktype, 10, 10), Color.black);    
                    if (i*10 - x + 10 > 0 && i*10 - x < 1206){
                        g2d.drawImage(BlockImage, i*10 - x, j*10, this);
                    }
                    
                }
            }
        }
    }
    
    public void drawMarkers (Graphics g, RenderingHints rh){
        Image markerImage;
    	Graphics2D g2d = (Graphics2D)g; 
        g2d.setRenderingHints(rh);
        for (Marker marker: map.markers){
            markerImage = marker.image;
            g2d.drawImage(markerImage, marker.posX - x, marker.posY, this);
        }
    }
     
    public void drawBottomLayer(Graphics g, RenderingHints rh){
    	
    	// BOTTOM LAYER :
        Graphics2D gBottom = (Graphics2D)g;
        gBottom.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));

        gBottom.setRenderingHints(rh);
        Image BottomLayer = ImageManager.LoadBufferedImage("BottomLayer.png", false);
        gBottom.drawImage(BottomLayer, 0, 600, this);
        Button button;
        
        Graphics2D g2d = (Graphics2D)g;
	g2d.setRenderingHints(rh);
        for (Button button1 : buttons) {
            button = button1;
            g2d.drawImage(button.buttonImage, button.x, button.y, this);
        }
        //ImageProducer producer = new ImageProducer();
        // ITEMS :
        /*for (int i = 0; i<4; i++){
                if(player.items[i] != null){
                        BufferedImage tempWeapon = ImageManager.LoadBufferedImage(player.items[i].itemSheetName, true);
                        Image item;
                        if (player.items[i].type.equalsIgnoreCase("weapon") || player.items[i].rotationAdjustable){
                                item = tempWeapon.getSubimage(player.items[i].width, 0, player.items[i].width, player.items[i].height).getScaledInstance((int) ( (double) player.items[i].width * 1.5),(int)( (double) player.items[i].height * 1.5),Image.SCALE_SMOOTH);
                                gBottom.drawImage(item, 170 - item.getWidth(null)/2 + i*150, 650 - item.getHeight(null)/2, this);
                        }
                        else
                        {
                                item = tempWeapon.getSubimage(0, 0, player.items[i].width, player.items[i].height).getScaledInstance((int) ( (double) player.items[i].width * 1.5),(int)( (double) player.items[i].height * 1.5),Image.SCALE_SMOOTH);
                                gBottom.drawImage(item, 145 - item.getWidth(null)/2 + i*150, 650 - item.getHeight(null)/2, this);
                        }

                }
        }*/
     
    }
    
    private class MAdapter extends MouseAdapter {
            @Override
    	public void mousePressed (MouseEvent e){
            int posX = (e.getX() + x )/ 10;
            int posY = e.getY()/ 10;
            if (posX >= 0 && posX < 240 && posY >=0 && posY < 60){
                if (mouseType.split(";").length == 1){
                    if (mouseType.equalsIgnoreCase("Null")){
                        Block block = map.blocks[posX][posY];
                        if (block != null){
                            for (int x = 0; x < block.dimX;x++ ){
                                for (int y = 0; y < block.dimY;y++ ){
                                    map.blocks[posX + x][posY + y] = null;
                                }
                            }
                        }

                    }
                    else{
                        Block block = new Block(mouseType);
                        for (int x = 0; x < block.dimX;x++ ){
                            for (int y = 0; y < block.dimY;y++ ){
                                map.blocks[posX + x][posY + y] = new Block("Void");
                            }
                        }
                        map.blocks[posX][posY] = block;
                        //if (mouseType.equalsIgnoreCase("Dirt")){
                        //    map.backgroundDesigns.add(new Design(posX*10, posY*10, "dirtWall Little"));
                        //}
                    }
                }
                else{
                    String[] split = mouseType.split(";");
                    switch (split[0]){
                        case "Design":
                            if (Design.designExists(split[1])){
                                movingDesign = new Design(posX*10, posY*10,split[1]);
                                map.backgroundDesigns.add(movingDesign);
                                
                            }
                            else if (split[1].equalsIgnoreCase("delete")){
                                
                                for (int i = map.backgroundDesigns.size() - 1 ; i >= 0 ; i--) {
                                    if (map.backgroundDesigns.get(i).getBounds().contains(e.getX() + x , e.getY()) && !map.backgroundDesigns.get(i).name.equalsIgnoreCase("sky")){
                                        map.backgroundDesigns.remove(i);
                                    }
                                }
                            }
                            break;
                        case "Marker":
                            if (Marker.markerExists(split[1])){
                                movingMarker = new Marker(posX*10, posY*10,split[1]);
                                map.markers.add(movingMarker);
                                
                            }
                            else if (split[1].equalsIgnoreCase("delete")){
                                
                                for (int i = map.markers.size() - 1 ; i >= 0 ; i--) {
                                    if (map.markers.get(i).getBounds().contains(e.getX() + x , e.getY())){
                                        map.markers.remove(i);
                                    }
                                }
                            }
                            break;
                    }
                }
                
            }
            for (Button button : buttons) {
                if (button.isMouseClickingOnButton(e.getX(), e.getY())) {
                    button.pressed = true;
                }
                repaint();
            }
    		
    	}
            @Override
    	public void mouseReleased (MouseEvent e){
            if (SaveButton.pressed){
                SaveButton.pressed = false;
                SaveButton.drawButton();
                String fileName = (String)JOptionPane.showInputDialog(getParent(), "Name of the map?", "Saving map", JOptionPane.QUESTION_MESSAGE,null,null,map.mapName);
                map.saveMapAs(fileName);
                //save(fileName);
		//this.sendMessage("Your age is " + s + ".\n");
            }
            else if(OpenButton.pressed){	
                OpenButton.pressed = false;
                OpenButton.drawButton(); 
                String fileName = JOptionPane.showInputDialog(getParent(), "Name of the map?", "Loading map", JOptionPane.QUESTION_MESSAGE);
                if (fileName != null && !fileName.equalsIgnoreCase("")){
                    map.mapName = fileName;
                    map.openMap();
                }
               
                //open(fileName);
            }
    	}
    }
    
    private class MMotionAdapter extends MouseMotionAdapter{
            @Override
    	public void mouseDragged (MouseEvent e) {
            int posX = (e.getX() +x)/ 10;
            int posY = e.getY() / 10;
            if (posX >= 0 && posX < 240 && posY >=0 && posY < 60){
                if (mouseType.split(";").length == 1){
                    if (mouseType.equalsIgnoreCase("null") ){
                        Block block = map.blocks[posX][posY];
                        if (map.blocks[posX][posY] != null){
                            for (int x = 0; x < block.dimX;x++ ){
                                for (int y = 0; y < block.dimY;y++ ){
                                    map.blocks[posX + x][posY + y] = null;
                                }
                            }
                        }

                    }
                    else{
                        Block block = new Block(mouseType);
                        if (block.dimX == 1 && block.dimY ==1){
                            map.blocks[posX][posY] = block;
                        }
                        
                        //if (mouseType.equalsIgnoreCase("Dirt")){
                        //    map.backgroundDesigns.add(new Design(posX*10, posY*10, "dirtWall Little"));
                        //}
                    } 
                }
                else{
                    String[] split = mouseType.split(";");
                    switch (split[0]){
                        case "Design":
                            if (Design.designExists(split[1])){
                                movingDesign.posX = posX*10;
                                movingDesign.posY = posY*10;
                            }
                            else if (split[1].equalsIgnoreCase("delete")){
                                for (int i = map.backgroundDesigns.size() - 1 ; i >= 0 ; i--) {
                                    if (map.backgroundDesigns.get(i).getBounds().contains(e.getX() + x, e.getY()) && !map.backgroundDesigns.get(i).name.equalsIgnoreCase("sky")){
                                        map.backgroundDesigns.remove(i);
                                    }
                                }
                            }
                        break;
                        case "Marker":
                            if (Marker.markerExists(split[1])){
                                movingMarker.posX = posX*10;
                                movingMarker.posY = posY*10;
                            }
                            else if (split[1].equalsIgnoreCase("delete")){
                                for (int i = map.markers.size() - 1 ; i >= 0 ; i--) {
                                    if (map.markers.get(i).getBounds().contains(e.getX() + x, e.getY()) && !map.markers.get(i).name.equalsIgnoreCase("sky")){
                                        map.markers.remove(i);
                                    }
                                }
                            }
                        break;
                    }
                }
                
            }
            repaint();
            
    	}
            @Override
        public void mouseMoved (MouseEvent e) {
                for (Button button : buttons) {
                    if (button.isMouseOnButton(e.getX(), e.getY())) {
                    }
                    repaint();
                }
    		

    	}
    }
    
    private void setKeyBindings() {
        ActionMap actionMap = getActionMap();
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = getInputMap(condition );

        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0 ,false), "keyQPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "keyDPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0 ,false), "keySpacePressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK ,false), "keyShiftPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0,false), "keyRPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1,0 ,false), "key1Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0, false), "key2Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3,0 ,false), "key3Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_4,0 ,false), "key4Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_5,0 ,false), "key5Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_6,0 ,false), "key6Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_7,0 ,false), "key7Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_8,0 ,false), "key8Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_9,0 ,false), "key9Pressed");
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0 ,true), "keyQReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0 , true), "keyDReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0 ,true), "keySpaceReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,0 ,true), "keyShiftReleased");
        
        actionMap.put("keyQPressed", new KeyAction("keyQPressed"));
        actionMap.put("keyDPressed", new KeyAction("keyDPressed"));
        actionMap.put("keySpacePressed", new KeyAction("keySpacePressed"));
        actionMap.put("keyShiftPressed", new KeyAction("keyShiftPressed"));
        actionMap.put("keyRPressed", new KeyAction("keyRPressed"));
        actionMap.put("key1Pressed", new KeyAction("key1Pressed"));
        actionMap.put("key2Pressed", new KeyAction("key2Pressed"));
        actionMap.put("key3Pressed", new KeyAction("key3Pressed"));
        actionMap.put("key4Pressed", new KeyAction("key4Pressed"));
        actionMap.put("key5Pressed", new KeyAction("key5Pressed"));
        actionMap.put("key6Pressed", new KeyAction("key6Pressed"));
        actionMap.put("key7Pressed", new KeyAction("key7Pressed"));
        actionMap.put("key8Pressed", new KeyAction("key8Pressed"));
        actionMap.put("key9Pressed", new KeyAction("key9Pressed"));
        
        actionMap.put("keyQReleased", new KeyAction("keyQReleased"));
        actionMap.put("keyDReleased", new KeyAction("keyDReleased"));
        actionMap.put("keySpaceReleased", new KeyAction("keySpaceReleased"));
        actionMap.put("keyShiftReleased", new KeyAction("keyShiftReleased"));

     }
    
    private class KeyAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public KeyAction(String actionCommand) {
           putValue(ACTION_COMMAND_KEY, actionCommand);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvt) {
        	
            String eventName = actionEvt.getActionCommand();

            switch (eventName){
            case "keyQPressed":
                    dx = -4;
                    break;
            case "keyQReleased":
                    dx = 0;
                    break;
            case "keyDPressed":
                    dx = 4;
                    break;
            case "keyDReleased":
                    dx = 0;
                    break;
            case "keySpacePressed":
                    String[] folderNames ={
                            "All",
                            "Wood and Dirt",
                            "Japanese",
                            "Brick",
                            "Trees",
                            "Deco"
                        };
                    String folderName = (String) JOptionPane.showInputDialog( getParent(), "", "Chosing design", JOptionPane.INFORMATION_MESSAGE, null, folderNames, "All");
                    String[] designNames = new String[0];
                    
                    switch (folderName){
                        case "All":
                            designNames = Design.designNames;
                            break;
                        case "Wood and Dirt":
                            designNames = new String [] {
                                "dirtWallBig",
                                "dirtWall",
                                "woodenPlankWall",
                                "woodenPlankWallBig",
                                "woodenWindow"
                                };
                            break;
                        case "Japanese":
                            designNames = new String [] {
                                "katana",
                                "treeCherry",
                                "paperWall",
                                "paperDoor",
                                "paperWindow",
                                "oldMannequin",
                                "redPylonWall"
                                };
                            break;
                        case "Brick":
                            designNames = new String [] {
                                "brickWall",
                                "brickWallBig",
                                "brickWindow",
                                "brickWindow2"
                                };
                            break;
                        case "Trees":
                            designNames = new String [] {
                                "treeGreen",
                                "treeBrown",
                                "treeCherry"
                                };
                            break;
                        case "Deco":
                            designNames = new String [] {
                                "safe",
                                "woodenCaseWall1",
                                "woodenCaseWall2",
                                "woodenCaseWall3",
                                "lampPost",
                                "woodenShelves"
                                };
                            break;
                            
                            
                    }
                    
                    String designName = (String) JOptionPane.showInputDialog( getParent(), "", "Chosing design", JOptionPane.INFORMATION_MESSAGE, null, designNames, "");
                    
                    if (designName.equalsIgnoreCase("")){
                        mouseType = "Design;delete";
                    }
                    else{
                        mouseType = "Design;"+designName;
                    }
                    break;
            case "keySpaceReleased":
                    break;
            case "keyShiftPressed":
                    String markerName = (String) JOptionPane.showInputDialog( getParent(), "Name of the marker?", "Chosing marker", JOptionPane.INFORMATION_MESSAGE, null, Marker.markerNames, "");
                    if (markerName.equalsIgnoreCase("")){
                        mouseType = "Marker;delete";
                    }
                    else{
                        mouseType = "Marker;"+markerName;
                    }
                    break;
            case "keyShiftReleased":
                    break;
            case "key1Pressed":
                    mouseType = "null";
                    break;
            case "key2Pressed":
                    mouseType = "Dirt";
                    break;
            case "key3Pressed":
                    mouseType = "Concrete";
                    break;
            case "key4Pressed":
                    mouseType = "Grass";
                    break;
            case "key5Pressed":
                    mouseType = "Wood";
                    break;
            case "key6Pressed":
                    mouseType = "Platform";
                    break;
            case "key7Pressed":
                String[] blockNames ={
                            "WoodenCase1",
                            "WoodenCase2",
                            "WoodenCase3"
                        };
                    mouseType = (String) JOptionPane.showInputDialog( getParent(), "", "Chosingtype", JOptionPane.INFORMATION_MESSAGE, null, blockNames, "WoodenCase1");
                    break;
            case "key8Pressed":
                String[] blockName ={
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
                    mouseType = (String) JOptionPane.showInputDialog( getParent(), "", "Chosing type", JOptionPane.INFORMATION_MESSAGE, null, blockName, "RedPylon1");
                    break;
            case "key9Pressed":
                    mouseType = "Pink";
                    break;
            
            case "keyRPressed":
                    transparency = !transparency;
                    imagesLibrary = new ImagesLibrary(transparency);
                    break;
            }
        	
        }
     }
	
}
