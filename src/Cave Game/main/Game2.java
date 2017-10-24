package com.mong.main;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
public class Game2 {
	public boolean running = false;

	Frame mainFrame;

	public Game2(GraphicsDevice gd){
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
			//BufferStrategy bs = mainFrame.getBufferStrategy();

			running = true;

			init();
			
			int updatesPerSecond = 60;
			long timeBetweenUpdates = 1000000000 / updatesPerSecond;
			
			//long msBetweenUpdates = 1000 / updatesPerSecond;

			int ticks = 0;
			int frames = 0;
			
			//long lastWaitTime = System.nanoTime();
			
			long secondTime = System.currentTimeMillis();
			
			long lastTime = System.nanoTime();
			long accumulator = 0;
			
			boolean shouldRender = false;
			boolean wait = false;
			
			//long beginningWaitTime = 0;
			//long endWaitTime = 0;
			
			while(running){
				
				long now = System.nanoTime();
				long timePassed = now - lastTime;
				lastTime = now;
				
				accumulator += timePassed;
				
				//long waitTime = System.nanoTime();
				//while(accumulator < timeBetweenUpdates){

					//accumulator += System.nanoTime() - waitTime;
					
				//}
				
				
				while(accumulator >= timeBetweenUpdates){
					//beginningWaitTime = System.nanoTime();
					//System.out.println("Accumulator: " + accumulator);
					///System.out.println("TimeBetweenUpdates: " + timeBetweenUpdates);
					//System.out.println("Ticks: " + ticks);
					tick();
					ticks++;
					accumulator -= timeBetweenUpdates;
					shouldRender = true;
					wait = true;
				}
				
				
				if(shouldRender){
					render();
					frames++;
					shouldRender = false;
					//endWaitTime = System.nanoTime();
				}
				
				if(secondTime + 1000 <= System.currentTimeMillis()){
					secondTime = System.currentTimeMillis();
					System.out.println("Frames: " + frames + " Ticks: " + ticks);
					frames = 0;
					ticks = 0;
				}
				


				if(wait){
					long extraTime = accumulator;
					long waitTime = System.nanoTime();
					while(extraTime < timeBetweenUpdates){
						extraTime += System.nanoTime() - waitTime;
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				

			}

		}finally{
			gd.setFullScreenWindow(null);
		}

	}

	private void init(){

	}

	private void tick(){

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
		new Game2(gd);
	}
}

/*
  
  
    double t = 0.0;
    double dt = 0.01;

    double currentTime = hires_time_in_seconds();
    double accumulator = 0.0;

    State previous;
    State current;

    while ( !quit )
    {
        double newTime = time();
        double frameTime = newTime - currentTime;
        if ( frameTime > 0.25 )
            frameTime = 0.25;
        currentTime = newTime;

        accumulator += frameTime;

        while ( accumulator >= dt )
        {
            previousState = currentState;
            integrate( currentState, t, dt );
            t += dt;
            accumulator -= dt;
        }

        const double alpha = accumulator / dt;

        State state = currentState * alpha + 
            previousState * ( 1.0 - alpha );

        render( state );
    }
    
   
    */

