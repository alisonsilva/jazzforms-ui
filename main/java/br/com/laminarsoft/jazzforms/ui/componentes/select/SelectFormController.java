package br.com.laminarsoft.jazzforms.ui.componentes.select;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class SelectFormController implements Initializable, IControllerComponent{

	@FXML private HBox selectFormPane;

	private CustomSelectForm customSelect;	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customSelect = new CustomSelectForm();
		customSelect.setId(CustomSelectForm.UI_COMPONENT_ID);
		customSelect.getStyleClass().add(CustomSelectForm.UNFOCUSED_STYLE_NAME);
		customSelect.setPrefHeight(30);
		selectFormPane.getChildren().add(customSelect);
		customSelect.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return customSelect;
	}

	
}
