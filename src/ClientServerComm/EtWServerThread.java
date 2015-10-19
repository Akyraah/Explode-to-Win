
package ClientServerComm;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EtWServerThread extends Thread{
    private Socket clientSocket = null;
    private EtWServer server = null;
    private int clientID = 0;
    
    private PrintWriter outStC = null;
    private BufferedReader inCtS = null;
    
    public EtWServerThread(Socket clientSocket, EtWServer server){
        this.clientSocket = clientSocket;
        this.server = server;
        try{
            inCtS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outStC = new PrintWriter(clientSocket.getOutputStream(),true);
            clientID = server.addClient(outStC);
            System.out.println("Client " + clientID + " has connected!");
        }
        catch(IOException ex){
            Logger.getLogger(EtWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
    	String inputLine;
         
        try{           
            while ((inputLine = inCtS.readLine()) != null) {
                /*String[] messageSplit = inputLine.split("&");
                if (messageSplit.length > 1){
                    System.out.println(System.nanoTime() - Long.parseLong(messageSplit[0]));
                }*/
                
                if (inputLine.startsWith("PLAYER")){
                    if (server.showAll){
                        System.out.println("Client " + clientID + " : " + inputLine);
                    }
                }
                else {
                    System.out.println("Client " + clientID + " : " + inputLine);
                }    	
                server.messageTreatment(inputLine);
                
            }
            //clientSocket.close();
        } catch (IOException ex) {
            
            //Logger.getLogger(EtWServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finally
        {
          try
          {
            System.out.println("Client number "+clientID+" has disconnected.");
            server.delClient(clientID);
            clientSocket.close();
          }
          catch (IOException ex){ 
              //Logger.getLogger(EtWServer.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
    } 
    
}
