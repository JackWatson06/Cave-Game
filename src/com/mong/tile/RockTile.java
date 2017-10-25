package com.mong.tile;

public class RockTile extends Tile{

	//This sets up a specific rock tile and tells the game it wants collisions
	
	public RockTile(int id, int x, int y, int xTile, int yTile) {
		super(id, x, y, xTile, yTile);
		collisions = true;
	}


}
