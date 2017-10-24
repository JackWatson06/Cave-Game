package com.mong.main;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

public class Game {

	public boolean running = false;
	
	Frame mainFrame;
	
	public Game(GraphicsDevice gd){
		try{
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			
			mainFrame = new Frame(gc);
			mainFrame.setUndecorated(true);
			mainFrame.setIgnoreRepaint(true);
			
			gd.setFullScreenWindow(mainFrame);
			if(gd.isDisplayChangeSupported()){
				chooseBestDisplayMode(gd);
			}
			
			mainFrame.createBufferStrategy(2);
			BufferStrategy bs = mainFrame.getBufferStrategy();
			
			running = true;
			
			int idealFPS = 60;
			long updatePerTick = 1000000000 / idealFPS;
			
			long lastTime = System.nanoTime();
			long lastSecond = System.currentTimeMillis();
			
			boolean shouldRender = false;
			
			int frames = 0;
			int ticks = 0;
			
			init();
			
			while(running){
				
				long now = System.nanoTime();
				
				while(now - lastTime > updatePerTick){
					float interpolation = (float) (now - lastTime) / updatePerTick;
					tick(interpolation);
					lastTime += updatePerTick;
					ticks++;
					shouldRender = true;
				}
				
                if ( now - lastTime > updatePerTick){
                   lastTime = now - updatePerTick;
                }
				
				if(shouldRender){
					render();
	                 Graphics g = bs.getDrawGraphics();
	                 if (!bs.contentsLost()) {
	                     //g.drawImage(image, 0, 0, 1920, 1080, null);
	                     bs.show();
	                     g.dispose();
	                 }
					frames++;
					shouldRender = false;
				}
				
				if(System.currentTimeMillis() - lastSecond > 1000){
					System.out.println("Frames: " + frames + " Ticks: " + ticks);
					frames = 0;
					ticks = 0;
					lastSecond = System.currentTimeMillis();
				}
				
				
				while(now - lastTime < updatePerTick){
					
                    Thread.yield();
                    
                    try {
                    	Thread.sleep(1);
                    }catch(Exception e) {
                    	e.printStackTrace();
                    } 
					
					now = System.nanoTime();
				}
			}
			
			
		}finally{
			gd.setFullScreenWindow(null);
		}
		
	}
	
	private void init(){
		
	}
	
	private void tick(float interpolation){

	}
	
	
	private void render(){
		
	}
	
	//Sets the display mode !!!REMEMBER TO ADD MORE!!!
	private DisplayMode[] bestDisplayModes = new DisplayMode[]{
			new DisplayMode(1920, 1080, 32, 0),
		};
	
	private DisplayMode getBestDisplayMode(GraphicsDevice gd){
		for(int i = 0; i < bestDisplayModes.length; i++){
			DisplayMode[] modes = gd.getDisplayModes();
			for(int j = 0; j < modes.length; j++){
				if(modes[j].getHeight() == bestDisplayModes[i].getHeight() &&
					modes[j].getWidth() == bestDisplayModes[i].getWidth() &&
					modes[j].getBitDepth() == bestDisplayModes[i].getBitDepth()){
					return bestDisplayModes[i];
				}
			}
		}
		return null;
	}
	
	private void chooseBestDisplayMode(GraphicsDevice gd){
		DisplayMode display = getBestDisplayMode(gd);
		if(display != null){
			gd.setDisplayMode(display);
		}else{
			gd.setDisplayMode(gd.getDisplayMode());
		}
	}
	
	public static void main(String[] args){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		new Game(gd);
	}
}
