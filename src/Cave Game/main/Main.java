package com.mong.main;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.mong.entity.Player;
import com.mong.graphics.Background;
import com.mong.graphics.CustomFont;
import com.mong.graphics.NumberFont;
import com.mong.graphics.Screen;
import com.mong.level.HighScore;
import com.mong.sound.Sound;
import com.mong.terrain.TerrainManager;

public class Main {

	public boolean running = false;
	
    Dimension toolkit = Toolkit.getDefaultToolkit().getScreenSize();
    
    public final int screenWidth = toolkit.width;
    public final int screenHeight = toolkit.height;
    
    public final int width = 1920;
    public final int height = 1080;
    
    private boolean scaledDown;
    
    private Screen screen  = new Screen(width, height, "/SpriteSheet.png", 0);
	public BufferedImage scaledImage;
	public int[] scaledPixels;
    
    private InputManager input;
    private TerrainManager terrainManager;
    private Player player;
    
    private Background background;
    
    public HighScore highScore;
    
    public int currentHighScore;
    
    public int tickCounter = 0;
    
    public int score = 0;

	public boolean deathScreen = false;
	
	private CustomFont youDied = new CustomFont(6, 4, 4, 5, 0, (height / 2) - (64 + 32), true, false);
	private CustomFont restart = new CustomFont(11, 4, 4, 4, 0, 0, true, true);
	private CustomFont enter = new CustomFont(11, 5, 4, 3, 0 , (height / 2) + (32), true, false);
	
	private boolean jumpAhead = false;
	
	private boolean lagStart = false;
	
	Frame mainFrame;
	
	public Main(GraphicsDevice gd){
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
			
			input = new InputManager(this, mainFrame);
			
			init();
			
			System.setProperty("-Dsun.java2d.opengl", "true");
			System.setProperty("-Dsun.java2d.d3d", "false");
			
			int idealFPS = 60;
			long updatePerTick = 1000000000 / idealFPS;
			
			long lastTime = System.nanoTime();
            int lastSecondTime = (int) (lastTime / 1000000000);
			
			boolean shouldRender = false;
			
			int maxUpdatesPerTick = 0;
			
			int frames = 0;
			int ticks = 0;
			
			//float interpolation = (float) (now - lastTime) / updatePerTick;
			
			while(running){
				
				long now = System.nanoTime();

				while(now - lastTime > updatePerTick && maxUpdatesPerTick < 10){

					tick();
					lastTime += updatePerTick;
					maxUpdatesPerTick++;
					ticks++;
					shouldRender = true;
				}


				if(shouldRender){
					maxUpdatesPerTick = 0;
					render();
					Graphics g = bs.getDrawGraphics();
					if (!bs.contentsLost()) {
						
						if(!scaledDown) {
							g.drawImage(getCompatableImage(screen.image), 0, 0, screenWidth, screenHeight, null);
						}else {
							g.drawImage(getCompatableImage(scaledImage), 0, 0, screenWidth, screenHeight, null);
						}
	                    bs.show();
	                    g.dispose();
						
					}
					frames++;
					shouldRender = false;
				}


				int thisSecond = (int) (lastTime / 1000000000);
				if (thisSecond > lastSecondTime)
				{
					System.out.println("Frames: " + frames + " Ticks: " + ticks);
					frames = 0;
					ticks = 0;
					lastSecondTime = thisSecond;

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
	
	private BufferedImage getCompatableImage(BufferedImage image) {
		
		
	    GraphicsConfiguration gfx_config = GraphicsEnvironment.
	            getLocalGraphicsEnvironment().getDefaultScreenDevice().
	            getDefaultConfiguration();
	    
	    if (image.getColorModel().equals(gfx_config.getColorModel())) {
	        return image;
	    }

	    BufferedImage new_image = gfx_config.createCompatibleImage(
	            image.getWidth(), image.getHeight(), image.getTransparency());
	    
	    return new_image; 
		
	}
	
    public void init(){
    	
    	if(screenWidth == width) {
    		scaledDown = true;
    		scaledImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
    		scaledPixels = ((DataBufferInt) scaledImage.getRaster().getDataBuffer()).getData();
    	}

    	terrainManager = new TerrainManager();
    	
    	background = new Background("/EndScreenBackground.png");
    	background.scaleImage(4, width, height);
    	
    	player = new Player(99, 100, 100, 0, 6, 0, 0, 0,0, this, input, terrainManager);
    	
    	highScore = new HighScore();
    	highScore.readHighScore();
    	currentHighScore = highScore.currentHighScore;
    	
    	Sound.music.playLoop();
    	
    }
	
    public void tick(){
    	
    	
    	tickCounter++;
    	
    	if(input.t){
    		lagStart = !lagStart;
    		input.t = false;
    	}
    	
    	if(!deathScreen){
    		player.resetButtons();
    	
        	player.tick(tickCounter, 1.0F);
    	
			screen.screenX = (player.x + 32) - (width / 2);
			
			if(screen.screenX < terrainManager.scrollBound){
				int add = terrainManager.scrollBound - screen.screenX;
				screen.screenX += add;
			}

        
        	terrainManager.tick(tickCounter, player);
        	if(jumpAhead){
        		terrainManager.jumpAhead(30, player);
        		jumpAhead = false;
        	}
    	}else{
    		if(input.enter){
    			deathScreen = false;
    			score = 0;
    	    	terrainManager = new TerrainManager();
    			player = new Player(99, 100, 100, 0, 6, 0, 0, 0,0, this, input, terrainManager);
    	    	Sound.loop = true;
    	    	Sound.music.playLoop();
    		}
    	}
    }
	
	
    public void render(){
    	
        for(int i = 0; i < screen.pixels.length; i++){
        	screen.pixels[i] = 0;
        } 
        
        if(!deathScreen){
        
        	player.render(screen);
        
        	terrainManager.render(screen);
        
        	NumberFont.render(screen, Integer.toString(score), width - (NumberFont.getTextLength(Integer.toString(score))) - 10 + screen.screenX, 10);
        
        	player.renderHealthBar(screen);
        
        }else{
        	screen.screenX = 0;
        	screen.screenY = 0;
        	background.render(screen);
        	youDied.render(screen, width, height);
        	restart.render(screen, width, height);
        	enter.render(screen, width, height);
        	String highScoreText = "highsc0re " + Integer.toString(currentHighScore);
        	NumberFont.render(screen, highScoreText, (width / 2) - (NumberFont.getTextLength(highScoreText) / 2), height - 40);
        	
        }
        
        if(scaledDown) {
        	
        	float ratioY = height / screenHeight;
        	float ratioX = width / screenWidth;
        	
        	for(int y = 0; y < screenHeight; y++) {
        		for(int x = 0; x < screenWidth; x++) {
        			scaledPixels[x + y * screenWidth] = screen.pixels[((int)(ratioX * x)) + ((int)(ratioY * y) * width)];
        		}
        	}
        }
    }
	
	//Sets the display mode !!!REMEMBER TO ADD MORE!!!
	private DisplayMode[] bestDisplayModes = new DisplayMode[]{
			new DisplayMode(1920, 1080, 32, 0),
			new DisplayMode(1600, 900, 32, 0),
			new DisplayMode(1366, 768, 32, 0)
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
		new Main(gd);
	}
}



