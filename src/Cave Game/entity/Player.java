package com.mong.entity;

import com.mong.graphics.Screen;
import com.mong.main.InputManager;
import com.mong.main.Main;
import com.mong.sound.Sound;
import com.mong.terrain.TerrainManager;
import com.mong.tile.Tile;

public class Player extends Entity{

	//These are basic variables for the player entity
	
	public boolean movedA = false;
	public boolean movedS = false;
	public boolean movedW = false;
	public boolean movedD = false;
	
	private InputManager input;
	private TerrainManager terrainManager;
	private Main main;
	
	private int moveSpeed = 6;
	private int currentSpeed;
	private boolean facing = true;
	
	private int xLock = 0;
	
	public boolean jump = false;
	
	public boolean gravity = false;
	public int beginningGravityTick;
	
	public int jGTick = 50;
	public int jGSpeed = 16;
	public int offset = 10;
	
	public float healthBar = 3F;
	private int attackRange = 64;
	
	private int lastStandX = 0;
	private int lastStandY = 0;
	
	private boolean hitAnimation = false;
	private int hitAnimationCounter = 0;
	
	//This sets up the player
	public Player(int id, int x, int y, int xTile, int yTile, int rightInset, int leftInset, int topInset, int bottomInset, Main main, InputManager input, TerrainManager terrainManager) {
		super(id, x, y, xTile, yTile, rightInset, leftInset, topInset, bottomInset);
		this.main = main;
		this.input = input;
		this.terrainManager = terrainManager;

	}

	//This updates the player logic
	public void tick(int tickCounter, float interpolation){
		
		jump(tickCounter, interpolation);
		gravity(tickCounter, interpolation);
		
		//This moves the player
		if(input.d){
			x += (int) (moveSpeed * interpolation);
			movedD = true;
			facing = true;
			currentSpeed = (int) (moveSpeed * interpolation);
		}
		if(input.a){
			x -= (int) (moveSpeed * interpolation);
			movedA = true;
			facing = false;
			currentSpeed = (int) (moveSpeed * interpolation);
		}

		
		//This allows the player to attack
		if(input.e){
			attack();
			input.e = false;
		}
		
		if(!input.a && !input.d){
			currentSpeed = 0;
		}
		
		//This locks the player x so they don't move backwards so they don't fall off the last terrain
		
		xLock = terrainManager.scrollBound;
		
		if(x < xLock){
			x = xLock;
		}
		
		checkCollisions(tickCounter, interpolation);
		
		checkEntityCollisions();
		
		//This sets a standing point if the player falls
		
		if(!jump && !gravity && tickCounter % 5 == 0){
			if(movedD){
				lastStandX = x - 16;
				lastStandY = y;
			}else{
				lastStandX = x + 16;
				lastStandY = y;
			}
		}
		
		
		//This damages the player if they fall
		if(y > 1920){
			takeDamage(0.5F);
			x = lastStandX;
			y = lastStandY;
			Sound.hurt.play();
		}
		
		//This displays the death screen and sets the highscore
		if(healthBar == 0.0F){
			main.deathScreen = true;
			main.highScore.writeHightScore(main.score);
			main.highScore.readHighScore();
			main.currentHighScore = main.highScore.currentHighScore;
        	Sound.loop = false;
        	Sound.music.stop();
		}
		
	}
	
	//This is the gravity for the player which is modeled by a parabola
	public void gravity(int tickCounter, float interpolation){
		if(!input.jump && gravity){
			int inputTick = tickCounter - beginningGravityTick;
			if(inputTick <= jGTick){
				double a = -jGSpeed / Math.pow(jGTick, 2);
				double equation = ((double)a * Math.pow(inputTick, 2)) + (((double) (-jGTick * 2) * a) * inputTick);
				y += equation * interpolation;
			}else{
				y += jGSpeed * interpolation;
			}
		}else{
			resetGravity(tickCounter, interpolation);
		}
	}
	
	//This is the jump for the player which is also modeled by a parabola
	
	public void jump(int tickCounter, float interpolation){
		if(input.jump){
			jump = true;
			gravity = false;
			int inputTick = tickCounter - input.jumpTick;
			double a = jGSpeed / Math.pow(jGTick, 2);
			double equation = ((double)a * Math.pow(inputTick, 2)) - (((double) (jGTick * 2) * a) * inputTick) + ((double)jGSpeed);
			y -=  equation * interpolation;
			if(inputTick >= jGTick - offset){
				input.jump = false;
				jump = false;
			}
		}else{
			jGTick = (int) (50 / interpolation);
			jump = false;
		}
	}
	
