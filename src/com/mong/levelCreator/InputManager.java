package com.mong.levelCreator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputManager implements KeyListener, MouseListener, MouseWheelListener{

	public boolean s = false;
	public boolean e = false;
	public boolean leftClick = false;
	public boolean middleClick = false;
	public boolean rightClick = false;
	
	public boolean enter = false;
	
	private Main main;
	
	
	public InputManager(Main main){
		main.addKeyListener(this);
		main.addMouseListener(this);
		main.addMouseWheelListener(this);
		
		this.main = main;
	}
	
	public void keyPressed(KeyEvent ev) {

		if(ev.getKeyCode() == KeyEvent.VK_S) s = true;
		if(ev.getKeyCode() == KeyEvent.VK_E) e = true;
		if(ev.getKeyCode() == KeyEvent.VK_ENTER) enter = true;
		if(ev.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
	}

	public void keyReleased(KeyEvent ev) {
		if(ev.getKeyCode() == KeyEvent.VK_S) s = false;
		if(ev.getKeyCode() == KeyEvent.VK_E) e = false;
		if(ev.getKeyCode() == KeyEvent.VK_ENTER) enter = false;
	}

	public void keyTyped(KeyEvent arg0) {
		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		main.editorId += -e.getWheelRotation();
		if(main.editorId < 0){
			main.editorId = 0;
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		
	}

	public void mouseEntered(MouseEvent arg0) {
		
	}

	public void mouseExited(MouseEvent arg0) {
		
	}

	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) leftClick = true;
		if(e.getButton() == MouseEvent.BUTTON2) middleClick = true;
		if(e.getButton() == MouseEvent.BUTTON3) rightClick = true;
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) leftClick = false;
		if(e.getButton() == MouseEvent.BUTTON2) middleClick = false;
		if(e.getButton() == MouseEvent.BUTTON3) rightClick = false;
		
	}

}
