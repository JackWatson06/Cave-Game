package com.mong.tile;

import com.mong.graphics.Screen;

public class AirTile extends Tile{

	//This class sets up a basic air tile and tells the game to ignore the players collisions with the tile
	
	public AirTile(int id, int x, int y) {
		super(id, x, y);
		collisions = false;
	}
	
	@Override
	public void render(Screen screen){
	}

}
