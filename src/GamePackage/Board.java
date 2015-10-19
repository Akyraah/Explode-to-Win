package GamePackage;
import Util.*;
import ClientServerComm.*;
import ExplodetoWin.ExplodeToWin;
import ExplodetoWin.Map;
import ExplodetoWin.TeamAndClassMenu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


public class Board extends JPanel implements Runnable {

/*      Akira: plateforme de saut
        Akira: bug sur la reception du e et le .getbound
        Akira: le clic sur les armes
        Akira: le bug de la tyrolienne de dos
        Akira: l'espace pour prendre la tyrolienne
        Retournement quand on tire
Balle qui traverse mur
        Bug à la connection lorsque l'autre clique/bouge
Possible bleu de gagner apres rouge gagne
Sync Block
        Co qui bug pour le choix de class
Peut placer block sur soit
toolAction outOfBoundException (y)
casser tyrolienne quand arrivée pu la
        objets consommables pas rechargeables
        setTeam
Grapin
Clic long sur Dynamite ???
        camera pas teamé
Scores et kill frag
1489
490 Player
        Pompe Dégats
Balle qui vole ???

*/
    private static final long serialVersionUID = 1L;
    
    private final int DELAY = 25;
    private Thread animator;
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    // TABLEAUX:
    private Player[] players;
    private ArrayList<PlaceableObject> objects;
    private ArrayList<KillPrint> kills;
    private boolean[][] fog;
    
    private int[] deathScores;
    private int[] teamKillScores;
    private int[] killScores;
    
    //OBJETS:
    public TeamAndClassMenu TaCMenu;
    private Map map;
    private Map mapWithFog;
    public ExplodeToWin etW;
    
    //BUTTONS:
    public Button[] itemButtons;

    //private ArrayList<ArrayList<Point>> fieldOfVision;
    private FogNode fieldOfVision;
    // VARIABLES:
    private final long deathTimer = 3;
    private long deathDate;
    public boolean blueSafeFull;
    public boolean redWin;
    public boolean blueWin;
    private long startDate;
    private long endDate = 360;
    private long elapsedTime;
    // COMM:
    public EtWClient client;
    public int numClient;
    public int maxConnections;

    public boolean isKeyQPressed = false;
    public boolean isKeyDPressed = false;
    public boolean isKeySpacePressed = false;
    public boolean isKeyShiftPressed = false;
    public boolean isKeyEPressed = false;
    public boolean isKeySPressed = false;
    
    public boolean isShowingScores = false;
    
    public String clientName;
    
    
    ImagesLibrary imagesLibrary;
    
    public long pingTest;
    
    //
    // INITIALISATION :
    //
    public Board(long elapsedTime, int maxConnections, EtWClient client, Map map, ExplodeToWin etW, String playerName)
    {
        setFocusable(true);
        setSize(1206,728);	
        this.requestFocus(true);
        this.setDoubleBuffered(true);
        this.setFocusTraversalKeysEnabled(false);
        
        this.etW = etW;
        this.maxConnections = maxConnections;
        this.startDate = - elapsedTime;
        clientName = playerName;
        
        setKeyBindings();
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMotionAdapter());
      
        setBackground(Color.white);
        setDoubleBuffered(true); 
        blueSafeFull = true;
        redWin = false;
        blueWin = false;
        //startDate = System.nanoTime();
        
        imagesLibrary = new ImagesLibrary(false);


        service.scheduleAtFixedRate(new PlayerSynchronisation(), 5000, 1000, TimeUnit.MILLISECONDS);
	initArrays();
        initClient(client);
        initMap(map);
        initFieldOfVision();
        
        itemButtons = new Button[4];
        for (int i = 0; i < 4; i++){
            itemButtons[i] = new Button("buttonItem.png",68,117 + 150 * i, 616);
        }
        


    }
    
    public void initArrays()
    {
    	
    	players = new Player[maxConnections];
        deathScores = new int[maxConnections];
        killScores = new int[maxConnections];
        teamKillScores = new int[maxConnections];
        objects = new ArrayList<>();
        kills = new ArrayList<>();
    }
    
    public void initMap(Map map)
    {
        this.map = map;
        mapWithFog = new Map(map.mapName);
        fog = new boolean[240][60];
    }
   
    public void initFieldOfVision()
    {
    	//fieldOfVision = new ArrayList<ArrayList<Point>>();
    	fieldOfVision = new FogNode();
    	int posStartX = 0;
    	int posStartY = 0;
    	int posX;
    	int posY;
    	int endPosX;
    	int endPosY;
    	int deltaPosX;
    	int deltaPosY;
    	int sx;
    	int sy;
    	int err;
    	int e2;
    	for (int i = -1; i < 3; i += 2){
            for (double angleDegree = -90; angleDegree <= 90; angleDegree += 0.5){	
                ArrayList<Point> list = new ArrayList<>();
                posX = posStartX;
                posY = posStartY;

                endPosX = (int) (i * Math.cos(Math.toRadians(angleDegree)) * 2000.0);
                endPosY = (int) (i * Math.sin(Math.toRadians(angleDegree)) * 2000.0);

                deltaPosX = Math.abs(endPosX - posX);
                deltaPosY = Math.abs(endPosY - posY);
                if (posX <= endPosX){
                        sx = 1;
                }
                else{
                        sx = -1;
                }
                if (posY <= endPosY){
                        sy = 1;
                }
                else{
                        sy= -1;
                }
                err = deltaPosX - deltaPosY;
                boolean outOfRange = false;
                while (!(posX == endPosX && posY == endPosY) && !outOfRange){
                    Point point = new Point();
                    point.x = posX;
                    point.y = posY;
                    if (Math.sqrt(Math.pow(posX,2) + Math.pow(posY,2)) < 100 && Math.abs(angleDegree) <= 75 && i == 1){

                            list.add(point);

                    }
                    else if (Math.sqrt(Math.pow(posX,2) + Math.pow(posY,2)) < 7){
                            list.add(point);
                    }
                    else {
                            outOfRange = true;
                    }
                    e2 = 2 * err;
                    if (e2 > - deltaPosY){
                            err = err - deltaPosY;
                            posX = posX + sx;
                    }
                    if( e2 < deltaPosX){
                            err = err + deltaPosX;
                            posY = posY + sy;
                    }
                }

                FogNode fogNode = fieldOfVision;
                for (Point list1 : list) {
                    fogNode = fogNode.addToEnd(list1);
                }

            }
    	}
    	

    	System.out.println("number of leaves: " + fieldOfVision.numberOfLeaves());
    	
    	/*for (double angleDegree = -90; angleDegree <= 90; angleDegree += 0.01){
    		
    		ArrayList<Point> list = new ArrayList<Point>();
        	posX = posStartX;
        	posY = posStartY;
        	
        	endPosX = (int) (Math.cos(Math.toRadians(angleDegree)) * 50);
        	endPosY = (int) (Math.sin(Math.toRadians(angleDegree)) * 50);
        	
        	deltaPosX = Math.abs(endPosX - posX);
        	deltaPosY = Math.abs(endPosY - posY);
        	if (posX <= endPosX){
        		sx = 1;
        	}
        	else{
        		sx = -1;
        	}
        	if (posY <= endPosY){
        		sy = 1;
        	}
        	else{
        		sy= -1;
        	}
        	err = deltaPosX - deltaPosY;

        	while (!(posX == endPosX && posY == endPosY)){
        		Point point = new Point();
        		point.x = posX;
        		point.y = posY;
        		if (Math.abs(point.x) < 120 && Math.abs(point.y) < 60){
        			list.add(point);
            		
        		}
        		e2 = 2 * err;
        		if (e2 > - deltaPosY){
        			err = err - deltaPosY;
        			posX = posX + sx;
        		}
        		if( e2 < deltaPosX){
        			err = err + deltaPosX;
        			posY = posY + sy;
        		}
        	}
        	fieldOfVision.add(list);
        	
        	//
    	}
        int total = 0;
        for (int i = 0; i < fieldOfVision.size(); i++){
        	for (int j = 0; j < fieldOfVision.get(i).size(); j++){
        		total ++;
        	}
        }
        System.out.println(total);*/
    }
    //
    // TIMERS :
    //
    
        @Override
    public void addNotify() 
    {
        super.addNotify();
    	animator = new Thread(this);
    	animator.start();
    }
    
    class PlayerSynchronisation implements Runnable 
    {
        @Override
    	public void run(){
    		sendPlayerToServer(players[numClient]);
    	}
    }
    
    //
    //
    // --------------
    // CYCLE DU JEU:
    // --------------
    //
    //
        @Override
