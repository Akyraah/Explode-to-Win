
package ClientServerComm;

import ExplodetoWin.Map;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import GamePackage.Block;

public class EtWServer {
    
    private PrintWriter[] tabClients;
    private static int nbClients=0;
    private static int maxConnections = 10;
    private long startDate;
    public boolean showAll = false;
    
    public Map map;
    
    public static void main(String[] args) {
        EtWServer server = new EtWServer(args);
        
    }

    public EtWServer(String[] args) {
        this.tabClients = new PrintWriter[maxConnections];
        map = new Map(args[1]);
        EtWServerControl serverControl = new EtWServerControl(this);
        System.out.println(args[0]);
        System.out.println(args[1]);
        if (args.length != 2) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        try{ 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            serverControl.printWelcomeMessage(portNumber);
            while ((nbClients < maxConnections) || (maxConnections == 0)){
                Socket clientSocket = serverSocket.accept();
                EtWServerThread thread = new EtWServerThread(clientSocket, this);
                thread.start();
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(EtWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //
    // -----------------
    //
    
    public int addClient(PrintWriter outStC){
        int freeClient = 0;
        while(tabClients[freeClient] != null){
            freeClient ++;
        }
        tabClients[freeClient] = (outStC);
        nbClients ++;
        int clientID = freeClient;
        sendOne(clientID, "Your clientID is " + clientID);
        if (nbClients == 1){
            startDate = System.nanoTime();
        }
        sendOne(clientID, "Max number of Connections: " + maxConnections);
        sendOne(clientID, "Time elapsed: " + String.valueOf(System.nanoTime() - startDate));
        sendAll("New connection from Client " + clientID);
        String[] messages = EtWClientServerProtocol.ConvertMapToString(map);
        for (int i = 0; i<241+map.backgroundDesigns.size()+map.markers.size(); i++){
            sendOne(clientID,messages[i]);
        }
        return (clientID);
    }
    
    public void delClient (int clientID){
        tabClients[clientID] = null;
        
    }
    
    public int getNbClients(){
        return nbClients;
    }
    
    public void setMaxConnections(int a){
        maxConnections = a;
    }
    //
    // -----------------
    //
    
    public void sendOne(int clientID, String message){
        if (tabClients[clientID] != null){
            tabClients[clientID].println(message);
            tabClients[clientID].flush();
        }
        else{
            System.out.println("Error: Client number " + clientID + " is unknown");
        }
    }
    
    public void sendAll(String message){
        for (PrintWriter p : tabClients){
            if (p!= null){
                p.println(message);
                p.flush();
            }
            

        }
    }
    
    public void messageTreatment(String message){
        sendAll(message);
        if (message.startsWith("REQUEST")){
            String[] messageSplit = message.split(";");
            switch (messageSplit[2]){
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
                    
        }
        
    }
    
    
}