	//This alows the player to attack
	
	public void attack(){
			
		if(facing){
			int axl = x + 63 - 16;
			int axr = x + 63 - 16 + attackRange;
			int ayu = y + 32 - (attackRange / 2);
			int ayb = y + 32 + (attackRange / 2);
			
			for(int i = 0; i < terrainManager.entities.size(); i++){
				int entityX = terrainManager.entities.get(i).x;
				int entityXR = terrainManager.entities.get(i).x + 63;
				int entityY = terrainManager.entities.get(i).y;
				int entityYB = terrainManager.entities.get(i).y + 63;
				
				if(axl < entityXR && axr > entityX && ayu < entityYB && ayb > entityY){
					terrainManager.entities.get(i).attack(0.5F, this);
				}
				
			}
		}else{
			int axl = x - attackRange;
			int axr = x;
			int ayu = y + 32 - (attackRange / 2);
			int ayb = y + 32 + (attackRange / 2);
			
			for(int i = 0; i < terrainManager.entities.size(); i++){
				int entityX = terrainManager.entities.get(i).x;
				int entityXR = terrainManager.entities.get(i).x + 63;
				int entityY = terrainManager.entities.get(i).y;
				int entityYB = terrainManager.entities.get(i).y + 63;
				
				if(axl < entityXR && axr > entityX && ayu < entityYB && ayb > entityY){
					terrainManager.entities.get(i).attack(0.5F, this);
				}
				
			}
		}
		
		hitAnimation = true;
				
	}
	
	//This causes the player to take damage
	
	public void takeDamage(float damage){
		healthBar -= damage;
	}
	
	//This resets the gravity variable
	public void resetGravity(int tickCounter, float interpolation){
		jGTick = (int) (50 / interpolation);
		beginningGravityTick = tickCounter;
	}
	
	//This checks the collisions with the player and the environment around but not the entities.
	
