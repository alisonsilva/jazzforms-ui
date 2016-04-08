package br.com.laminarsoft.jazzforms.ui.componentes.chart;

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

public class ChartController implements Initializable, IControllerComponent{

	@FXML private BorderPane chartPane;
	@FXML private Label lblTitle;
	
	private CustomChart customChart;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customChart = new CustomChart();
		customChart.setPrefHeight(200);
		customChart.setId(CustomChart.UI_COMPONENT_ID);
		customChart.setUnfocusedStyle();
		chartPane.setCenter(customChart);
		customChart.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
		
		lblTitle.setEffect(ds);
	}


	@Override
	public Node getBackComponent() {
		return customChart;
	}

	
}
