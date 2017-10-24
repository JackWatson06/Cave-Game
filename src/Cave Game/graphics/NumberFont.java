package com.mong.graphics;

public class NumberFont {

	//This sets up the variables for rendering a font
	
	private static String numbers = new String("0123456789"
											  +"higscre ");
	private static int[] pixelLength = {6,5,6,6,6,6,6,6,6,6,
									   +6,5,6,6,6,6,6,16};
	
	private static int spacing = 8;
	
	//This renders the message to the screen
	public static void render(Screen screen, String numberRender, int x, int y){
		for(int i = 0; i < numberRender.length(); i++){
			char currentCharacter = numberRender.charAt(i);
			for(int j = 0; j < numbers.length(); j++){
				if(currentCharacter == numbers.charAt(j)){
					if(j < 10){
						screen.render(x, y, j, 14, 1, 1, 4);
						x += pixelLength[j] * 4 + spacing;
					}else{
						screen.render(x, y, j - 10, 15, 1, 1, 4);
						x += pixelLength[j] * 4 + spacing;
					}
				}
			}
		}
	}
	
	//This gets the length of the message one wants to render
	public static int getTextLength(String text){
		int textSize = 0;
		for(int i = 0; i < text.length(); i++){
			for(int j = 0; j < numbers.length(); j++){
				if(text.charAt(i) == numbers.charAt(j)){
					if(i != text.length() - 1){
						textSize += pixelLength[j] * 4 + spacing;
					}else{
						textSize += pixelLength[j] * 4;
					}
				}
			}
		}

		return textSize;
	}
}
