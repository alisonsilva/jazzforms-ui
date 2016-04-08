package br.com.laminarsoft.jazzforms.ui.componentes.toolbar;

import javafx.scene.layout.HBox;

public class TBHBox extends HBox {

	public String position = "left";
	public TBHBox(String position, double spacing) {
		super(spacing);
		this.position = position;
	}	
}
