
package ExplodetoWin;

import GamePackage.Block;
import GamePackage.Design;
import GamePackage.Marker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Map {
    
    BufferedWriter out;
    
    public ArrayList<Design> backgroundDesigns;
    public ArrayList<Marker> markers;
    public Block[][] blocks;
    public String mapName;
    
    public Marker blueSpawnMarker;
    public Marker redSpawnMarker;
    
    public Map(String mapName){
        this.mapName = mapName;
        openMap();
    }
    
    public void openMap (){
        blocks = new Block[240][60];
        backgroundDesigns = new ArrayList<>();
        markers = new ArrayList<>();
        for (int i=0; i<240; i++){
        	for (int j=0; j<60; j++){
        		blocks[i][j] = null;
        	}
        }
        BufferedReader in;
        String read;
    	String[] args;
    	int nbLines = 0;
        try {
            
            // Calcul nombre de lignes :
            in = new BufferedReader(new FileReader(mapName + ".txt"));

            while ((in.readLine()) != null) {
                    nbLines++;
            }
            in.close();
            
            // Lecture du fichier:
            in = new BufferedReader(new FileReader(mapName + ".txt"));
            backgroundDesigns.add(new Design(0,0,"sky"));
            backgroundDesigns.add(new Design(1200,0,"sky"));
            for (int i =0; i < nbLines; i++){
                    read = in.readLine();
                    args = read.split(";");
                    switch (args.length){
                        case 3 : // Blocks
                            if (!args[2].equalsIgnoreCase("null")){
                                if (args[2].equalsIgnoreCase("Concrete")){
                                    args[2] = "Brick";
                                }
                                Block block = new Block(args[2]);
                                int posX = Integer.parseInt(args[0]);
                                int posY = Integer.parseInt(args[1]);
                                block.x = posX * 10;
                                block.y = posY * 10;
                                blocks[posX][posY] = block;
                                if (block.type.equalsIgnoreCase("Dirt")||block.type.equalsIgnoreCase("Grass")){
                                    backgroundDesigns.add(new Design(posX*10,posY*10,"dirtWall"));
                                }
                            }
                            break;
                        case 4 : // Designs ou markers
                            switch (args[0]){
                                case "background":
                                    backgroundDesigns.add(new Design(Integer.parseInt(args[1]),Integer.parseInt(args[2]),args[3]));
                                    break;
                                case "marker":
                                    markers.add(new Marker(Integer.parseInt(args[1]),Integer.parseInt(args[2]),args[3]));
                                    break; 
                            }
                            break;
                    }
            }
            in.close();
        }catch(IOException e){
            System.out.println("There was a problem:" + e);
        }
        
    }
    
    public void saveMapAs (String fileName){
        try{
            System.out.println("SAVE");
        out = new BufferedWriter(new FileWriter(fileName + ".txt",false));
        for (int i = 0; i < 240; i++){
            for (int j = 0; j < 60; j++){
                String type;
                if (blocks[i][j] == null){
                        type = "null";
                }
                else{
                        type = blocks[i][j].type;
                }
                String message = i + ";" + j + ";" + type;
                out.write(message);
                out.newLine();
            }
        }
        for (Design design: backgroundDesigns){
            String message = "background;"+design.posX+";"+design.posY+";"+design.name;
            out.write(message);
            out.newLine();
        }
        for (Marker marker: markers){
            String message = "marker;"+marker.posX+";"+marker.posY+";"+marker.name;
            out.write(message);
            out.newLine();
        }
        out.close();
        }catch(IOException e){
                System.out.println("There was a problem:" + e);
        }
        
        mapName = fileName;
    }
    
    public void saveMap (){
        saveMapAs(mapName);
    }
}
