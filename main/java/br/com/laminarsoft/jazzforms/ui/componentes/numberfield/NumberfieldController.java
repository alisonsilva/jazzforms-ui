package br.com.laminarsoft.jazzforms.ui.componentes.numberfield;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class NumberfieldController implements Initializable, IControllerComponent{

	@FXML private BorderPane textfieldPane;
	private CustomNumberfield customNumberField;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customNumberField = new CustomNumberfield();
		customNumberField.setId(CustomNumberfield.UI_COMPONENT_ID);
		customNumberField.getStyleClass().add(CustomNumberfield.UNFOCUSED_STYLE_NAME);
		customNumberField.setPrefHeight(19);
		textfieldPane.setCenter(customNumberField);
		customNumberField.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return customNumberField;
	}

	
}
