package com.mong.level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HighScore {

	//This is the currentHighScore
	
	public int currentHighScore;
	
	//This reads the highScore from the file
	
	public void readHighScore(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("HighScore")));
			String highScore = br.readLine();
			currentHighScore = Integer.parseInt(highScore);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//This writes a new highscore to the file if it is necessary
	
	public void writeHightScore(int inputHighScore){
		if(inputHighScore > currentHighScore){
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("HighScore")));
				bw.write(Integer.toString(inputHighScore));
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
