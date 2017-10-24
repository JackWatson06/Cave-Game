package com.mong.main;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class InputManager implements KeyListener{

	//THIS CLASS JUST KEEPS TRACK OF THE KEYS THAT ARE PRESSED
	
	private Main main;
	
	public boolean w = false;
	public boolean a = false;
	public boolean s = false;
	public boolean d = false;
	public boolean e = false;
	public boolean t = false;
	
	public boolean enter = false;
	
	public boolean jumpAvailability = true;
	public int jumpTick = 0;
	public boolean jump = false;
	
	public InputManager(Main main, Frame frame){
		//main.addKeyListener(this);
		frame.addKeyListener(this);
		this.main = main;
	}
	
	
	public void keyPressed(KeyEvent ev) {
		if(ev.getKeyCode() == KeyEvent.VK_W) w = true;
		if(ev.getKeyCode() == KeyEvent.VK_S) s = true;
		if(ev.getKeyCode() == KeyEvent.VK_D) d = true;
		if(ev.getKeyCode() == KeyEvent.VK_A) a = true;
		if(ev.getKeyCode() == KeyEvent.VK_E) e = true;
		if(ev.getKeyCode() == KeyEvent.VK_T) t = true;
		if(ev.getKeyCode() == KeyEvent.VK_ENTER) enter = true;
		if(ev.getKeyChar() == KeyEvent.VK_ESCAPE) System.exit(0);
		if(ev.getKeyChar() == KeyEvent.VK_SPACE){
			if(jumpAvailability){
				jumpAvailability = false;
				jump = true;
				jump();
			}
		}
		
	}

	public void keyReleased(KeyEvent ev) {

		if(ev.getKeyCode() == KeyEvent.VK_W) w = false;
		if(ev.getKeyCode() == KeyEvent.VK_S) s = false;
		if(ev.getKeyCode() == KeyEvent.VK_D) d = false;
		if(ev.getKeyCode() == KeyEvent.VK_A) a = false;
		if(ev.getKeyCode() == KeyEvent.VK_E) e = false;
		if(ev.getKeyCode() == KeyEvent.VK_T) t = false;
		if(ev.getKeyCode() == KeyEvent.VK_ENTER) enter = false;
		
	}


	public void keyTyped(KeyEvent e) {

		
	}
	
	public void jump(){
		jumpTick = main.tickCounter;
	}

}
