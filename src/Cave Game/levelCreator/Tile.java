package com.mong.levelCreator;

public class Tile {

	
	public int x,y;
	public int id;
	
	public int xTile, yTile;
	
	public Tile(int id, int x, int y, int xTile, int yTile){
		this.x = x * 64;
		this.y = y * 64;
		this.xTile = xTile;
		this.yTile = yTile;
		this.id = id;
	}
	
	public Tile(int id, int x, int y){
		this.id = id;
	}
	
	public void render(Screen screen){
		screen.render(x, y, xTile, yTile, 1, 1, 4);
	}
}
