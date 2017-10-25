package com.mong.entity;

import com.mong.graphics.Screen;
import com.mong.main.Main;
import com.mong.sound.Sound;
import com.mong.terrain.TerrainManager;
import com.mong.tile.Tile;

public class Zombie extends Entity{


	//This sets up the basic variables for a zombie and inherits basic variables from it's entity parent.
	private boolean facing = false;
	
	private int moveSpeed = 2;
	
	private float health = 2F;
	
	private boolean gravity = false;
	
	public int beginningGravityTick;
	public int jGTick = 50;
	public int jGSpeed = 16;
	public int offset = 10;
	
	private TerrainManager terrainManager;
	
	//This sets up a basic zombie.
	
	public Zombie(int id, int x, int y, int xTile, int yTile, int rightInset, int leftInset, int topInset, int bottomInset, TerrainManager terrainManager) {
		super(id, x, y, xTile, yTile, rightInset, leftInset, topInset, bottomInset);
		
		this.terrainManager = terrainManager;

	}
	
	//This updates the entity logic.
	@Override
	public void tick(int currentTick, Player player, TerrainManager terrain){
		if(health == 0F || y > 1920){
			terrain.remove(this);
		}
		if(!facing){
			x+=moveSpeed;
		}else{
			x-=moveSpeed;
		}
		
		if(gravity){
			gravity(currentTick);
		}
		
		checkGroundCollision(currentTick, terrain);
		
	}
	
	//This renders the zombie to the screen
	@Override
	public void render(Screen screen){
		if(!facing){
			screen.render(x, y, 11, 3, 1, 1, 4);
		}else{
			screen.render(x, y, 10, 3, 1, 1, 4);
		}
	}
	
	//This gets called when the player collides and moves the player backwards but if the player can not move backwards it moves the zombie forward
	@Override
	public void collide(Main main, Player player){
		Sound.hurt.play();
		player.healthBar -= 0.5;
		if(x > player.x){
			for(int i = player.x; i > player.x - 100; i--){
				Tile testTile = terrainManager.getTile((int)Math.floor(i / 64), (int) Math.floor((player.y + player.topInset) / 64));
				if(testTile.collisions){
					for(int j = x + 63 - rightInset; j < x + 63 - rightInset + 100; j++){
						Tile testTileZombie = terrainManager.getTile((int)Math.floor(j / 64), (int) Math.floor((y + topInset) / 64));
						if(testTileZombie.collisions){
							x = (testTileZombie.x * 64) - 64 + rightInset;
							return;
						}
					}
					x+=100;
					return;
				}
			}
			player.x-=100;
		}else{
			for(int i = player.x + 63 - player.rightInset; i <  player.x + 63 - player.rightInset + 100; i++){
				Tile testTile = terrainManager.getTile((int)Math.floor(i / 64), (int) Math.floor((player.y + player.topInset) / 64));
				if(testTile.collisions){
					for(int j = x + leftInset; j > x + leftInset - 100; j--){
						Tile testTileZombie = terrainManager.getTile((int)Math.floor(j / 64), (int) Math.floor((y + topInset) / 64));
						if(testTileZombie.collisions){
							x = (testTileZombie.x * 64) + 64 - leftInset;
							return;
						}
					}
					x-=100;
					return;
				}
			}

			player.x+=100;
		}
	}
	
	//This is called when the player attacks and moves the zombie forward.
	@Override
	public void attack(float damage, Player player){
		Sound.hit.play();
		if(x > player.x){
			for(int i = x + 63 - rightInset; i < x + 63 - rightInset + 100; i++){
				Tile testTile = terrainManager.getTile((int)Math.floor(i / 64), (int) Math.floor((y + topInset) / 64));
				if(testTile.collisions){
					x = (testTile.x * 64) - 64 + rightInset;
					health -= damage;
					return;
				}
			}
			x+=100;
		}else{
			for(int i = x + leftInset; i > x + leftInset - 100; i--){
				Tile testTile = terrainManager.getTile((int)Math.floor(i / 64), (int) Math.floor((y + topInset) / 64));
				if(testTile.collisions){
					x = (testTile.x * 64) + 64 - leftInset;
					System.out.println("Testing");
					health -= damage;
					return;
				}
			}
			x-=100;
		}
		
		health -= damage;
	}
	
	//This is gravity for the zombie
	public void gravity(int tickCounter){
		if(gravity){
			int inputTick = tickCounter - beginningGravityTick;
			if(inputTick <= jGTick){
				double a = -jGSpeed / Math.pow(jGTick, 2);
				double equation = ((double)a * Math.pow(inputTick, 2)) + (((double) (-jGTick * 2) * a) * inputTick);
				y += equation;
			}else{
				y += jGSpeed;
			}
		}else{
			resetGravity(tickCounter);
		}
	}
	
	//This resets the gravity variable for the zombie
	public void resetGravity(int tickCounter){
		beginningGravityTick = tickCounter;
	}
	
	//This checks the collisions for the zombie
	public void checkGroundCollision(int currentTick, TerrainManager terrainManager){
		int ex = x + leftInset;
		int exr = x + 63 - rightInset;
		int eyb = y + 63;
		int ey = y + topInset;
		
		boolean setUp = false;
		boolean setLeft = false;
		boolean setRight = false;
		
		Tile uL = terrainManager.getTile((int)Math.floor(ex / 64), (int) Math.floor(ey / 64));
		Tile uR = terrainManager.getTile((int)Math.floor(exr / 64), (int) Math.floor(ey / 64));
		
		Tile bL = terrainManager.getTile((int)Math.floor(ex / 64), (int) Math.floor(eyb / 64));
		Tile bR = terrainManager.getTile((int)Math.floor(exr / 64), (int) Math.floor(eyb / 64));

		Tile gL = terrainManager.getTile((int)Math.floor(ex  / 64), (int) Math.floor((eyb + 1) / 64));
		Tile gR = terrainManager.getTile((int)Math.floor(exr / 64), (int) Math.floor((eyb + 1) / 64));

		
		if((!gL.collisions && !gR.collisions) && !gravity){
			gravity = true;
			resetGravity(currentTick);
		}
		
		if(gL.collisions || gR.collisions && gravity){
			gravity = false;
		}
		
		if(bL.collisions || bR.collisions){
			setUp = true;
		}
		
		if(((gL.collisions && !gR.collisions) && (!facing && exr - moveSpeed < gL.x * 64 + 64)) || ((!gL.collisions && gR.collisions) && (facing && ex + moveSpeed > gR.x * 64 - 1))){
			if(facing){
				x+=moveSpeed;
				facing = false;
			}else{
				x-=moveSpeed;
				facing = true;
			}
		}
		
		if(uR.collisions){
			setLeft = true;
			setUp = false;
		}
		
		if(uL.collisions){
			setRight = true;
			setUp = false;
		}
		
		if(setUp){
			
				y = (bR.y * 64) - 64;
				gravity = false;
				
			

		}
		if(setLeft){	
				x = (uR.x * 64) - 64 + rightInset;
				if(facing){
					facing = false;
				}else{
					facing = true;
				}
		}
		if(setRight){
				x = (uL.x * 64) + 64 - leftInset;
				if(facing){
					facing = false;
				}else{
					facing = true;
				}
			
		}
	}
	
}
