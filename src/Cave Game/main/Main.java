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
    
    public final int width = toolkit.width;
    public final int height = toolkit.height;
    
    private Screen screen  = new Screen(width, height, "/SpriteSheet.png", 0);
    
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
			
			int idealFPS = 60;
			long updatePerTick = 1000000000 / idealFPS;
			
			long lastTime = System.nanoTime();
            int lastSecondTime = (int) (lastTime / 1000000000);
			
			boolean shouldRender = false;
			
			int frames = 0;
			int ticks = 0;
			
			input = new InputManager(this, mainFrame);
			
			init();
			
			System.setProperty("-Dsun.java2d.opengl", "true");
			
			while(running){
				
				long now = System.nanoTime();
				
				while(now - lastTime > updatePerTick){
					float interpolation = (float) (now - lastTime) / updatePerTick;
					tick(interpolation);
					ticks++;
					shouldRender = true;
					lastTime += updatePerTick;
				}
				
				
				if(shouldRender){
					render();
	                Graphics g = bs.getDrawGraphics();
	                 if(!bs.contentsLost()) {
	                     g.drawImage(screen.image, 0, 0, 1920, 1080, null);
	                     bs.show();
	                     g.dispose();
	                 }
					frames++;
					shouldRender = false;
				}
				
                if(now - lastTime > updatePerTick){
                    lastTime = now - updatePerTick;
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
	
    public void init(){
    	

    	terrainManager = new TerrainManager();
    	
    	background = new Background("/EndScreenBackground.png");
    	background.scaleImage(4, width, height);
    	
    	player = new Player(99, 100, 100, 0, 6, 0, 0, 0,0, this, input, terrainManager);
    	
    	highScore = new HighScore();
    	highScore.readHighScore();
    	currentHighScore = highScore.currentHighScore;
    	
    	Sound.music.playLoop();
    	
    }
	
    public void tick(float interpolation){
    	
    	
    	tickCounter++;
    	
    	if(input.t){
    		lagStart = !lagStart;
    		input.t = false;
    	}
    	
    	if(!deathScreen){
    		player.resetButtons();
    	
        	player.tick(tickCounter, interpolation);
    	
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
		new Main(gd);
	}
}






































/*import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mong.entity.Player;
import com.mong.graphics.Background;
import com.mong.graphics.CustomFont;
import com.mong.graphics.NumberFont;
import com.mong.graphics.Screen;
import com.mong.level.HighScore;
import com.mong.sound.Sound;
import com.mong.terrain.TerrainManager;
 
public class Main {
     
    private static Color[] COLORS = new Color[] {
        Color.red, Color.blue, Color.green, Color.white, Color.black,
        Color.yellow, Color.gray, Color.cyan, Color.pink, Color.lightGray,
        Color.magenta, Color.orange, Color.darkGray };
    private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {
        new DisplayMode(1920, 1080, 32, 0),
        new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 16, 0),
        new DisplayMode(640, 480, 8, 0)
    };
    
	public boolean running = false;
    
	
    Dimension toolkit = Toolkit.getDefaultToolkit().getScreenSize();
    
    public final int width = toolkit.width;
    public final int height = toolkit.height;
    
    private Screen screen  = new Screen(width, height, "/SpriteSheet.png", 0);
    
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
	private boolean loadData = false;
	
	private int fps = 0;
	private int frameCount = 0;
	
    Frame mainFrame;
	
    public Main(int numBuffers, GraphicsDevice device) {
    	

        try {


            GraphicsConfiguration gc = device.getDefaultConfiguration();
            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);
            device.setFullScreenWindow(mainFrame);
            if (device.isDisplayChangeSupported()) {
                chooseBestDisplayMode(device);
            }
            Rectangle bounds = mainFrame.getBounds();
            mainFrame.createBufferStrategy(numBuffers);
            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
            
            input = new InputManager(this, mainFrame);
            
            init();
            
            final double GAME_HERTZ = 60.0;

            final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;

            final int MAX_UPDATES_BEFORE_RENDER = 5;
            double lastUpdateTime = System.nanoTime();
            double lastRenderTime = System.nanoTime();
            
            final double TARGET_FPS = 60;
            final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
            
            int lastSecondTime = (int) (lastUpdateTime / 1000000000);
            
            int frames = 0;
            int ticks = 0;
            
            while (running)
            {
               double now = System.nanoTime();
               int updateCount = 0;
                  while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
                  {
                     tick(1.0F);
                     lastUpdateTime += TIME_BETWEEN_UPDATES;
                     updateCount++;
                     ticks++;
                  }
         

                  if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
                  {
                     lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                  }
               
                  float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
                  render();
                  Graphics g = bufferStrategy.getDrawGraphics();
                  if (!bufferStrategy.contentsLost()) {
                      g.drawImage(screen.image, 0, 0, 1920, 1080, null);
                      bufferStrategy.show();
                      g.dispose();
                      frames++;
                  }
                  lastRenderTime = now;
               
                  int thisSecond = (int) (lastUpdateTime / 1000000000);
                  if (thisSecond > lastSecondTime)
                  {
                     //System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                	  System.out.println("Frames: " + frames + " Ticks: " + ticks);
                	  frames = 0;
                	  ticks = 0;
                     fps = frameCount;
                     frameCount = 0;
                     lastSecondTime = thisSecond;
                  }
               

                  while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
                  {
                     Thread.yield();
                  
                     try {Thread.sleep(1);} catch(Exception e) {} 
                  
                     now = System.nanoTime();
               }
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            /*for (float lag = 2000.0f; lag > 0.00000006f; lag = lag / 1.33f) {
                for (int i = 0; i < numBuffers; i++) {
                    Graphics g = bufferStrategy.getDrawGraphics();
                    if (!bufferStrategy.contentsLost()) {
                        g.setColor(COLORS[i]);
                        g.fillRect(0,0,bounds.width, bounds.height);
                        g.setColor(Color.WHITE);
                        g.fillRect(blockX, blockY, 200, 200);
                        bufferStrategy.show();
                        g.dispose();
                    }
                    try {
                        Thread.sleep((int)lag);
                    } catch (InterruptedException e) {}
                }
            }*/
        /*} catch (Exception e) {
            e.printStackTrace();
        } finally {
            device.setFullScreenWindow(null);
        }

        
    }
    
    public void init(){
    	
		running = true;

    	terrainManager = new TerrainManager();
    	
    	background = new Background("/EndScreenBackground.png");
    	background.scaleImage(4, width, height);
    	
    	player = new Player(99, 100, 100, 0, 6, 0, 0, 0,0, this, input, terrainManager);
    	
    	highScore = new HighScore();
    	highScore.readHighScore();
    	currentHighScore = highScore.currentHighScore;
    	
    	Sound.music.playLoop();
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
    }
    
    public void tick(float interpolation){
    	
    	
    	tickCounter++;
    	
    	if(input.t){
    		loadData = true;
    		lagStart = !lagStart;
    		input.t = false;
    	}
    	
    	if(!deathScreen){
    		player.resetButtons();
    	
        	player.tick(tickCounter, interpolation);
    	
			screen.screenX = (player.x + 32) - (width / 2);
			
			if(screen.screenX < terrainManager.scrollBound){
				int add = terrainManager.scrollBound - screen.screenX;
				screen.screenX += add;
			}

        
        	terrainManager.tick(tickCounter, player);
        	if(jumpAhead){
        		terrainManager.jumpAhead(15, player);
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
    
    private static DisplayMode getBestDisplayMode(GraphicsDevice device) {
        for (int x = 0; x < BEST_DISPLAY_MODES.length; x++) {
            DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].getWidth() == BEST_DISPLAY_MODES[x].getWidth()
                   && modes[i].getHeight() == BEST_DISPLAY_MODES[x].getHeight()
                   && modes[i].getBitDepth() == BEST_DISPLAY_MODES[x].getBitDepth()
                   ) {
                    return BEST_DISPLAY_MODES[x];
                }
            }
        }
        return null;
    }
     
    public static void chooseBestDisplayMode(GraphicsDevice device) {
        DisplayMode best = getBestDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }
     
    public static void main(String[] args) {
        try {
            int numBuffers = 2;
            if (args != null && args.length > 0) {
                numBuffers = Integer.parseInt(args[0]);
                if (numBuffers < 2 || numBuffers > COLORS.length) {
                    System.err.println("Must specify between 2 and "
                        + COLORS.length + " buffers");
                    System.exit(1);
                }
            }
            GraphicsEnvironment env = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            Main test = new Main(numBuffers, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
*/

