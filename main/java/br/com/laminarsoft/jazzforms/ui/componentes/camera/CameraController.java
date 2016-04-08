package br.com.laminarsoft.jazzforms.ui.componentes.camera;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class CameraController implements Initializable, IControllerComponent{

	@FXML private BorderPane cameraPane;
	private CustomCamera customCamera;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customCamera = new CustomCamera();
		customCamera.setId(CustomCamera.UI_COMPONENT_ID);
		customCamera.getStyleClass().add(CustomCamera.UNFOCUSED_STYLE_NAME);
		customCamera.setPrefHeight(19);
		cameraPane.setCenter(customCamera);
		customCamera.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return customCamera;
	}

	
}
