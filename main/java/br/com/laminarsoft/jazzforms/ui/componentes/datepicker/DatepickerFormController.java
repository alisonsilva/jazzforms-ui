package br.com.laminarsoft.jazzforms.ui.componentes.datepicker;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class DatepickerFormController implements Initializable, IControllerComponent{

	@FXML private HBox datePickerFormPane;

	private CustomDatepickerForm datePicker;	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		datePicker = new CustomDatepickerForm(new Date());
		datePicker.setId(CustomDatepicker.UI_COMPONENT_ID);
		datePicker.getStyleClass().add(CustomDatepicker.UNFOCUSED_STYLE_NAME);
		datePicker.setPrefHeight(30);
		datePickerFormPane.getChildren().add(datePicker);
		datePicker.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
	}


	@Override
	public Node getBackComponent() {
		return datePicker;
	}

	
}
