package com.mong.terrain;

public class TerrainGenerator {

	public int[] easyTerrains = new int[15];
	public int[] mediumTerrains = new int[15];
	public int[] hardTerrains = new int[1000];
	
	//THIS CLASS GENERATES THE TERRAINS BY PICKING RANDOM NUMBERS
	
	//EASY TERRAINS pick a number between 1-40;
	//MEDIUM TERRAINS pick a number between 1-60;
	//HARD TERRAINS pick a number between 1 - 80;
	
	public TerrainGenerator(){
		for(int i = 0; i < easyTerrains.length; i++){
			int currentNumber = (int) Math.ceil(Math.random() * 15);
			while(currentNumber == 1){
				currentNumber = (int) Math.ceil(Math.random() * 15);
			}
			easyTerrains[i] = currentNumber;
		}
		
		easyTerrains[0] = 1;
		
		for(int i = 0; i < mediumTerrains.length; i++){
			mediumTerrains[i] = (int) Math.ceil(Math.random() * 7);
		}
		
		for(int i = 0; i < hardTerrains.length; i++){
			hardTerrains[i] = (int) Math.ceil(Math.random() * 2);
		}
	}
}
