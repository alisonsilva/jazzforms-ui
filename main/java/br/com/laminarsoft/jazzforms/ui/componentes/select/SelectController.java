package br.com.laminarsoft.jazzforms.ui.componentes.select;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class SelectController implements Initializable, IControllerComponent{

	@FXML private BorderPane textfieldPane;
	private CustomSelect customSelect;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customSelect = new CustomSelect();
		customSelect.setId(CustomSelect.UI_COMPONENT_ID);
		customSelect.getStyleClass().add(CustomSelect.UNFOCUSED_STYLE_NAME);
		customSelect.setPrefHeight(19);
		textfieldPane.setCenter(customSelect);
		customSelect.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}	


	@Override
	public Node getBackComponent() {
		return customSelect;
	}

	
}
