package org.openjfx;

public class Difficulty {
	private int width;
	private int height;
	private int numMines;
	private String difficulty;
	public Difficulty(String diff) {
		setDifficulty(diff);
	}

	public Difficulty(int width, int height, int numMines) {
		setDifficulty(width, height, numMines);
	}
	
	public void setDifficulty(String diff) {
		difficulty = diff;
		if(diff.equals("Beginner")) {	
			width = 8;
			height = 8;
			numMines = 10; //10
		}
		else if(diff.equals("Intermediate")) {
			width = 16;
			height = 16;
			numMines = 40; //40
		}
		else if(diff.equals("Expert")) {
			width = 32;
			height = 16;
			numMines = 99; //99
		}
		else {
			//default
			width = 5;
			height = 5;
			numMines = 3;
		}
	}
	
	public void setDifficulty(int width, int height, int numMines) {
		difficulty = "Custom";
		if(width > 99) this.width = 99;
		else if(width < 8) this.width = 8;
		else this.width = width;
		
		if(height > 99) this.height = 99;
		else if(height < 1) this.height = 1;
		else this.height = height;
		
		//if(numMines > this.width*this.height) this.numMines = this.width*this.height - 1; 
		if(numMines > this.width*this.height) this.numMines = (this.width*this.height)/ 2;
		else if(numMines < 1) this.numMines = 1;
		else this.numMines = numMines;
		System.out.println("\nSet Values:" +
							"Width: " + this.width +
							" Height: " + this.height + 
							" Num mines: " + this.numMines);
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getNumMines() { return numMines; }
	public String getDiff() { return difficulty; }
		
}
