package Util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Button {
	private String ButtonSheetName;
    public BufferedImage buttonImage;
    public BufferedImage buttonSheet;
    public Rectangle imageRectangle;
    public int imageSheetNumber;
    public int height;
    public int width;
    public int x;
    public int y;
    
    public boolean pressed = false;
    
	public Button(String ButtonSheetName, int height, int x, int y){
		this.ButtonSheetName = ButtonSheetName;
		this.height = height;
		this.x = x;
		this.y = y;
		imageSheetNumber = 0;
		buttonSheet = ImageManager.LoadBufferedImage(this.ButtonSheetName, true);
		width = buttonSheet.getWidth();
		drawButton();
		imageRectangle = new Rectangle(x,y,width,this.height);
	}
	
	public boolean isMouseOnButton(int mouseX, int mouseY)
	{
		if (imageRectangle.contains(mouseX, mouseY)){
                    imageSheetNumber = 1;
                    drawButton();
                    return true;
		}
		imageSheetNumber = 0;
		drawButton();
		return false;
	}
	
	public boolean isMouseClickingOnButton(int mouseX, int mouseY)
	{
		if (imageRectangle.contains(mouseX, mouseY)){
                    imageSheetNumber = 2;
                    drawButton();
                    return true;
		}
		drawButton();
		return false;
	}
	
	public void drawButton(){
            /*if (!pressed){
                imageSheetNumber = 0;
            }*/
            buttonImage = buttonSheet.getSubimage(0, height*imageSheetNumber, width, this.height);
	}
	
	

}
