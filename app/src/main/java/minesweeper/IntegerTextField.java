package minesweeper;

import java.util.regex.Pattern;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class IntegerTextField extends TextField {
	TextFormatter<Integer> formatter;
	
	public IntegerTextField () {
		super();
		//Make sure only Integers can be entered into the TextField
		formatter = new TextFormatter<>(
			    new IntegerStringConverter(), 
			    0,  
			    c -> Pattern.matches("\\d*", c.getText()) ? c : null );
		setTextFormatter(formatter);
	}
	
	public int getValue() {
		return Integer.parseInt(getText());
	}
}