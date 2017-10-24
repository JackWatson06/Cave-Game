package com.mong.entity;

import com.mong.graphics.Screen;
import com.mong.main.Main;
import com.mong.sound.Sound;
import com.mong.terrain.TerrainManager;

public class Gem extends Entity{

	//This is the basic variables for the gem class

	private int pointValue;
	private int tickCounter = 0;
	
	private boolean add = true;
	
	int ySubtract = 0;
	int yAdding = 0;
	
	public Gem(int id, int x, int y, int xTile, int yTile, int rightInset, int leftInset, int topInset, int bottomInset, int pointValue) {
		super(id, x, y, xTile, yTile, rightInset, leftInset, topInset, bottomInset);
		this.pointValue = pointValue;
		this.removeOnCollide = true;
	}
	
	//This updates the game logic
	@Override
	public void tick(int currentTick, Player player, TerrainManager terrain){

		if(tickCounter < 60 && tickCounter % 2 == 0){
			ySubtract++;
			y--;
		}else if(tickCounter < 120 && tickCounter % 2 == 0){
			yAdding++;
			y++;
		}else if(tickCounter >= 120){
			add = false;
			tickCounter = 0;
			ySubtract = 0;
			yAdding = 0;
		}
		
		if(add){
			tickCounter++;
		}else{
			add = true;
		}
	}
	
	//This renders the gem
	@Override
	public void render(Screen screen){
		screen.render(x + leftInset, y + topInset, xTile, yTile, 1, 1, 3);
	}
	
	//This is called when the player collides with the gem.
	@Override
	public void collide(Main main, Player player){
		main.score += pointValue;
		Sound.coinPickup.play();
	}
	
	

}
