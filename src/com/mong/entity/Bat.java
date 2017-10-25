package com.mong.entity;

import com.mong.graphics.Screen;
import com.mong.main.Main;
import com.mong.sound.Sound;
import com.mong.terrain.TerrainManager;

public class Bat extends Entity{

	//This sets up the basic varaibles for the bat plus the extension of the entity.
	
	private boolean entityState = false;
	private int squareRadius = 150;
	
	private boolean lastPosX = false;
	private boolean lastNegX = false;
	private boolean lastPosY = false;
	private boolean lastNegY = false;
	
	private int speed = 3;
	
	private float health = 1F;
	
	private boolean red = false;
	private int redTimer = 0;
	
	//This sets up a basic bat object
	
	public Bat(int id, int x, int y, int xTile, int yTile, int rightInset, int leftInset, int topInset, int bottomInset) {
		super(id, x, y, xTile, yTile, rightInset, leftInset, topInset, bottomInset);
		this.removeOnCollide = false;
	}
	
	//This updates the bat logic
	@Override
	public void tick(int currentTick, Player player, TerrainManager terrain){
		if(health == 0F){
			terrain.remove(this);
		}
		if(player.x < x + squareRadius && player.x + 63 - 16 > x - squareRadius && player.y < y + squareRadius && player.y + 63 > y - squareRadius){
			entityState = true;
		}
		if(entityState){
			if(x > player.x){
				x -= speed;
				lastNegX = true;
			}else{
				lastNegX = false;
			}
			if(x < player.x){
				x += speed;
				lastPosX = true;
			}else{
				lastPosX = false;
			}
			if(y < player.y){
				y += speed;
				lastPosY = true;
			}else{
				lastPosY = false;
			}
			if(y > player.y){
				y -= speed;
				lastNegY = true;
			}else{
				lastNegY = false;
			}
			
		}
	}
	
	//This renders the bat, and if the bat is hit it will render a red version for a short amount of time.
	@Override
	public void render(Screen screen){
		if(!entityState && !red){
			screen.render(x, y, 11, 2, 1, 1, 4);
		}else if(!red){
			screen.render(x, y, 10, 2, 1, 1, 4);
		}
		
		if(red && redTimer < 15){
			screen.render(x, y, 9, 2, 1, 1, 4);
			redTimer++;
		}else if(redTimer >= 15){
			red = false;
			redTimer = 0;
			screen.render(x, y, 10, 2, 1, 1, 4);
		}
	}
	
	//This is called when the player collides with the bat and moves the bat backwards
	@Override
	public void collide(Main main, Player player){
		player.healthBar -= 0.5;
		Sound.hurt.play();
		if(lastPosX){
			x-=200;
		}
		if(lastNegX){
			x+=200;
		}
		if(lastPosY){
			y-=200;
		}
		if(lastNegY){
			y+=200;
		}
		
	}
	
	//This is called when the player attacks the bat and moves the bat backwards
	@Override
	public void attack(float damage, Player player){
		Sound.hit.play();
		red = true;
		if(lastPosX){
			x-=100;
		}
		if(lastNegX){
			x+=100;
		}
		if(lastPosY){
			y-=100;
		}
		if(lastNegY){
			y+=100;
		}
		health -= damage;
	}

}
