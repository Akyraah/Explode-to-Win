package ClientServerComm;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;

import GamePackage.Board;
import GamePackage.Player;
import GamePackage.PlayerClass;
import ExplodetoWin.LoadingUI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EtWClient extends Thread{
    
    public Socket clientSocket = null;
    public PrintWriter outCtS = null;
    public BufferedReader inStC = null;
    public int clientID;
    public Board board;
    public LoadingUI loadingUI;
    public boolean boardSet = false;
    
    public EtWClient(String[] args, LoadingUI loadingUI){
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        this.loadingUI = loadingUI;
        
        try{
            clientSocket = new Socket(hostName, portNumber);
            inStC = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outCtS = new PrintWriter(clientSocket.getOutputStream(),true);
            System.out.println("You have successfully connected to " + hostName + " on port " + portNumber);
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
        
    }
    
    @Override
    public void run() {
    	String inputLine; 
        try{
            while ((inputLine = inStC.readLine()) != null) {
            	System.out.println("Server : " + inputLine);
                if (boardSet){
                    sendMessageToBoard(inputLine);
                }
                else{
                    sendMessageToLoadingUI(inputLine);
                }
               
                if (inputLine.startsWith("Your clientID is ")){
                    clientID = Integer.parseInt(inputLine.substring(17));
                    System.out.println("ClientID : " + clientID);

                }
                else if (inputLine.startsWith("New connection from Client ")){
                    sendServer("Hello client " + Integer.parseInt(inputLine.substring(27)));
                }
            }
            //clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(EtWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendMessageToBoard(String message){
        board.receiveMessageFromServer(message);
    }
    
    public void sendMessageToLoadingUI(String message){
        loadingUI.receiveMessageFromServer(message);
    }
    
    public void setBoard(Board board){
        this.board = board;
        boardSet = true;
        board.numClient = this.clientID;
        this.board.joinGame();
    }
    
    public void sendServer(String message){
        outCtS.println(message);
    }
    
    public void sendEventToServer(MouseEvent mE, ActionEvent aE, String event, int offset){
        String message = "";
        /*if (event != null){
            String[] messageSplit = event.split("&");
            if (messageSplit.length > 1){
                message = messageSplit[0] + "&" + EtWClientServerProtocol.ConvertEventToString(mE, aE, messageSplit[1], clientID, offset);
                System.out.println(String.valueOf(System.nanoTime() - Long.parseLong(messageSplit[0])));
            }
            else {
                message = EtWClientServerProtocol.ConvertEventToString(mE, aE, event, clientID, offset);
            }
        }
        else {
            
        }*/
                message = EtWClientServerProtocol.ConvertEventToString(mE, aE, event, clientID, offset);
        
    	
    	sendServer(message);
    }
    
    public void sendPlayerToServer(Player player){
    	String message = EtWClientServerProtocol.ConvertPlayerToString(player, clientID);
    	sendServer(message);
    }
    
    public void sendClassToServer(PlayerClass c){
    	String message = EtWClientServerProtocol.ConvertClassToString(c, clientID);
    	sendServer(message);
    }
    
    public void sendRequestToServer(String request){
    	String message = EtWClientServerProtocol.ConvertRequestToString(request, clientID);
    	sendServer(message);
    }
}

