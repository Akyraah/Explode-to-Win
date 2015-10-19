package ExplodetoWin;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;

import Util.*;
import javax.swing.JPanel;

import ClientServerComm.EtWServer;
import GamePackage.Design;
import javax.swing.JOptionPane;


public class StartMenu extends JPanel{

    private static final long serialVersionUID = 1L;

    public Button JoinServerButton;
    public Button StartServerButton;
    public Button EditMapButton;

    public ExplodeToWin explodeToWin;

    public ArrayList<Button> buttons;
    public String defaultServer;
    public String defaultName;
    
    public StartMenu(ExplodeToWin explodeToWin, String defaultServer, String defaultName) {

        this.explodeToWin = explodeToWin;

        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMotionAdapter());

        this.defaultServer = defaultServer;
        this.defaultName = defaultName;
        setBackground(Color.BLACK);
        setDoubleBuffered(true);    

        JoinServerButton = new Button("ButtonJoinServer.png",150,300,100);
        StartServerButton = new Button("ButtonStartServer.png",150,300,250);
        EditMapButton = new Button("ButtonEditMap.png",150,300,400);

        buttons = new ArrayList<>();
        buttons.add(JoinServerButton);
        buttons.add(StartServerButton);
        buttons.add(EditMapButton);

        repaint();

     }
	
    public void removeStartMenu()
    {
        explodeToWin.remove(this);
    }
	
    public void paint(Graphics g) {
    	
        super.paint(g);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHints(rh);

        for (Button button : buttons) {
            g2d.drawImage(button.buttonImage, button.x, button.y, this);
        }
		

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
	}
	
    private class MAdapter extends MouseAdapter {
    	public void mousePressed (MouseEvent e){
            for (int i = 0; i < buttons.size(); i++){
                if (buttons.get(i).isMouseClickingOnButton(e.getX(), e.getY())){
                    buttons.get(i).pressed = true;
                }
                    repaint();
            }
    	}
    	public void mouseReleased (MouseEvent e){
            if (StartServerButton.pressed){
                try {
                    Runtime.getRuntime().exec("cmd /c start EtWServer.bat");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                StartServerButton.pressed = false;
            }
            else if(JoinServerButton.pressed){
                String port = (String)JOptionPane.showInputDialog(getParent(), "Port?", "", JOptionPane.QUESTION_MESSAGE,null,null,"10001");
                String server = "";
                if (defaultServer != null){
                    server = (String)JOptionPane.showInputDialog(getParent(), "Server?", "", JOptionPane.QUESTION_MESSAGE,null,null,defaultServer);
                }
                else {
                    server = (String)JOptionPane.showInputDialog(getParent(), "Server?", "", JOptionPane.QUESTION_MESSAGE,null,null,"Kirito");
                }
                
                String name = "";
                if (defaultName != null){
                    name = (String)JOptionPane.showInputDialog(getParent(), "Name?", "", JOptionPane.QUESTION_MESSAGE,null,null,defaultName);
                }
                else {
                    name = (String)JOptionPane.showInputDialog(getParent(), "Name?", "", JOptionPane.QUESTION_MESSAGE,null,null,"Guest");
                }  
                explodeToWin.addLoadingUI(port, server, name);
                JoinServerButton.pressed = false;
                //removeStartMenu();

            }
            else if(EditMapButton.pressed){

                explodeToWin.addMapMaker();
                EditMapButton.pressed = false;

            }
    	}
    }
    
    private class MMotionAdapter extends MouseMotionAdapter{
    	public void mouseMoved (MouseEvent e) {
            for (int i = 0; i < buttons.size(); i++){
                if (buttons.get(i).isMouseOnButton(e.getX(), e.getY())){

                }
                repaint();
            }
    		

    	}
    }


}
