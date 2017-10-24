package com.mong.levelCreator;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Background {
	
	public int width, height;
	public int[] scaledDown;
	public int[] pixels;
	
	public Background(int width, int height, String path){
		BufferedImage image;
		try {
			image = ImageIO.read(Main.class.getResource(path));
			height = image.getHeight();
			width = image.getWidth();
			scaledDown = image.getRGB(0, 0, width, height, null, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		scaleImage(4, width, height);
	}
	
	public void scaleImage(int scale, int screenWidth, int screenHeight) {
		pixels = new int[screenWidth * screenHeight];
		for(int by = 0; by < height; by++){
			for(int bx = 0; bx < width; bx++){
				int current = scaledDown[bx + (by * width)];
				for(int y = 0; y < scale; y++){
					for(int x = 0; x < scale; x++){
						pixels[(x + (bx * scale)) + ((y + (by * scale)) * screenWidth)] = current;
					}
				}
			}
		}
		
	}
	
	public void render(Screen screen){
		for(int i = 0; i < pixels.length; i++){
			screen.pixels[i] = pixels[i];
		}
	}

}
