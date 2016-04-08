package br.com.laminarsoft.jazzforms.ui.componentes.checkbox;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class CheckboxFormController implements Initializable, IControllerComponent{

	@FXML private HBox checkboxFormPane;

	private CustomCheckboxForm custCheckboxForm;	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		custCheckboxForm = new CustomCheckboxForm();
		custCheckboxForm.setId(CustomCheckboxForm.UI_COMPONENT_ID);
		custCheckboxForm.getStyleClass().add(CustomCheckboxForm.UNFOCUSED_STYLE_NAME);
		custCheckboxForm.setPrefHeight(30);
		checkboxFormPane.getChildren().add(custCheckboxForm);
		checkboxFormPane.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return custCheckboxForm;
	}

	
}
