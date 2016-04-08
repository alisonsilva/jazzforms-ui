package br.com.laminarsoft.jazzforms.ui.componentes.dataview;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class DataviewController implements Initializable, IControllerComponent{

	@FXML private BorderPane dataView;
	@FXML private Label lblTitle;
	@FXML private Label lblInstructions;
	
	private CustomDataview customDataview;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customDataview = new CustomDataview();
		customDataview.setId(CustomDataview.UI_COMPONENT_ID);
		customDataview.setUnfocusedStyle();
		dataView.setCenter(customDataview);
		customDataview.setTitleLabel(lblTitle);
		customDataview.setInstructionsLabel(lblInstructions);
		customDataview.setTitle(lblTitle.getText());
		customDataview.setInstruction(lblInstructions.getText());
		customDataview.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
		customDataview.getBackComponent().setJfxPreferedHeight(200);
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
		
		lblTitle.setEffect(ds);
	}


	@Override
	public Node getBackComponent() {
		return customDataview;
	}

	
}
