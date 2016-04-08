package br.com.laminarsoft.jazzforms.ui.componentes.numberfield;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class NumberfieldFormController implements Initializable, IControllerComponent{

	@FXML private HBox textFieldFormPane;

	private CustomNumberfieldForm custTextField;	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		custTextField = new CustomNumberfieldForm();
		custTextField.setId(CustomNumberfieldForm.UI_COMPONENT_ID);
		custTextField.getStyleClass().add(CustomNumberfieldForm.UNFOCUSED_STYLE_NAME);
		custTextField.setPrefHeight(30);
		textFieldFormPane.getChildren().add(custTextField);
		custTextField.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getOnKeyTyped());
	}


	@Override
	public Node getBackComponent() {
		return custTextField;
	}

	
}
