package br.com.laminarsoft.jazzforms.ui.componentes.button;

import java.net.URL;
import java.util.ResourceBundle;

import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class ButtonController implements Initializable, IControllerComponent{

	@FXML private BorderPane buttonPane;
	private CustomButton button;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		button = new CustomButton();
		button.setId(CustomButton.UI_COMPONENT_ID);
		button.setUnfocusedStyle();
		button.setPrefHeight(24);
		buttonPane.setCenter(button);
		button.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return button;
	}

	
}
