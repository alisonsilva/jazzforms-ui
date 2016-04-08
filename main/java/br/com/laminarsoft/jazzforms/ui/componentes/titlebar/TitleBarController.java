package br.com.laminarsoft.jazzforms.ui.componentes.titlebar;

import java.net.URL;
import java.util.ResourceBundle;

import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class TitleBarController implements Initializable, IControllerComponent{

	@FXML private BorderPane titleBar;
	private CustomTitleBar barra;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		barra = new CustomTitleBar();
		barra.setId(CustomTitleBar.UI_COMPONENT_ID);
		barra.getStyleClass().add("x-toolbar-top");
		barra.setPrefHeight(60);
		titleBar.setCenter(barra);
		barra.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return barra;
	}

	
}
