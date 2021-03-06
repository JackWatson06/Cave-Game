package com.mong.levelCreator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Screen {

	public int width, height;
	
	public BufferedImage image;
	public int[] pixels;
	
	private SpriteSheet sheet;
	
	public int screenX = 0;
	public int screenY = 0;
	
	public Screen(int width, int height, String spriteSheet){
		this.width = width;
		this.height = height;
		
		screenY = (17 * 64) - height;
		
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		
		try{
			sheet = new SpriteSheet(ImageIO.read(Main.class.getResourceAsStream(spriteSheet)));
		}catch(IOException e){
			
		}
	}
	
	public void render(int xp, int yp, int startXTile, int startYTile, int spriteWidth, int spriteHeight, int scale){
		
		int windowLX = screenX;
		int windowRX = screenX + width;
		int windowTY = screenY;
		int windowBY = screenY + height;
		
		
		if(xp + (spriteWidth * (scale * 16) - 1) >= windowLX && xp < windowRX && yp + (spriteHeight * (scale * 16) - 1) >= windowTY && yp < windowBY){
			
			
			xp -= screenX;
			yp -= screenY;
		
			int[] Data = new int[(spriteWidth * 16) * (spriteHeight * 16)];
			int[] ScaledData = new int[((spriteWidth * 16) * (spriteHeight * 16)) * (scale * scale)];
		
			int pixelsWidth = spriteWidth * 16;
			int pixelsHeight = spriteHeight * 16;
		
			for(int y = 0; y < pixelsHeight; y++){
				for(int x = 0; x < pixelsWidth; x++){
					Data[x + (y * pixelsWidth)] = sheet.pixels[((startYTile * 16 + y) * sheet.width) + ((startXTile * 16) + x)];
				}
			}
			
			
			for(int dy = 0; dy < pixelsHeight; dy++){
				for(int dx = 0; dx < pixelsWidth; dx++){
					int currentColor = Data[dx + (dy * pixelsWidth)];
					for(int y = 0; y < scale; y++){
						for(int x = 0; x < scale; x++){
							ScaledData[(x + (dx * scale)) + ((y + (dy * scale)) * (pixelsWidth * scale))] = currentColor;
						}
					}
				}
				
			}
			
			for(int y = 0; y < pixelsHeight * scale; y++){
				for(int x = 0; x < pixelsWidth * scale; x++){
					if(xp + x >= windowLX - screenX && xp + x < windowRX - screenX && yp + y >= windowTY - screenY && yp + y < windowBY - screenY){
						if(ScaledData[x + (y * (pixelsWidth * scale))] != -16250872){
								pixels[(((yp + y) * width)) + (xp + x)] = ScaledData[x + (y * (pixelsWidth * scale))];
						}
					}
				}
			}
		}
		
	}
}
