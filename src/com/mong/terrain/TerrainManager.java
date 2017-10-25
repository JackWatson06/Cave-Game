package com.mong.terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.mong.entity.Bat;
import com.mong.entity.Entity;
import com.mong.entity.Gem;
import com.mong.entity.Player;
import com.mong.entity.Zombie;
import com.mong.graphics.Screen;
import com.mong.tile.AirTile;
import com.mong.tile.RockTile;
import com.mong.tile.Tile;

public class TerrainManager {
	
	//This sets up variables for the terrain Manager
	
	private final int loadedTerrains = 3;
	
	public final int levelWidth = 30;
	public final int levelHeight = 17;
	
	public final int terrainWidth = levelWidth * loadedTerrains;
	
	public Tile[] tiles = new Tile[levelWidth * levelHeight * loadedTerrains];
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<Integer> entityLoading = new ArrayList<Integer>();
	
	private int[] levelD = new int[levelWidth * levelHeight * loadedTerrains];
	private int[] entityD;
	
	private TerrainGenerator terrainGen = new TerrainGenerator();
	
	private int[] currentTerrains = new int[3];
	
	private int currentLocation = 1;
	
	private int entityOffset = 0;
	
	public int scrollBound = 0;
	
	//This starts the game and loads the first 3 terrains.
	public TerrainManager(){
		loadTerrain(1, 1, currentLocation);
		updateTerrain();
		entityOffset++;
		loadTerrain(1 ,terrainGen.easyTerrains[currentLocation + 1], currentLocation + 1);
		updateTerrain();
		entityOffset++;
		loadTerrain(1 ,terrainGen.easyTerrains[currentLocation + 2], currentLocation + 2);
		updateTerrain();
	}
	
	public void tick(int currentTick, Player player){
		
		//This loads the terrains as the player moves across the screen
		if(player.x > currentLocation * 1920){
			currentLocation++;
			scrollBound = (currentLocation - 2) * 1920;
			if(currentLocation != 2 && currentLocation + 1 <= 14){
				loadTerrain(1 ,terrainGen.easyTerrains[currentLocation + 1], currentLocation + 1);
				entityOffset++;
				updateTerrain();
			}else if(currentLocation + 1 > 14 && currentLocation + 1 <= 29){
				loadTerrain(2 ,terrainGen.mediumTerrains[currentLocation + 1 - 15], currentLocation + 1);
				entityOffset++;
				updateTerrain();
			}else if(currentLocation + 1> 29){
				loadTerrain(3 ,terrainGen.hardTerrains[currentLocation + 1 - 30], currentLocation + 1);
				entityOffset++;
				updateTerrain();
			}
		}
		
		//This updates the entity logic
		for(int i = 0; i < entities.size(); i++){
			entities.get(i).tick(currentTick, player, this);
		}
		
	}
	
	
	//This method which is strictly for testing also the game to jump forward a certain amount
	public void jumpAhead(int location, Player player){
		currentLocation = location;
		entityOffset = location - 2;
		if(currentLocation <= 14){
			loadTerrain(1 ,terrainGen.easyTerrains[currentLocation], currentLocation);
			entityOffset++;
			updateTerrain();
		}else if(currentLocation > 14 && currentLocation <= 29){
			loadTerrain(2 ,terrainGen.mediumTerrains[currentLocation - 15], currentLocation);
			entityOffset++;
			updateTerrain();
		}else if(currentLocation> 29){
			loadTerrain(3 ,terrainGen.hardTerrains[currentLocation - 30], currentLocation);
			entityOffset++;
			updateTerrain();
		}
		
		if(currentLocation + 1<= 14){
			loadTerrain(1 ,terrainGen.easyTerrains[currentLocation + 1 + 1], currentLocation + 1);
			entityOffset++;
			updateTerrain();
		}else if(currentLocation + 1 <= 29){
			loadTerrain(2 ,terrainGen.mediumTerrains[currentLocation + 1 - 15], currentLocation + 1);
			entityOffset++;
			updateTerrain();
		}else if(currentLocation + 1> 29){
			loadTerrain(3 ,terrainGen.hardTerrains[currentLocation + 1 - 30], currentLocation + 1);
			entityOffset++;
			updateTerrain();
		}
		
		if(currentLocation + 2 <= 14){
			loadTerrain(1 ,terrainGen.easyTerrains[currentLocation + 2], currentLocation + 2);
			entityOffset++;
			updateTerrain();
		}else if(currentLocation + 2 > 14 && currentLocation + 2 <= 29){
			loadTerrain(2 ,terrainGen.mediumTerrains[currentLocation + 2 - 15], currentLocation + 2);
			entityOffset++;
			updateTerrain();
		}else if(currentLocation + 2> 29){
			loadTerrain(3 ,terrainGen.hardTerrains[currentLocation + 2 - 30], currentLocation + 2);
			entityOffset++;
			updateTerrain();
		}
		
		player.x = currentLocation * 1920;
	}
	
	
	//This renders the level to the screen
	public void render(Screen screen){
		for(int i = 0; i < tiles.length; i++){
			tiles[i].render(screen);
		}
		
		for(int i = 0; i < entities.size(); i++){
			entities.get(i).render(screen);
		}
	}
	
