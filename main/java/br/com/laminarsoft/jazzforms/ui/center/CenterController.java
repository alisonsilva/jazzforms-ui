package br.com.laminarsoft.jazzforms.ui.center;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TabPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TitleBar;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.center.handler.DesenhaComponenteHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.IComponentesDesenho;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.button.CustomButton;
import br.com.laminarsoft.jazzforms.ui.componentes.carousel.CustomCarousel;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.ComponentWrapper;
import br.com.laminarsoft.jazzforms.ui.componentes.fieldset.CustomFieldset;
import br.com.laminarsoft.jazzforms.ui.componentes.formpanel.CustomFormPanel;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.tabpanel.CustomTabpanel;
import br.com.laminarsoft.jazzforms.ui.componentes.titlebar.CustomTitleBar;
import br.com.laminarsoft.jazzforms.ui.componentes.toolbar.CustomToolBar;
import br.com.laminarsoft.jazzforms.ui.componentes.util.EquivalenciaComponentesUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

@SuppressWarnings("all")
public class CenterController implements Initializable, ChangeListener<Number>, ICenterDispatcher, ISelectedChangeListener {

	@FXML private SplitPane centerAreaSplit;	
	@FXML private BorderPane borderCanvas;	
	@FXML private BorderPane borderDetails;	
	@FXML private HBox topPaginacao;	
	@FXML private Slider sliderWidth;	
	@FXML private Slider sliderHeight;	
	@FXML private Tab detailsObjectsTab;	
	@FXML private Tab avancadoObjectsTab;	
	@FXML private TabPane detailsObjects;	
	@FXML private Button btnCancela;
	@FXML private Button btnConfirma;	
	@FXML private Button btnNovaPagina;	
	@FXML private Button btnApagaPagina;
	
	@FXML private ComboBox<Label> btnBarComboContaineres;

	
	
	List<ScrollPane> paginas = new ArrayList<ScrollPane>();
	
	private Pagina paginaAtual;
    private Pagination paginacao;
    private ProjetoContainer projetoContainer = new ProjetoContainer();
    
    
    
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		assert centerAreaSplit != null : "fx:id=\"centerAreaSplit\" não foi injetado";
		assert borderCanvas != null : "fx:id=\"canvasProjeto\" não foi injetado";
		assert sliderWidth != null : "fx:id=\"sliderWidth\" não foi injetado";
		assert sliderHeight != null : "fx:id=\"sliderHeight\" não foi injetado";
		
		AwesomeDude.setIcon(btnCancela, AwesomeIcon.TIMES_CIRCLE, "16px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnConfirma, AwesomeIcon.CHECK_CIRCLE, "16px", ContentDisplay.RIGHT);
		
		inicializaProjeto();
	}


	private void inicializaProjeto() {
	    centerAreaSplit.setDividerPosition(0, 0.3);

		btnCancela.setDisable(true);
		btnConfirma.setDisable(true);
		paginaAtual = new Pagina();
		ScrollPane scrPane = new ScrollPane();
		scrPane.setContent(paginaAtual);
		scrPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);		
		scrPane.setStyle("-fx-border-radius: 10; -fx-background-radius: 10;");
		
		PaginaContainer paginaContainer = new PaginaContainer();
		paginaContainer.paginaAtual = paginaAtual;
		projetoContainer.paginas.add(paginaContainer);
		paginaContainer.projetoPai = projetoContainer;
		
		paginas.add(scrPane);
		
		paginaAtual.setId(Pagina.ID_PAGINA_ATUAL);
		
		paginaAtual.setStyle(paginaAtual.getUnfocusedtStyle());
		
		
		MouseClickEventsCanvas mouseEventsListener = new MouseClickEventsCanvas(paginaAtual, paginaContainer, detailsObjectsTab, avancadoObjectsTab, this); 
		
		paginaAtual.setOnMouseClicked(mouseEventsListener);
		paginaAtual.setOnMouseClickedHandler(mouseEventsListener);
		
