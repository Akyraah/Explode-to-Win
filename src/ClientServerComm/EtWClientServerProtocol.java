package ClientServerComm;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import ExplodetoWin.Map;
import GamePackage.Block;
import GamePackage.Player;
import GamePackage.PlayerClass;
import GamePackage.Design;
import GamePackage.Marker;
import java.util.ArrayList;

public class EtWClientServerProtocol {

    public static String ConvertEventToString(MouseEvent mE, ActionEvent aE, String event, int numClient, int offset)
    {
        if (mE != null)
        {
                int posX = mE.getX();
                int posY = mE.getY();

                String message = "EVENT;" + numClient + ";mE;" + mE.getID() + ";" + (posX + offset) + ";" + posY + ";";
                return message;

        }
        else if (aE != null){
                String message = "EVENT;" + numClient + ";aE;" + aE.getActionCommand() + ";";
                return message;
        }
        else if (event != null){
                String message;
                
                if (event.startsWith("bullet")){
                        message = "EVENT;" + numClient + ";" + event;
                }
                else{
                        message = "EVENT;" + numClient + ";"+ event + ";";
                }
                return message;
        }
        return null;
    }

    public static String ConvertPlayerToString(Player player, int numClient){

        int x = player.x;
        int y = player.y;
        int dx = player.dx;
        int dy = player.dy;
        boolean isWalking = player.isWalking;
        boolean isInAir = player.isInAir;
        boolean isJumping = player.isJumping;
        boolean isFiring = player.isFiring;
        int orientation = player.orientation;
        int animationCount = player.animationCount;
        int jumpingCount = player.jumpingCount;
        String team = player.team;

        String message;
        message = "PLAYER;" + numClient + ";" + x + ";" + y + ";" + dx + ";" + dy + ";" + isWalking + ";" + isInAir + ";" + isJumping + ";" + isFiring + ";" + orientation + ";" + animationCount + ";" + jumpingCount + ";" + team + ";" + player.isDead + ";" + player.health + ";" + player.itemNumber + ";" + player.name +";";
        return message;
    }
    
    public static String ConvertClassToString(PlayerClass c, int numClient){
        String[] items;
        items = new String[4];
        for (int i = 0; i < 4; i++){
            if (c.items[i] != null){
                items[i] = c.items[i].name;
            }
            else {
                items[i] = "null";
            }
        }

        String message;
        message = "CLASS;" + numClient + ";" + items[0] + ";" + items[1] + ";" + items[2] + ";" + items[3] + ";";
        return message;
    }

    public static String ConvertRequestToString(String request, int numClient){
        String message;
        message = "REQUEST;" + numClient + ";" + request + ";";
        return message;
    }

    public static String[] ConvertMapToString(Map map){
        String[] messages = new String[241 + map.backgroundDesigns.size() + map.markers.size()];
        messages[0] = "NAME;"+map.mapName+";"+(map.backgroundDesigns.size()+map.markers.size());
        for (int i = 0; i< 240; i++){
            messages[i + 1] = "MAP;" + i + ";";
            for (int j = 0; j <60; j++){
                if (map.blocks[i][j] != null){
                    messages[i+1] += map.blocks[i][j].type +";";
                }
                else{
                    messages[i+1] += "null;";
                }
            }
        }
        for (int i = 0; i<map.backgroundDesigns.size(); i++){
            Design design = map.backgroundDesigns.get(i);
            messages[241 + i] = "MAP;" + "background;" + design.posX + ";" + design.posY + ";" + design.name;
        }
        for (int i = 0; i<map.markers.size(); i++){
            Marker marker = map.markers.get(i);
            messages[241 + i + map.backgroundDesigns.size()] = "MAP;" + "marker;" + marker.posX + ";" + marker.posY + ";" + marker.name;
        }
        return messages;
    }
    
    public static Map ConvertStringToMap(ArrayList<String> mapStrings){
        Map map = new Map("defaultMap");
        for (int i = 0; i< 240; i++){
            
        String[] messageSplit = mapStrings.get(i).split(";");
        //System.out.println(message);
            for (int j = 2; j < 62; j++){
                if (messageSplit[j].equalsIgnoreCase("null")){
                    map.blocks[i][j-2] = null;
                }
                else{
                    Block block = new Block(messageSplit[j]);
                    block.x = i * 10;
                    block.y = (j-2)*10;
                    map.blocks[i][j-2] = block;
                    
                    //System.out.println(map.blocks[i][j-2].type);
                }
            }
        }
        for (int i = 240; i < mapStrings.size(); i++){
            String[] messageSplit;
            messageSplit = mapStrings.get(i).split(";");
            switch (messageSplit[1]){
                case "background":
                    map.backgroundDesigns.add(new Design(Integer.parseInt(messageSplit[2]),Integer.parseInt(messageSplit[3]),messageSplit[4]));
                break;
                case "marker":
                    map.markers.add(new Marker(Integer.parseInt(messageSplit[2]),Integer.parseInt(messageSplit[3]),messageSplit[4]));
                break;
            }
        }
        
        return map;
    }

        
}