	//This removes entities from the loaded entity list.
	public void remove(Entity entity){
		for(int i = 0; i < entities.size(); i++){
			if(entities.get(i).equals(entity)){
				entities.remove(i);
				entityLoading.remove(i);
				i--;
			}
		}
	}
	
	//This updates the terrain when a new level is loaded
	public void updateTerrain(){
		for(int y = 0; y < levelHeight; y++){
			int currentTerrain = 0;
			int xOffset = 0;
			for(int x = 0; x < terrainWidth; x++){

				if((x) % 30 == 0 && x != 0){
					currentTerrain++;
					xOffset = levelWidth * (currentTerrains[currentTerrain] - 1);
					
				}else if(x == 0){
					xOffset = levelWidth * (currentTerrains[currentTerrain] - 1);
				}
				
				
				//This is the list of all the possible blocks/tiles in the game
				
				if(levelD[x + y * terrainWidth] == 0) tiles[x + y * terrainWidth] = new AirTile(0, xOffset, y);
				if(levelD[x + y * terrainWidth] == 1) tiles[x + y * terrainWidth] = new RockTile(1, xOffset, y, 0, 0);
				if(levelD[x + y * terrainWidth] == 2) tiles[x + y * terrainWidth] = new RockTile(2, xOffset, y, 1, 0);
				if(levelD[x + y * terrainWidth] == 3) tiles[x + y * terrainWidth] = new RockTile(3, xOffset, y, 3, 0);
				if(levelD[x + y * terrainWidth] == 4) tiles[x + y * terrainWidth] = new RockTile(4, xOffset, y, 4, 0);
				if(levelD[x + y * terrainWidth] == 5) tiles[x + y * terrainWidth] = new RockTile(5, xOffset, y, 3, 1);
				if(levelD[x + y * terrainWidth] == 6) tiles[x + y * terrainWidth] = new RockTile(6, xOffset, y, 4, 1);
				if(levelD[x + y * terrainWidth] == 7) tiles[x + y * terrainWidth] = new RockTile(7, xOffset, y, 6, 0);
				if(levelD[x + y * terrainWidth] == 8) tiles[x + y * terrainWidth] = new RockTile(8, xOffset, y, 7, 0);
				if(levelD[x + y * terrainWidth] == 9) tiles[x + y * terrainWidth] = new RockTile(9, xOffset, y, 6, 1);
				if(levelD[x + y * terrainWidth] == 10) tiles[x + y * terrainWidth] = new RockTile(10, xOffset, y, 7, 1);
				if(levelD[x + y * terrainWidth] == 11) tiles[x + y * terrainWidth] = new RockTile(11, xOffset, y, 9, 0);
				if(levelD[x + y * terrainWidth] == 12) tiles[x + y * terrainWidth] = new RockTile(12, xOffset, y, 10, 0);
				if(levelD[x + y * terrainWidth] == 13) tiles[x + y * terrainWidth] = new RockTile(13, xOffset, y, 9, 1);
				if(levelD[x + y * terrainWidth] == 14) tiles[x + y * terrainWidth] = new RockTile(14, xOffset, y, 10, 1);
				if(levelD[x + y * terrainWidth] == 15) tiles[x + y * terrainWidth] = new RockTile(15, xOffset, y, 12, 0);
				if(levelD[x + y * terrainWidth] == 16) tiles[x + y * terrainWidth] = new RockTile(16, xOffset, y, 13, 0);
				if(levelD[x + y * terrainWidth] == 17) tiles[x + y * terrainWidth] = new RockTile(17, xOffset, y, 12, 1);
				if(levelD[x + y * terrainWidth] == 18) tiles[x + y * terrainWidth] = new RockTile(18, xOffset, y, 13, 1);
				if(levelD[x + y * terrainWidth] == 19) tiles[x + y * terrainWidth] = new RockTile(19, xOffset, y, 0, 2);
				if(levelD[x + y * terrainWidth] == 20) tiles[x + y * terrainWidth] = new RockTile(20, xOffset, y, 1, 2);
				if(levelD[x + y * terrainWidth] == 21) tiles[x + y * terrainWidth] = new RockTile(21, xOffset, y, 0, 3);
				if(levelD[x + y * terrainWidth] == 22) tiles[x + y * terrainWidth] = new RockTile(22, xOffset, y, 1, 3);
				if(levelD[x + y * terrainWidth] == 23) tiles[x + y * terrainWidth] = new RockTile(23, xOffset, y, 3, 2);
				if(levelD[x + y * terrainWidth] == 24) tiles[x + y * terrainWidth] = new RockTile(24, xOffset, y, 4, 2);
				if(levelD[x + y * terrainWidth] == 25) tiles[x + y * terrainWidth] = new RockTile(25, xOffset, y, 3, 3);
				if(levelD[x + y * terrainWidth] == 26) tiles[x + y * terrainWidth] = new RockTile(26, xOffset, y, 4, 3);
				if(levelD[x + y * terrainWidth] == 27) tiles[x + y * terrainWidth] = new RockTile(27, xOffset, y, 6, 2);
				if(levelD[x + y * terrainWidth] == 28) tiles[x + y * terrainWidth] = new RockTile(28, xOffset, y, 7, 2);
				if(levelD[x + y * terrainWidth] == 29) tiles[x + y * terrainWidth] = new RockTile(29, xOffset, y, 6, 3);
				if(levelD[x + y * terrainWidth] == 30) tiles[x + y * terrainWidth] = new RockTile(30, xOffset, y, 7, 3);
				if(levelD[x + y * terrainWidth] == 31) tiles[x + y * terrainWidth] = new RockTile(31, xOffset, y, 0, 1);
				if(levelD[x + y * terrainWidth] == 32) tiles[x + y * terrainWidth] = new RockTile(32, xOffset, y, 1, 1);
				if(levelD[x + y * terrainWidth] == 33) tiles[x + y * terrainWidth] = new Tile(33, xOffset, y, 0, 5);
				if(levelD[x + y * terrainWidth] == 34) tiles[x + y * terrainWidth] = new Tile(34, xOffset, y, 1, 5);
				if(levelD[x + y * terrainWidth] == 35) tiles[x + y * terrainWidth] = new RockTile(35, xOffset, y, 2, 6);
				if(levelD[x + y * terrainWidth] == 36) tiles[x + y * terrainWidth] = new RockTile(36, xOffset, y, 3, 6);
				if(levelD[x + y * terrainWidth] == 37) tiles[x + y * terrainWidth] = new RockTile(37, xOffset, y, 5, 6);
				if(levelD[x + y * terrainWidth] == 38) tiles[x + y * terrainWidth] = new RockTile(38, xOffset, y, 6, 6);
				if(levelD[x + y * terrainWidth] == 39) tiles[x + y * terrainWidth] = new RockTile(39, xOffset, y, 5, 7);
				if(levelD[x + y * terrainWidth] == 40) tiles[x + y * terrainWidth] = new RockTile(40, xOffset, y, 6, 7);
				if(levelD[x + y * terrainWidth] == 41) tiles[x + y * terrainWidth] = new RockTile(41, xOffset, y, 8, 6);
				if(levelD[x + y * terrainWidth] == 42) tiles[x + y * terrainWidth] = new RockTile(42, xOffset, y, 9, 6);
				if(levelD[x + y * terrainWidth] == 43) tiles[x + y * terrainWidth] = new RockTile(43, xOffset, y, 8, 7);
				if(levelD[x + y * terrainWidth] == 44) tiles[x + y * terrainWidth] = new RockTile(44, xOffset, y, 9, 7);
				if(levelD[x + y * terrainWidth] == 45) tiles[x + y * terrainWidth] = new RockTile(45, xOffset, y, 11, 6);
				if(levelD[x + y * terrainWidth] == 46) tiles[x + y * terrainWidth] = new RockTile(46, xOffset, y, 12, 6);
				if(levelD[x + y * terrainWidth] == 47) tiles[x + y * terrainWidth] = new RockTile(47, xOffset, y, 11, 7);
				if(levelD[x + y * terrainWidth] == 48) tiles[x + y * terrainWidth] = new RockTile(48, xOffset, y, 12, 7);
				if(levelD[x + y * terrainWidth] == 49) tiles[x + y * terrainWidth] = new RockTile(49, xOffset, y, 14, 6);
				if(levelD[x + y * terrainWidth] == 50) tiles[x + y * terrainWidth] = new RockTile(50, xOffset, y, 15, 6);
				if(levelD[x + y * terrainWidth] == 51) tiles[x + y * terrainWidth] = new RockTile(51, xOffset, y, 14, 7);
				if(levelD[x + y * terrainWidth] == 52) tiles[x + y * terrainWidth] = new RockTile(52, xOffset, y, 15, 7);
				if(levelD[x + y * terrainWidth] == 53) tiles[x + y * terrainWidth] = new RockTile(53, xOffset, y, 2, 8);
				if(levelD[x + y * terrainWidth] == 54) tiles[x + y * terrainWidth] = new RockTile(54, xOffset, y, 3, 8);
				if(levelD[x + y * terrainWidth] == 55) tiles[x + y * terrainWidth] = new RockTile(55, xOffset, y, 2, 9);
				if(levelD[x + y * terrainWidth] == 56) tiles[x + y * terrainWidth] = new RockTile(56, xOffset, y, 3, 9);
				if(levelD[x + y * terrainWidth] == 57) tiles[x + y * terrainWidth] = new RockTile(57, xOffset, y, 5, 8);
				if(levelD[x + y * terrainWidth] == 58) tiles[x + y * terrainWidth] = new RockTile(58, xOffset, y, 6, 8);
				if(levelD[x + y * terrainWidth] == 59) tiles[x + y * terrainWidth] = new RockTile(59, xOffset, y, 5, 9);
				if(levelD[x + y * terrainWidth] == 60) tiles[x + y * terrainWidth] = new RockTile(60, xOffset, y, 6, 9);
				if(levelD[x + y * terrainWidth] == 61) tiles[x + y * terrainWidth] = new RockTile(61, xOffset, y, 8, 8);
				if(levelD[x + y * terrainWidth] == 62) tiles[x + y * terrainWidth] = new RockTile(62, xOffset, y, 9, 8);
				if(levelD[x + y * terrainWidth] == 63) tiles[x + y * terrainWidth] = new RockTile(63, xOffset, y, 8, 9);
				if(levelD[x + y * terrainWidth] == 64) tiles[x + y * terrainWidth] = new RockTile(64, xOffset, y, 9, 9);
				if(levelD[x + y * terrainWidth] == 65) tiles[x + y * terrainWidth] = new RockTile(65, xOffset, y, 2, 7);
				if(levelD[x + y * terrainWidth] == 66) tiles[x + y * terrainWidth] = new RockTile(66, xOffset, y, 3, 7);
				if(levelD[x + y * terrainWidth] == 67) tiles[x + y * terrainWidth] = new RockTile(67, xOffset, y, 2, 10);
				if(levelD[x + y * terrainWidth] == 68) tiles[x + y * terrainWidth] = new RockTile(68, xOffset, y, 3, 10);
				if(levelD[x + y * terrainWidth] == 69) tiles[x + y * terrainWidth] = new RockTile(69, xOffset, y, 5, 10);
				if(levelD[x + y * terrainWidth] == 70) tiles[x + y * terrainWidth] = new RockTile(70, xOffset, y, 6, 10);
				if(levelD[x + y * terrainWidth] == 71) tiles[x + y * terrainWidth] = new RockTile(71, xOffset, y, 5, 11);
				if(levelD[x + y * terrainWidth] == 72) tiles[x + y * terrainWidth] = new RockTile(72, xOffset, y, 6, 11);
				if(levelD[x + y * terrainWidth] == 73) tiles[x + y * terrainWidth] = new RockTile(73, xOffset, y, 8, 10);
				if(levelD[x + y * terrainWidth] == 74) tiles[x + y * terrainWidth] = new RockTile(74, xOffset, y, 9, 10);
				if(levelD[x + y * terrainWidth] == 75) tiles[x + y * terrainWidth] = new RockTile(75, xOffset, y, 8, 11);
				if(levelD[x + y * terrainWidth] == 76) tiles[x + y * terrainWidth] = new RockTile(76, xOffset, y, 9, 11);
				if(levelD[x + y * terrainWidth] == 77) tiles[x + y * terrainWidth] = new RockTile(77, xOffset, y, 11, 10);
				if(levelD[x + y * terrainWidth] == 78) tiles[x + y * terrainWidth] = new RockTile(78, xOffset, y, 12, 10);
				if(levelD[x + y * terrainWidth] == 79) tiles[x + y * terrainWidth] = new RockTile(79, xOffset, y, 11, 11);
				if(levelD[x + y * terrainWidth] == 80) tiles[x + y * terrainWidth] = new RockTile(80, xOffset, y, 12, 11);
				if(levelD[x + y * terrainWidth] == 81) tiles[x + y * terrainWidth] = new RockTile(81, xOffset, y, 14, 10);
				if(levelD[x + y * terrainWidth] == 82) tiles[x + y * terrainWidth] = new RockTile(82, xOffset, y, 15, 10);
				if(levelD[x + y * terrainWidth] == 83) tiles[x + y * terrainWidth] = new RockTile(83, xOffset, y, 14, 11);
				if(levelD[x + y * terrainWidth] == 84) tiles[x + y * terrainWidth] = new RockTile(84, xOffset, y, 15, 11);
				if(levelD[x + y * terrainWidth] == 85) tiles[x + y * terrainWidth] = new RockTile(85, xOffset, y, 2, 12);
				if(levelD[x + y * terrainWidth] == 86) tiles[x + y * terrainWidth] = new RockTile(86, xOffset, y, 3, 12);
				if(levelD[x + y * terrainWidth] == 87) tiles[x + y * terrainWidth] = new RockTile(87, xOffset, y, 2, 13);
				if(levelD[x + y * terrainWidth] == 88) tiles[x + y * terrainWidth] = new RockTile(88, xOffset, y, 3, 13);
				if(levelD[x + y * terrainWidth] == 89) tiles[x + y * terrainWidth] = new RockTile(89, xOffset, y, 5, 12);
				if(levelD[x + y * terrainWidth] == 90) tiles[x + y * terrainWidth] = new RockTile(90, xOffset, y, 6, 12);
				if(levelD[x + y * terrainWidth] == 91) tiles[x + y * terrainWidth] = new RockTile(91, xOffset, y, 5, 13);
				if(levelD[x + y * terrainWidth] == 92) tiles[x + y * terrainWidth] = new RockTile(92, xOffset, y, 6, 13);
				if(levelD[x + y * terrainWidth] == 93) tiles[x + y * terrainWidth] = new RockTile(93, xOffset, y, 8, 12);
				if(levelD[x + y * terrainWidth] == 94) tiles[x + y * terrainWidth] = new RockTile(94, xOffset, y, 9, 12);
				if(levelD[x + y * terrainWidth] == 95) tiles[x + y * terrainWidth] = new RockTile(95, xOffset, y, 8, 13);
				if(levelD[x + y * terrainWidth] == 96) tiles[x + y * terrainWidth] = new RockTile(96, xOffset, y, 9, 13);
				if(levelD[x + y * terrainWidth] == 97) tiles[x + y * terrainWidth] = new RockTile(97, xOffset, y, 2, 11);
				if(levelD[x + y * terrainWidth] == 98) tiles[x + y * terrainWidth] = new RockTile(98, xOffset, y, 3, 11);
				
				xOffset++;
			}
			
		}
		for(int i = 0; i < entityD.length; i+=3){
			int currentX = entityD[i + 1] + (1920 * entityOffset);
			int currentY = entityD[i + 2];
			
			if(entityD[i] == 100){
				entities.add(new Gem(100, currentX, currentY, 0, 4, 0, 0, 0, 0, 3));
				entityLoading.add(entityOffset + 1);
			}
			if(entityD[i] == 101){
				entities.add(new Gem(101, currentX, currentY, 1, 4, 4, 3, 1, 1, 1));
				entityLoading.add(entityOffset + 1);
			}
			if(entityD[i] == 102){
				entities.add(new Gem(102, currentX, currentY, 2, 4, 0, 0, 0, 0, 5));
				entityLoading.add(entityOffset + 1);
			}
			if(entityD[i] == 103){
				entities.add(new Bat(103, currentX, currentY, -1, -1, 0, 0, 5, 3));
				entityLoading.add(entityOffset + 1);
			}
			if(entityD[i] == 104){
				entities.add(new Zombie(104, currentX, currentY, -1, -1, 4, 5, 5, 0, this));
				entityLoading.add(entityOffset + 1);
			}
			

		}
		
	}
	
