package com.mong.tile;

import com.mong.graphics.Screen;

public class Tile {

	//THIS CLASS SETS UP THE BASIC TILE OBJECT
	
	public int id;
	public int x, y;
	private int xTile, yTile;
	
	public boolean collisions;
	
	public Tile(int id, int x, int y, int xTile, int yTile){
		this.id = id;
		this.x = x;
		this.y = y;
		this.xTile = xTile;
		this.yTile = yTile;
	}
	
	public Tile(int id, int x, int y){
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public void render(Screen screen){
		screen.render(x * 64, y * 64, xTile, yTile, 1, 1, 4);
	}
	
	
}
