package br.com.laminarsoft.jazzforms.ui.componentes.textfield;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class TextfieldController implements Initializable, IControllerComponent{

	@FXML private BorderPane textfieldPane;
	private CustomTextfield customTextField;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customTextField = new CustomTextfield();
		customTextField.setId(CustomTextfield.UI_COMPONENT_ID);
		customTextField.getStyleClass().add(CustomTextfield.UNFOCUSED_STYLE_NAME);
		customTextField.setPrefHeight(19);
		textfieldPane.setCenter(customTextField);
		customTextField.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return customTextField;
	}

	
}
