package br.com.laminarsoft.jazzforms.ui.componentes.gps;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class GPSController implements Initializable, IControllerComponent{

	@FXML private BorderPane gpsPane;
	private CustomGPS customGPS;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customGPS = new CustomGPS();
		customGPS.setId(CustomGPS.UI_COMPONENT_ID);
		customGPS.getStyleClass().add(CustomGPS.UNFOCUSED_STYLE_NAME);
		customGPS.setPrefHeight(19);
		gpsPane.setCenter(customGPS);
		customGPS.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return customGPS;
	}

	
}
