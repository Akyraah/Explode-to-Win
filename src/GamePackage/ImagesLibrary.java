/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package GamePackage;

import Util.ImageManager;
import java.awt.Image;



public class ImagesLibrary {
    public Image[] designImages;
    public Image[] blockImages;
    
    public boolean mapEditor;
    
    public ImagesLibrary(boolean mapEditor){
        this.mapEditor = mapEditor;
        designImages = new Image[Design.designNames.length];
        for (int i = 1; i< designImages.length; i++){
            System.out.println(Design.designNames[i]);
            designImages[i] = ImageManager.LoadBufferedImage("Design" + Design.designNames[i] + ".png", true); 
        }
            
        blockImages = new Image[Block.blockNames.length];
        for (int i = 0; i< blockImages.length; i++){
            System.out.println(Block.blockNames[i]);
            boolean transparency = !(mapEditor && Block.blockNames[i].equalsIgnoreCase("void"));
            blockImages[i] = ImageManager.LoadBufferedImage("Block" + Block.blockNames[i] + ".png", transparency); 
        }
    }
}
