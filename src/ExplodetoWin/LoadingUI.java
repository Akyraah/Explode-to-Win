/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ExplodetoWin;

import ClientServerComm.EtWClient;
import javax.swing.JPanel;
import ClientServerComm.EtWClientServerProtocol;
import java.util.ArrayList;
import GamePackage.Player;
import Util.Button;
import Util.ImageManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 *
 * @author Aurélien
 */
public class LoadingUI extends JPanel{
    public Map map;
    public EtWClient client;
    public ExplodeToWin etW;
    public ArrayList<String> mapStrings;
    public String mapName;
    public int objectsNumber;
    public int progress;
    public long timeElapsed;
    public int maxConnections;
    public String port;
    public String server;
    public String name;
    
    public Player loadingPlayer;
    
    
    public LoadingUI(ExplodeToWin etW,String port, String server, String name){
        this.etW = etW;
        progress = 0;
        mapStrings = new ArrayList<>();
        map = new Map("defaultMap");
        this.port = port;
        this.server = server;
        this.name = name;
        
        setSize(1206,728);	
        this.requestFocus();
        this.setDoubleBuffered(true);
        setBackground(Color.BLACK);
        
    }
    
    public void initGame(){
        map.mapName = mapName;
        etW.addBoard(client, map, timeElapsed, maxConnections, name);
        
        
    }
    public void initClient()
    {
    	String[] args = new String[2];
    	args[0] = server;
    	args[1] = port;
        
        loadingPlayer = new Player(null,0);
        loadingPlayer.isWalking = true;
    	
	client = new EtWClient(args,this);
        client.start();
        repaint();
        
    }
    
    public void receiveMessageFromServer(String message)
    {
        if (message.startsWith("Time elapsed: ")){
            timeElapsed = Long.valueOf(message.substring(14));
            System.out.println("timeElapsed : " + timeElapsed);
        }
        if (message.startsWith("Max number of Connections: ")){
            maxConnections = Integer.valueOf(message.substring(27));
            System.out.println("maxConnections : " + maxConnections);
        }
    	String[] messageSplit = message.split(";");
        //System.out.println(message);
    	
    	switch (messageSplit[0]){
            case "MAP":
                mapStrings.add(message);
                progress += 1;
                loadingPlayer.moving();
                repaint();
                if (progress == 240 + objectsNumber){
                    map = EtWClientServerProtocol.ConvertStringToMap(mapStrings);
                    initGame();
                }
            break;
            case "NAME":
                mapName = messageSplit[1];
                objectsNumber = Integer.parseInt(messageSplit[2]);
            break;
        }

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
        Image playerImage;
        Image loadingBarImage;
        
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHints(rh);
        
        playerImage = loadingPlayer.getImage();
        if (objectsNumber > 0){
            double percent = ((double) progress)/(240 + objectsNumber);
            System.out.println(percent);
            g2d.drawImage(playerImage, (int) (380 + percent * 400), 510 , this);

            loadingBarImage = ImageManager.LoadBufferedImage("staminaBar.png", Boolean.TRUE);
            if ((int) (400 * percent) > 0){
                loadingBarImage = loadingBarImage.getScaledInstance((int)(400 * percent), 20, java.awt.Image.SCALE_SMOOTH);
                g2d.drawImage(loadingBarImage, 400, 550,this);
            }
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
}


