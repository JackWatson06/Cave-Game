package com.mong.graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet {

	//THIS CLASS JUST LOADS THE DATA FOR THE SPRITE SHEET
	
	public int width, height;
	
	public int[] pixels;
	
	public SpriteSheet(BufferedImage image){
		this.width = image.getWidth();
		this.height = image.getHeight();
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		
	}
	
}
