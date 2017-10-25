package com.mong.levelCreator;

public class AirTile extends Tile{

	
	public int x,y;
	public int id;
	
	public int xTile, yTile;
	
	public AirTile(int id, int x, int y){
		super(id, x, y);
	}
	
	@Override
	public void render(Screen screen){

	}
}
