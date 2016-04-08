package br.com.laminarsoft.jazzforms.ui.componentes.formpanel;

import javafx.scene.layout.VBox;

public class FRMVBox extends VBox {

	public String position = "center";
	
	public FRMVBox(String position, double spacing) {
		super(spacing);
		this.position = position;
	}
}