	//This loads the data from a terrain file and stores this loaded data into int arrays
	public void loadTerrain(int difficulty, int loadTerrain, int terrainOffset){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("terrains/" + "Terrain_" + difficulty + "_" + loadTerrain)));
			
			try{
				terrainOffset--;
				int subtractAmount = terrainOffset / 3;
				int offset = terrainOffset - (subtractAmount * 3);
				
				
				currentTerrains[offset] = terrainOffset + 1;

				for(int y = 0; y < levelHeight; y++){
					
					String currentLine = br.readLine();
					
					int lastFoundId = 0;
					for(int x = 0; x < levelWidth; x++){
						String currentId = "";
						for(int i = lastFoundId; i < currentLine.length(); i++){
							if(currentLine.charAt(i) == ','){
								lastFoundId = i + 1;
								break;
							}else{
								currentId += currentLine.charAt(i);
							}
						}
						levelD[ (x + offset * levelWidth) + y * terrainWidth] = Integer.parseInt(currentId);
					}
				}
				
				//Delete the last levels entities/ remove the entities from the array as this data array is for loading one level.
				for(int i = 0; i < entityLoading.size(); i++){
					if(entityLoading.get(i) == terrainOffset - 2){
						entities.remove(i);
						entityLoading.remove(i);
						i--;
					}
				}
				
				int totalEntities = Integer.parseInt(br.readLine());
				
				if(totalEntities > 0){
					entityD = new int[totalEntities * 3];
					for(int i = 0; i < totalEntities; i++){
						entityD[i * 3] = Integer.parseInt(br.readLine());
						entityD[i * 3 + 1] = Integer.parseInt(br.readLine());
						entityD[i * 3 + 2] = Integer.parseInt(br.readLine());
					}
				}
				
				br.close();
				
			}catch(IOException e){
				e.printStackTrace();
			}
			

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}
	
	//This gets a tile from the screen.
	public Tile getTile(int x, int y){
		if(y < 17 && x >= 0){
			if(x >= 90){
				int offset = x / 90;
				x -= 90 * offset;
			}
			
			if(x + y * terrainWidth >= 0 && x + y * terrainWidth < 1530){
				Tile currentTile = tiles[x + y * terrainWidth];
				return currentTile;
			}else{
				return new AirTile(0,0,0);
			}
		}
		return new AirTile(0,0,0);
	}

}