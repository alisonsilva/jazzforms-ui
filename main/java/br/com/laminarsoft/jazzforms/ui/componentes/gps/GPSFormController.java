package br.com.laminarsoft.jazzforms.ui.componentes.gps;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class GPSFormController implements Initializable, IControllerComponent{

	@FXML private HBox textFieldFormPane;

	private CustomGPSForm custTextField;	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		custTextField = new CustomGPSForm();
		custTextField.setId(CustomGPSForm.UI_COMPONENT_ID);
		custTextField.getStyleClass().add(CustomGPSForm.UNFOCUSED_STYLE_NAME);
		custTextField.setPrefHeight(30);
		textFieldFormPane.getChildren().add(custTextField);
		custTextField.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getOnKeyTyped());
	}


	@Override
	public Node getBackComponent() {
		return custTextField;
	}

	
}
