package org.openjfx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

//EAch of the tiles in the game area
public class Tile extends Button {
	private static boolean isNewGame;
	private String name;
	private int state;
	private int row, col;
	ImageView imageCover, imageTile;
	private boolean isFlagged = false;
	private boolean isMine = false;
	private boolean isCovered = true;
	
	public Tile(String name, int row, int col) {
		this.name = name;
		if(name.equals("mine-grey")) {
			isMine = true; 
			state = 9;
		}
		else {
			state = Integer.parseInt(name);
		}
		this.row = row;
		this.col = col;
		//int size = 50;
		imageCover  = new ImageView(new Image(getClass().getResource("Cover.png").toExternalForm()));
		imageTile = new ImageView(new Image(getClass().getResource(name + ".png").toExternalForm()));
/*
		imageCover.setFitHeight(size);
		imageCover.setFitWidth(size);

		imageTile.setFitHeight(size);
		imageTile.setFitWidth(size);
*/
		setPadding(new Insets(0,0,0,0));
		setGraphic(imageCover);
	}
	
	public void setState(int s) {
		if(s == 9) {
			this.name = "mine-grey";
			isMine = true; 
			state = 9;
		}
		else {
			this.name = Integer.toString(s);
			isMine = false;
			state = s;
		}

                imageTile = new ImageView(new Image(getClass().getResource(name + ".png").toExternalForm()));
	}

	public void uncover() {
		setGraphic(imageTile);
		isCovered = false;
	}
	
	public void cover() {
		setGraphic(imageCover);
		isCovered = true;
	}
	
	public void setRedMine() {
		if(isMine()) {
			imageTile = new ImageView(new Image(getClass().getResource("mine-red.png").toExternalForm()));
			setGraphic(imageTile);
		}
		else {
			System.out.println("Tile can't be set to red mine if not a mine");
		}
	}
	
	public void setIfMisFlagged() {
		if(!isMine() && isFlagged) {
			imageTile = new ImageView(new Image(getClass().getResource("mine-misflagged.png").toExternalForm()));
			setGraphic(imageTile);
		}
		
	}
	
	public void setFlag(boolean b) { 
		isFlagged = b; 
		if(b) 
			imageCover = new ImageView(new Image(getClass().getResource("flag.png").toExternalForm()));
		else 
			imageCover = new ImageView(new Image(getClass().getResource("Cover.png").toExternalForm()));
		setGraphic(imageCover);
	}
	public int getRow() { return row; }
	public int getCol() { return col; }
	public int getState() { return state; }
	public boolean isMine() { return isMine; }
	public boolean isCovered() { return isCovered; }
	public boolean isFlagged() { return isFlagged; }
	public static void setNewGame(boolean b) { isNewGame = b; }
	public static boolean getIsNewGame() { return isNewGame; }
	
}
