package com.mong.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.mong.main.Main;

public class Background {

	//THIS CLASS JUST LOADS DATA FOR A BACKGRAOUND WHICH WOULD BE EVENTUALLY RENDERED TO THE SCREEN
	
	private int width, height;
	
	public int[] pixelsScaled;
	public int[] pixelsNotScaled;
	
	public Background(String path){
		
		try{
			BufferedImage image = ImageIO.read(Main.class.getResourceAsStream(path));
			this.width = image.getWidth();
			this.height = image.getHeight();
			pixelsNotScaled = image.getRGB(0, 0, width, height, null, 0, width);
		}catch(Exception e){
			
		}
	}
	
	public void scaleImage(int scale, int screenWidth, int screenHeight){
		pixelsScaled = new int[screenWidth * screenHeight];
		for(int by = 0; by < height; by++){
			for(int bx = 0; bx < width; bx++){
				int current = pixelsNotScaled[bx + (by * width)];
				for(int y = 0; y < scale; y++){
					for(int x = 0; x < scale; x++){
						pixelsScaled[(x + (bx * scale)) + ((y + (by * scale)) * screenWidth)] = current;
					}
				}
			}
		}
	}
	
	public void render(Screen screen){
		for(int i = 0; i < pixelsScaled.length; i++){
			screen.pixels[i] = pixelsScaled[i];
		}
	}
}