	/*
	 	public void gameLoop(){
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;   

		// keep looping round til the game ends
		while (gameRunning)
		{
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double)OPTIMAL_TIME);

			// update the frame counter
			lastFpsTime += updateLength;
			fps++;

			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000000000)
			{
				System.out.println("(FPS: "+fps+")");
				lastFpsTime = 0;
				fps = 0;
			}

			// update the game logic
			doGameUpdates(delta);

			// draw everyting
			render();

			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give 
			// us our final value to wait for
			// remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
			try{Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 )};
		}
	}

	private void doGameUpdates(double delta){
		for (int i = 0; i < stuff.size(); i++)
		{
			// all time-related values must be multiplied by delta!
			Stuff s = stuff.get(i);
			s.velocity += Gravity.VELOCITY * delta;
			s.position += s.velocity * delta;

			// stuff that isn't time-related doesn't care about delta...
			if (s.velocity >= 1000)
			{
				s.color = Color.RED;
			}
			else
			{
				s.color = Color.BLUE;
			}
		}
	}
	 */



/*
 * 			!!!Loop with consistent 60 fps!!!
 * 
 * 			int idealFPS = 60;
			long updatePerTick = 1000000000 / idealFPS;
			
			long lastTime = System.nanoTime();
            int lastSecondTime = (int) (lastTime / 1000000000);
			
			boolean shouldRender = false;
			
			int frames = 0;
			int ticks = 0;
			
			
			while(running){
				
				long now = System.nanoTime();
				
				while(now - lastTime > updatePerTick){
					tick();
					lastTime += updatePerTick;
					ticks++;
					shouldRender = true;
				}
				
				
				if(shouldRender){
					render();
	                 Graphics g = bs.getDrawGraphics();
	                 if (!bs.contentsLost()) {
	                	 if(input.test){
	                		 idealFPS = 1;
	             			 updatePerTick = 1000000000 / idealFPS;
	                	 }
	             		if(!artifactMenu && !mainShipWorld){
	            			
	            			g.drawImage(screenTiles.image, 0, 0, width, height, null);
	            			
	            		}else if(artifactMenu && !mainShipWorld){
	            			
	            			g.drawImage(screenTiles.image, 0, 0, width, height, null);
	            			g.drawImage(screenCollection.image, 0, 0, width, height, null);
	            			
	            		}else if(!artifactMenu && mainShipWorld){
	            			g.drawImage(screenShip.image, 0, 0, width, height, null);
	            		}
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
              	  this.ticks = ticks;
              	  this.frames = frames;
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
 * 
 * 
 * 
 */

/*
 * 				
 * 			!!! My loop with accumulator wait!!!
 * 
 * 			int updatesPerSecond = 60;
			long timeBetweenUpdates = 1000000000 / updatesPerSecond;
			
			long msBetweenUpdates = 1000 / updatesPerSecond;

			int ticks = 0;
			int frames = 0;
			
			long lastWaitTime = System.nanoTime();
			
			long secondTime = System.currentTimeMillis();
			
			long lastTime = System.nanoTime();
			long accumulator = 0;
			
			boolean shouldRender = false;
			boolean wait = false;
			
			long beginningWaitTime = 0;
			long endWaitTime = 0;
			
			while(running){
				
				long now = System.nanoTime();
				long timePassed = now - lastTime;
				lastTime = now;
				
				accumulator += timePassed;
				
				//long waitTime = System.nanoTime();
				//while(accumulator < timeBetweenUpdates){

					//accumulator += System.nanoTime() - waitTime;
					
				//}
				
				
				while(accumulator >= timeBetweenUpdates){
					beginningWaitTime = System.nanoTime();
					//System.out.println("Accumulator: " + accumulator);
					///System.out.println("TimeBetweenUpdates: " + timeBetweenUpdates);
					//System.out.println("Ticks: " + ticks);
					tick();
					ticks++;
					accumulator -= timeBetweenUpdates;
					shouldRender = true;
					wait = true;
				}
				
				
				if(shouldRender){
					render();
					frames++;
					shouldRender = false;
					endWaitTime = System.nanoTime();
				}
				
				if(secondTime + 1000 <= System.currentTimeMillis()){
					secondTime = System.currentTimeMillis();
					System.out.println("Frames: " + frames + " Ticks: " + ticks);
					frames = 0;
					ticks = 0;
				}
				


				if(wait){
					long extraTime = accumulator + (endWaitTime - beginningWaitTime);
					long waitTime = System.nanoTime();
					while(extraTime < timeBetweenUpdates){
						extraTime += System.nanoTime() - waitTime;
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				

			}
 * 
 * 
 * 
 */

/*
 * 
 * 	
 * 			!!!My loop with extraTime wait!!!
 * 			int updatesPerSecond = 60;
			long timeBetweenUpdates = 1000000000 / updatesPerSecond;
			
			long msBetweenUpdates = 1000 / updatesPerSecond;

			int ticks = 0;
			int frames = 0;
			
			long lastWaitTime = System.nanoTime();
			
			long secondTime = System.currentTimeMillis();
			
			long lastTime = System.nanoTime();
			long accumulator = 0;
			
			boolean shouldRender = false;
			boolean wait = false;
			
			long beginningWaitTime = 0;
			long endWaitTime = 0;
			
			while(running){
				
				long now = System.nanoTime();
				long timePassed = now - lastTime;
				lastTime = now;
				
				accumulator += timePassed;
				
				//long waitTime = System.nanoTime();
				//while(accumulator < timeBetweenUpdates){

					//accumulator += System.nanoTime() - waitTime;
					
				//}
				
				
				while(accumulator >= timeBetweenUpdates){
					beginningWaitTime = System.nanoTime();
					//System.out.println("Accumulator: " + accumulator);
					///System.out.println("TimeBetweenUpdates: " + timeBetweenUpdates);
					//System.out.println("Ticks: " + ticks);
					tick();
					ticks++;
					accumulator -= timeBetweenUpdates;
					shouldRender = true;
					wait = true;
				}
				
				
				if(shouldRender){
					render();
					frames++;
					shouldRender = false;
					endWaitTime = System.nanoTime();
				}
				
				if(secondTime + 1000 <= System.currentTimeMillis()){
					secondTime = System.currentTimeMillis();
					System.out.println("Frames: " + frames + " Ticks: " + ticks);
					frames = 0;
					ticks = 0;
				}
				


				if(wait){
					long extraTime = (timeBetweenUpdates - (endWaitTime - beginningWaitTime));
					long waitTime = System.nanoTime();
					while(extraTime > 0){
						extraTime -= System.nanoTime() - waitTime;
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				

			}
 * 
 * 
 * 
 */

/*
 * 
 * 
 * 			
 * 			!!! My loop with last time wait!!!
 * 			int updatesPerSecond = 60;
			long timeBetweenUpdates = 1000000000 / updatesPerSecond;

			int ticks = 0;
			int frames = 0;

			long secondTime = System.nanoTime();

			long lastTime = System.nanoTime();
			long accumulator = 0;

			boolean shouldRender = false;
			long accumulatorLeftOvers = 0;

			while(running){

				long now = System.nanoTime();
				long timePassed = now - lastTime;
				lastTime = now;

				accumulator += timePassed;
				
				//System.out.println("Accumulator: " + accumulator);
				System.out.println("Ticks: " + ticks);
				System.out.println("Accumulator: " + accumulator);


				while(accumulator >= timeBetweenUpdates){
					tick();
					ticks++;
					accumulator -= timeBetweenUpdates;
					System.out.println("AccumulatorAfter :" + accumulator);
					accumulatorLeftOvers+=accumulator;
					shouldRender = true;
				}
				
				//System.out.println("AccumulatorAfter: " + accumulator);


				if(shouldRender){
					render();
					Graphics g = bs.getDrawGraphics();
					if (!bs.contentsLost()) {
						if(!artifactMenu && !mainShipWorld){

							g.drawImage(screenTiles.image, 0, 0, width, height, null);

						}else if(artifactMenu && !mainShipWorld){

							g.drawImage(screenTiles.image, 0, 0, width, height, null);
							g.drawImage(screenCollection.image, 0, 0, width, height, null);

						}else if(!artifactMenu && mainShipWorld){
							g.drawImage(screenShip.image, 0, 0, width, height, null);
						}
						bs.show();
						g.dispose();
					}
					frames++;
					shouldRender = false;
				}
				

				if(secondTime + 1000000000 <= System.nanoTime()){
					secondTime = System.nanoTime();
					System.out.println("Frames: " + frames + " Ticks: " + ticks);
					System.out.println("LeftOvers: " + accumulatorLeftOvers);
					accumulatorLeftOvers = 0;
					this.ticks = ticks;
					this.frames = frames;
					frames = 0;
					ticks = 0;
				}



				while(System.nanoTime() - lastTime + accumulator < timeBetweenUpdates){
					
				
					Thread.yield();
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
						
				}
					


			}
 * 
 * 
 */


 
