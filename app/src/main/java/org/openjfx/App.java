package org.openjfx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/* Some Issues:
 *  - Large custom games with few bombs cause strange behavior with reveal()
 *  - Game does not move mines, instead re-makes makes board until a valid board is picked,
 *    This limits the freedom of custom game, and can cause performance issues on large customs
 *  - When all tiles except mines are revealed on the first click, sometimes the game will win,
 *    sometimes not until the user clicks a number.
 *    
 * Other Notes:
 * 	- HighScores.txt must be in res/HighScores/HighScores.txt
 *  - HighScores.txt must be in this format:
 *    		name1,#
 *    		name2,#
 *    		name3,#
 *    	Where # is the time elapsed
 *      Only one comma separating the name and the time is allowed
 *      the program will remove commas when a user enters their name
 *      Ex.
 *      	Mark,29
 *		Mark II,110
 *		Evil Mark,353
 */
public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage theStage) {
		BorderPane pane = new BorderPane();
		GridPane playArea = new GridPane();
		playArea.setAlignment(Pos.CENTER);
		HBox top = new HBox(10);
		Timeline timer;
		
		pane.setStyle("-fx-background-color: #bfbfbf;-fx-border-color: #fafafa #787878 #787878 #fafafa; -fx-border-width: 6; -fx-border-radius: 0.001;");
	    top.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width: 6; -fx-border-radius: 0.001;");
	    playArea.setStyle("-fx-border-color:  #787878 #fafafa #fafafa #787878; -fx-border-width: 5; -fx-border-radius: 0.001;");
	    
		
		Difficulty diff = new Difficulty("Beginner");
		NumberPane time = new NumberPane();
		NumberPane bombCount = new NumberPane(diff.getNumMines());		
		FaceButton topBtn = new FaceButton();
		
		timer = new Timeline(new KeyFrame(Duration.seconds(1), e->  {
			time.increase();
		}));
		
		timer.setCycleCount(Timeline.INDEFINITE);
		
		Menu menu1 = new Menu("Difficulty");
		Menu menu2 = new Menu("Highscores");
		MenuItem menuItem1 = new MenuItem("Beginner");
		MenuItem menuItem2 = new MenuItem("Intermediate");
		MenuItem menuItem3 = new MenuItem("Expert");
		MenuItem menuItem4 = new MenuItem("Custom");
		MenuItem menuItem5 = new MenuItem("Show High Scores");
		//Rest game to new difficulties, A little messy
		menuItem1.setOnAction(e -> {
			diff.setDifficulty("Beginner");
			newGame(playArea, topBtn, bombCount, time, diff, timer);
			theStage.sizeToScene();
			System.out.println("Difficulty set to: " + diff.getDiff());
		});
		menuItem2.setOnAction(e -> {
			diff.setDifficulty("Intermediate");
			newGame(playArea, topBtn, bombCount, time, diff, timer);
			theStage.sizeToScene();
			System.out.println("Difficulty set to: " + diff.getDiff());
		});
		menuItem3.setOnAction(e -> {
			diff.setDifficulty("Expert");
			newGame(playArea, topBtn, bombCount, time, diff, timer);
			theStage.sizeToScene();
			System.out.println("Difficulty set to: " + diff.getDiff());
		});		
		//Display custom dialog box where width, height, and Number of mines specified for a custom difficulty
		menuItem4.setOnAction(e -> {
			Dialog<int[]> diffDLG = new Dialog<>();
			diffDLG.setTitle("Custom Difficulty");
			diffDLG.setHeaderText("Please, enter values");
			
			ButtonType OKBTN = new ButtonType("OK", ButtonData.OK_DONE);
			diffDLG.getDialogPane().getButtonTypes().addAll(OKBTN, ButtonType.CANCEL);
			
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));
			
			IntegerTextField widthTXF = new IntegerTextField();
			widthTXF.setPromptText("Enter a width");
			IntegerTextField heightTXF = new IntegerTextField();
			heightTXF.setPromptText("Enter a height");
			IntegerTextField numMinesTXF = new IntegerTextField();
			numMinesTXF.setPromptText("Enter the number of mines");

			grid.add(new Label("Width:"), 0, 0);
			grid.add(widthTXF, 1, 0);
			grid.add(new Label("Height:"), 0, 1);
			grid.add(heightTXF, 1, 1);
			grid.add(new Label("Number of Mines:"), 0, 2);
			grid.add(numMinesTXF, 1, 2);
			
			diffDLG.getDialogPane().setContent(grid);
			diffDLG.initOwner(theStage);
			diffDLG.setResultConverter(dialogButton -> {
			    if (dialogButton == OKBTN) {
			        return new int[] {widthTXF.getValue(), heightTXF.getValue(), numMinesTXF.getValue()};
			    }
			    return null;
			});
			
			Optional<int[]> result = diffDLG.showAndWait();

			result.ifPresent(values -> {
			    System.out.println("Input Values:\nWidth: " + values[0] + " height: " + values[1] + " NumMines: " + values[2]);
				diff.setDifficulty(values[0], values[1], values[2]);
				newGame(playArea, topBtn, bombCount, time, diff, timer);
				theStage.sizeToScene();
				System.out.println("Difficulty set to: " + diff.getDiff());
			});
			
			
		});
		//Display a dialog box showing high scores based on HighScores.txt
		menuItem5.setOnAction(e -> {
			String output = "";
			try {
				File highscores = new File(getClass().getResource("HighScores/HighScores.txt").getFile());
				Scanner input = new Scanner(highscores);
				for(int i = 0; i < 3; i++) {
					String[] s = input.nextLine().split(",");
					String d = "";
					if(i == 0) d = "Beginner:";
					if(i == 1) d = "Intermediate:";
					if(i == 2) d = "Expert:";
					output += String.format("%-15s %-5s seconds %s\n",d,s[1],s[0]);
				}
				input.close();
			}
			catch(FileNotFoundException e1) {
				System.out.println("HighScores.txt is Missing: " + e1);
			}
			
			
			Alert display = new Alert(AlertType.NONE, output, ButtonType.OK);
			display.setTitle("Fastest Mine Sweepers"); 
			display.initOwner(theStage);
			display.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
			         display.close();
			     }
			});
		});
		menu1.getItems().add(menuItem1);
		menu1.getItems().add(menuItem2);
		menu1.getItems().add(menuItem3);
		menu1.getItems().add(menuItem4);
		menu2.getItems().add(menuItem5);
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().add(menu1);
		menuBar.getMenus().add(menu2);
		
		newGame(playArea, topBtn, bombCount, time, diff, timer);
		topBtn.setPadding(new Insets(0,0,0,0));
		//Reset the game with the same difficulty on the top button press
		topBtn.setOnMouseClicked(e -> {
			newGame(playArea, topBtn, bombCount, time, diff, timer);
			theStage.sizeToScene();
			topBtn.setSmile();
		});
		
		//Add all the nodes to various panes, then the scene the the stage
		VBox vbox = new VBox();
		top.getChildren().add(time);
		top.getChildren().add(topBtn);
		top.getChildren().add(bombCount);
		top.setAlignment(Pos.CENTER);
		vbox.getChildren().add(menuBar);
		vbox.getChildren().add(top);
		pane.setTop(vbox);
		pane.setBottom(playArea);
		Scene scene = new Scene(pane);
		theStage.setTitle("Minesweeper");
		theStage.getIcons().add(new Image(getClass().getResource("mine-grey.png").toExternalForm()));
		theStage.setScene(scene);
		theStage.show();
	}
	
	public boolean checkWin(Tile[][] tiles) {
		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles[row].length; col++) {
				//tiles[row][col].uncover(); //InstaWin!
				if(tiles[row][col].isCovered()  && !tiles[row][col].isMine()) {
					return false;
				}
			}
		}
		
		disableBoard(tiles);
		return true;
	}
	
	public void winGame(NumberPane time, Timeline timer, FaceButton topBtn, Difficulty diff) {
		topBtn.setWin();
		timer.stop();
		int t = time.getValue();
		//Check if leader board worthy and if so show dialog box to enter name and update leader board
		int leadingTime = 999;
		int diffNum = -1;
		switch (diff.getDiff()) {
		case "Beginner":
			diffNum = 0;
			break;
		case "Intermediate":
			diffNum = 1;
			break;
		case "Expert":
			diffNum = 2;
			break;
		default:
			return;
		}
		try {
			File highscores = new File(getClass().getResource("HighScores/HighScores.txt").getFile());
			Scanner input = new Scanner(highscores);
			String[] leaders = new String[3];
			
			for(int i = 0; i < 3; i++) {
				leaders[i] = input.nextLine();
			}
			String[] diffleader = leaders[diffNum].split(",");
			leadingTime = Integer.parseInt(diffleader[1]);
			
			if(t < leadingTime) {
				//show dialog box
				int g = diffNum;
				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("New Record!");
				dialog.setHeaderText("New Record for " + diff.getDiff() + " difficulty!");
				dialog.setContentText("Please enter your name:");
				PrintWriter output = new PrintWriter(highscores);
				Optional<String> result = dialog.showAndWait();
				result.ifPresent(name -> {
					String s = "";				
					for(int i = 0; i < 3; i++) {
						if(i == g) {	
							s += name.replaceAll(",", "") + "," + t + "\n";							
						}
						else {
							s += leaders[i] + "\n";
						}
					}
					output.println(s);
				});
				output.close();
			}
			
			input.close();
		}
		catch(FileNotFoundException e1) {
			System.out.println("HighScores.txt is Missing: " + e1);
		}
	}
	
	public void loseGame(Tile[][] tiles, int drow, int dcol, FaceButton topBtn) {
		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles[row].length; col++) {
				if(tiles[row][col].isMine()) {
					tiles[row][col].uncover();
				}
				
				tiles[row][col].setIfMisFlagged();
				tiles[row][col].setMouseTransparent(true);
			}
		}
		tiles[drow][dcol].uncover();
		tiles[drow][dcol].setRedMine();
		topBtn.setDead();
	}
	
	public void disableBoard(Tile[][] tiles) { 
		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles[row].length; col++) {
				tiles[row][col].setMouseTransparent(true);
			}
		}
	}
	//Recursively uncover surrounding tiles, Base Case: A Number Tile
	
	//Uncover blank spots recursively, Base Case: Numbered Tiles
	public void reveal(Tile[][] tiles, int row, int col) {
		if(tiles[row][col].isFlagged()) {
			return;
		}
		else if(tiles[row][col].getState() > 0 || !tiles[row][col].isCovered()) {
			tiles[row][col].uncover();
			return;
		}

		for(int r = row-1; r <= row+1; r++) {
			for(int c = col-1; c <= col+1; c++) {
				tiles[row][col].uncover();
				if(isValidSpot(tiles,r,c) && (r != row || c != col)) 
					reveal(tiles, r, c);
			}
		}
	}
	
	public boolean checkCorrectFlagging(Tile[][] tiles, int row, int col, FaceButton topBtn) {
		//Count the amount of flags around the selected number tile
		int count = 0;
		for(int r = row-1; r <= row+1; r++) {
			for(int c = col-1; c <= col+1; c++) {
				if(isValidSpot(tiles, r, c) && (r != row || c != col)) 
					if(tiles[r][c].isFlagged()) count++;
			}
		}
		//Reveal surrounding spots if there is a correct number of flags
		if(tiles[row][col].getState() == count) {
			for(int r = row-1; r <= row+1; r++) {
				for(int c = col-1; c <= col+1; c++) {
					//Check if a revealed spot is a mine lose, else if check win, else call reveal()
					if(isValidSpot(tiles,r,c) && (r != row || c != col) && !tiles[r][c].isFlagged())  {
						if(tiles[r][c].isMine()){
							tiles[r][c].uncover();
							loseGame(tiles, r, c, topBtn);
							return true;
						}
						else {
							reveal(tiles, tiles[r][c].getRow(),tiles[r][c].getCol());
						}
					}
				}
			}
		}
		return false;
	}
	
	//Check if first click of a game has no mines around it  in a 3x3
	public boolean isValidFirstMove(int[][] tiles, int row, int col) {
		if(tiles[row][col] > 0) return false;
		for(int r = row-1; r <= row+1; r++) {
			for(int c = col-1; c <= col+1; c++) {
				if(isValidSpot(tiles, r, c) && tiles[r][c] > 8) 
					return false;
			}
		}
		return true;
	}
	
	//Check if specified row and col is within a given 2d array
	public boolean isValidSpot(Tile[][] tiles, int row, int col) {
		int height = tiles.length;
		int width = tiles[0].length;
		return !(row < 0 || col < 0 || row >  height-1 || col > width-1);
	}
	
	public boolean isValidSpot(int[][] tiles, int row, int col) {
		int height = tiles.length;
		int width = tiles[0].length;
		return !(row < 0 || col < 0 || row >  height-1 || col > width-1);
	}
	 
	public void reStateBoard(Tile[][] tiles, int[][] board) {
		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles[row].length; col++) {
				Integer n = board[row][col];
				tiles[row][col].setState(n);

			}
		}
	}
	//Setting up the game area and all the buttons
	//unrow and uncol are for if the first click of the game has mines around it then change the board until it is valid
	//pass the row and col values of the first click into a new newGame and reveal() that spot at the end of the function
	public void newGame(GridPane gp, FaceButton topBtn, NumberPane bombCount, NumberPane time, Difficulty diff, Timeline timer) {
		Tile.setNewGame(true);
		gp.getChildren().clear();
		topBtn.setSmile();
		bombCount.setMax(diff.getNumMines());
		time.reset();
		timer.stop();
		int[][] board = newBoard(diff);
		Tile[][] tiles = new Tile[board.length][board[0].length];
		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles[row].length; col++) {
				Integer n = board[row][col];
				if(n == 9)
					tiles[row][col] = new Tile("mine-grey", row, col);
				else
					tiles[row][col] = new Tile(n.toString(), row, col);
				
				Tile b = tiles[row][col];
				
				b.setOnMousePressed(e -> {
					topBtn.setO();
				});
				b.setOnMouseReleased(e -> {
					MouseButton button = e.getButton(); //get PRIMARY or SECONDARY mouse button
					String btnName = button.toString();
					topBtn.setSmile();
					
					if(btnName.equals("PRIMARY") && !b.isFlagged()) {
						//Check if the 3x3 area around the first click of the game is free of bombs and
						//make a new board until it is, then call newGame
						if(Tile.getIsNewGame()) {
							if(!isValidFirstMove(board, b.getRow(), b.getCol())) {
								int[][] num = newBoard(diff);
								System.out.println("Mine clicked on first try");
								while(!isValidFirstMove(num, b.getRow(), b.getCol())) {
									num = newBoard(diff);
									System.out.println("Trying agian...");
								}

								reStateBoard(tiles, num);
								reveal(tiles, b.getRow(), b.getCol());
								System.out.println("Finished");

							}
							else {
								reveal(tiles, b.getRow(), b.getCol());
								if(checkWin(tiles)) {
									b.uncover();
									winGame(time, timer, topBtn, diff);
									return;
								}
							}
							Tile.setNewGame(false);
							timer.play();
						}
						else {
							if(b.isCovered()) {
								Tile.setNewGame(false);
								if(b.isMine()){
									loseGame(tiles,b.getRow(),b.getCol(),topBtn);
									timer.stop();
								}
								else {
									reveal(tiles, b.getRow(), b.getCol());
									if(checkWin(tiles)) {
										b.uncover();
										winGame(time, timer, topBtn, diff);
									}
								}
							}
							else if (b.getState() > 0) {
								if(checkCorrectFlagging(tiles, b.getRow(), b.getCol(),topBtn)) {
									timer.stop();
								}
								if(checkWin(tiles)) {
									b.uncover();
									winGame(time, timer, topBtn, diff);
								}
							}
						}
					}
					else if(btnName.equals("SECONDARY") && b.isCovered()){
						b.setFlag(!b.isFlagged());
						if(b.isFlagged()) bombCount.decrease();
						else bombCount.increase();
					}
						
				});
				//gridpane is backwards, col first row second
				gp.add(b, col, row);
			}
		}
	}
	
	//Return a randomized game board based on the difficulty
	public int[][] newBoard(Difficulty diff) {
		int width = diff.getWidth();
		int height = diff.getHeight();
		int numMines = diff.getNumMines();
		int[][] board = new int[height][width];
		
		for(int i = 0; i < numMines; i++) {
			int randRow = (int)(Math.random()*height);
			int randCol = (int)(Math.random()*width);
			if(board[randRow][randCol] == 9) {
				i--;
				continue;
			}
			board[randRow][randCol] = 9;
		}
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				if(board[row][col] == 9) {
					for(int r = row-1; r <= row+1; r++) {
						for(int c = col-1; c <= col+1; c++) {
							if(isValidSpot(board, r, c) && board[r][c] < 9) 
								board[r][c]++;
						}
					}
				}
			}
		}
		return board;		
	}
}
