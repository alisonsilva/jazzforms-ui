package br.com.laminarsoft.jazzforms.ui.componentes.fieldset;

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

public class FieldsetController implements Initializable, IControllerComponent{

	@FXML private BorderPane fieldset;
	@FXML private Label lblTitle;
	@FXML private Label lblInstructions;
	
	private CustomFieldset fieldSet;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		fieldSet = new CustomFieldset();
		fieldSet.setPrefHeight(100);
		fieldSet.setId(CustomFieldset.UI_COMPONENT_ID);
		fieldSet.setUnfocusedStyle();
		fieldset.setCenter(fieldSet);
		fieldSet.setTitleLabel(lblTitle);
		fieldSet.setInstructionsLabel(lblInstructions);
		fieldSet.setTitle(lblTitle.getText());
		fieldSet.setInstruction(lblInstructions.getText());
		fieldSet.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
		
		lblTitle.setEffect(ds);
	}


	@Override
	public Node getBackComponent() {
		return fieldSet;
	}

	
}
