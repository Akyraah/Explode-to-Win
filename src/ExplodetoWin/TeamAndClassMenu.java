package ExplodetoWin;

import Util.Button;
import GamePackage.Board;
import GamePackage.PlayerClass;
import Util.ImageManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

public class TeamAndClassMenu extends JPanel{
    private static final long serialVersionUID = 1L;
    
    public Button blueTeamButton;
    public Button redTeamButton;
    public Button selectClass1;
    public Button selectClass2;
    public Button selectClass3;
    public Button selectClass4;
    Board board;
    
    public int state = 0;
    public PlayerClass[] classes;

    public ArrayList<Button> buttons;
    
    public TeamAndClassMenu(Board board){
        setFocusable(true);
        setSize(1206,728);	
        this.requestFocus();
        this.setDoubleBuffered(true);
        
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMotionAdapter());


        setBackground(Color.BLACK);
        this.board = board;
        
        blueTeamButton = new Button("ButtonBlueTeam.png",700,0,0);
        redTeamButton = new Button("ButtonRedTeam.png",700,600,0);
        selectClass1 = new Button("buttonClass.png",80,0,50);
        selectClass2 = new Button("buttonClass.png",80,0,180);
        selectClass3 = new Button("buttonClass.png",80,0,310);
        selectClass4 = new Button("buttonClass.png",80,0,440);
        
        buttons = new ArrayList<>();
        buttons.add(blueTeamButton);
        buttons.add(redTeamButton);
        
        classes = new PlayerClass[4];
        classes[0] = new PlayerClass("Ak","Gun","Barricade","Pylon");
        classes[1] = new PlayerClass("Pomp","Gun","Dynamite","Shovel");
        classes[2] = new PlayerClass("Sniper","Gun","Camera",null);
        classes[3] = new PlayerClass("Uzi","Gun","LandMine",null);
        repaint();

        
    }
    @Override
    public void addNotify() 
    {
        super.addNotify();
    	repaint();
    }
    @Override
    public void paint(Graphics g) {
    	
        super.paint(g);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHints(rh);
        
        if (state == 1){
            for (int c=0; c<4; c++){
                for (int i = 0; i<4; i++){
                    if(classes[c].items[i] != null){
                            BufferedImage tempWeapon = ImageManager.LoadBufferedImage(classes[c].items[i].itemSheetName, true);
                            Image item;
                            if (classes[c].items[i].type.equalsIgnoreCase("weapon") || classes[c].items[i].rotationAdjustable){
                                    item = tempWeapon.getSubimage(classes[c].items[i].width, 0, classes[c].items[i].width, classes[c].items[i].height).getScaledInstance((int) ( (double) classes[c].items[i].width * 2.5),(int)( (double) classes[c].items[i].height * 2.5),Image.SCALE_SMOOTH);
                                    g2d.drawImage(item, 240 - item.getWidth(null)/2 + i*150, 80 - item.getHeight(null)/2 + 130 * c, this);
                            }
                            else
                            {
                                    item = tempWeapon.getSubimage(0, 0, classes[c].items[i].width, classes[c].items[i].height).getScaledInstance((int) ( (double) classes[c].items[i].width * 2.5),(int)( (double) classes[c].items[i].height * 2.5),Image.SCALE_SMOOTH);
                                    g2d.drawImage(item, 215 - item.getWidth(null)/2 + i*150, 80 - item.getHeight(null)/2 + 130 * c, this);
                            }

                    }
                }
            }
            
            //g2d.drawImage(ImageManager.LoadImage("ClassMenu.png", true),0,0,this);
        }
        for (Button button : buttons) {
            g2d.drawImage(button.buttonImage, button.x, button.y, this);
        }
        
		

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    
    public void changeState(){
        if (state == 0){
            state = 1;
            buttons = new ArrayList<>();
            buttons.add(selectClass1);
            buttons.add(selectClass2);
            buttons.add(selectClass3);
            buttons.add(selectClass4);
        }
        else if (state == 1){
            state = 0;
            buttons = new ArrayList<>();
            buttons.add(blueTeamButton);
            buttons.add(redTeamButton);
        }
        repaint();
    }
    
    
    private class MAdapter extends MouseAdapter {
            @Override
    	public void mousePressed (MouseEvent e){
                for (Button button : buttons) {
                    if (button.isMouseClickingOnButton(e.getX(), e.getY())) {
                        button.pressed = true;
                    }
                    repaint();
                }
    	}
            @Override
    	public void mouseReleased (MouseEvent e){
            if (blueTeamButton.pressed){
                blueTeamButton.pressed = false;
                blueTeamButton.drawButton();
                board.setTeam("blue");
                changeState();
                
                
            }
            else if(redTeamButton.pressed){
                redTeamButton.pressed = false;
                redTeamButton.drawButton();
                board.setTeam("red");
                changeState();
                


            }
            else if (selectClass1.pressed){
                board.respawn(classes[0]);
            }
            else if (selectClass2.pressed){
                board.respawn(classes[1]);
            }
            else if (selectClass3.pressed){
                board.respawn(classes[2]);
            }
            else if (selectClass4.pressed){
                board.respawn(classes[3]);
            }
    	}
    }
    
    private class MMotionAdapter extends MouseMotionAdapter{
            @Override
    	public void mouseMoved (MouseEvent e) {
                for (Button button : buttons) {
                    if (button.isMouseOnButton(e.getX(), e.getY())) {
                    }
                    repaint();
                    
                }
    		

    	}
    }
    
    
}