		mouseEventsListener.addSelectChangeListener(this);
		
	    sliderWidth.valueProperty().addListener(this);	    
	    sliderHeight.valueProperty().addListener(this);
	    
	    paginacao = new Pagination(1,0);
	    paginacao.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
	    paginacao.setPageFactory(new Callback<Integer, Node>() {
			
			@Override
			public Node call(Integer param) {
				if (paginaAtual != null) paginaAtual.setId("");
				ScrollPane atual = paginas.get(param);
				paginaAtual = (Pagina)atual.getContent();
				paginaAtual.setId("paginaDesenhoAtual");
				desenhaCanvas();
				return atual;
			}
		});
	    
	    
		paginaAtual.setOnMouseEntered(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mouseEvent) {
				if(paginaAtual.getComponenteDesenho() > 0) {
					paginaAtual.setFocused();
				}
			}
			
		});
		paginaAtual.setOnMouseExited(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mouseEvent) {
				if(paginaAtual.getComponenteDesenho() > 0) {
					paginaAtual.setUnfocused();
				}
			}			
		});
		
	    
	    
	    AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(paginacao, 10.0);
        AnchorPane.setRightAnchor(paginacao, 10.0);
        AnchorPane.setBottomAnchor(paginacao, 10.0);
        AnchorPane.setLeftAnchor(paginacao, 10.0);
        anchor.getChildren().addAll(paginacao);	    
	    
	    borderCanvas.setCenter(paginacao);
	    
	    Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(3000);
					desenhaCanvas();
				} catch (InterruptedException e) {
					if(isCancelled()) {
						updateMessage("cancelado");
					}
				}				
				return null;
			}
	    	
		};
		
		ListCell<Label> buttonCellContainer = new ListCell<Label>() {

			@Override
			protected void updateItem(Label item, boolean isEmpty) {
				super.updateItem(item, isEmpty);
				
				ImageView img = null;
				if (item != null) {
					switch (item.getText().toUpperCase()) {
					case "CONTAINER":
						img = null;
						break;
					case "FORM PANEL":
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/formpanel.png")));
						break;
					case "PICKER":
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/picker.png")));
						break;
					case "FIELD SET":
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/fieldset.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "MESSAGE BOX" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/messagebox.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "TOOL BAR" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/toolbar.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "ACTION SHEET" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/actionsheet.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "NAVIGATION VIEW" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/navigationview.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "TITLE BAR" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/titlebar.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "TAB PANEL" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/tabpanel.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "MAPA" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/map.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "CAROUSEL" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/carousel.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "NESTED LIST" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/nestedlist.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "LIST VIEW" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/listview.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "DATA VIEW" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/dataview.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					}
				}
				setGraphic(img);
				//if (item != null) setAlignment(Pos.CENTER);
				setText(item == null ? "Layout" : item.getText());
			}

		};		