	public void checkCollisions(int tickCounter, float interpolation){

		int px = x;
		int pxr = x + 63 - 16;
		int py = y;
		int pyb = y + 63;
		
		boolean setUp = false;
		boolean setDown = false;
		boolean setLeft = false;
		boolean setRight = false;
		
		Tile uL = terrainManager.getTile((int)Math.floor(px / 64), (int) Math.floor(py / 64));
		Tile uR = terrainManager.getTile((int)Math.floor(pxr / 64), (int) Math.floor(py / 64));
		
		Tile bL = terrainManager.getTile((int)Math.floor(px / 64), (int) Math.floor(pyb / 64));
		Tile bR = terrainManager.getTile((int)Math.floor(pxr / 64), (int) Math.floor(pyb / 64));

		Tile gL = terrainManager.getTile((int)Math.floor(px  / 64), (int) Math.floor((pyb + 1) / 64));
		Tile gR = terrainManager.getTile((int)Math.floor(pxr / 64), (int) Math.floor((pyb + 1) / 64));
		if(movedD){
			 gL = terrainManager.getTile((int)Math.floor((px - currentSpeed)  / 64), (int) Math.floor((pyb + 1) / 64));
			 gR = terrainManager.getTile((int)Math.floor((pxr - currentSpeed) / 64), (int) Math.floor((pyb + 1) / 64));
		}else if(movedA){
			 gL = terrainManager.getTile((int)Math.floor((px + currentSpeed)  / 64), (int) Math.floor((pyb + 1) / 64));
			 gR = terrainManager.getTile((int)Math.floor((pxr + currentSpeed) / 64), (int) Math.floor((pyb + 1) / 64));
		}
		
		Tile ugL = terrainManager.getTile((int)Math.floor(px  / 64), (int) Math.floor((py - 1) / 64));
		Tile ugR = terrainManager.getTile((int)Math.floor(pxr / 64), (int) Math.floor((py - 1) / 64));
		if(movedD){
			 ugL = terrainManager.getTile((int)Math.floor((px - currentSpeed)  / 64), (int) Math.floor((py - 1) / 64));
			 ugR = terrainManager.getTile((int)Math.floor((pxr - currentSpeed) / 64), (int) Math.floor((py - 1) / 64));
		}else if(movedA){
			 ugL = terrainManager.getTile((int)Math.floor((px + currentSpeed)  / 64), (int) Math.floor((py - 1) / 64));
			 ugR = terrainManager.getTile((int)Math.floor((pxr + currentSpeed) / 64), (int) Math.floor((py - 1) / 64));
		}
		if((!gL.collisions && !gR.collisions) && !jump && !gravity){
			gravity = true;
			resetGravity(tickCounter, interpolation);
		}
		
		if(uR.collisions && !uL.collisions && !bL.collisions && !bR.collisions){
			if(pxr - currentSpeed <= uR.x * 64){
				setLeft = true;
				setDown = false;
				setUp = false;
			}else{
				setDown = true;
				setUp = false;
				setLeft = false;
			}
		}
		if(uL.collisions && !uR.collisions && !bL.collisions && !bR.collisions){
			if(px + currentSpeed >= (uL.x * 64) + 63){
				setRight = true;
				setDown = false;
				setUp = false;
			}else{
				setDown = true;
				setUp = false;
				setLeft = false;
			}
		}
		if(bL.collisions && !uR.collisions && !uL.collisions && !bR.collisions){
			if(px + currentSpeed >= (bL.x * 64) + 63){
				setRight = true;
				setDown = false;
				setUp = false;
			}else{
				setUp = true;
				setDown = false;
				setLeft = false;
			}
		}
		if(bR.collisions && !uL.collisions && !bL.collisions && !uR.collisions){
			if(pxr - currentSpeed <= bR.x * 64){
				setLeft = true;
				setDown = false;
				setUp = false;
			}else{
				setUp = true;
				setDown = false;
				setLeft = false;
			}
		}
		
		if(bL.collisions && bR.collisions){
			setUp = true;
			setLeft = false;
			setRight = false;
		}
		if(uL.collisions && uR.collisions){
			setDown = true;
			setLeft = false;
			setRight = false;
		}
		if(uR.collisions && bR.collisions){
			setLeft = true;
			setUp = false;
			setDown = false;
		}
		if(uL.collisions && bL.collisions){
			setRight = true;
			setUp = false;
			setDown = false;
		}
		if(uR.collisions && bL.collisions && gravity){
			setUp = true;
			setLeft = true;
		}
		if(uR.collisions && bL.collisions && jump){
			setDown = true;
			setRight = true;
		}
		if(uL.collisions && bR.collisions && gravity){
			setUp = true;
			setRight = true;
		}
		if(uL.collisions && bR.collisions && jump){
			setDown = true;
			setLeft = true;
		}
		
		if(ugL.collisions && ugR.collisions && gL.collisions && gR.collisions){
			input.jumpAvailability = true;
		}
		if(ugL.collisions && ugR.collisions && gL.collisions){
			input.jumpAvailability = true;
		}
		if(ugL.collisions && ugR.collisions && gR.collisions){
			input.jumpAvailability = true;
		}
		if(ugL.collisions && gL.collisions && gR.collisions){
			input.jumpAvailability = true;
		}
		if(ugR.collisions && gL.collisions && gR.collisions){
			input.jumpAvailability = true;
		}
		if(ugR.collisions && gL.collisions){
			input.jumpAvailability = true;
		}
		if(ugL.collisions && gR.collisions){
			input.jumpAvailability = true;
		}
		
		
		boolean data = false;
		if(data){
			System.out.println("UboveL: " + uL);
			System.out.println("UboveR: " + uR);
			System.out.println();
			System.out.println("BelowL: " + bL);
			System.out.println("BelowR: " + bR);
			System.out.println();
			System.out.println("GravityL: " + gL);
			System.out.println("GravityR: " + gR);
			System.out.println();
			System.out.println("GravityUL: " + ugL);
			System.out.println("GravityUR: " + ugR);
			System.out.println("Player X: " + x);
			System.out.println("Player Y: " + y);
		}
		
		if(setDown){
				y = (uR.y * 64) + 64;
				jump = false;
				input.jump = false;
		}
		if(setUp){
			
				y = (bR.y * 64) - 64;
				input.jumpAvailability = true;
				gravity = false;
			

		}
		if(setLeft){	
				x = (uR.x * 64) - 48;
		}
		if(setRight){
				x = (uL.x * 64) + 64;
			
		}
	}
	
	//This checks the collisions with entities.
	
