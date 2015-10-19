/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientServerComm;

import java.io.*;

class EtWServerControl extends Thread
{
  EtWServer server;
  BufferedReader in;
  String strCommande="";
  Thread t;

  public EtWServerControl (EtWServer _server)
  {
    server=_server;
    in = new BufferedReader(new InputStreamReader(System.in));
    t = new Thread(this);
    t.start();
  }
  
  public void printWelcomeMessage(int port){
    System.out.println("Server started on port " + port + "!");
    System.out.println("--------");
    System.out.println("Number of clients connected : "+server.getNbClients());
    System.out.println("--------");
    System.out.println("Quit : \"quit\"");
    System.out.println("Number of clients connected : \"nb\"");
    System.out.println("Show all echoed messages (including Players Sync) : \"showAll\"");
    System.out.println("--------");
      
  }

  public void run()
  {
    try
    {
      while ((strCommande=in.readLine())!=null)
      {
        if (strCommande.equalsIgnoreCase("quit"))
          System.exit(0);
        else if(strCommande.equalsIgnoreCase("nb"))
        {

          System.out.println("Number of clients connected : "+server.getNbClients());
          System.out.println("--------");
        }
        else if(strCommande.startsWith("set max ")){
            try{
                 int a = Integer.parseInt(strCommande.substring(8));
                 System.out.println("Max number of clients set to : " + a);
                 server.setMaxConnections(a);
            }
            catch(NumberFormatException e){
                System.out.println("Wrong argument");
            }
            System.out.println("--------");
           
            
        }
        else if(strCommande.equalsIgnoreCase("showAll")){
            server.showAll = true;
        }
        else
        {
          System.out.println("This sentence isn't supported");
          System.out.println("Quit : \"quit\"");
          System.out.println("Number of clients connected : \"nb\"");
          System.out.println("Show all echoed messages (including Players Sync) : \"showAll\"");
          System.out.println("--------");
        }
        System.out.flush();
      }
    }
    catch (IOException e) {}
  }
}
