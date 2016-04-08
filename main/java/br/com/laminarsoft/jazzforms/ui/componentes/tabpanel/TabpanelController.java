package br.com.laminarsoft.jazzforms.ui.componentes.tabpanel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class TabpanelController implements Initializable, IControllerComponent{

	@FXML private BorderPane tabPanel;
	private CustomTabpanel barra;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		barra = new CustomTabpanel();
		barra.setId(CustomTabpanel.UI_COMPONENT_ID);
		barra.getStyleClass().add("x-toolbar-top");
		barra.setPrefHeight(60);
		tabPanel.setCenter(barra);
		barra.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return barra;
	}

	
}
