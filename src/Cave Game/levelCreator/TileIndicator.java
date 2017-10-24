package com.mong.levelCreator;

public class TileIndicator {

	private int x = 0;
	private int y = 0;
	
	public void render(Screen screen, Tile tile){
		screen.render(x, y, 13, 14, 2, 2, 4);
		if(tile.id != 0){
			screen.render(32, 32, tile.xTile, tile.yTile, 1, 1, 4);
		}
	}

	public void render(Screen screen, Entity entity) {
		screen.render(x, y, 13, 14, 2, 2, 4);
		screen.render(32, 32, entity.xTile, entity.yTile, 1, 1, 4);
	}
}
