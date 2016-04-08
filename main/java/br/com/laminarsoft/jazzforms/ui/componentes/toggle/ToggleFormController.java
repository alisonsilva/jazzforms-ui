package br.com.laminarsoft.jazzforms.ui.componentes.toggle;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class ToggleFormController implements Initializable, IControllerComponent{

	@FXML private HBox toggleFormPane;

	private CustomToggleForm custToggleForm;	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		custToggleForm = new CustomToggleForm();
		custToggleForm.setId(CustomToggleForm.UI_COMPONENT_ID);
		custToggleForm.getStyleClass().add(CustomToggleForm.UNFOCUSED_STYLE_NAME);
		custToggleForm.setPrefHeight(30);
		toggleFormPane.getChildren().add(custToggleForm);
		custToggleForm.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return custToggleForm;
	}

	
}
