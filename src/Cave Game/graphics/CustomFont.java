package com.mong.graphics;

public class CustomFont {
	
	private int xTile, yTile;
	private int scale;
	private int x, y;
	private boolean centerHorizontal, centerVerticle;
	private int spriteLength;
	
	
	public CustomFont(int xTile, int yTile, int scale, int spriteLength, int x, int y, boolean centerHorizontal, boolean centerVerticle){
		this.xTile = xTile;
		this.yTile = yTile;
		this.x = x;
		this.y = y;
		this.centerHorizontal = centerHorizontal;
		this.centerVerticle = centerVerticle;
		this.scale = scale;
		this.spriteLength = spriteLength;
	}
	
	public void render(Screen screen, int width, int height){
		if(centerHorizontal && !centerVerticle){
			screen.render((width / 2) - (spriteLength * (16 * scale) / 2), y, xTile, yTile, spriteLength, 1, scale);
		}else if(centerVerticle && !centerHorizontal){
			screen.render(x, (height / 2) - ((16 * scale) / 2), xTile, yTile, spriteLength, 1, scale);
		}else if(centerHorizontal && centerVerticle){
			screen.render((width / 2) - (spriteLength * (16 * scale) / 2), (height / 2) - ((16 * scale) / 2), xTile, yTile, spriteLength, 1, scale);
		}else{
			screen.render(x, y, xTile, yTile, spriteLength, 1, scale);
		}
	}
}