//		btnBarComboLayouts.setButtonCell(buttonCell);
		btnBarComboContaineres.setButtonCell(buttonCellContainer);		
		
		new Thread(task).start();
    }
	
	
	@Override
    public void novoProjeto() {
		final CenterController atual = this;
		
		boolean pgAlterada = false;
		for(int idx = 0; idx < paginas.size(); idx++) {
			ScrollPane scrPane = paginas.get(idx);
			Pagina pag = (Pagina)scrPane.getContent();
			if (pag.isAtualizada()) {
				pgAlterada = true;
				break;
			}
		}
		if(pgAlterada) {
			JFDialog dialog = new JFDialog();
			EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					atual.paginas.clear();
					atual.projetoContainer.paginas.clear();
					atual.inicializaProjeto();
					detailsObjectsTab.setContent(null);
					avancadoObjectsTab.setContent(null);
				}
			};
			dialog.show("Foram feitas algumas atualizações ainda não salvas. Tem certeza que deseja continuar?", "Sim", "Não", act1);
		} else {
			atual.paginas.clear();
			atual.projetoContainer.paginas.clear();
			atual.inicializaProjeto();
			atual.projetoContainer.projetoId = null;
			atual.projetoContainer.descricao = "";
			atual.projetoContainer.nome = "";
			detailsObjectsTab.setContent(null);
			avancadoObjectsTab.setContent(null);
		}
    }
	
	@Override
    public boolean isProjetoNaoSalvo() {
		boolean prjNSalvo = false;
		for(ScrollPane scrPanePg : paginas) {
			Pagina pg = (Pagina)scrPanePg.getContent();
			if(pg.isAtualizada()) {
				prjNSalvo = true;
				break;
			}
		}
	    return prjNSalvo;
    }


	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		desenhaCanvas();		
	}

	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		if (paginaAtual.getOnMouseClickedHandler().getRaizDetalhes() != null) {
			paginaAtual.getOnMouseClickedHandler().getRaizDetalhes().controller.btnCancelaAction(actionEvent);
		}
		if (paginaAtual.getOnMouseClickedHandler().getRaizAvancado() != null) {
			paginaAtual.getOnMouseClickedHandler().getRaizAvancado().controller.btnCancelaAction(actionEvent);
		}
	}

	
	@FXML
	protected void btnConfirmaAction(ActionEvent actionEvent) {
		if (paginaAtual.getOnMouseClickedHandler().getRaizDetalhes() != null) {
			paginaAtual.getOnMouseClickedHandler().getRaizDetalhes().controller.btnConfirmaAction(actionEvent);
		}
		if (paginaAtual.getOnMouseClickedHandler().getRaizAvancado() != null) {
			paginaAtual.getOnMouseClickedHandler().getRaizAvancado().controller.btnConfirmaAction(actionEvent);
		}
		paginaAtual.setAtualizada(true);
	}	
	
	@FXML
	protected void novaPaginaAction(ActionEvent actionEvent) {
		paginaAtual.setId("");
		
		novaPagina(false);
	}


	@FXML 
	protected void btnBarComboContaineres(ActionEvent event) {
		Pagina pagina = JazzForms.getPaginaDesenhoAtual();
		switch(btnBarComboContaineres.getValue().getText().toUpperCase()) {
		case "CONTAINER" :
			pagina.setComponenteDesenho(IComponentesDesenho.ARROW);
			pagina.setCursor(Cursor.DEFAULT);
			break;
		case "TITLE BAR" : 
			pagina.setComponenteDesenho(IComponentesDesenho.TITLEBAR);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;	
		case "TOOL BAR" :
			pagina.setComponenteDesenho(IComponentesDesenho.TOOLBAR);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;
		case "FIELD SET" :
			pagina.setComponenteDesenho(IComponentesDesenho.FIELDSET);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;
		case "FORM PANEL" :
			pagina.setComponenteDesenho(IComponentesDesenho.FORMPANEL);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;
		case "TAB PANEL" :
			pagina.setComponenteDesenho(IComponentesDesenho.TABPANEL);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;
		case "CAROUSEL" :
			pagina.setComponenteDesenho(IComponentesDesenho.CAROUSEL);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;
		case "DATA VIEW" :
			pagina.setComponenteDesenho(IComponentesDesenho.DATAVIEW);
			pagina.setCursor(Cursor.HAND);
			pagina.setBtnBarComboContaineres(btnBarComboContaineres);
			break;
		}		
	}	
	
	@FXML
	protected void btnBarTextField(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.CAMPOTEXTO);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnBarNumberField(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.CAMPONUMERO);
		pagina.setCursor(Cursor.HAND);
	}	
	
	@FXML
	protected void btnBarButton(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.BOTAO);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnBarLabel(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.LABEL);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnRadioButton(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.RADIOBUTTON);
		pagina.setCursor(Cursor.HAND);
	}

	@FXML
	protected void btnBarCheckBox(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.CHECKBUTTON);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnBarCampoData(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.DATA);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnBarToggle(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.TOGGLE);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnTextArea(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.AREATEXTO);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnBarComboBox(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.COMBOBOX);
		pagina.setCursor(Cursor.HAND);
	}
	
	@FXML
	protected void btnBarArrow(ActionEvent event){
		btnBarComboContaineres.getSelectionModel().select(0);
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.ARROW);
		pagina.setCursor(Cursor.DEFAULT);
	}
	
	@FXML
	protected void btnPicture(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.PICTURE);
		pagina.setCursor(Cursor.DEFAULT);
	}
	
	@FXML
	protected void btnChart(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.CHART);
		pagina.setCursor(Cursor.DEFAULT);
	}	
	
	@FXML
	protected void btnGps(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.GPS);
		pagina.setCursor(Cursor.DEFAULT);
	}
	
	@FXML
	protected void btnPhoto(ActionEvent event) {
		Pagina pagina = (Pagina)topPaginacao.getScene().lookup("#paginaDesenhoAtual");
		pagina.setComponenteDesenho(IComponentesDesenho.PHOTO);
		pagina.setCursor(Cursor.DEFAULT);
	}		
	
	private PaginaContainer novaPagina(boolean carregandoProjeto) {
		int pgIndex = paginacao.getCurrentPageIndex();
		int pgCount = paginacao.getPageCount();
		
		if(carregandoProjeto) { //sobrescrever a primeira página (página zero)
			pgIndex = paginas.size() - 1;
		}
		
		paginaAtual = new Pagina();
		ScrollPane scrPane = new ScrollPane();
		scrPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);		
		scrPane.setContent(paginaAtual);
		scrPane.setStyle("-fx-border-radius: 10; -fx-background-radius: 10;");
		
		paginas.add(pgIndex + 1, scrPane);

		paginaAtual.setId(Pagina.ID_PAGINA_ATUAL);
		
		paginaAtual.setStyle(paginaAtual.getUnfocusedtStyle());
		
		PaginaContainer paginaContainer = new PaginaContainer();
		paginaContainer.paginaAtual = paginaAtual;
		projetoContainer.paginas.add(paginaContainer);
		paginaContainer.projetoPai = projetoContainer;		
		
		
		MouseClickEventsCanvas mouseEventsListener = new MouseClickEventsCanvas(paginaAtual, paginaContainer, detailsObjectsTab, avancadoObjectsTab, this); 
		
		paginaAtual.setOnMouseClicked(mouseEventsListener);
		paginaAtual.setOnMouseClickedHandler(mouseEventsListener);
		
