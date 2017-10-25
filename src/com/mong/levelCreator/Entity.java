package com.mong.levelCreator;

public class Entity {

	public int x,y;
	public int id;
	
	public int xTile, yTile;
	
	public Entity(int id, int x, int y, int xTile, int yTile){
		this.id = id;
		this.x = x;
		this.y = y;
		this.xTile = xTile;
		this.yTile = yTile;
	}
	
	public void render(Screen screen){
		screen.render(x, y, xTile, yTile, 1, 1, 4);
	}
}
