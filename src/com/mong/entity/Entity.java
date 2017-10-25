package com.mong.entity;

import com.mong.graphics.Screen;
import com.mong.main.Main;
import com.mong.terrain.TerrainManager;

public class Entity {

	
	//THIS ENTIRE CLASS JUST SETS UP THE BASICS FOR AN ENTITY OBJECT.
	
	public int x,y;
	public int xTile,yTile;
	public int id;
	
	public int leftInset, rightInset, topInset, bottomInset;
	
	public boolean removeOnCollide;
	
	
	public Entity(int id, int x, int y, int xTile, int yTile, int rightInset, int leftInset, int topInset, int bottomInset){
		this.id = id;
		this.x = x;
		this.y = y;
		this.xTile = xTile;
		this.yTile = yTile;
		this.topInset = topInset * 4;
		this.bottomInset = bottomInset * 4;
		this.leftInset = leftInset * 4;
		this.rightInset = rightInset * 4;
	}
	
	public void tick(int currentTick, Player player, TerrainManager terrain){
		
	}
	
	public void render(Screen screen){
		screen.render(x, y, xTile, yTile, 1, 1, 4);
	}
	
	public void collide(Main main, Player player){
		
	}
	
	public void attack(float damage, Player player){
		
	}
	
}
