package br.com.laminarsoft.jazzforms.ui.componentes.toolbar;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Panel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TabPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
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

public class CustomToolBar extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent, EventTarget {
	public static final String FOCUSED_STYLE_NAME = "focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "unfocused-style";
	public static final String UI_COMPONENT_ID = "CustomTitleBar";
	
	private ToolBar toolBar;
	private Label label;
	private IJFUIBackComponent paneParent;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private TBHBox leftBox;
	private TBHBox rightBox;
	private TBHBox centerBox;
	
	protected boolean deleted = false;
	
	public CustomToolBar() {
		super();
		leftBox = new TBHBox("left", 3);
		rightBox = new TBHBox("right", 3);
		centerBox = new TBHBox("center", 3);
		leftBox.setPadding(new Insets(10, 15, 10, 15));
		rightBox.setPadding(new Insets(10, 15, 10, 15));
		centerBox.setPadding(new Insets(10, 15, 10, 15));
		centerBox.setAlignment(Pos.CENTER);
		label = new Label();
		label.setText("TOOL BAR");
		label.getStyleClass().add("x-label");
		centerBox.getChildren().add(label);
		this.setLeft(leftBox);
		this.setRight(rightBox);
		this.setCenter(centerBox);
		
		TargetDragEventHandler.getInstance(leftBox, false, this);
		TargetDragEventHandler.getInstance(rightBox, false, this);
		TargetDragEventHandler.getInstance(centerBox, false, this);
		
		toolBar = new ToolBar();
		toolBar.setDocked("top");
		toolBar.setTitle(label.getText());
		toolBar.setFieldId(IdentificadorUtil.getNextIdentificadorToolbar());
		
		final CustomToolBar atual = this;
		
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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/toolbar/ToolBarDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/toolbar/ToolBarAvancado.fxml");
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
					desenhaComponente.desenhaComponente(leftBox, toolBar, atual);						
				}
				event.consume();
			}			
		});
	}
	
	
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if (container instanceof FieldSet) {
			ret = false;
		} else if (container instanceof FormPanel) {
			ret = true;
		} else if(container instanceof Panel) {
			ret = true;
		} else if (container instanceof TabPanel) {
			ret = true;
		} else if (container instanceof Carousel) {
			ret = true;
		}
		return ret;
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
	public Component getBackComponent() {
		return toolBar;
	}
	
	@Override
	public Node getCustomComponent() {
		return this;
	}

	@Override
    public void removeComponent(JFBorderPane component) {
		Component backComponent = component.getBackComponent();
		if(leftBox.getChildren().contains(component.getParent())) {
			leftBox.getChildren().remove(component.getParent());
			this.toolBar.getItems().remove(backComponent);
		} else if(rightBox.getChildren().contains(component.getParent())) {
			rightBox.getChildren().remove(component.getParent());
			this.toolBar.getItems().remove(backComponent);
		} else if(centerBox.getChildren().contains(component.getParent())) {
			centerBox.getChildren().remove(component.getParent());
			this.toolBar.getItems().remove(backComponent);
		}
    }

	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
		this.toolBar.getItems().remove(component.getBackComponent());
		this.toolBar.getItems().add(component.getBackComponent());
		switch (posicao) {
		case "left" :
			leftBox.getChildren().add(component.getParent());
			component.getBackComponent().setDocked("left");
			break;
		case "right" :
			rightBox.getChildren().add(component.getParent());
			component.getBackComponent().setDocked("right");
			break;
		case "center" :
			centerBox.getChildren().add(component.getParent());
			component.getBackComponent().setDocked("center");
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
	    this.toolBar = (ToolBar)componente;
	    this.label.setText(this.toolBar.getTitle());
    }

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(ToolBar toolBar) {
		this.toolBar = toolBar;
	}

	public void setLabelText(String text) {
		label.setText(text);
		toolBar.setTitle(text);
	}
	
	public String getLabelText() {
		return label.getText();
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
