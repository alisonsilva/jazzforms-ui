package br.com.laminarsoft.jazzforms.ui.componentes.formpanel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class FormPanelController implements Initializable, IControllerComponent{

	@FXML private BorderPane formPanel;
	private CustomFormPanel custFormPanel;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		custFormPanel = new CustomFormPanel();
		custFormPanel.setId(CustomFormPanel.UI_COMPONENT_ID);
		custFormPanel.getStyleClass().add(".x-form-panel");
		custFormPanel.setPrefHeight(150);
		formPanel.setCenter(custFormPanel);
		custFormPanel.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return custFormPanel;
	}

	
}
