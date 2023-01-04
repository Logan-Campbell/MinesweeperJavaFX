package minesweeper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

//Control for the time and bomb count features
public class NumberPane extends HBox {
	private static int fitHeight = 50;
	private static int fitWidth = 25;
	private int MAX_VALUE;
	private int value;
	private ImageView imageOnes, imageTens, imageHuns;
	//used for time
	public NumberPane() {
		super(0);
		setStyle("-fx-background-color: #000000; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width: 3; -fx-border-radius: 0.001;");
		value = 0;
		MAX_VALUE = 999;
		updateImages();
		
	}
	//used for bomb counter
	public NumberPane(int value) {
		super(0);
		setStyle("-fx-background-color: #000000; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width: 3; -fx-border-radius: 0.001;");
		this.value = value;
		MAX_VALUE = value;
		updateImages();
	}
	
	public void increase() {
		if(value < MAX_VALUE)
			value++;
		updateImages();
	}
	
	public void decrease() {
		if(value > 0)
			value--;
		updateImages();
	}
	
	public void reset() {
		if(MAX_VALUE < 999)
			value = MAX_VALUE;
		else
			value = 0;
		updateImages();
	}
	
	public void setMax(int max) {
		MAX_VALUE = max;
		reset();
	}
	
	public int getValue() { return value; }
	
	private void updateImages() {
		imageOnes = new ImageView(new Image(getClass().getResource("digits/" + (value%10)+ ".png").toExternalForm()));
		imageTens = new ImageView(new Image(getClass().getResource("digits/" + (value/10%10)+ ".png").toExternalForm()));
		imageHuns = new ImageView(new Image(getClass().getResource("digits/" + (value/100%10)+ ".png").toExternalForm()));
		imageOnes.setFitHeight(fitHeight);
		imageOnes.setFitWidth(fitWidth);
		imageTens.setFitHeight(fitHeight);
		imageTens.setFitWidth(fitWidth);
		imageHuns.setFitHeight(fitHeight);
		imageHuns.setFitWidth(fitWidth);
		getChildren().setAll(imageHuns,	imageTens,	imageOnes);
	}
}