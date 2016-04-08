package br.com.laminarsoft.jazzforms.ui.componentes.carousel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Panel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TabPanel;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.center.handler.DesenhaComponenteHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.TargetDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CustomCarousel extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "fieldset-focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "fieldset-unfocused-style";
	public static final String UI_COMPONENT_ID = "CustomCarousel";
	
	private Carousel carousel;
	private IJFUIBackComponent paneParent;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private CARVBox centerBoxAtual;
	private CARVBox bottomBox;
	private CARVBox topBox;
	private BorderPane painelAtual;
	
	private HBox leftButtonArea;
	private HBox rightButtonArea;
	private HBox upButtonArea;
	private HBox downButtonArea;
	
	protected boolean deleted = false;
	
	private List<BorderPane> paineis = new ArrayList<BorderPane>();	
	private int posicaoTab = 0;
	private Label lblPosicao;
	
	public CustomCarousel() {
		super();
		
		BorderPane painel = new BorderPane();
		
		centerBoxAtual = new CARVBox("center", 3);
		centerBoxAtual.setId("fieldset-centerbox");
		this.getStyleClass().add(UNFOCUSED_STYLE_NAME);
		this.setPadding(new Insets(5, 5, 5, 5));
		
		bottomBox = new CARVBox("bottom", 3);
		topBox = new CARVBox("top", 3);
		
		TargetDragEventHandler.getInstance(topBox, false, this);
		TargetDragEventHandler.getInstance(centerBoxAtual, false, this);

		
		painel.setTop(topBox);
		painel.setBottom(bottomBox);
		
		carousel = new Carousel();
		carousel.setFieldId(IdentificadorUtil.getNextIdentificadorCarousel());
		painel.setCenter(centerBoxAtual);
		painelAtual = painel;
		carousel.setJfxPreferedHeight(this.getPrefHeight());
		this.setCenter(painel);
		
		br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tab = new br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab();
		paineis.add(posicaoTab, painel);
		carousel.getPaginas().add(tab);
		
		final CustomCarousel atual = this;

		this.setOnMouseEntered(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mouseEvent) {
				atual.setFocusedStyle();
			}			
		});
		
		this.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				IJFSelectedComponent source = (IJFSelectedComponent)mouseEvent.getSource();
				if (!JazzForms.getPaginaDesenhoAtual().containsSelectedNode(source)) {
					atual.setUnfocusedStyle();
				}
			}
		});
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				int compDesenho = JazzForms.getPaginaDesenhoAtual().getComponenteDesenho();
				
				if (compDesenho == 0) {
					atual.requestFocus();
					if (event.isControlDown() && event.getClickCount() == 1) {
						if (paginaPai.containsSelectedNode(atual)) {
							paginaPai.removeSelectedNode(atual);
						} else {
							paginaPai.addSelectedNode(atual);
						}
						detalhesTab.setContent(null);
						avancadoTab.setContent(null);
					} else if (!event.isControlDown() && event.getClickCount() == 1) {
						paginaPai.clearSelectedNodes();
						paginaPai.addSelectedNode(atual);
						DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
						try {
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/carousel/CarouselDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/carousel/CarouselAvancado.fxml");
							avancadoTab.setContent(null);
							avancadoTab.setContent((Node) vo.root);

							rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizAvancado(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);
							
							ISelectedChangeListener listener = (ISelectedChangeListener)JazzForms.CENTER_CONTROLLER;
							listener.fireSelectedChange(paginaPai.getSelectedNodes());
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
						}
					} 
				} else {	
					DesenhaComponenteHandler desenhaComponente = DesenhaComponenteHandler.getInstance();
					desenhaComponente.setDetalhesTab(detalhesTab);
					desenhaComponente.setAvancadoTab(avancadoTab);
					DialogFactory.DialogRetVO vo = desenhaComponente.desenhaComponente(painelAtual, carousel, atual);
					if(vo != null) {
						if(vo.root instanceof BorderPane && ((BorderPane) vo.root).getCenter() instanceof JFBorderPane) {
							JFBorderPane pane = (JFBorderPane)((BorderPane) vo.root).getCenter();
							atual.carousel.getPaginas().get(posicaoTab).getItems().add(pane.getBackComponent());
							atual.carousel.getItems().clear();
						}
					}
				}
				event.consume();
			}			
		});
	}
	
	void movePaginaDireita() {
		if(posicaoTab < (paineis.size() - 1)) {
			posicaoTab++;
			this.lblPosicao.setText((posicaoTab + 1) + "/" + paineis.size());
			painelAtual = (BorderPane)paineis.get(posicaoTab);
			this.setCenter(painelAtual);
		}
 	}
	
	void movePaginaEsquerda() {
		if (posicaoTab > 0) {
			posicaoTab--;
			this.lblPosicao.setText((posicaoTab + 1) + "/" + paineis.size());
			painelAtual = (BorderPane)paineis.get(posicaoTab);
			this.setCenter(painelAtual);
		}
	}
	
	public int getPosicaoTab() {
		return posicaoTab;
	}
	
	public BorderPane adicionaPagina() {
		br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tab = new br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab();
		centerBoxAtual = new CARVBox("center", 3);
		topBox = new CARVBox("top", 3);
		bottomBox = new CARVBox("top", 3);
		centerBoxAtual.setId("fieldset-centerbox");
		BorderPane pn = new BorderPane();
		pn.setCenter(centerBoxAtual);
		pn.setTop(topBox);
		pn.setBottom(bottomBox);
		painelAtual = pn;
		
		paineis.add(++posicaoTab, painelAtual);
		this.setCenter(pn);
		carousel.getPaginas().add(posicaoTab, tab);
        this.lblPosicao.setText((posicaoTab + 1) + "/" + paineis.size());
        return pn;
	}
	
	public BorderPane adicionaPaginaVisual() {
		centerBoxAtual = new CARVBox("center", 3);
		topBox = new CARVBox("top", 3);
		bottomBox = new CARVBox("top", 3);
		centerBoxAtual.setId("fieldset-centerbox");
		
		painelAtual = new BorderPane();
		painelAtual.setCenter(centerBoxAtual);
		painelAtual.setTop(topBox);
		painelAtual.setBottom(bottomBox);
		
		paineis.add(++posicaoTab, painelAtual);
		this.setCenter(painelAtual);
        this.lblPosicao.setText((posicaoTab + 1) + "/" + paineis.size());
        return painelAtual;
	}	
	
	public void removePagina() {
		if (paineis.size() > 1) {
	        paineis.remove(posicaoTab);
	        carousel.getPaginas().remove(posicaoTab);
	        posicaoTab = 0;
	        painelAtual = (BorderPane)paineis.get(0);
	        this.setCenter(painelAtual);
	        this.lblPosicao.setText("1/" + paineis.size());
        } else {
        	JFDialog dialog = new JFDialog();
        	dialog.show("Não é possível excluir a única página do carousel", "Ok");
        }
	}
	
	/**
	 * Recupera a página de interesse representada por um VBox
	 * @param posicao A posição da página desejada. A página inicial é a de número 1
	 * @return A página de interesse representada por um VBox
	 */
	public BorderPane getPagina(int posicao) {
		BorderPane ret = null;
		if(paineis.size() >= posicao) {
			ret = paineis.get(posicao - 1);
		}
		return ret;
	}
	
	public void setButtonsAreas(HBox leftButtonArea, HBox rightButtonArea, HBox upButtonArea, HBox downButtonArea) {
		this.leftButtonArea = leftButtonArea;
		this.rightButtonArea = rightButtonArea;
		this.upButtonArea = upButtonArea;
		this.downButtonArea = downButtonArea;
	}
	
	public void setLblPosicao(Label lblPosicao) {
		this.lblPosicao = lblPosicao;
	}
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if (container instanceof FormPanel) {
			ret = true;
		} else if(container instanceof Panel) {
			ret = true;
		} else if(container instanceof TabPanel) {
			ret = true;
		}
		return ret;
    }

	

	@Override
	public Component getBackComponent() {
		return carousel;
	}
	
	@Override
	public Node getCustomComponent() {
		return this;
	}

	
	@Override
    public boolean isDeleted() {
	    return deleted;
    }



	@Override
    public void setDeleted(boolean deleted) {
		this.deleted = deleted;
    }



	@Override
    public void removeComponent(JFBorderPane component) {
		this.carousel.getPaginas().get(posicaoTab).getItems().remove(component.getBackComponent());
		this.carousel.getItems().remove(component.getBackComponent());

		((VBox)painelAtual.getTop()).getChildren().remove(component.getParent());
//		((VBox)this.getTop()).getChildren().remove(component.getParent());
		((VBox)painelAtual.getBottom()).getChildren().remove(component.getParent());
//		((VBox)this.getBottom()).getChildren().remove(component.getParent());
		((VBox)painelAtual.getCenter()).getChildren().remove(component.getParent());
//		((VBox)this.getCenter()).getChildren().remove(component.getParent());
    }

	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
		switch(posicao) {
		case  "top" : 
			((VBox)painelAtual.getTop()).getChildren().add(component.getParent());
			break;
		case "bottom" :
			((VBox)painelAtual.getBottom()).getChildren().add(component.getParent());
			break;
		case "center" :
			((VBox)painelAtual.getCenter()).getChildren().add(component.getParent());
			break;			
		}
		component.getBackComponent().setDocked(posicao);
		this.carousel.getPaginas().get(posicaoTab).getItems().add(component.getBackComponent());
    }

	@Override
	public void setFocusedStyle() {
		if (!this.getStyleClass().contains(FOCUSED_STYLE_NAME)) {
			if (this.getStyleClass().contains(UNFOCUSED_STYLE_NAME)) {
				this.getStyleClass().remove(UNFOCUSED_STYLE_NAME);
			}
			this.getStyleClass().add(FOCUSED_STYLE_NAME);
		}
	}

	@Override
	public void setUnfocusedStyle() {
		if(this.getStyleClass().contains(FOCUSED_STYLE_NAME)) {
			this.getStyleClass().remove(FOCUSED_STYLE_NAME);
			this.getStyleClass().add(UNFOCUSED_STYLE_NAME);
		}
	}
	
	@Override
    public void inicializaCustomComponent(Component componente) {
		this.carousel = (Carousel)componente;
		this.setPrefHeight(componente.getJfxPreferedHeight());
    }


	public Carousel getCarousel() {
		return carousel;
	}

	public void setCarousel(Carousel carousel) {
		this.carousel = carousel;
	}

	@Override
    public void setContainerPai(IJFUIBackComponent containerPai) {
		this.paneParent = containerPai;
    }
	
	
	@Override
    public IJFUIBackComponent getContainerPai() {
	    return this.paneParent;
    }

	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		if(this.carousel.getDirection().equalsIgnoreCase("horizontal")) {
			this.leftButtonArea.setVisible(true);
			this.rightButtonArea.setVisible(true);
			this.upButtonArea.setVisible(false);
			this.downButtonArea.setVisible(false);
		} else {
			this.leftButtonArea.setVisible(false);
			this.rightButtonArea.setVisible(false);
			this.upButtonArea.setVisible(true);
			this.downButtonArea.setVisible(true);
		}
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDetalhesTab(Tab detalhesTab) {
		this.detalhesTab = detalhesTab;
	}

	@Override
	public void setAvancadoTab(Tab avancadoTab) {
		this.avancadoTab = avancadoTab;
	}

	@Override
	public void setPagina(Pagina pagina) {
		this.paginaPai = pagina;
	}
	
	
}
