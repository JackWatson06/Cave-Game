package com.mong.levelCreator;

import java.awt.image.BufferedImage;

public class SpriteSheet {

	public int width, height;
	
	public int[] pixels;
	
	public SpriteSheet(BufferedImage buffer){
		this.width = buffer.getWidth();
		this.height = buffer.getHeight();
		
		pixels = buffer.getRGB(0, 0, width, height, null, 0, width);
	}
}
