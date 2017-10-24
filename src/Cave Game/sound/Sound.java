package com.mong.sound;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	
	//This loads sounds for the program
	public static final Sound music = new Sound("/music.wav");
	public static final Sound coinPickup = new Sound("/Pickup_Coin_Softer.wav");
	public static final Sound hurt = new Sound("/Hurt.wav");
	public static final Sound hit = new Sound("/Hit_Hurt18.wav");
	public static boolean loop = true;
	
	private AudioClip clip;
	
	public Sound(String name){
		try{
			clip = Applet.newAudioClip(Sound.class.getResource(name));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//This plays the sound
	public void play(){
		try{
			new Thread(){
				public void run(){
					clip.play();
				}
			}.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//This stops the sound
	public void stop(){
		clip.stop();
	}
	
	
	//This plays a sound on a loop such as the music
	public void playLoop(){
		try{
			new Thread(){
				public void run(){
					while(loop){
						clip.play();
						try {
							Thread.sleep(28800);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
