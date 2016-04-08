package br.com.laminarsoft.jazzforms.ui.componentes.carousel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CarouselController implements Initializable, IControllerComponent{

	@FXML private BorderPane carousel;
	@FXML private Label lblPosicao;
	@FXML private HBox btnTopArea;
	@FXML private HBox btnDownArea;
	@FXML private HBox btnLeftArea;
	@FXML private HBox btnRightArea;
	
	private CustomCarousel customCarousel;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		customCarousel = new CustomCarousel();
		customCarousel.setPrefHeight(200);
		customCarousel.setId(CustomCarousel.UI_COMPONENT_ID);
		customCarousel.setUnfocusedStyle();
		carousel.setCenter(customCarousel);
		customCarousel.setOnKeyPressed(JazzForms.getPaginaDesenhoAtual().getKeyListener());
		customCarousel.setLblPosicao(lblPosicao);
		customCarousel.setButtonsAreas(btnLeftArea, btnRightArea, btnTopArea, btnDownArea);
	}


	@Override
	public Node getBackComponent() {
		return customCarousel;
	}

	@FXML
	public void btnLeftAction(ActionEvent event) {
		customCarousel.movePaginaEsquerda();
	}

	@FXML
	public void btnRightAction(ActionEvent event) {
		customCarousel.movePaginaDireita();
	}
	
	@FXML
	public void btnUpAction(ActionEvent event) {
		customCarousel.movePaginaEsquerda();
	}
	
	@FXML
	public void btnDownAction(ActionEvent event) {
		customCarousel.movePaginaDireita();
	}

	@FXML
	public void btnRemoveTabAction(ActionEvent event) {
		JFDialog dialog = new JFDialog();

		EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				customCarousel.removePagina();
			}
		};
		dialog.show("Tem certeza que deseja excluir a página atual do carousel?", "Sim", "Não", act1);
		event.consume();		
	}	
	
	@FXML
	public void btnAddTabAction(ActionEvent event) {
		customCarousel.adicionaPagina();
	}	
}
