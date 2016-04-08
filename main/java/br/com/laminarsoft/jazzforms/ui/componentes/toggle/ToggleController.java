package br.com.laminarsoft.jazzforms.ui.componentes.toggle;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class ToggleController implements Initializable, IControllerComponent{

	@FXML private BorderPane textfieldPane;
	private CustomToggle customToggle;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customToggle = new CustomToggle();
		customToggle.setId(CustomToggle.UI_COMPONENT_ID);
		customToggle.getStyleClass().add(CustomToggle.UNFOCUSED_STYLE_NAME);
		customToggle.setPrefHeight(19);
		textfieldPane.setCenter(customToggle);
		customToggle.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return customToggle;
	}

	
}
