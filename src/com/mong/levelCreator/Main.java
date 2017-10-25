package com.mong.levelCreator;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Main extends Canvas implements Runnable{

	/**
	 * Cave Game Level Editor
	 */
	private static final long serialVersionUID = 1L;
	private static JLabel levelName = new JLabel("Level:");
	private static JTextField levelField = new JTextField(8);
	private static JButton confirm = new JButton("Confirm");
	private static JLabel loadLabel = new JLabel("Load:");
	private static JTextField loadField = new JTextField(8);
	
	private static boolean waiting = true;
	
	Dimension toolkit = Toolkit.getDefaultToolkit().getScreenSize();
	public final int width = toolkit.width;
	public final int height = toolkit.height;
	
	private boolean running = false;
	
	public InputManager input = new InputManager(this);
	
	private Screen screen = new Screen(width, height, "/SpriteSheet.png");
	
	private final int levelWidth = 30;
	private final int levelHeight = 17;
	
	private int[] levelData = new int[levelWidth * levelHeight];
	
	private Tile[] tileData = new Tile[levelWidth * levelHeight];
	private int[] entityData = new int[0];
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public Selector selector = new Selector();
	public int editorId;
	public Tile editorTile;
	public Entity editorEntity;
	
	private TileIndicator tileIndicator = new TileIndicator();
	
	private int lastXPlace = -1;
	private int lastYPlace = -1;
	private int lastEditorId = -1;
	
	private boolean displayS = false;
	private int displaySCounter = 0;
	
	
	

	public void start(){
		running = true;
		new Thread(this).start();
	}
	
	//This checks if a file wants to be loaded and if so loads it
	public void init(){
		if(loadField.getText().length() > 0){
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File("terrains/" + loadField.getText())));
				
				try{
					
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
							levelData[x + y * levelWidth] = Integer.parseInt(currentId);
						}
					}
					
					int totalEntities = Integer.parseInt(br.readLine());
					
					if(totalEntities > 0){
						entityData = new int[totalEntities * 3];
						for(int i = 0; i < totalEntities; i++){
							for(int j = 0; j < 3; j++){
								entityData[i * 3 + j] = Integer.parseInt(br.readLine());
							}
						}
					}
					
				}catch(IOException e){
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
		}
		update();
	}

	//Main game loop for the level editor
	public void run() {

		int desiredFPS = 60;
		long updateSeconds = 1_000_000_000 / desiredFPS;
		int frames = 0;
		int ticks = 0;
		long update = 0;
		long lastTime = System.nanoTime();
		long reset = System.currentTimeMillis();
		boolean render = false;
		
		init();
		
		while(running){
			long now = System.nanoTime();
			update += now - lastTime;
			lastTime = now;
			
			if(update >= updateSeconds){
				update = 0;
				ticks++;
				tick();
				render = true;
			}
			if(render){
				render = false;
				frames++;
				render();
			}
			
			if(System.currentTimeMillis() >= reset + 1000){
				reset = System.currentTimeMillis();
				System.out.println("Frames: " + frames + " Ticks: " + ticks);
				frames = 0;
				ticks = 0;
			}
		}
		
	}
	
	//Updates logic
	public void tick(){
		
		selector.tick();
		
		editorTile = editorTile(editorId);
		editorEntity = editorEntity(editorId);
		
		
		if(input.leftClick){
			
			int mouseX = selector.currentMouseXNS;
			int mouseY = selector.currentMouseYNS;
			
			if(mouseX != lastXPlace || mouseY != lastYPlace || editorId != lastEditorId){
				if(editorTile != null){
					levelData[mouseX + mouseY * levelWidth] = editorId;
				}
				if(editorEntity != null){
					int preEntityData[] = new int[entityData.length + 3];
					
					int lastIValue = -1;
					for(int i = 0; i < entityData.length; i++){
						preEntityData[i] = entityData[i];
						lastIValue = i;
					}
					
					preEntityData[lastIValue + 1] = editorEntity.id;
					preEntityData[lastIValue + 2] = editorEntity.x;
					preEntityData[lastIValue + 3] = editorEntity.y;
					
					entityData = preEntityData.clone();
				}
				
				lastXPlace = mouseX;
				lastYPlace = mouseY;
				lastEditorId = editorId;
				update();
			}
		}
		if(input.rightClick){
			
			for(int y = 0; y < levelHeight; y++){
				for(int x = 0; x < levelWidth; x++){
					if(selector.currentMouseXNS == x && selector.currentMouseYNS == y){
						levelData[x + y * levelWidth] = 0;
					}
				}
			}
			
			for(int i = 0; i < entityData.length; i+=3){
				if(entityData[i + 1] == selector.currentMouseXS && entityData[i + 2] == selector.currentMouseYS){
					int[] preEntityData = new int[entityData.length - 3];
					int deleteNumber = 0;
					for(int j = 0; j < entityData.length; j+=3){
						if(j != i){
							preEntityData[j - deleteNumber] = entityData[j];
							preEntityData[j + 1 - deleteNumber] = entityData[j + 1];
							preEntityData[j + 2 - deleteNumber] = entityData[j + 2];
						}else{
							deleteNumber = 3;
						}
					}
					
					entityData = preEntityData.clone();
					i -= 3;
				}
			}
			
			update();

		}
		if(input.s){
			save();
			input.s = false;
		}
		if(input.e || input.middleClick){
			int currentId = 0;
			currentId = pickBlock();
			if(currentId == 0){
				currentId = pickEntity();
			}
			editorId = currentId;
			input.e = false;
			input.middleClick = false;
		}
		
	}
	
	//Renders game
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(2);
			requestFocus();
			return;
		}
		
		
		for(int i = 0; i < screen.pixels.length; i++){
			screen.pixels[i] = 0;
		}
		//background

		
		for(int i = 0; i < tileData.length; i++){
			tileData[i].render(screen);
		}
		
		for(int i = 0; i < entities.size(); i++){
			entities.get(i).render(screen);
		}
		
		if(editorTile != null){
			editorTile.render(screen);
			tileIndicator.render(screen, editorTile);
		}
		
		if(editorEntity != null){
			editorEntity.render(screen);
			tileIndicator.render(screen, editorEntity);
		}
	
		selector.render(screen);
		
		if(displayS && displaySCounter < 180){
			displaySCounter++;
			screen.render(width - 84, 20, 12, 15, 1, 1, 4);
		}else if(displaySCounter >= 180){
			displaySCounter = 0;
			displayS = false;
		}
		
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(screen.image, 0, 0, width, height, null);
		g.dispose();
		bs.show();
	}
	
	//updates the tile data for the level
	public void update(){
		for(int y = 0; y < levelHeight; y++){
			for(int x = 0; x < levelWidth; x++){
				if(levelData[x + y * levelWidth] == 0) tileData[x + y * levelWidth] = new AirTile(0, x, y);
				if(levelData[x + y * levelWidth] == 1) tileData[x + y * levelWidth] = new Tile(1, x, y, 0, 0);
				if(levelData[x + y * levelWidth] == 2) tileData[x + y * levelWidth] = new Tile(2, x, y, 1, 0);
				if(levelData[x + y * levelWidth] == 3) tileData[x + y * levelWidth] = new Tile(3, x, y, 3, 0);
				if(levelData[x + y * levelWidth] == 4) tileData[x + y * levelWidth] = new Tile(4, x, y, 4, 0);
				if(levelData[x + y * levelWidth] == 5) tileData[x + y * levelWidth] = new Tile(5, x, y, 3, 1);
				if(levelData[x + y * levelWidth] == 6) tileData[x + y * levelWidth] = new Tile(6, x, y, 4, 1);
				if(levelData[x + y * levelWidth] == 7) tileData[x + y * levelWidth] = new Tile(7, x, y, 6, 0);
				if(levelData[x + y * levelWidth] == 8) tileData[x + y * levelWidth] = new Tile(8, x, y, 7, 0);
				if(levelData[x + y * levelWidth] == 9) tileData[x + y * levelWidth] = new Tile(9, x, y, 6, 1);
				if(levelData[x + y * levelWidth] == 10) tileData[x + y * levelWidth] = new Tile(10, x, y, 7, 1);
				if(levelData[x + y * levelWidth] == 11) tileData[x + y * levelWidth] = new Tile(11, x, y, 9, 0);
				if(levelData[x + y * levelWidth] == 12) tileData[x + y * levelWidth] = new Tile(12, x, y, 10, 0);
				if(levelData[x + y * levelWidth] == 13) tileData[x + y * levelWidth] = new Tile(13, x, y, 9, 1);
				if(levelData[x + y * levelWidth] == 14) tileData[x + y * levelWidth] = new Tile(14, x, y, 10, 1);
				if(levelData[x + y * levelWidth] == 15) tileData[x + y * levelWidth] = new Tile(15, x, y, 12, 0);
				if(levelData[x + y * levelWidth] == 16) tileData[x + y * levelWidth] = new Tile(16, x, y, 13, 0);
				if(levelData[x + y * levelWidth] == 17) tileData[x + y * levelWidth] = new Tile(17, x, y, 12, 1);
				if(levelData[x + y * levelWidth] == 18) tileData[x + y * levelWidth] = new Tile(18, x, y, 13, 1);
				if(levelData[x + y * levelWidth] == 19) tileData[x + y * levelWidth] = new Tile(19, x, y, 0, 2);
				if(levelData[x + y * levelWidth] == 20) tileData[x + y * levelWidth] = new Tile(20, x, y, 1, 2);
				if(levelData[x + y * levelWidth] == 21) tileData[x + y * levelWidth] = new Tile(21, x, y, 0, 3);
				if(levelData[x + y * levelWidth] == 22) tileData[x + y * levelWidth] = new Tile(22, x, y, 1, 3);
				if(levelData[x + y * levelWidth] == 23) tileData[x + y * levelWidth] = new Tile(23, x, y, 3, 2);
				if(levelData[x + y * levelWidth] == 24) tileData[x + y * levelWidth] = new Tile(24, x, y, 4, 2);
				if(levelData[x + y * levelWidth] == 25) tileData[x + y * levelWidth] = new Tile(25, x, y, 3, 3);
				if(levelData[x + y * levelWidth] == 26) tileData[x + y * levelWidth] = new Tile(26, x, y, 4, 3);
				if(levelData[x + y * levelWidth] == 27) tileData[x + y * levelWidth] = new Tile(27, x, y, 6, 2);
				if(levelData[x + y * levelWidth] == 28) tileData[x + y * levelWidth] = new Tile(28, x, y, 7, 2);
				if(levelData[x + y * levelWidth] == 29) tileData[x + y * levelWidth] = new Tile(29, x, y, 6, 3);
				if(levelData[x + y * levelWidth] == 30) tileData[x + y * levelWidth] = new Tile(30, x, y, 7, 3);
				if(levelData[x + y * levelWidth] == 31) tileData[x + y * levelWidth] = new Tile(31, x, y, 0, 1);
				if(levelData[x + y * levelWidth] == 32) tileData[x + y * levelWidth] = new Tile(32, x, y, 1, 1);
				if(levelData[x + y * levelWidth] == 33) tileData[x + y * levelWidth] = new Tile(33, x, y, 0, 5);
				if(levelData[x + y * levelWidth] == 34) tileData[x + y * levelWidth] = new Tile(34, x, y, 1, 5);
				if(levelData[x + y * levelWidth] == 35) tileData[x + y * levelWidth] = new Tile(35, x, y, 2, 6);
				if(levelData[x + y * levelWidth] == 36) tileData[x + y * levelWidth] = new Tile(36, x, y, 3, 6);
				if(levelData[x + y * levelWidth] == 37) tileData[x + y * levelWidth] = new Tile(37, x, y, 5, 6);
				if(levelData[x + y * levelWidth] == 38) tileData[x + y * levelWidth] = new Tile(38, x, y, 6, 6);
				if(levelData[x + y * levelWidth] == 39) tileData[x + y * levelWidth] = new Tile(39, x, y, 5, 7);
				if(levelData[x + y * levelWidth] == 40) tileData[x + y * levelWidth] = new Tile(40, x, y, 6, 7);
				if(levelData[x + y * levelWidth] == 41) tileData[x + y * levelWidth] = new Tile(41, x, y, 8, 6);
				if(levelData[x + y * levelWidth] == 42) tileData[x + y * levelWidth] = new Tile(42, x, y, 9, 6);
				if(levelData[x + y * levelWidth] == 43) tileData[x + y * levelWidth] = new Tile(43, x, y, 8, 7);
				if(levelData[x + y * levelWidth] == 44) tileData[x + y * levelWidth] = new Tile(44, x, y, 9, 7);
				if(levelData[x + y * levelWidth] == 45) tileData[x + y * levelWidth] = new Tile(45, x, y, 11, 6);
				if(levelData[x + y * levelWidth] == 46) tileData[x + y * levelWidth] = new Tile(46, x, y, 12, 6);
				if(levelData[x + y * levelWidth] == 47) tileData[x + y * levelWidth] = new Tile(47, x, y, 11, 7);
				if(levelData[x + y * levelWidth] == 48) tileData[x + y * levelWidth] = new Tile(48, x, y, 12, 7);
				if(levelData[x + y * levelWidth] == 49) tileData[x + y * levelWidth] = new Tile(49, x, y, 14, 6);
				if(levelData[x + y * levelWidth] == 50) tileData[x + y * levelWidth] = new Tile(50, x, y, 15, 6);
				if(levelData[x + y * levelWidth] == 51) tileData[x + y * levelWidth] = new Tile(51, x, y, 14, 7);
				if(levelData[x + y * levelWidth] == 52) tileData[x + y * levelWidth] = new Tile(52, x, y, 15, 7);
				if(levelData[x + y * levelWidth] == 53) tileData[x + y * levelWidth] = new Tile(53, x, y, 2, 8);
				if(levelData[x + y * levelWidth] == 54) tileData[x + y * levelWidth] = new Tile(54, x, y, 3, 8);
				if(levelData[x + y * levelWidth] == 55) tileData[x + y * levelWidth] = new Tile(55, x, y, 2, 9);
				if(levelData[x + y * levelWidth] == 56) tileData[x + y * levelWidth] = new Tile(56, x, y, 3, 9);
				if(levelData[x + y * levelWidth] == 57) tileData[x + y * levelWidth] = new Tile(57, x, y, 5, 8);
				if(levelData[x + y * levelWidth] == 58) tileData[x + y * levelWidth] = new Tile(58, x, y, 6, 8);
				if(levelData[x + y * levelWidth] == 59) tileData[x + y * levelWidth] = new Tile(59, x, y, 5, 9);
				if(levelData[x + y * levelWidth] == 60) tileData[x + y * levelWidth] = new Tile(60, x, y, 6, 9);
				if(levelData[x + y * levelWidth] == 61) tileData[x + y * levelWidth] = new Tile(61, x, y, 8, 8);
				if(levelData[x + y * levelWidth] == 62) tileData[x + y * levelWidth] = new Tile(62, x, y, 9, 8);
				if(levelData[x + y * levelWidth] == 63) tileData[x + y * levelWidth] = new Tile(63, x, y, 8, 9);
				if(levelData[x + y * levelWidth] == 64) tileData[x + y * levelWidth] = new Tile(64, x, y, 9, 9);
				if(levelData[x + y * levelWidth] == 65) tileData[x + y * levelWidth] = new Tile(65, x, y, 2, 7);
				if(levelData[x + y * levelWidth] == 66) tileData[x + y * levelWidth] = new Tile(66, x, y, 3, 7);
				if(levelData[x + y * levelWidth] == 67) tileData[x + y * levelWidth] = new Tile(67, x, y, 2, 10);
				if(levelData[x + y * levelWidth] == 68) tileData[x + y * levelWidth] = new Tile(68, x, y, 3, 10);
				if(levelData[x + y * levelWidth] == 69) tileData[x + y * levelWidth] = new Tile(69, x, y, 5, 10);
				if(levelData[x + y * levelWidth] == 70) tileData[x + y * levelWidth] = new Tile(70, x, y, 6, 10);
				if(levelData[x + y * levelWidth] == 71) tileData[x + y * levelWidth] = new Tile(71, x, y, 5, 11);
				if(levelData[x + y * levelWidth] == 72) tileData[x + y * levelWidth] = new Tile(72, x, y, 6, 11);
				if(levelData[x + y * levelWidth] == 73) tileData[x + y * levelWidth] = new Tile(73, x, y, 8, 10);
				if(levelData[x + y * levelWidth] == 74) tileData[x + y * levelWidth] = new Tile(74, x, y, 9, 10);
				if(levelData[x + y * levelWidth] == 75) tileData[x + y * levelWidth] = new Tile(75, x, y, 8, 11);
				if(levelData[x + y * levelWidth] == 76) tileData[x + y * levelWidth] = new Tile(76, x, y, 9, 11);
				if(levelData[x + y * levelWidth] == 77) tileData[x + y * levelWidth] = new Tile(77, x, y, 11, 10);
				if(levelData[x + y * levelWidth] == 78) tileData[x + y * levelWidth] = new Tile(78, x, y, 12, 10);
				if(levelData[x + y * levelWidth] == 79) tileData[x + y * levelWidth] = new Tile(79, x, y, 11, 11);
				if(levelData[x + y * levelWidth] == 80) tileData[x + y * levelWidth] = new Tile(80, x, y, 12, 11);
				if(levelData[x + y * levelWidth] == 81) tileData[x + y * levelWidth] = new Tile(81, x, y, 14, 10);
				if(levelData[x + y * levelWidth] == 82) tileData[x + y * levelWidth] = new Tile(82, x, y, 15, 10);
				if(levelData[x + y * levelWidth] == 83) tileData[x + y * levelWidth] = new Tile(83, x, y, 14, 11);
				if(levelData[x + y * levelWidth] == 84) tileData[x + y * levelWidth] = new Tile(84, x, y, 15, 11);
				if(levelData[x + y * levelWidth] == 85) tileData[x + y * levelWidth] = new Tile(85, x, y, 2, 12);
				if(levelData[x + y * levelWidth] == 86) tileData[x + y * levelWidth] = new Tile(86, x, y, 3, 12);
				if(levelData[x + y * levelWidth] == 87) tileData[x + y * levelWidth] = new Tile(87, x, y, 2, 13);
				if(levelData[x + y * levelWidth] == 88) tileData[x + y * levelWidth] = new Tile(88, x, y, 3, 13);
				if(levelData[x + y * levelWidth] == 89) tileData[x + y * levelWidth] = new Tile(89, x, y, 5, 12);
				if(levelData[x + y * levelWidth] == 90) tileData[x + y * levelWidth] = new Tile(90, x, y, 6, 12);
				if(levelData[x + y * levelWidth] == 91) tileData[x + y * levelWidth] = new Tile(91, x, y, 5, 13);
				if(levelData[x + y * levelWidth] == 92) tileData[x + y * levelWidth] = new Tile(92, x, y, 6, 13);
				if(levelData[x + y * levelWidth] == 93) tileData[x + y * levelWidth] = new Tile(93, x, y, 8, 12);
				if(levelData[x + y * levelWidth] == 94) tileData[x + y * levelWidth] = new Tile(94, x, y, 9, 12);
				if(levelData[x + y * levelWidth] == 95) tileData[x + y * levelWidth] = new Tile(95, x, y, 8, 13);
				if(levelData[x + y * levelWidth] == 96) tileData[x + y * levelWidth] = new Tile(96, x, y, 9, 13);
				if(levelData[x + y * levelWidth] == 97) tileData[x + y * levelWidth] = new Tile(97, x, y, 2, 11);
				if(levelData[x + y * levelWidth] == 98) tileData[x + y * levelWidth] = new Tile(98, x, y, 3, 11);
			}
		}
		
		entities.clear();
		for(int i = 0; i < entityData.length; i+=3){
			int currentX = entityData[i + 1];
			int currentY = entityData[i + 2];
			
			if(entityData[i] == 100) entities.add(new Entity(100, currentX, currentY, 0, 4));
			if(entityData[i] == 101) entities.add(new Entity(101, currentX, currentY, 1, 4));
			if(entityData[i] == 102) entities.add(new Entity(102, currentX, currentY, 2, 4));
			if(entityData[i] == 103) entities.add(new Entity(103, currentX, currentY, 11, 2));
			if(entityData[i] == 104) entities.add(new Entity(104, currentX, currentY, 11, 3));
		}
		
	}
	
	private File currentFile;
	
	//Saves the currentLevel to a file
	public void save(){
		if(loadField.getText().length() > 0){
			currentFile = new File("terrains/" + loadField.getText());
		}else{
			currentFile = new File("terrains/" + levelField.getText());
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(currentFile));
			
			for(int y = 0; y < levelHeight; y++){
				for(int x = 0; x < levelWidth; x++){
					bw.write(Integer.toString(levelData[x + y * levelWidth]));
					bw.write(",");
				}
				bw.write("\n");
			}
			
			if(entityData.length > 0){
				bw.write(Integer.toString(entityData.length / 3));
				bw.write("\n");
				for(int i = 0; i < entityData.length; i+=3){
					bw.write(Integer.toString(entityData[i]));
					bw.write("\n");
					bw.write(Integer.toString(entityData[i + 1]));
					bw.write("\n");
					bw.write(Integer.toString(entityData[i + 2]));
					bw.write("\n");		
				}
			}else{
				bw.write(Integer.toString(0));
			}
			
			bw.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		displayS = true;
	}
	
	public Tile editorTile(int id){
		
		int x = selector.currentMouseXNS;
		int y = selector.currentMouseYNS;
		
		
		if(id == 0) return new AirTile(0, x, y);
		if(id == 1) return new Tile(1, x, y, 0, 0);
		if(id == 2) return new Tile(2, x, y, 1, 0);
		if(id == 3) return new Tile(3, x, y, 3, 0);
		if(id == 4) return new Tile(4, x, y, 4, 0);
		if(id == 5) return new Tile(5, x, y, 3, 1);
		if(id == 6) return new Tile(6, x, y, 4, 1);
		if(id == 7) return new Tile(7, x, y, 6, 0);
		if(id == 8) return new Tile(8, x, y, 7, 0);
		if(id == 9) return new Tile(9, x, y, 6, 1);
		if(id == 10) return new Tile(10, x, y, 7, 1);
		if(id == 11) return new Tile(11, x, y, 9, 0);
		if(id == 12) return new Tile(12, x, y, 10, 0);
		if(id == 13) return new Tile(13, x, y, 9, 1);
		if(id == 14) return new Tile(14, x, y, 10, 1);
		if(id == 15) return new Tile(15, x, y, 12, 0);
		if(id == 16) return new Tile(16, x, y, 13, 0);
		if(id == 17) return new Tile(17, x, y, 12, 1);
		if(id == 18) return new Tile(18, x, y, 13, 1);
		if(id == 19) return new Tile(19, x, y, 0, 2);
		if(id == 20) return new Tile(20, x, y, 1, 2);
		if(id == 21) return new Tile(21, x, y, 0, 3);
		if(id == 22) return new Tile(22, x, y, 1, 3);
		if(id == 23) return new Tile(23, x, y, 3, 2);
		if(id == 24) return new Tile(24, x, y, 4, 2);
		if(id == 25) return new Tile(25, x, y, 3, 3);
		if(id == 26) return new Tile(26, x, y, 4, 3);
		if(id == 27) return new Tile(27, x, y, 6, 2);
		if(id == 28) return new Tile(28, x, y, 7, 2);
		if(id == 29) return new Tile(29, x, y, 6, 3);
		if(id == 30) return new Tile(30, x, y, 7, 3);
		if(id == 31) return new Tile(31, x, y, 0, 1);
		if(id == 32) return new Tile(32, x, y, 1, 1);
		if(id == 33) return new Tile(33, x, y, 0, 5);
		if(id == 34) return new Tile(34, x, y, 1, 5);
		if(id == 35) return new Tile(35, x, y, 2, 6);
		if(id == 36) return new Tile(36, x, y, 3, 6);
		if(id == 37) return new Tile(37, x, y, 5, 6);
		if(id == 38) return new Tile(38, x, y, 6, 6);
		if(id == 39) return new Tile(39, x, y, 5, 7);
		if(id == 40) return new Tile(40, x, y, 6, 7);
		if(id == 41) return new Tile(41, x, y, 8, 6);
		if(id == 42) return new Tile(42, x, y, 9, 6);
		if(id == 43) return new Tile(43, x, y, 8, 7);
		if(id == 44) return new Tile(44, x, y, 9, 7);
		if(id == 45) return new Tile(45, x, y, 11, 6);
		if(id == 46) return new Tile(46, x, y, 12, 6);
		if(id == 47) return new Tile(47, x, y, 11, 7);
		if(id == 48) return new Tile(48, x, y, 12, 7);
		if(id == 49) return new Tile(49, x, y, 14, 6);
		if(id == 50) return new Tile(50, x, y, 15, 6);
		if(id == 51) return new Tile(51, x, y, 14, 7);
		if(id == 52) return new Tile(52, x, y, 15, 7);
		if(id == 53) return new Tile(53, x, y, 2, 8);
		if(id == 54) return new Tile(54, x, y, 3, 8);
		if(id == 55) return new Tile(55, x, y, 2, 9);
		if(id == 56) return new Tile(56, x, y, 3, 9);
		if(id == 57) return new Tile(57, x, y, 5, 8);
		if(id == 58) return new Tile(58, x, y, 6, 8);
		if(id == 59) return new Tile(59, x, y, 5, 9);
		if(id == 60) return new Tile(60, x, y, 6, 9);
		if(id == 61) return new Tile(61, x, y, 8, 8);
		if(id == 62) return new Tile(62, x, y, 9, 8);
		if(id == 63) return new Tile(63, x, y, 8, 9);
		if(id == 64) return new Tile(64, x, y, 9, 9);
		if(id == 65) return new Tile(65, x, y, 2, 7);
		if(id == 66) return new Tile(66, x, y, 3, 7);
		if(id == 67) return new Tile(67, x, y, 2, 10);
		if(id == 68) return new Tile(68, x, y, 3, 10);
		if(id == 69) return new Tile(69, x, y, 5, 10);
		if(id == 70) return new Tile(70, x, y, 6, 10);
		if(id == 71) return new Tile(71, x, y, 5, 11);
		if(id == 72) return new Tile(72, x, y, 6, 11);
		if(id == 73) return new Tile(73, x, y, 8, 10);
		if(id == 74) return new Tile(74, x, y, 9, 10);
		if(id == 75) return new Tile(75, x, y, 8, 11);
		if(id == 76) return new Tile(76, x, y, 9, 11);
		if(id == 77) return new Tile(77, x, y, 11, 10);
		if(id == 78) return new Tile(78, x, y, 12, 10);
		if(id == 79) return new Tile(79, x, y, 11, 11);
		if(id == 80) return new Tile(80, x, y, 12, 11);
		if(id == 81) return new Tile(81, x, y, 14, 10);
		if(id == 82) return new Tile(82, x, y, 15, 10);
		if(id == 83) return new Tile(83, x, y, 14, 11);
		if(id == 84) return new Tile(84, x, y, 15, 11);
		if(id == 85) return new Tile(85, x, y, 2, 12);
		if(id == 86) return new Tile(86, x, y, 3, 12);
		if(id == 87) return new Tile(87, x, y, 2, 13);
		if(id == 88) return new Tile(88, x, y, 3, 13);
		if(id == 89) return new Tile(89, x, y, 5, 12);
		if(id == 90) return new Tile(90, x, y, 6, 12);
		if(id == 91) return new Tile(91, x, y, 5, 13);
		if(id == 92) return new Tile(92, x, y, 6, 13);
		if(id == 93) return new Tile(93, x, y, 8, 12);
		if(id == 94) return new Tile(94, x, y, 9, 12);
		if(id == 95) return new Tile(95, x, y, 8, 13);
		if(id == 96) return new Tile(96, x, y, 9, 13);
		if(id == 97) return new Tile(97, x, y, 2, 11);
		if(id == 98) return new Tile(98, x, y, 3, 11);
		
		return null;
	}
	
	public Entity editorEntity(int id){
		
		int x = selector.currentMouseXS;
		int y = selector.currentMouseYS;
		
		if(id == 100) return new Entity(100, x, y, 0, 4);
		if(id == 101) return new Entity(101, x, y, 1, 4);
		if(id == 102) return new Entity(102, x, y, 2, 4);
		if(id == 103) return new Entity(103, x, y, 11, 2);
		if(id == 104) return new Entity(104, x, y, 11, 3);
		return null;
	}
	
	public int pickBlock(){
		int currentX = selector.currentMouseXNS;
		int currentY = selector.currentMouseYNS;
		if(levelData[currentX + currentY * levelWidth] == 0){
			return 0;
		}else{
			return levelData[currentX + currentY * levelWidth];
		}

	}
	
	public int pickEntity(){
		int currentX = selector.currentMouseXS;
		int currentY = selector.currentMouseYS;
		
		for(int i = 0; i < entityData.length; i+=3){
			if(currentX == entityData[i + 1] && currentY == entityData[i + 2]){
				return entityData[i];
			}
		}
		
		return 0;
	}
	
	//Main method that is called and triggers the start of the program
	public static void main(String[] args){
		
		JFrame menuFrame = new JFrame();
		menuFrame.setSize(175, 130);
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setLayout(new FlowLayout());
		
		ActionListener act = new ActionListener(){

			public void actionPerformed(ActionEvent a) {
				if(a.getSource().equals(confirm)){
					menuFrame.dispose();
					waiting = false;
				}
			}
		};
		
		menuFrame.add(levelName);
		menuFrame.add(levelField);
		menuFrame.add(loadLabel);
		menuFrame.add(loadField);
		menuFrame.add(confirm);
		
		confirm.addActionListener(act);
		
		menuFrame.setVisible(true);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		menuFrame.setLocation(dim.width / 2 - menuFrame.getSize().width / 2,dim.height / 2 - menuFrame.getSize().height / 2);
		
		levelField.setText("Terrain_D_#");
		loadField.setText("Terrain_1_");
		
		while(waiting){
			try{
				Thread.sleep(20);
			}catch(Exception e){
				
			}
		}
		Main game = new Main();
		
		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setLayout(new BorderLayout());
		frame.add(game, BorderLayout.CENTER);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.start();
		
		
	}


	
}