	public void checkEntityCollisions(){
		
		int px = x;
		int pxr = x + 63 - 16;
		int py = y;
		int pyb = y + 63;
		
		for(int i = 0; i < terrainManager.entities.size(); i++){
			Entity currentEntity = terrainManager.entities.get(i);
			int entityX = currentEntity.x + currentEntity.leftInset;
			int entityXR = currentEntity.x + 63 - currentEntity.rightInset;
			int entityY = currentEntity.y + currentEntity.topInset;
			int entityYB = currentEntity.y + 63 - currentEntity.bottomInset;
			
			if(px < entityXR && pxr > entityX && py < entityYB && pyb > entityY){
				terrainManager.entities.get(i).collide(main, this);
				if(terrainManager.entities.get(i).removeOnCollide){
					terrainManager.entities.remove(i);
					terrainManager.entityLoading.remove(i);
					i--;
				}
			}
			
		}
	}
	
	//This resets the move buttons
	
	public void resetButtons(){
		movedD = false;
		movedA = false;
		movedS = false;
		movedW = false;
	}
	
	
	
	//This renders the player
	@Override
	public void render(Screen screen){
		if(!hitAnimation){
			if(facing){
				screen.render(x, y, xTile, yTile, 1, 1, 4);
			}else{
				screen.render(x, y, xTile + 1, yTile, 1, 1, 4);
			}
		}else{
			
			//This renders the hit animation for the player
			
			if(hitAnimationCounter < 8){
				if(facing){
					screen.render(x, y, xTile, yTile + 1, 1, 1, 4);
				}else{
					screen.render(x, y, xTile + 1, yTile + 1, 1, 1, 4);
				}
			}else if(hitAnimationCounter >= 8 && hitAnimationCounter < 16){
				if(facing){
					screen.render(x, y, xTile, yTile + 2, 1, 1, 4);
				}else{
					screen.render(x, y, xTile + 1, yTile + 2, 1, 1, 4);
				}
			}else{
				if(facing){
					screen.render(x, y, xTile, yTile + 1, 1, 1, 4);
				}else{
					screen.render(x, y, xTile + 1, yTile + 1, 1, 1, 4);
				}
			}
			
			hitAnimationCounter++;
			
			if(hitAnimationCounter > 24){
				hitAnimationCounter = 0;
				hitAnimation = false;
			}
		}
		

	}
	
	//This renders the healthBar when it is at different stages.
	
	public void renderHealthBar(Screen screen){
		if(healthBar == 3F || healthBar == 2F || healthBar == 1F){
			if(healthBar == 3F){
				for(int i = 0; i < 3; i++){
					screen.render(10 + screen.screenX + (i * 56), 10, 11, 14, 1, 1, 4);
				}
			}else if(healthBar == 2F){
				for(int i = 0; i < 3; i++){
					if(i == 2){
						screen.render(10 + screen.screenX + (i * 56), 10, 10, 14, 1, 1, 4);
						continue;
					}else{
						screen.render(10 + screen.screenX + (i * 56), 10, 11, 14, 1, 1, 4);
					}
				}
			}else{
				for(int i = 0; i < 3; i++){
					if(i != 0){
						screen.render(10 + screen.screenX + (i * 56), 10, 10, 14, 1, 1, 4);
						continue;
					}else{
						screen.render(10 + screen.screenX + (i * 56), 10, 11, 14, 1, 1, 4);
					}
				}
			}
		}else{
			if(healthBar == 2.5F){
				for(int i = 0; i < 3; i++){
					if(i == 2){
						screen.render(10 + screen.screenX + (i * 56), 10, 12, 14, 1, 1, 4);
						continue;
					}else{
						screen.render(10 + screen.screenX + (i * 56), 10, 11, 14, 1, 1, 4);
					}
				}
			}else if(healthBar == 1.5F){
				for(int i = 0; i < 3; i++){
					if(i == 0){
						screen.render(10 + screen.screenX + (i * 56), 10, 11, 14, 1, 1, 4);
						continue;
					}else if(i == 1){
						screen.render(10 + screen.screenX + (i * 56), 10, 12, 14, 1, 1, 4);
					}else{
						screen.render(10 + screen.screenX + (i * 56), 10, 10, 14, 1, 1, 4);
					}
				}
			}else if(healthBar == 0.5F){
				for(int i = 0; i < 3; i++){
					if(i != 0){
						screen.render(10 + screen.screenX + (i * 56), 10, 10, 14, 1, 1, 4);
						continue;
					}else{
						screen.render(10 + screen.screenX + (i * 56), 10, 12, 14, 1, 1, 4);
					}
				}
			}
		}
		
		if(healthBar == 0F){
			for(int i = 0; i < 3; i++){
				screen.render(10 + screen.screenX + (i * 56), 10, 10, 14, 1, 1, 4);
			}
		}
	}
}