//		KeyListenerCanvas keyListener = new KeyListenerCanvas(paginaAtual, projetoContainer);
//		mouseEventsListener.addSelectChangeListener(keyListener);
		mouseEventsListener.addSelectChangeListener(this);	
//		paginaAtual.addEventFilter(KeyEvent.KEY_PRESSED, keyListener);
		
		if(carregandoProjeto) { //sobrescrever a primeira página (página zero)
			pgCount = paginas.size();
		}

		paginacao.setPageCount(pgCount + 1);
		paginacao.setCurrentPageIndex(pgIndex + 1);

		if(carregandoProjeto) { //sobrescrever a primeira página (página zero)
			paginacao.setPageCount(paginas.size());
		}

		return paginaContainer;
	}
	
	@FXML
	protected void apagaPaginaAction(ActionEvent actionEvent) {
		final int pgIndex = paginacao.getCurrentPageIndex();
		final int pgCount = paginacao.getPageCount();

		if (pgCount > 1) {
			JFDialog dialog = new JFDialog();
			EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					if (pgCount > 1) {
						
						PaginaContainer paginaContainer = projetoContainer.paginas.get(pgCount - 1);
						for (ComponenteContainer compContainer : paginaContainer.componentes) {
							compContainer.paginaPai = null;
							for (ComponenteContainer compInterno : compContainer.componentesInternos) {
								compInterno.componentePai = null;
							}
							compContainer.componentesInternos.clear();
						}
						
						paginaContainer.projetoPai = null;
						projetoContainer.paginas.remove(paginaContainer);
						
						paginaAtual = null;
						paginas.remove(pgIndex);
						paginacao.setPageCount(pgCount - 1);
					}
				}
			};
			dialog.show("Tem certeza que deseja excluir a página atual?", "Sim", "Não",	act1);
		}
	}
	

	@Override
	public void carregaProjeto(Projeto projeto) {
		JazzForms.paginas.clear();
		DesenhaComponenteHandler desHandler = DesenhaComponenteHandler.getInstance();
		if (desHandler.getDetalhesTab() == null) {
			desHandler.setDetalhesTab(detailsObjectsTab);
		}
		if (desHandler.getAvancadoTab() == null) {
			desHandler.setAvancadoTab(avancadoObjectsTab);
		}
		
		//remove as paginas atuais
		paginas.clear();
		for (PaginaContainer paginaContainer : projetoContainer.paginas) {
			for (ComponenteContainer compContainer : paginaContainer.componentes) {
				compContainer.paginaPai = null;
				for (ComponenteContainer compInterno : compContainer.componentesInternos) {
					compInterno.componentePai = null;
				}
				compContainer.componentesInternos.clear();
			}
			
			paginaContainer.projetoPai = null;
		}
		projetoContainer.paginas.clear();
		paginaAtual = null;			
		
	    paginacao = new Pagination(1,0);
	    paginacao.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
	    paginacao.setPageFactory(new Callback<Integer, Node>() {
			
			@Override
			public Node call(Integer param) {
				if (paginaAtual != null) paginaAtual.setId("");
				ScrollPane atual = paginas.get(param);
				paginaAtual = (Pagina)atual.getContent();
				paginaAtual.setId("paginaDesenhoAtual");
				desenhaCanvas();
				return atual;
			}
		});
		
	    AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(paginacao, 10.0);
        AnchorPane.setRightAnchor(paginacao, 10.0);
        AnchorPane.setBottomAnchor(paginacao, 10.0);
        AnchorPane.setLeftAnchor(paginacao, 10.0);
        anchor.getChildren().addAll(paginacao);	    
	    
	    borderCanvas.setCenter(paginacao);
		
		//cria novas paginas
		
		projetoContainer = new ProjetoContainer();
		projetoContainer.descricao = projeto.getDescricao();
		projetoContainer.nome = projeto.getNome();
		projetoContainer.projetoId = projeto.getId();
		projetoContainer.aplicacao = projeto.getAplicacao();
		projetoContainer.versionControl = projeto.versionControl == null ? true : projeto.versionControl;
		
		projetoContainer.paginas = new ArrayList<PaginaContainer>();
		boolean firstPage = true;
		for(br.com.laminarsoft.jazzforms.persistencia.model.Pagina pagina : projeto.getPaginas()) {
			PaginaContainer pgc = novaPagina(firstPage);
			Pagina pg = pgc.paginaAtual;
			JazzForms.paginas.put(pg.getNome(), pg);
			JazzForms.paginas.remove(Pagina.ID_PAGINA_ATUAL);
			JazzForms.paginas.put(Pagina.ID_PAGINA_ATUAL, pg);
			
			pg.setBackgroundImage(pagina.getBackgroundImage());
			pg.setBackgroundColor(pagina.getBackgroundColor());
			pg.setDescricao(pagina.getDescricao());
			pg.setNome(pagina.getNome());
			pg.setPacoteCodigoCustomizado(pagina.getPacoteCodigoCustomizacao());
			pg.setPaginaModel(pagina);

			for (Component component : pagina.getContainer().getItems()) {
				ComponentWrapper wrapper = new ComponentWrapper();
				wrapper.componente = component;
				wrapper.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(component.getFieldType())[0];
				wrapper.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(component.getFieldType())[1];
				
				Pane pane = null;
				if (component.getDocked().equalsIgnoreCase("top")) {
					pane = (Pane)pg.getTop();
				} else if(component.getDocked().equalsIgnoreCase("bottom")) {
					pane = (Pane)pg.getBottom();
				} else if(component.getDocked().equalsIgnoreCase("center")) {
					pane = (Pane)pg.getCenter();
				}
				criaVisualComponent(pane, wrapper, pg, pagina.getContainer(), false);
			}
			firstPage = false;
		}
	}

	private DialogFactory.DialogRetVO criaVisualComponent(Pane pane, ComponentWrapper wrapper, IJFUIBackComponent parent, Container parentContainer, boolean form) {
		DialogFactory.DialogRetVO vo = DesenhaComponenteHandler.getInstance().criaCustomComponent(pane, wrapper, parent, parentContainer, form, -1, false);
		Component backcomp = ((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent();
		if (backcomp instanceof Container) {
			Container cont = (Container)backcomp;
			
			if(cont instanceof FormPanel) {// caso o componente seja um formulário, percorrer seu conteúdo
				CustomFormPanel frmPanel = (CustomFormPanel)((IControllerComponent) vo.controller).getBackComponent();
				Pane innerPane = (Pane)((ScrollPane)frmPanel.getCenter()).getContent();
				Pane topPane = (Pane)(frmPanel.getTop());
				Pane bottomPane = (Pane)frmPanel.getBottom();
				ComponentWrapper wrapperTmp = new ComponentWrapper();
//				frmPanel.setPrefHeight(backcomp.getJfxPreferedHeight());
				((Container) backcomp).getItems().removeAll(Collections.singleton(null));
				for(Component cmpTmp : ((Container) backcomp).getItems()) {
					wrapperTmp.componente = cmpTmp;
					wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[0];
					wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[1];
					
					String dockPos = cmpTmp.getDocked();
					if (dockPos.equalsIgnoreCase("top")) {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(topPane, wrapperTmp, frmPanel, cont, true);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(topPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					} else if(dockPos.equalsIgnoreCase("center")) {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(innerPane, wrapperTmp, frmPanel, cont, true);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					} else if(dockPos.equalsIgnoreCase("bottom")) {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(bottomPane, wrapperTmp, frmPanel, cont, true);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(bottomPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					} else {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(innerPane, wrapperTmp, frmPanel, cont, true);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					}
//					DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, frmPanel, cont, true, -1, false);
				}				
			} else if(cont instanceof ToolBar) {
				CustomToolBar toolBar = (CustomToolBar)((IControllerComponent) vo.controller).getBackComponent();
				ComponentWrapper wrapperTmp = new ComponentWrapper();
				((Container) backcomp).getItems().removeAll(Collections.singleton(null));
				for(Component cmpTmp : ((Container) backcomp).getItems()) {
					wrapperTmp.componente = cmpTmp;
					wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[0];
					wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[1];
					String dockPos = cmpTmp.getDocked();
					Pane paneTmp = null;
					if (dockPos.equalsIgnoreCase("left")) {
						paneTmp = (Pane)toolBar.getLeft();
					} else if (dockPos.equalsIgnoreCase("center")) {
						paneTmp = (Pane)toolBar.getCenter();
					} else if(dockPos.equalsIgnoreCase("right")) {
						paneTmp = (Pane)toolBar.getRight();
					}
					DesenhaComponenteHandler.getInstance().criaCustomComponent(paneTmp, wrapperTmp, toolBar, cont, false, -1, false);
				}
			} else if(cont instanceof FieldSet) {
				CustomFieldset cfSet = (CustomFieldset)((IControllerComponent)vo.controller).getBackComponent();
				Pane innerPane = (Pane)cfSet.getCenter();
				ComponentWrapper wrapperTmp = new ComponentWrapper();
//				cfSet.setPrefHeight(cont.getJfxPreferedHeight());
				for(Component cmpTmp : ((Container) backcomp).getItems()) {
					wrapperTmp.componente = cmpTmp;
					wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[0];
					wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[1];
//					criaVisualComponent(innerPane, wrapperTmp, cfSet, cont, false);
					DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, cfSet, cont, true, -1, false);
				}
			} else if(cont instanceof TitleBar) {
				CustomTitleBar ctBar = (CustomTitleBar)((IControllerComponent)vo.controller).getBackComponent();
				ComponentWrapper wrapperTmp = new ComponentWrapper();
				for(Component cmpTmp : ((Container) backcomp).getItems()) {
					wrapperTmp.componente = cmpTmp;
					wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[0];
					wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[1];
					String dockPos = cmpTmp.getDocked();
					Pane paneTmp = null;
					if (dockPos.equalsIgnoreCase("left")) {
						paneTmp = (Pane)ctBar.getLeft();
					} else if(dockPos.equalsIgnoreCase("right")) {
						paneTmp = (Pane)ctBar.getRight();
					}
					DesenhaComponenteHandler.getInstance().criaCustomComponent(paneTmp, wrapperTmp, ctBar, cont, false, -1, false);
				}				
			} else if(cont instanceof Carousel) {
				CustomCarousel cCar = (CustomCarousel)((IControllerComponent)vo.controller).getBackComponent();
				ComponentWrapper wrapperTmp = new ComponentWrapper();
				int pagina = 1;
				((Carousel) backcomp).getPaginas().removeAll(Collections.singleton(null));
//				cCar.setPrefHeight(cont.getJfxPreferedHeight());
				for(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tabTmp : ((Carousel) backcomp).getPaginas()) {
					Pane painelCarousel = null;
					if (pagina == 1) {
	                    painelCarousel = cCar.getPagina(pagina);
                    } else {
                    	painelCarousel = cCar.adicionaPaginaVisual();
                    }		
					tabTmp.getItems().removeAll(Collections.singleton(null));
					for (Component cmpTmp : tabTmp.getItems()) {
	                    wrapperTmp.componente = cmpTmp;
	                    wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[0];
	                    wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[1];
	                    criaVisualComponent(painelCarousel, wrapperTmp, cCar, cont, false);
                    }
					pagina++;
				}				
			} else if(cont instanceof TabPanel) {
				CustomTabpanel ctabPanel = (CustomTabpanel)((IControllerComponent)vo.controller).getBackComponent();
				ComponentWrapper wrapperTmp = new ComponentWrapper();
//				ctabPanel.setPrefHeight(cont.getJfxPreferedHeight());
				int pagina = 1;
				for(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tabTmp : ((TabPanel)backcomp).getTabs()) {
					br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button btn = new br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button();					
					btn.setDocked("center");
					btn.setUi("action-round");
					btn.setButtonText(tabTmp.getText());
					btn.setIconName(tabTmp.getImagem());
					
			        
			        wrapperTmp.componente = btn;
                    wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(btn.getFieldType())[0];
                    wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(btn.getFieldType())[1];
                    Pane paneTmp = (Pane)ctabPanel.getCenter();
                    
                    DialogFactory.DialogRetVO voBtn = DesenhaComponenteHandler.getInstance().criaCustomComponent(paneTmp, wrapperTmp, ctabPanel, cont, false, -1, false);
                    CustomButton cb = (CustomButton)((IControllerComponent)voBtn.controller).getBackComponent();
			        cb.setDefaultUnfocusedStyle("action-round");
			        cb.setPosicaoEnabled(false);
			        cb.setDockedEnabled(false);
			        cb.setUiEnabled(false);
			        
			        VBox vbox = new VBox(3);
			        vbox.getStyleClass().add("tab-vbox");
			        for (Component cmpTmp : tabTmp.getItems()) {
			        	wrapperTmp.componente = cmpTmp;
	                    wrapperTmp.tipoComponente = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[0];
	                    wrapperTmp.tipocomponenteForm = EquivalenciaComponentesUtil.getNomeClassesComponent(cmpTmp.getFieldType())[1];
	                    criaVisualComponent(vbox, wrapperTmp, ctabPanel, cont, false);
			        }
			        ctabPanel.adicionaNovaAba(cb, tabTmp, vbox);
				}
				if (ctabPanel.getPaineis().size() > 0) {
					((VBox)((BorderPane)pane.getParent()).getCenter()).getChildren().add(ctabPanel.getPaineis().values().iterator().next());
				}
			}
			
		}
		return vo;		
	}
	
	
	
	private CenterController.ComponenteContainer addNewComponenteContainerPagina(Node node, PaginaContainer paginaAtualContainer) {		
		ComponenteContainer componenteContainer = new ComponenteContainer();
		componenteContainer.componenteAtual = node;
		componenteContainer.paginaPai = paginaAtualContainer;
		paginaAtualContainer.componentes.add(componenteContainer);
		return componenteContainer;
	}	
	
	@Override
	public ProjetoContainer getProjetoComponentesContainer() {
		return projetoContainer;
	}
	
	@Override
	public void setProjetoComponentesContainer(ProjetoContainer projeto) {
		this.projetoContainer = projeto;
		//TODO remover o projeto anterior e carregar o projeto novo
	}


	@Override
	public void fireSelectedChange(List<IJFSelectedComponent> selected) {
		if (this.detailsObjectsTab.getContent() == null) {
			btnCancela.setDisable(true);
			btnConfirma.setDisable(true);			
		} else {
			btnCancela.setDisable(false);
			btnConfirma.setDisable(false);			
		}
	}
	
	

	protected void desenhaCanvas() {
		int multiploWidth = 2;
		int paddingRight = 20;
		int paddingBottom = 20;
		int paddingTop = 20;
		int paddingLeft = 20;
		
		double widthOriginal = paginas.get(paginacao.getCurrentPageIndex()).getMaxWidth();
		double heightOriginal = paginas.get(paginacao.getCurrentPageIndex()).getMaxHeight();
		
		double x = (borderCanvas.getWidth()/(multiploWidth*100))*(sliderWidth.getValue());
		double y = (borderCanvas.getHeight()/(multiploWidth*100))*(sliderHeight.getValue());
		
		double width = (borderCanvas.getWidth() - ((borderCanvas.getWidth()/multiploWidth)*sliderWidth.getValue())/sliderWidth.getMax());
		double height = (borderCanvas.getHeight() - (((borderCanvas.getHeight()/multiploWidth)*sliderHeight.getValue())/sliderHeight.getMax()));
		
		paginas.get(paginacao.getCurrentPageIndex()).setLayoutX(x+paddingLeft);
		paginas.get(paginacao.getCurrentPageIndex()).setLayoutY(y+paddingTop);
		paginas.get(paginacao.getCurrentPageIndex()).setMaxWidth(width-paddingRight);
		((Pagina)paginas.get(paginacao.getCurrentPageIndex()).getContent()).setMaxWidth(width-paddingRight);
		paginas.get(paginacao.getCurrentPageIndex()).setMaxHeight(height-paddingBottom);
	}	
	
	private class Staging {
		Node object;
		double positionX;
		double positionY;
		double grpWidth;
		double grpHeight;
	}
	
	public class ProjetoContainer {
		public List<PaginaContainer> paginas = new ArrayList<PaginaContainer>();
		public Long projetoId;
		public String nome = "";
		public String descricao = "";
		public Boolean aplicacao = true;
		public Boolean versionControl = true;
		public String versionControlDescription = "";
	}
	
	public class PaginaContainer {
		public Pagina paginaAtual;
		public ProjetoContainer projetoPai;
		public List<ComponenteContainer> componentes = new ArrayList<ComponenteContainer>();
	}
	
	public class ComponenteContainer {
		public Node componenteAtual;
		public PaginaContainer paginaPai;
		public ComponenteContainer componentePai;
		public List<ComponenteContainer> componentesInternos = new ArrayList<ComponenteContainer>();
	}	
	
	
	
	
}


