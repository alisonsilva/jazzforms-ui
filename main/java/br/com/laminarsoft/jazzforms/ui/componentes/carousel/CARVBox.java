package br.com.laminarsoft.jazzforms.ui.componentes.carousel;

import javafx.scene.layout.VBox;

public class CARVBox extends VBox {

	public String position = "center";
	
	public CARVBox(String position, double spacing) {
		super(spacing);
		this.position = position;
	}
}
