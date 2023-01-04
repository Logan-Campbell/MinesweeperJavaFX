package org.openjfx;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class FaceButton extends Button {
	private ImageView imageSmile, imageWin, imageDead, imageO;
	public FaceButton() {
		imageSmile = new ImageView(getClass().getResource("face-smile.png").toExternalForm());
		imageWin = new ImageView(getClass().getResource("face-win.png").toExternalForm());
		imageDead = new ImageView(getClass().getResource("face-dead.png").toExternalForm());
		imageO = new ImageView(getClass().getResource("face-O.png").toExternalForm());
		setGraphic(imageSmile);
	}
	
	public void setSmile() { setGraphic(imageSmile); }
	public void setWin() { setGraphic(imageWin); }
	public void setDead() { setGraphic(imageDead); }
	public void setO() { setGraphic(imageO); }
}