public void run ()
    {
    	long beforeTime, timeDiff, sleep ;
    	beforeTime = System.currentTimeMillis();
    	while (true) {
            cycle ();
            repaint ();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;
            if (sleep < 0) {
                    sleep = 1;
            }

            try {
                    Thread.sleep(sleep);
            } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
    	}
    }
    
    private void cycle()
    { 
        // Timer de temps :
        elapsedTime = System.nanoTime() - startDate;
        long elapsedTimeInSeconds =  TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        if (elapsedTimeInSeconds > endDate){
            blueWin = true;
        }
        
        // Gestion Deplacement Joueurs et Balles :
        for (Player p : players) {
            if (p != null){
                if (!p.isDead){
                   playerMovement(p);
                }
            
                for (int i = p.bullets.size() -1; i>=0; i--) {
                    if(bulletMovement(p.bullets.get(i), p)){
                        p.bullets.remove(i);
                    }
                }
            }

            
        }
        
        // Gestion des kills :
        for (int i = kills.size() - 1; i >= 0; i --){
            kills.get(i).timer ++;
            if ( kills.get(i).timer >= kills.get(i).endTimer){
                kills.remove(i);
            }
        }
        
        // Resolution des actions spécifiques aux objets:
        
        for (int j = objects.size() - 1; j >= 0 ; j--) {
            PlaceableObject obj = objects.get(j);
            obj.objectAction();
            boolean remove = false;
            if (!obj.exists) {
                resolveObjectAction(obj);
                remove = true;
            }
            int posX;
            int posY;
            switch(obj.name){
                case "Camera":
                    posX = obj.x/10;
                    posY = obj.y/10;
                    if (map.blocks[posX][posY-1] == null && map.blocks[posX+1][posY-1] == null){
                        remove = true;
                    }
                    break;
                case "Pylon":
                    posX = obj.x/10;
                    posY = obj.y/10;
                    if (map.blocks[posX][posY+4] == null && map.blocks[posX+1][posY+1] == null){
                        remove = true;
                    }
                    break;
                case "LandMine":
                    for (Player p : players){
                        if (p != null){
                            double distance = Math.sqrt(Math.pow(p.getBounds().getCenterX() - obj.getBounds().getCenterX(), 2) + Math.pow(p.getBounds().getCenterY() - obj.getBounds().getCenterY(),2));
                            if (distance < obj.detectionRadius && p.getBounds().getCenterY() < obj.getBounds().getCenterY()){
                                resolveObjectAction(obj);
                                remove = true;
                            }
                        }

                    }
                    break;
            }
            
            if (remove){
                objects.remove(j);
            }
        }
    	
        Player thisP = players[numClient];
    	thisP.actionTiming();
		
        // Gestion du tir:
    	if (thisP.fire() && thisP.items[thisP.itemNumber].type.equalsIgnoreCase("weapon")){
            String message = "bullet;" + String.valueOf(thisP.itemNumber) +";";
            for (int i = 0; i < thisP.items[thisP.itemNumber].bulletNumber; i++){
                double angleVariation = (Math.random() - 0.5) * 2 * thisP.items[thisP.itemNumber].angleVariation;
                message += angleVariation +";";
                thisP.items[thisP.itemNumber].fireCount = 1;
            }
            sendEventToServer(null,null,message);
        }


    }
    
    public void playerMovement(Player p)
    {
        p.moving();
        Rectangle r1 = p.getBounds();
        Rectangle[] r1X;
        Rectangle[] r1Y;
        Rectangle r2;
        boolean[] collidedX;
        boolean[] collidedY;
        int depX;
        int depY;
        if (p.isFollowingLine){
            double distance = Math.sqrt(Math.pow(p.getBounds().getCenterX() - p.lineFollowed.xSup, 2) + Math.pow(p.getBounds().getCenterY() - p.lineFollowed.ySup, 2));
            double speed = 6;
            depX = (int)(((p.lineFollowed.xSup - p.x )/distance)*speed);
            depY = (int)(((p.lineFollowed.ySup - p.y)/distance)*speed);
            
            if (distance < 25){
                p.isFollowingLine = false;
            }
        }
        else {
            depX = p.dx;
            depY = p.dy;
        
            if ((p.isRunning && !p.isInAir )|| (p.isRunning && p.isJumping)){
                depX += p.dx;
            }
        }

        
        r1X = new Rectangle[Math.abs(depX) + 1];
        r1Y = new Rectangle[Math.abs(depY) + 1];
        collidedX = new boolean[Math.abs(depX) + 1];
        collidedY = new boolean[Math.abs(depY) + 1];
        
        for (int i = 0; i <= Math.abs(depX); i++){
            r1X[i] = new Rectangle(r1.x + i * (int)Math.signum(depX), r1.y,r1.width,r1.height);
            collidedX[i] = false;
        }
        for (int i = 0; i <= Math.abs(depY); i++){
            r1Y[i] = new Rectangle(r1.x, r1.y + i* (int)Math.signum(depY),r1.width,r1.height);
            collidedY[i] = false;
        }

        boolean sol = false;
        int posX = p.x + depX;
        int posY = p.y + depY;

        int leftTile = (int)Math.floor((float)posX / 10);
        int rightTile = (int)Math.ceil(((float)(posX + r1.width)/ 10));
        int topTile = (int)Math.floor((float)posY / 10);
        int bottomTile = (int)Math.ceil(((float)(posY + r1.height) / 10));
        boolean intersects = false;
        r1Y[0].height += 2;
        for (int y = topTile; y <= bottomTile; ++y)
        {
            for (int x = leftTile; x <= rightTile; ++x)
            {
                if (y >= 0 && y < 60 && x >= 0 && x < 240){
                    if (map.blocks[x][y] != null){
                        r2 = map.blocks[x][y].getBounds();
                        for (int i = 1; i <= Math.abs(depX); i++){
                            if (r1X[i].intersects(r2) && ! map.blocks[x][y].type.equalsIgnoreCase("Platform")){
                                collidedX[i] = true;
                            }
                        }
                        for (int i = 1; i <= Math.abs(depY); i++){
                            if (r1Y[i].intersects(r2) && ! (map.blocks[x][y].type.equalsIgnoreCase("Platform") && p.isPassingTrough)){
                                collidedY[i] = true;
                            }
                        }
                        
                        if (r1.intersects(r2) && !map.blocks[x][y].type.equalsIgnoreCase("Platform")){
                            intersects = true;
                        }
                        if (r1Y[0].intersects(r2) && !(map.blocks[x][y].type.equalsIgnoreCase("Platform") && p.isPassingTrough)){
                            if ((r2.y) >= (r1.y + r1.getBounds().height)){
                                sol = true;
                            }
                        }
                        if (r1Y[0].intersects(r2) && (map.blocks[x][y].type.equalsIgnoreCase("Platform") && p.isRestablishing)){
                            if ((r2.y) >= (r1.y + r1.getBounds().height)){
                                sol = true;
                                
                            }
                        }

                    }
                }
                
            }
        }

        if (p.y >= 600 - p.height) {
            p.y = 600 - p.height;
            sol = true;
        }
        


        //
        // ------------------------
        //

        // Résolution des mouvements :
        int depRX = Math.abs(depX);
        int depRY = Math.abs(depY);
        
        while(collidedX[depRX]){
            depRX --;
        }
        while(collidedY[depRY]){
            depRY --;
        }
        
        p.x += (int) Math.signum(depX)*depRX;
        
        while (depRY > 0){
            if (p.isRestablishing){
                for (int x = leftTile; x <= rightTile; x++){
                    if (x >= 0 && bottomTile >= 0 && x < 240 && bottomTile < 60){
                        if (map.blocks[x][bottomTile] != null){
                            r2 = map.blocks[x][bottomTile].getBounds();
                            if (r1Y[0].intersects(r2) && (map.blocks[x][bottomTile].type.equalsIgnoreCase("Platform") && p.isRestablishing)){
                                if ((r2.y) >= (r1.y + r1.getBounds().height)){
                                    sol = true;
                                    depRY = 0;
                                }
                            }
                        }
                    }


                }
            }
            if (depRY != 0){
                r1Y[0].y += (int) Math.signum(depY);
                p.y += (int) Math.signum(depY);
            
            }
            depRY --;
            

        }
        
        if (!intersects && p.isRestablishing && sol){
            p.isRestablishing = false;
            p.isPassingTrough = false;
            p.isInAir = false;
            depY = 0;
        }
        
        
        
        /*if(Math.abs(depY)> 0 && collidedY[1]){
            p.isJumping = false;
        }*/
        if (!p.isJumping){
            p.isInAir = !sol;
            if (p.isPassingTrough){
                p.isPassingTrough = !sol;
            }
        }
       
    }
    
    public Boolean bulletMovement(Bullet b, Player p)
    {
        b.dx = b.dx + b.bulletSpeed*Math.cos(b.angle);
    	b.dy = b.dy + b.bulletSpeed*Math.sin(b.angle);
    	
    	int DX;
    	int DY;
    	
    	DX = (int) b.dx;
    	DY = (int) b.dy;
    	b.dx = b.dx - DX;
    	b.dy = b.dy - DY;
        
    	Rectangle r1;
        r1 = b.getBounds();
        Rectangle r2;
        
                
        int xDep = b.x;
        int yDep = b.y;
        int xFin = b.x + DX;
        int yFin = b.y + DY;
        
        int startX = Math.min(xDep/10, xFin/10);
        int endX = Math.max(xDep/10, xFin/10) + 1;
        int startY = Math.min(yDep/10, yFin/10);
        int endY = Math.max(yDep/10, yFin/10) + 1;
        
        boolean intersected = false;
        Line2D line = new Line2D.Float(xDep,yDep,xFin,yFin);
        
        
        for (int i = 0; i < players.length; i ++) {
            if (players[i] != null && b.name.equalsIgnoreCase("normal")){
                if (!players[i].isDead){
                    r2 = players[i].getBounds();
                    if (line.intersects(r2) && !intersected) { 
                        players[i].loseHealth(b.weapon.bulletDamage);
                        if (players[i].isDead){
                            if (i == numClient){
                                deathDelay();
                            }
                            kills.add(new KillPrint(100,p, players[i], b.weapon.name));
                            if(p.team.equalsIgnoreCase(players[i].team)){
                                teamKillScores[p.num] ++;
                            }
                            else{
                                killScores[p.num] ++;
                            }
                            deathScores[i] ++;
                        }
                        if (players[i].isDead){
                            players[i].isFiring = false;
                        }
                        return true;
                    }
                }
            }
            
        }
        boolean bool = false;
        if (b.x > 0 && b.x <2400 && b.y>0 && b.y < 600){
            for (int x = startX; x<endX; x++){
                for (int y = startY; y<endY; y++){
                    if (x > 0 && y  > 0 && x < map.blocks.length && y < map.blocks[0].length){
                        if (map.blocks[x][y] != null){
                            r2 = map.blocks[x][y].getBounds();
                            if (line.intersects(r2)){
                                if (b.name.equalsIgnoreCase("pylon")){
                                   b.pylon.setPos(r2);
                                }
                                bool = true;
                            }
                        }
                    }
                }
            }
        }
        else{
            return true;
        }
        if (bool){
            return true;
        }
        
        b.x += DX;
    	b.y += DY;
        b.drawBullet();
        
        return false;
    }
    
            //
            // Actions :
            //
    
    public void toolAction(int eventX, int eventY, Player player)
    {
    	double distanceAuCarre;
        Item item = player.items[player.itemNumber];
        distanceAuCarre = Math.pow((player.getBounds().getCenterX() - eventX), 2) + Math.pow((player.getBounds().getCenterY() - eventY), 2);
        Boolean actionResolved = false;
    	switch (item.name){
            case "Dynamite":
    		if (distanceAuCarre < Math.pow(item.range, 2)){
                    String[] args = new String[3];
                    args[0] = String.valueOf(item.delayTimer);
                    args[1] = String.valueOf(item.radius);
                    args[2] = String.valueOf(item.damage);
                    PlaceableObject obj = new PlaceableObject("Dynamite", eventX, eventY, args);
                    obj.numPlayer = player.num;
                    objects.add(obj);
                    actionResolved = true;
        	}
    		break;
            case "Shovel":
                if (distanceAuCarre < Math.pow(item.range, 2)){
                    String[] args = new String[3];
                    args[0] = Integer.toString(item.radius);
                    objects.add(new PlaceableObject("Shovel", eventX, eventY, args));
                    actionResolved = true;
                }
                break;
            case "Camera":
                if (distanceAuCarre < Math.pow(item.range, 2) ){
                    boolean placeable = true;
                    if (!(map.blocks[eventX/10][eventY/10] == null)){
                        placeable = false;
                    }
                    
                    if (eventY/10 - 1 >= 0){
                        if ((map.blocks[eventX/10 - 1][eventY/10] == null) ){
                            placeable = false;
                        }
                    }
                    if (placeable){
                        String[] args = new String[3];
                        args[0] = player.team;
                        args[1] = Integer.toString(player.orientation);
                        objects.add(new PlaceableObject("Camera", (eventX/10)*10, (eventY/10)*10, args));
                        actionResolved = true;
                    }

                }
                break;
            case "Barricade":
                if (distanceAuCarre < Math.pow(item.range, 2) && map.blocks[eventX/10][eventY/10] == null){
                    if (player.num == numClient){
                        sendRequestToServer("Block;"+(eventX/10)+";"+(eventY/10)+";"+"Barricade"+";");
                    }
                    actionResolved = true;
                }
                break;
            case "Pylon":
                if (distanceAuCarre < Math.pow(item.range, 2)){
                    int leftTile = (player.x + player.width/2)/10;
                    int rightTile = leftTile + 1;
                    int topTile = (player.y + player.height - 40)/10;
                    int bottomTile = topTile + 3;
                    player.setOrientation(eventX);
                    double factor = -1 + 2*player.orientation;
                     
                    boolean possibleToPlace = true;
                    for (int x = leftTile; x <= rightTile; x++){
                        for (int y = topTile; y <= bottomTile; y++){
                            if (map.blocks[x][y] != null){
                                possibleToPlace = false;
                            }
                        }
                    }
                    if (leftTile >= 0 && rightTile >= 0 && leftTile < 240 && rightTile < 240 && bottomTile + 1 > 0 && bottomTile + 1 < 60) {
                        if (map.blocks[leftTile][bottomTile+1] == null && map.blocks[rightTile][bottomTile+1] == null){
                            possibleToPlace = false;
                        }
                    }

                    
                    if (possibleToPlace &&  (topTile*10 < eventY)){
                        double dX = (eventX - rightTile*10);    	
                        double dY = (eventY - topTile*10);

                        double rotationAngle = Math.atan(dY/dX);
                        Bullet b = new Bullet( rightTile*10,topTile*10, (int) (15 * factor), rotationAngle, player.items[player.itemNumber], "pylon");
                        
                        player.bullets.add(b);	
                        
                        String[] args = new String[3];
                        args[0] = Integer.toString(0);
                        args[1] = Integer.toString(0);
                        PlaceableObject obj = new PlaceableObject("Pylon", leftTile*10, topTile*10, args);
                        
                        objects.add(obj);
                        b.pylon = obj;
                        actionResolved = true;
                    }
                
                

                }
                break;
            case "LandMine":
                if (distanceAuCarre < Math.pow(item.range, 2)){
                    boolean placeable = true;
                    if (!(map.blocks[eventX/10][eventY/10] == null )){
                        placeable = false;
                    }
                    if ((eventY + 1 < 60)){
                        if ((map.blocks[eventX/10][eventY/10] == null )){
                            placeable = false;
                        }
                        
                    }
                    if (placeable){
                        String[] args = new String[5];
                        args[0] = player.team;
                        args[1] = players[numClient].team;
                        args[2] = String.valueOf(item.radius);
                        args[3] = String.valueOf(item.detectionRadius);
                        args[4] = String.valueOf(item.damage);
                        PlaceableObject obj = new PlaceableObject("LandMine", (eventX/10)*10, (eventY/10)*10, args);
                        obj.numPlayer = player.num;
                        objects.add(obj);
                        actionResolved = true;
                    }
        	}
                break;
    	}
        
        if (actionResolved && item.consummable){ 
            item.magazine -= 1;
            if (item.magazine == 0){
                player.items[player.itemNumber] = null;
                player.c.items[player.itemNumber] = null;
                player.changeItem(0);
                player.isFiring = false;
            }
        }
        

    	
    }
    
    public void resolveObjectAction(PlaceableObject obj)
    {
    	switch(obj.name){
            case "Dynamite":	
                    removeBlocks(obj, "All");
                    inflictDamage(obj, "Progressive");
            break;
            case "Shovel":
                    removeBlocks(obj, "Dirt");
                    removeBlocks(obj, "Grass");
                    removeBlocks(obj, "Smoke");
            break;
            case "LandMine":
                    removeBlocks(obj, "All");
                    inflictDamage(obj, "Progressive");
                break;
            }
    }
    
    public void removeBlocks(PlaceableObject obj, String condition)
    {
        int x = obj.x;
        int y = obj.y;
        int radius = obj.radius;
    	int startX = x/10;
        int startY = y/10;
        int startLoop = (int) Math.ceil((double) radius / (double) 10);
        for (int k = -startLoop; k<(startLoop + 1); k++){		
                for (int l = -startLoop; l<(startLoop + 1); l++){	
                        if ((startX + k) > 0 && (startY + l)  > 0 && startX + k < map.blocks.length && startY + l < map.blocks[0].length){
                        if (map.blocks[startX + k][startY + l] != null){
                                if (map.blocks[startX + k][startY + l].type.equalsIgnoreCase(condition) || condition.equalsIgnoreCase("All")){
                                        int blockX = (int) map.blocks[startX + k][startY + l].getBounds().getCenterX();
                                        int blockY = (int) map.blocks[startX + k][startY + l].getBounds().getCenterY();
                                        if (Math.sqrt(Math.pow(blockX - x, 2) + Math.pow(blockY - y,2)) < radius){
                                            if (obj.numPlayer == numClient){
                                                sendRequestToServer("Block;"+(startX+k)+";"+(startY+l)+";"+"null"+";");
                                            }    
                                        }
                                }
                        }
                        }

                }
        }
    }
    
    public void inflictDamage(PlaceableObject obj, String method)
    {
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null){
                if (!players[i].isDead){
                int playerX = (int) players[i].getBounds().getCenterX();
                int playerY = (int) players[i].getBounds().getCenterY();
                double distance = Math.sqrt(Math.pow(playerX - obj.x, 2) + Math.pow(playerY - obj.y,2));
                if (distance < obj.radius) {
                    switch (method) {
                        case "Constant":
                            players[i].loseHealth(obj.damage);
                            break;
                        case "Progressive":
                            int trueDamage;
                            if (distance > 0){
                                trueDamage = (int) (   ((double)obj.damage * 500) / Math.pow((double)distance,2)    );
                            }
                            else{
                                trueDamage = obj.damage;
                            }
                            players[i].loseHealth(trueDamage);
                            break;
                    }
                }
                if (players[i].isDead){
                    if (i == numClient){
                        deathDelay();
                    }
                    kills.add(new KillPrint(100,players[obj.numPlayer], players[i], obj.name));
                    if(players[obj.numPlayer].team.equalsIgnoreCase(players[i].team)){
                        teamKillScores[obj.numPlayer] ++;
                    }
                    else{
                        killScores[obj.numPlayer] ++;
                    }
                    deathScores[i] ++;
                }
            }
            }
            
            
        }
    }

    //
    //
    // ----------
    // GRAPHISME
    // ----------
    //
    //
    
        @Override
    public void paint(Graphics g)
    {	
        //super.paint(g);
        drawBufferedBoard(g);
        
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    
    public void drawBufferedBoard(Graphics g){
        Graphics bufferedGraphics;
        Image bufferedBoard = createImage(1206,728);
        bufferedGraphics = bufferedBoard.getGraphics();
        
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        drawDesigns(bufferedGraphics,rh);
        drawObjects(bufferedGraphics,rh);
        drawBottomLayer(bufferedGraphics,rh);
       
        
        bufferedGraphics.dispose();
        g.drawImage(bufferedBoard, 0, 0, this);
        
    }
    
    public void drawObjects(Graphics g, RenderingHints rh)
    {	

        
        Image BlockImage;
        Image BulletImage;

        //Image fogImage;
        Image fieldOfVisionImage;
         
    	Graphics2D g2d = (Graphics2D)g; 
        g2d.setRenderingHints(rh);

        //Calcul du Champ de Vision:
        for (int i = 0; i < 240; i++){
            for (int j = 0; j < 60; j++){
                fog[i][j] = true;
            }
    	}
        for (Player p: players){
            if (p != null){
                if (p.team.equalsIgnoreCase(players[numClient].team)){
                    double px = p.getBounds().getCenterX();
                    double py = p.getBounds().getCenterY();
                    calculateFog(px, py, p.orientation,"Player");
                }
            }

        }
        for (PlaceableObject obj: objects){
            if (obj.name.equalsIgnoreCase("Camera")){
                if (obj.team.equalsIgnoreCase(players[numClient].team)){
                    double px = obj.x + 4;
                    double py = obj.y + 4;
                    calculateFog(px, py, obj.orientation, "Camera");
                }

            }
        }
        

        // Affichage des Blocs:

        for (int i = 0; i<mapWithFog.blocks.length; i++){
            for (int j=0; j<mapWithFog.blocks[0].length; j ++){
                if (mapWithFog.blocks[i][j] != null){
                BlockImage = imagesLibrary.blockImages[mapWithFog.blocks[i][j].factor];//imageTempB.getSubimage(0,10*mapWithFog.blocks[i][j].factor, 10, 10); //Transparency.makeColorTransparent(imageTempB.getSubimage(0,10*blocktype, 10, 10), Color.black);    
                    if (i*10 - getOffset() + 10 > 0 && i*10 - getOffset() < 1206){
                        g2d.drawImage(BlockImage, i*10 - getOffset(), j*10, this);
                    }
                    
                }
            }
        }


        
        for (Player p : players) {
            if (p != null){
                
                    // Affichage des Joueurs:
                drawPlayer (g2d, p);
                
                   // Affichage des Balles :
                for (Bullet b : p.bullets) {
                    if (b.x > 0 && b.y > 0 && b.x < 2400 && b.y < 600){
                        if (!fog[b.x/10][b.y/10]){
                            BulletImage = b.bulletImage;
                            if (b.x - getOffset() + BulletImage.getWidth(this) > 0 && b.x - getOffset() < 1206){
                                g2d.drawImage(BulletImage, (b.x - b.width)- getOffset(), b.y - b.width, this);
                            }
                            if (b.name.equalsIgnoreCase("pylon")){
                                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 0 }, 0));
                                g2d.setColor(Color.DARK_GRAY);
                                g2d.drawLine(b.pylon.x  + 10 - getOffset(), b.pylon.y, b.x - getOffset(), b.y);
                            }
                        }
                    }
                }
            }
            
            
        } 
        for (PlaceableObject obj : objects) {
            boolean visibleByPlayer = false;
            if (obj.visible && !fog[obj.x/10][obj.y/10]){
                visibleByPlayer = true;
            }
            else{
                if (obj.name.equalsIgnoreCase("Pylon")){
                    int startX = Math.min((obj.x + 10)/10, (obj.xSup / 10));
                    int endX = Math.max((obj.x + 10)/10, (obj.xSup / 10));
                    int startY = Math.min((obj.y + 10)/10, (obj.ySup / 10));
                    int endY = Math.max((obj.y + 10)/10, (obj.ySup / 10));
                    Line2D line = new Line2D.Float(obj.x  + 10, obj.y, obj.xSup, obj.ySup);
                    for (int x = startX ; x < endX +1; x ++){
                        for (int y = startY ; y < endY +1; y ++){
                            if (x >= 0 && y >= 0 && x <  240 && y < 60){
                                if (!fog[x][y]){
                                    visibleByPlayer = true;
                                }
                            }
                        }
                    }
                }
            }
            if (visibleByPlayer){
                int width = obj.objectImage.getWidth(null);
                int height = obj.objectImage.getHeight(null);
                if (obj.x - width/2 - getOffset() + obj.objectImage.getWidth(this) > 0 && obj.x - width/2 - getOffset() < 1206){
                     g2d.drawImage(obj.objectImage, obj.x - obj.offsetX - getOffset(), obj.y - obj.offsetY, this);
                }
                if (obj.name.equalsIgnoreCase("Pylon") && obj.set){
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 0 }, 0));
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawLine(obj.x  + 10 - getOffset(), obj.y, obj.xSup - getOffset(), obj.ySup);
                }
            }
        }

        // Affichage du champ de vision

        Graphics2D g2dbis = (Graphics2D)g;
        g2dbis.setRenderingHints(rh);
        g2dbis.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.10f));
        fieldOfVisionImage = ImageManager.LoadImage("fieldOfVision2.png", false);

        /*for (int i = 0; i < 240; i ++){
            for (int j = 0; j < 60; j++){
                if (fog[i][j]){
                    if (i*10 - getOffset() + fieldOfVisionImage.getWidth(this) > 0 && i*10 - getOffset() < 1206){
                        g2dbis.drawImage(fieldOfVisionImage,i*10 - getOffset() ,j* 10,this);
                    }
                    
                }
            }
        }
        */
        if (!players[numClient].isDead){
            for (int i = 0; i < 240; i ++){
                for (int j = 0; j < 60; j++){
                    if (!fog[i][j]){
                        if (i*10 - getOffset() + fieldOfVisionImage.getWidth(this) > 0 && i*10 - getOffset() < 1206){
                            g2dbis.drawImage(fieldOfVisionImage,i*10 - getOffset() ,j* 10,this);
                        }

                    }
                }
            }
        }
        
        // Affichage de l'écran de mort :
        
        g2dbis.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
        if (players[numClient].isDead){
            BufferedImage deathImage = ImageManager.LoadBufferedImage("DeathScreen.png", false);
            long elapsedTime = (System.nanoTime() - deathDate);
            String message = "";
            long elapsedTimeInSeconds =  TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
            
            message = String.valueOf(deathTimer - (int) elapsedTimeInSeconds);
                
            //deathImage =ImageManager.ImageToBufferedImage(deathImage, deathImage.getWidth(this), deathImage.getHeight(this)); //ImageManager.AddString(ImageManager.ImageToBufferedImage(deathImage, deathImage.getWidth(this), deathImage.getHeight(this)), cooldown, 580, 300, 30);
            g2d.drawImage(deathImage,0,0,this);
            
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 60) );
            g2d.setColor(Color.WHITE);
            g2d.drawString(message, 580,400);
        }
        if (blueWin){
            BufferedImage blueWinImage = ImageManager.LoadBufferedImage("blueWinScreen.png", false);
            g2d.drawImage(blueWinImage, 0, 0, this);
        }
        else if (redWin){
            BufferedImage redWinImage = ImageManager.LoadBufferedImage("redWinScreen.png", false);
            g2d.drawImage(redWinImage, 0, 0, this);
        }
        g2dbis.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        
        
        // Affichage du Timer :
        
        long elapsedTimeInSeconds =  TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        String message = String.valueOf(endDate - (int) elapsedTimeInSeconds);
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30) );
        g2d.setColor(Color.WHITE);
        g2d.drawString(message, 550,30);
        
        // Affichage des kills:
        
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20) );
        for (int i = 0; i < kills.size(); i++){
            Item item = new Item(kills.get(i).itemType,0);
            BufferedImage tempWeapon = ImageManager.LoadBufferedImage(item.itemSheetName, true);
            Image image;
            if (item.type.equalsIgnoreCase("weapon") || item.rotationAdjustable){
                    image = tempWeapon.getSubimage(item.width, 0, item.width, item.height).getScaledInstance((int) ( (double) item.width * 1.5),(int)( (double) item.height * 1.5),Image.SCALE_SMOOTH);
                    g2d.drawImage(image, 1100 - image.getWidth(null)/2, 30 + i*22 - image.getHeight(null)/2, this);
            }
            else
            {
                    image = tempWeapon.getSubimage(0, 0, item.width, item.height).getScaledInstance((int) ( (double) item.width * 1.5),(int)( (double) item.height * 1.5),Image.SCALE_SMOOTH);
                    g2d.drawImage(image, 1050 - image.getWidth(null)/2 + 30, 30 + i*22 - image.getHeight(null)/2, this);
            }
            if (kills.get(i).killer.team.equalsIgnoreCase("Red")){
                g2d.setColor(Color.RED);
            }
            else{
                g2d.setColor(Color.BLUE);
            }
            g2d.drawString(kills.get(i).killer.name,1000,30 + i * 22);

            if (kills.get(i).victim.team.equalsIgnoreCase("Red")){
                g2d.setColor(Color.RED);
            }
            else{
                g2d.setColor(Color.BLUE);
            }
            g2d.drawString(kills.get(i).victim.name,1130,30 + i * 22);
        }
        
        if (isShowingScores){
            g2dbis.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15) );
            BufferedImage scoreImage = ImageManager.LoadBufferedImage("scoreBoardBackground.png", false);
            g2d.drawImage(scoreImage, 200, 100, this);
            int blueNumber = 0;
            int redNumber = 0;
            for (int i = 0; i < players.length; i++){
                if (players[i] != null){
                    if (players[i].team.equalsIgnoreCase("Red")){
                        g2d.setColor(Color.RED);
                        g2d.drawString(players[i].name,605,170 + redNumber * 17);
                        g2d.drawString(String.valueOf(killScores[i]),910,170 + redNumber * 17);
                        g2d.drawString(String.valueOf(teamKillScores[i]),940,170 + redNumber * 17);
                        g2d.drawString(String.valueOf(deathScores[i]),970,170 + redNumber * 17);
                        redNumber ++;
                    }
                    else{
                        g2d.setColor(Color.BLUE);
                        g2d.drawString(players[i].name,205,170 + blueNumber * 17);
                        g2d.drawString(String.valueOf(killScores[i]),520,170 + blueNumber * 17);
                        g2d.drawString(String.valueOf(teamKillScores[i]),550,170 + blueNumber * 17);
                        g2d.drawString(String.valueOf(deathScores[i]),580,170 + blueNumber * 17);
                        blueNumber ++;
                    }
                }
            }
        }
  
        

            
            

        
    }
    
    public void drawPlayer(Graphics2D g2d, Player p){
        Image PlayerImage;
        Image BagImage;
        Image WeaponImage;
        Image lineHolderImage;
        
        int leftTile = (int)Math.floor((float)p.x / 10);
        int rightTile = (int)Math.ceil(((float)(p.x + p.getBounds().width)/ 10)) - 1;
        int topTile = (int)Math.floor((float)p.y / 10);
        int bottomTile = (int)Math.ceil(((float)(p.y + p.getBounds().height) / 10));
        boolean visible = false;
        for (int x = leftTile; x <=rightTile; x++){
            for (int y = topTile; y <=bottomTile; y++){
                if (x >= 0 && y >= 0 && x < 240 && y < 60){
                    if (!fog[x][y] && !p.isDead){
                        visible = true;
                    }
                }
            }
        }
        if (visible){
            if (p.isCarryingBag){
                BagImage = ImageManager.LoadBufferedImage("bag.png", true).getSubimage(14 * p.orientation, 0, 14, 13);
                BagImage = BagImage.getScaledInstance((int) Math.round(BagImage.getWidth(null)*2), (int) Math.round(BagImage.getHeight(null)*2), java.awt.Image.SCALE_SMOOTH);
                int offX;
                if (p.orientation == 1){
                    offX = -7;
                }
                else{
                    offX = 7;
                }
                g2d.drawImage(BagImage, p.x - getOffset() + offX, p.y + 10,this);
            }

            PlayerImage = p.getImage();
            if (p.x - getOffset() + PlayerImage.getWidth(this) > 0 && p.x - getOffset() < 1206){
                g2d.drawImage(PlayerImage, p.x - getOffset(), p.y, this);
            }
            if (p.isFollowingLine){
                lineHolderImage = ImageManager.LoadBufferedImage("lineHolder.png", true);
                lineHolderImage = lineHolderImage.getScaledInstance((int) Math.round(lineHolderImage.getWidth(null)*1.5), (int) Math.round(lineHolderImage.getHeight(null)*2), java.awt.Image.SCALE_SMOOTH);
                int offX;
                if (p.orientation == 1){
                    offX = -7;
                }
                else{
                    offX = 7;
                }
                g2d.drawImage(lineHolderImage, p.x - getOffset() + 3, p.y -2,this);
            }
        }
        
        // Affichage des Armes:
        
        if (!p.isDead){
            if (p.isFiring || p.isCarryingTool){
                if (p.x/10 >= 0 && p.y/10 >= 0 && p.x/10 < 240 && p.y/10 < 60){
                    if (!fog[p.x/10][p.y/10]){
                        Item w = p.items[p.itemNumber];
                        if (w != null){
                            WeaponImage = w.itemImage;
                            g2d.drawImage(WeaponImage, p.x + w.adjustOffSetX(p.orientation) - w.largeImageOffSet - getOffset(), p.y + w.offSetY - w.largeImageOffSet,this);

                        }
                    }
                }

            }   // Affichage des ObjetsPla�ables:
        }
    }
    
    public void drawDesigns(Graphics g, RenderingHints rh){
        Image designImage;
        Graphics2D g2d = (Graphics2D)g; 
        g2d.setRenderingHints(rh);

        for (Design design: map.backgroundDesigns){
            designImage = imagesLibrary.designImages[design.factor];
            if (design.posX - getOffset() + designImage.getWidth(this) > 0 && design.posX - getOffset() < 1206){
                g2d.drawImage(designImage, design.posX - getOffset() , design.posY, this);
            }
            
        }
    }
    
    public void drawBottomLayer(Graphics g, RenderingHints rh)
    {
    	
    	// BOTTOM LAYER :
        Graphics2D gBottom = (Graphics2D)g;
        gBottom.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));

        gBottom.setRenderingHints(rh);
        Image BottomLayer = ImageManager.LoadBufferedImage("BottomLayer.png", false);
        gBottom.drawImage(BottomLayer, 0, 600, this);
        // MAGAZINE : 
        Player player = players[numClient];
        gBottom.setFont(new Font("TimesRoman", Font.PLAIN, 25) );
        gBottom.setColor(Color.DARK_GRAY);
        gBottom.drawString(Integer.toString(player.items[player.itemNumber].magazine), 740,643);
        gBottom.drawString(Integer.toString(player.items[player.itemNumber].magazineCapacity), 765,672);
        // HEALTH :
        gBottom.setFont(new Font("TimesRoman", Font.PLAIN, 20) );
        gBottom.drawString(Integer.toString(player.health),970,674);
        gBottom.drawString(" / " + Integer.toString(player.maxHealth), 1000, 674);
        double percentage = (double) player.health/ (double) player.maxHealth;
        BufferedImage temp = ImageManager.LoadBufferedImage("healthBar.png", false);
        int width = (int) (temp.getWidth()*percentage);
        if (width > 0){
                Image healthBar = temp.getSubimage(0, 0, width, temp.getHeight());
                gBottom.drawImage(healthBar, 900, 646, this);
        }
        // STAMINA :
        percentage = (double) player.stamina/ (double) player.maxStamina;

        temp = ImageManager.LoadBufferedImage("staminaBar.png", false);
        width = (int) (temp.getWidth()*percentage);
        if (width > 0){
                Image staminaBar = temp.getSubimage(0, 0, width, temp.getHeight());
                gBottom.drawImage(staminaBar, 900, 623, this);
        }
        // RELOAD :
        Item it = player.items[player.itemNumber];
        if (it.isReloading){
                percentage = (double) it.reloadingCount/ (double) it.reloadTime;

                temp = ImageManager.LoadBufferedImage("reloadBar.png", false);
                int height = (int) (temp.getHeight()*percentage);
                if (height > 0){
                        Image staminaBar = temp.getSubimage(0, 0, temp.getWidth(), height);
                        gBottom.drawImage(staminaBar, 817, 673 - height, this);
                }
        }
    		       	
        // ITEMS :
        for (int i = 0; i<4; i++){
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
                                gBottom.drawImage(item, 116 - item.getWidth(null)/2 + i*150 + 30, 650 - item.getHeight(null)/2, this);
                        }

                }
        }
        
        for (Button button : itemButtons) {
            
            gBottom.drawImage(button.buttonImage, button.x, button.y, this);
        }
    		       	
    	       	
    	        
    }
    
    public void calculateFog(double px, double py, int orientation, String typeOfViewer)
    {

    	int posPX = (((int)px) / 10);
    	int posPY = ((int)py) / 10 ;
    	calculateFieldOfVision(px,py,posPX, posPY, fieldOfVision, orientation,typeOfViewer);
    }
    
    public void calculateFieldOfVision(double px, double py, int posPX,int posPY, FogNode node, int orientation, String typeOfViewer)
    {
        double distanceAuCarre = Math.pow((px - ((-1 + 2 * orientation) * node.x + posPX)*10), 2) + Math.pow((py - ((-1 + 2 * orientation) * node.y + posPY)*10), 2);
                    
    	int posX = (-1 + 2 * orientation) * node.x + posPX;
    	int posY = (-1 + 2 * orientation) * node.y + posPY;
    	if (posX >= 0 && posY >= 0 && posX < 240 && posY < 60){
            compareBlocksArrays(posX,posY);
            fog[posX][posY] = false;
            if (map.blocks[posX][posY] == null){
                for (FogNode leafList : node.leafList) {
                    calculateFieldOfVision(px,py,posPX, posPY, leafList, orientation,typeOfViewer);
                }
            }
            else if (map.blocks[posX][posY].type.equalsIgnoreCase("Barricade") && !typeOfViewer.equalsIgnoreCase("Camera")){
                if (distanceAuCarre < Math.pow(30,2)){
                    for (FogNode leafList : node.leafList) {
                        calculateFieldOfVision(px,py,posPX, posPY, leafList, orientation,typeOfViewer);
                    }
                }

            }
    	}
    	
    }
    
    public void compareBlocksArrays(int posX, int posY)
    {
    	
    	if (map.blocks[posX][posY] == null && mapWithFog.blocks[posX][posY] != null){
            mapWithFog.blocks[posX][posY] = null;
        }
        else if (map.blocks[posX][posY] != null && mapWithFog.blocks[posX][posY] == null){
            Block block = new Block(map.blocks[posX][posY].type);
            block.x = map.blocks[posX][posY].x;
            block.y = map.blocks[posX][posY].y;
            mapWithFog.blocks[posX][posY] = block;
        }
        else if (map.blocks[posX][posY] != null && mapWithFog.blocks[posX][posY] != null){
            if (map.blocks[posX][posY].type.equalsIgnoreCase(mapWithFog.blocks[posX][posY].type))
            {
            } else {
                Block block = new Block(map.blocks[posX][posY].type);
                block.x = map.blocks[posX][posY].x;
                block.y = map.blocks[posX][posY].y;
                mapWithFog.blocks[posX][posY] = block;
                }
        }
    	
    }
    
    //
    //
    // ------------
    // RESPAWN UI:
    // ------------
    //
    //
    
    public void setTeam(String team)
    {
        players[numClient].team = team;
        String spawnName ="";
        switch (team){
            case "red":
                spawnName = "redSpawn";
                break;
            case "blue":
                spawnName = "blueSpawn";
                break;
        }
        for (Marker marker: map.markers){
            if (marker.name.equalsIgnoreCase(spawnName)){
                players[numClient].spawnX = marker.posX;
                players[numClient].spawnY = marker.posY;
            }
        }
        players[numClient].teamSet = false;
    }
    
    public void respawn(PlayerClass c)
    {
        remove(TaCMenu);
        TaCMenu.setVisible(false);
        TaCMenu = null;
        players[numClient].respawn(c);
        sendClassToServer(c);
        setVisible(true);
        requestFocus();   
    }
    
    public void addTeamAndClassMenu()
    {
        //deathDelay();
        TaCMenu = new TeamAndClassMenu(this);
        setVisible(false);
        etW.add(TaCMenu);
    }
    
    public void deathDelay()
    {
        
        Timer t = new Timer();
        deathDate = System.nanoTime();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                addTeamAndClassMenu();

            }
        }, deathTimer * 1000);
        
        


    }
    
    //
    //
    // ------------
    // UTILITAIRE
    // ------------
    //
    //
    
    public void printAllNodes(Graphics2D g2d, FogNode node, Image fogImage){
    	g2d.drawImage(fogImage,node.x*10,node.y * 10,this);
        for (FogNode leafList : node.leafList) {
            printAllNodes(g2d, leafList, fogImage);
        }
    }
    
    public int getOffset(){
        int offsetX = 0;
        Player player = players[numClient];
        if (player.x < 600){
            offsetX = 0;
        }
        else if (player.x >= 1800){
            offsetX = 1200;
        }
        else{
            offsetX = player.x - 600;
        }
        return offsetX;
    }
    
    //
    //
    // ------------------------------
    // COMM AVEC LE CLIENT/SERVEUR :
    // ------------------------------
    //
    //
    
    public void joinGame()
    {
    	sendRequestToServer("PlayersData");
    	/*while (playerslength < numClient){
            Player newPlayer = new Player(this);
            players.add(newPlayer);
    	}*/
        addTeamAndClassMenu();
        Player player = new Player(this,numClient);
    	player.x = 200;
    	player.y = 200;
        player.name = clientName;
        
    	System.out.println("numClient : " + numClient);
    	//System.out.println("players.size : " + players.size());
    	players[numClient] = player;
    	sendPlayerToServer(players[numClient]);
        startDate += System.nanoTime();
    }
    
    public void initClient(EtWClient client)
    {

        this.client = client;
    }
    
    public void receiveMessageFromServer(String message)
    {

    	String[] messageSplit = message.split(";");
    	/*for (int i = 0; i< messageSplit.length; i++){
    		System.out.println(messageSplit[i]);
    	}*/
    	
    	int numPlayer;
    	
    	
    	switch (messageSplit[0]){
    	
    	case "EVENT":
    		try{
    			numPlayer = Integer.parseInt(messageSplit[1]);
        	}
    		catch (NumberFormatException e){
    			break;	
    		}
    		switch(messageSplit[2]){
        	case "aE":
                    if (players[numPlayer] != null){
                        String eventName = messageSplit[3];
        		Player player = players[numPlayer];
                        boolean PylonDetected = false;
                        if (eventName.equalsIgnoreCase("keySpacePressed") && !players[numPlayer].isDead){
                            for (PlaceableObject obj:objects){
                                if(obj.name.equalsIgnoreCase("Pylon")){
                                    double distanceAuCarre = Math.pow((player.getBounds().getCenterX() - obj.getBounds().getCenterX()), 2) + Math.pow((player.getBounds().getCenterY() - obj.getBounds().getCenterY()), 2);
                                    if (distanceAuCarre < Math.pow(25,2) && Math.abs(player.getBounds().getCenterX() - (obj.x + 10)) < 10 && obj.set){
                                        player.isFollowingLine = true;
                                        player.x = obj.x - 13;
                                        player.y = obj.y - 5;
                                        player.lineFollowed = obj;
                                        PylonDetected = true;
                                    }
                                }
                            }
                        }
                        if (!PylonDetected){
                            players[numPlayer].keyEvent(eventName);
                        }
                        //System.out.println((System.nanoTime() - pingTest) / 1000000);
                    }
                    

                    break;
        	case "mE":
                    try{
                        int eventID = Integer.parseInt(messageSplit[3]);
                        int eventX = Integer.parseInt(messageSplit[4]);
                        int eventY = Integer.parseInt(messageSplit[5]);
                        if (players[numPlayer] != null){
                            players[numPlayer].mouseEvent(eventID, eventX, eventY);
                        }

                        
                    }
                    catch (NumberFormatException e){
                        break;	
                    }
        		break;
        	case "fE":
        		break;
        	case "bullet":
                    try{
                        if (players[numPlayer] != null){
                            
                            int numItem = Integer.parseInt(messageSplit[3]);
                            for (int i = 4; i < messageSplit.length; i++){
                                players[numPlayer].launchBullet(Double.parseDouble(messageSplit[i]), numItem);
                            }
                            
                        }
                    }
                    catch (NumberFormatException e){
                        break;	
                    }
                    break;
                case "keyEPressed":
                    
                    if (players[numPlayer] != null){
                        if (!players[numPlayer].isDead){
                            Player player = players[numPlayer];
                            int posX = Integer.parseInt(messageSplit[3]);
                            int posY = Integer.parseInt(messageSplit[4]);
                            System.out.println("keyEPressed At " + posX + "," + posY);
                            
                            double distanceAuCarre;

                                for (Marker marker:map.markers){
                                    distanceAuCarre = Math.pow((player.getBounds().getCenterX() - marker.posX), 2) + Math.pow((player.getBounds().getCenterY() - marker.posY), 2);
                                    if (distanceAuCarre < Math.pow(30, 2)){
                                        if (marker.name.equalsIgnoreCase("blueSafe") && player.team.equalsIgnoreCase("red") && blueSafeFull){
                                            blueSafeFull = false;
                                            player.isCarryingBag = true;
                                        }
                                        if (marker.name.equalsIgnoreCase("redSafe") && player.team.equalsIgnoreCase("red") && player.isCarryingBag){
                                            player.isCarryingBag = false;
                                            redWin = true;
                                        }
                                    }

                                }
                                
                            double distanceSourisCarre = Math.pow((player.getBounds().getCenterX() - posX), 2) + Math.pow((player.getBounds().getCenterY() - posY), 2);

                            if (distanceSourisCarre < Math.pow(60, 2)){
                                for (int i = objects.size() - 1; i >= 0; i++){
                                    PlaceableObject obj = objects.get(i);
                                    if(obj.name.equalsIgnoreCase("Camera")){
                                        if (obj.getBounds().contains(posX, posY)){
                                            obj.orientation = 1 - obj.orientation;
                                        }
                                    }
                                    if(obj.name.equalsIgnoreCase("Pylon")){
                                        for (int j = 0; j < player.items.length; j++){
                                            if ( player.items[j] == null){
                                                player.items[j] = new Item("Pylon", player.width);
                                                if (obj.getBounds().contains(posX, posY)){
                                                    objects.remove(i);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "item1Pressed":
                    if (players[numPlayer] != null){
                        if (players[numPlayer].items[0] != null){
                            players[numPlayer].changeItem(0);
                        }
                        
                    }
                    break;
                case "item2Pressed":
                    if (players[numPlayer] != null){
                        if (players[numPlayer].items[1] != null){
                            players[numPlayer].changeItem(1);
                        }
                    }
                    break;
                case "item3Pressed":
                    if (players[numPlayer] != null){
                        if (players[numPlayer].items[2] != null){
                            players[numPlayer].changeItem(2);
                        }
                    }
                    break;
                case "item4Pressed":
                    if (players[numPlayer] != null){
                        if (players[numPlayer].items[3] != null){
                            players[numPlayer].changeItem(3);
                        }
                    }
                    break;
        	}
                
    		break;
            
    	case "PLAYER":
    		try{
    			numPlayer = Integer.parseInt(messageSplit[1]);
        	}
    		catch (NumberFormatException e){
    			break;	
    		}
    		if (numPlayer != numClient){
                    if (players[numPlayer] == null){
                        Player player = new Player(this,numPlayer);
                        players[numPlayer] = (player);
                    }
                    try{
                    players[numPlayer].x = Integer.parseInt(messageSplit[2]);
                    players[numPlayer].y = Integer.parseInt(messageSplit[3]);
                    players[numPlayer].dx = Integer.parseInt(messageSplit[4]);
                    players[numPlayer].dy = Integer.parseInt(messageSplit[5]);
                    players[numPlayer].isWalking = Boolean.valueOf(messageSplit[6]);
                    players[numPlayer].isInAir = Boolean.valueOf(messageSplit[7]);
                    players[numPlayer].isJumping = Boolean.valueOf(messageSplit[8]);
                    players[numPlayer].isFiring = Boolean.valueOf(messageSplit[9]);
                    players[numPlayer].orientation = Integer.parseInt(messageSplit[10]);
                    players[numPlayer].animationCount = Integer.parseInt(messageSplit[11]);
                    players[numPlayer].jumpingCount = Integer.parseInt(messageSplit[12]);
                    if (!players[numPlayer].team.equalsIgnoreCase(messageSplit[13])){
                         players[numPlayer].team = messageSplit[13];
                         players[numPlayer].teamSet = false;
                    }
                   
                    players[numPlayer].isDead =  Boolean.valueOf(messageSplit[14]);
                    players[numPlayer].health = Integer.parseInt(messageSplit[15]);
                    players[numPlayer].itemNumber = Integer.parseInt(messageSplit[16]);
                    players[numPlayer].name = messageSplit[17];
                    players[numPlayer].drawPlayer();
                    
                    }
                    catch (NumberFormatException e){
                            break;	
                    }
            	}

    		break;
        case "CLASS":
    		try{
    			numPlayer = Integer.parseInt(messageSplit[1]);
        	}
    		catch (NumberFormatException e){
    			break;	
    		}
                    try{
                        if (players[numPlayer] != null){
                            for (int i = 0; i <4 ; i++){
                                if (messageSplit[2 + i].equalsIgnoreCase("null")){
                                    players[numPlayer].items[i] = null;
                                }
                                else{
                                    players[numPlayer].items[i] = new Item(messageSplit[2 + i], players[numPlayer].width);
                                }
                            } 
                        }

                    }
                    catch (NumberFormatException e){
                            break;	
                    }

    		break;
    	case "REQUEST":
    		try{
    			numPlayer = Integer.parseInt(messageSplit[1]);
        	}
    		catch (NumberFormatException e){
    			break;	
    		}
                switch(messageSplit[2]){
                    case "PlayersData":
                        if (numPlayer != numClient){
        			sendPlayerToServer(players[numClient]);
                                sendClassToServer(players[numClient].c);
        		}
                        break;
                    case "Block":
                        int x = Integer.parseInt(messageSplit[3]);
                        int y = Integer.parseInt(messageSplit[4]);
                        if (messageSplit[5].equalsIgnoreCase("null")){
                            map.blocks[x][y] = null;
                        }
                        else {
                            map.blocks[x][y] = new Block(messageSplit[5]);
                            map.blocks[x][y].x = x*10;
                            map.blocks[x][y].y = y*10;
                        }
                        break;
                }
    		if (messageSplit[2].equalsIgnoreCase("PlayersData"))
    		{
    			if (numPlayer != numClient){
        			sendPlayerToServer(players[numClient]);
                                sendClassToServer(players[numClient].c);
        		}
    		}
    		break;
    	}
    	

    }
    
    public void sendEventToServer(MouseEvent mE, ActionEvent aE, String event)
    {
    	client.sendEventToServer(mE, aE, event, getOffset());
        if (aE != null){
            if (aE.getActionCommand().endsWith("Released")){
                new PlayerSynchronisation();  
            }
        }
        
    }
    
    public void sendPlayerToServer(Player player)
    {
    	client.sendPlayerToServer(player);
    }
    
    public void sendClassToServer(PlayerClass c)
    {
    	client.sendClassToServer(c);
    }
    
    public void sendRequestToServer(String message)
    {
    	client.sendRequestToServer(message);
    }
    
    //public void 
    
    //
    //
    // -------------
    // EVENEMENTS :
    // -------------
    //
    //
    
    private class MAdapter extends MouseAdapter {
            @Override
    	public void mousePressed (MouseEvent e){
    		//players.get(numClient).mousePressed(e);
                for (Button button : itemButtons) {
                    if (button.isMouseClickingOnButton(e.getX(), e.getY())) {
                        button.pressed = true;
                    }
                    repaint();
                }
    		sendEventToServer(e, null,null);
    	}
            @Override
    	public void mouseReleased (MouseEvent e){
    		//players.get(numClient).mouseReleased(e);
    		sendEventToServer(e, null,null);
                
             
                for (int i = 0; i < 4; i++){
                    if (itemButtons[i].pressed){
                        itemButtons[i].pressed = false;
                        itemButtons[i].drawButton();
                        String message = "item" + (i+1) + "Pressed";
                        
                        sendEventToServer(null,null,message);
                        
                        //save(fileName);
                        //this.sendMessage("Your age is " + s + ".\n");
                    }
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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E,0 ,false), "keyEPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK ,false), "keyShiftPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0,false), "keyRPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0,false), "keySPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1,0 ,false), "key1Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0, false), "key2Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3,0 ,false), "key3Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_4,0 ,false), "key4Pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0 ,false), "keyTabPressed");
        
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK ,false), "keyDPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.SHIFT_DOWN_MASK ,false), "keyQPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,InputEvent.SHIFT_DOWN_MASK ,false), "keySpacePressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,InputEvent.SHIFT_DOWN_MASK ,false), "keyTabPressed");
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0 ,true), "keyQReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0 , true), "keyDReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0 ,true), "keySpaceReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,0 ,true), "keyShiftReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E,0 ,true), "keyEReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0,true), "keySReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0 ,true), "keyTabReleased");
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK ,true), "keyDReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.SHIFT_DOWN_MASK ,true), "keyQReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,InputEvent.SHIFT_DOWN_MASK ,true), "keySpaceReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,InputEvent.SHIFT_DOWN_MASK ,true), "keyTabReleased");
        
        actionMap.put("keyQPressed", new KeyAction("keyQPressed"));
        actionMap.put("keyDPressed", new KeyAction("keyDPressed"));
        actionMap.put("keySpacePressed", new KeyAction("keySpacePressed"));
        actionMap.put("keyShiftPressed", new KeyAction("keyShiftPressed"));
        actionMap.put("keyRPressed", new KeyAction("keyRPressed"));
        actionMap.put("keyEPressed", new KeyAction("keyEPressed"));
        actionMap.put("keySPressed", new KeyAction("keySPressed"));
        actionMap.put("key1Pressed", new KeyAction("key1Pressed"));
        actionMap.put("key2Pressed", new KeyAction("key2Pressed"));
        actionMap.put("key3Pressed", new KeyAction("key3Pressed"));
        actionMap.put("key4Pressed", new KeyAction("key4Pressed"));
        actionMap.put("keyTabPressed", new KeyAction("keyTabPressed"));
       
        
        actionMap.put("keyQReleased", new KeyAction("keyQReleased"));
        actionMap.put("keyDReleased", new KeyAction("keyDReleased"));
        actionMap.put("keySpaceReleased", new KeyAction("keySpaceReleased"));
        actionMap.put("keyShiftReleased", new KeyAction("keyShiftReleased"));
        actionMap.put("keyEReleased", new KeyAction("keyEReleased"));
        actionMap.put("keySReleased", new KeyAction("keySReleased"));
        actionMap.put("keyTabReleased", new KeyAction("keyTabReleased"));
        
     }
    
    private class KeyAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public KeyAction(String actionCommand) {
           putValue(ACTION_COMMAND_KEY, actionCommand);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvt) {
        	
        	String eventName = actionEvt.getActionCommand();
        	boolean doIHaveToSendTheInfo = true;
        	switch (eventName){
        	case "keyQPressed":
        		if( !isKeyQPressed)
            	{
        			isKeyQPressed = true;
            	}
        		else {
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keyQReleased":
        		isKeyQPressed = false;
        		break;
        	case "keyDPressed":
        		if( !isKeyDPressed)
            	{
            		isKeyDPressed = true;
            	}
        		else {
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keyDReleased":
        		isKeyDPressed = false;
        		break;
        	case "keySpacePressed":
        		if( !isKeySpacePressed)
                        {
                            isKeySpacePressed = true;
                        }
        		else {
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keySpaceReleased":
        		isKeySpacePressed = false;
        		break;
        	case "keyShiftPressed":
        		if( !isKeyShiftPressed)
            	{
            		isKeyShiftPressed = true;
            	}
        		else {
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keyShiftReleased":
        		isKeyShiftPressed = false;
        		break;
                case "keyEPressed":
        		if( !isKeyEPressed)
            	{
            		isKeyEPressed = true;
            	}
        		else {
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keyEReleased":
        		isKeyEPressed = false;
        		break;
                    
                case "keySPressed":
        		if( !isKeySPressed)
            	{
            		isKeySPressed = true;
            	}
        		else {
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keySReleased":
        		isKeySPressed = false;
        		break;
        	case "key1Pressed":
        		if (players[numClient].itemNumber == 0){
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "key2Pressed":
        		if (players[numClient].itemNumber == 1){
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "key3Pressed":
        		if (players[numClient].itemNumber == 2){
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "key4Pressed":
        		if (players[numClient].itemNumber == 3){
        			doIHaveToSendTheInfo = false;
        		}
        		break;
        	case "keyRPressed":
        		if (players[numClient].items[players[numClient].itemNumber].isReloading){
        			doIHaveToSendTheInfo = false;
        		}
        		break;
                case "keyTabPressed":
                    isShowingScores = true;
                    doIHaveToSendTheInfo = false;
                    break;
                case "keyTabReleased":
                    isShowingScores = false;
                    doIHaveToSendTheInfo = false;
                    break;
        	}


        	if (doIHaveToSendTheInfo && !eventName.equalsIgnoreCase("keyEPressed")){
        		sendEventToServer(null,actionEvt,null);
                        //pingTest = System.nanoTime();
                        
        	}
                else if (eventName.equalsIgnoreCase("keyEPressed")){
                    int x = 0;
                    int y = 0;
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(p,etW);
                    x = p.x - 3 + getOffset();
                    y = p.y - 26;
                    sendEventToServer(null,null,(String) "keyEPressed;" + x +";" + y + ";");
                }
        	
        }
     }
    
    private class MMotionAdapter extends MouseMotionAdapter{
            @Override
    	public void mouseDragged (MouseEvent e) {
    		//players.get(numClient).mouseDragged(e);
    		sendEventToServer(e, null, null);

    	}
        @Override
        public void mouseMoved (MouseEvent e) {
                for (Button button : itemButtons) {
                    if (button.isMouseOnButton(e.getX(), e.getY())) {
                    }
                    repaint();
                }
    		

    	}
    }
}