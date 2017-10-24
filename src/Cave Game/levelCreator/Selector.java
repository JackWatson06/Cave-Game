package com.mong.levelCreator;

import java.awt.MouseInfo;

public class Selector {
	
	public int currentMouseXS;
	public int currentMouseYS;
	
	public int currentMouseXNS;
	public int currentMouseYNS;
	
	public Selector(){
		
	}
	
	public void tick(){
		currentMouseXS = ((MouseInfo.getPointerInfo().getLocation().x) / 64) * 64;
		currentMouseYS = ((MouseInfo.getPointerInfo().getLocation().y) / 64) * 64;
		

		
		currentMouseXNS = ((MouseInfo.getPointerInfo().getLocation().x) / 64);
		currentMouseYNS = ((MouseInfo.getPointerInfo().getLocation().y) / 64);
		

	}
	
	
	public void render(Screen screen){

		screen.render(currentMouseXS, currentMouseYS, 15, 15, 1, 1, 4);
	}
}
