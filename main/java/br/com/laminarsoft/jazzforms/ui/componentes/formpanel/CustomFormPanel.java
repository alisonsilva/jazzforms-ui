package br.com.laminarsoft.jazzforms.ui.componentes.formpanel;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
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
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.FormTargetDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.SourceDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.TargetDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;

public class CustomFormPanel extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "unfocused-style";
	public static final String UI_COMPONENT_ID = "CustomFormPanel";
	
	private FormPanel formPanel;
	private IJFUIBackComponent paneParent;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private FRMVBox centerBox;
	private FRMVBox topBox;
	private FRMVBox bottomBox;
	
	private ScrollPane scroll;
	
	protected boolean deleted = false;
	
	public CustomFormPanel() {
		super();
		formPanel = new FormPanel();
		topBox = new FRMVBox("top", 3);
		bottomBox = new FRMVBox("bottom", 3);
		
		formPanel.setFieldId(IdentificadorUtil.getNextIdentificadorFormpanel());
		centerBox = new FRMVBox("center", 3);		
		centerBox.setPadding(new Insets(10, 15, 10, 15));
		centerBox.setPrefWidth(500);
		centerBox.setPrefHeight(300);
		
		FormTargetDragEventHandler.getInstance(centerBox, this);
//		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);
		
		scroll = new ScrollPane();
		scroll.setPrefHeight(150);
		scroll.setContent(centerBox);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setStyle("-fx-background-color:transparent;");
		this.getStyleClass().add("formpanel-centerbox");
		this.setCenter(scroll);
		this.setTop(topBox);
		this.setBottom(bottomBox);
		
		final CustomFormPanel atual = this;

		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
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
				atual.requestFocus();
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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/formpanel/FormPanelDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/formpanel/FormPanelAvancado.fxml");
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
					desenhaComponente.desenhaComponente(centerBox, formPanel, atual);						
				}
				event.consume();
			}			
		});
	}
	
	
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if (container instanceof FormPanel) {
			ret = false;
		} else if(container instanceof Panel) {
			ret = true;
		} else if (container instanceof TabPanel) {
			ret = true;
		} else if (container instanceof Carousel) {
			ret = true;
		} else if (container instanceof FieldSet) {
			ret = true;
		}
		return ret;
    }



	@Override
	public Component getBackComponent() {
		return formPanel;
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
		if(centerBox.getChildren().contains(component.getParent())) {
			centerBox.getChildren().remove(component.getParent());
			this.formPanel.getItems().remove(component.getBackComponent());
		} else if (topBox.getChildren().contains(component.getParent())) {
			topBox.getChildren().remove(component.getParent());
			this.formPanel.getItems().remove(component.getBackComponent());
		} else if(bottomBox.getChildren().contains(component.getParent())) {
			bottomBox.getChildren().remove(component.getParent());
			this.formPanel.getItems().remove(component.getBackComponent());
		}
    }

	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
		this.formPanel.getItems().remove(component.getBackComponent());
		this.formPanel.getItems().add(component.getBackComponent());
		switch (posicao) {
		case "center" :
			centerBox.getChildren().add(component.getParent());
			break;
		case "top" :
			topBox.getChildren().add(component.getParent());
			component.getBackComponent().setDocked("top");
			break;
		case "bottom" :
			bottomBox.getChildren().add(component.getParent());
			component.getBackComponent().setDocked("bottom");
			break;
		}
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
		if (componente instanceof FormPanel) {
			this.formPanel = (FormPanel)componente;
			this.setPrefHeight(componente.getJfxPreferedHeight());
		} else {
			super.inicializaCustomComponent(componente);
		}
    }


	public FormPanel getFormPanel() {
		return formPanel;
	}

	public void setFormPanel(FormPanel formPanel) {
		this.formPanel = formPanel;
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
		// TODO Auto-generated method stub
		
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
