package br.com.laminarsoft.jazzforms.ui.componentes.tabpanel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
import br.com.laminarsoft.jazzforms.ui.componentes.IComponentesDesenho;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.button.CustomButton;
import br.com.laminarsoft.jazzforms.ui.componentes.button.ILabelChangeListener;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.DragResizer;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;

public class CustomTabpanel extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent, EventTarget {
	public static final String FOCUSED_STYLE_NAME = "focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "unfocused-style";
	public static final String UI_COMPONENT_ID = "CustomTitleBar";
	
	private TabPanel tabPanel;
	private IJFUIBackComponent paneParent;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private HBox centerBox;
	
	private Map<CustomButton, VBox> paineis = new HashMap<CustomButton, VBox>();
	
	protected boolean deleted = false;
	
	public CustomTabpanel() {
		super();
		centerBox = new HBox(3);
		centerBox.setPadding(new Insets(10, 15, 10, 15));
		centerBox.setAlignment(Pos.CENTER);
		this.setCenter(centerBox);
		
		tabPanel = new TabPanel();
		tabPanel.setDocked("top");
		tabPanel.setFieldId(IdentificadorUtil.getNextIdentificadorTabpanel());
		final CustomTabpanel atual = this;
		
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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/tabpanel/TabpanelDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/tabpanel/TabpanelAvancado.fxml");
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
					DialogFactory.DialogRetVO vo = desenhaComponente.desenhaComponente(centerBox, tabPanel, atual);
					if ( vo.root instanceof BorderPane && ((BorderPane) vo.root).getCenter() instanceof CustomButton) {
	                    final CustomButton cb = (CustomButton) ((BorderPane) vo.root).getCenter();
	                    cb.setDefaultUnfocusedStyle("action-round");
	                    cb.setPosicaoEnabled(false);
	                    cb.setDockedEnabled(false);
	                    cb.setUiEnabled(false);
	                    cb.getBackComponent().setDocked("center");
	                    cb.getBackComponent().setUi("action-round");
	                    
	                    
	                    final br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tb = new br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab();
	                    tb.hsId = cb.getBackComponent().hashCode();
	                    tb.setText(cb.getLabelText());
	                    tb.setImagem(cb.getBackComponent().getUi());
	                    tabPanel.getTabs().add(tb);
	                    
	                    cb.addLabelChangeListener(new ILabelChangeListener() {
							
							@Override
							public void fireLabelChange(String label) {
								tb.setText(label);
							}
						});

	                    
	                    final VBox box = new VBox(3);
	                    box.getStyleClass().add("tab-vbox");
	            		
	            		Pane boxParent = ((Pane)((BorderPane)paneParent).getCenter());
	            		box.setPrefSize(boxParent.getPrefWidth(), 200);
	            		
	            		boxParent.heightProperty().addListener(new ChangeListener<Number>() {

							@Override
                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
								box.setPrefHeight(newValue.doubleValue());
                            }	            			
						});
	                    
	                    paineis.put(cb, box);
	                    
	                    cb.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

							@Override
                            public void handle(MouseEvent event) {
								Pane boxParent = ((Pane)((BorderPane)paneParent).getCenter());
								boxParent.getChildren().clear();
								boxParent.getChildren().add(paineis.get(cb));
                            }
	                    	
	                    });
	                    
	                    box.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
                            public void handle(MouseEvent event) {
								int compDesenho = JazzForms.getPaginaDesenhoAtual().getComponenteDesenho();
								if (compDesenho != 0) {
									DesenhaComponenteHandler desenhaComponente = DesenhaComponenteHandler.getInstance();
									desenhaComponente.setDetalhesTab(detalhesTab);
									desenhaComponente.setAvancadoTab(avancadoTab);
									DialogFactory.DialogRetVO voDesenhado = desenhaComponente.desenhaComponente(box, tabPanel, atual);
									if (compDesenho == IComponentesDesenho.FORMPANEL) {
										DragResizer.makeResizable((Region)voDesenhado.root, tabPanel);
                                    } 
									
									if (voDesenhado.root instanceof BorderPane && ((BorderPane) voDesenhado.root).getCenter() instanceof JFBorderPane) {
										JFBorderPane pane = (JFBorderPane)((BorderPane) voDesenhado.root).getCenter();
										tb.getItems().add(pane.getBackComponent());
										atual.tabPanel.getItems().clear();
									}
								}
                            }	                    	
	                    });
                    }
				}
				event.consume();
			}			
		});
	}
	
	/**
	 * Esse método é utilizado para reconstrução da Tab. 
	 * @param cb O botão que irá aparecer na barra de Tabs. Esse botão já deve ter sido completamente criado 
	 * @param tb O objeto tab que foi armazenado com todas as informações referentes à tab
	 * @param box Esse é o conteúdo da tab em formato visual. Uma VBox já preenchida com o conteúdo da tab
	 */
	public void adicionaNovaAba(final CustomButton cb, final br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tb, final VBox box) {
		final CustomTabpanel atual = this;
		
//        final CustomButton cb = customButton;
//        cb.setDefaultUnfocusedStyle("action-round");
//        cb.setPosicaoEnabled(false);
//        cb.setDockedEnabled(false);
//        cb.setUiEnabled(false);
//        cb.getBackComponent().setDocked("center");
//        cb.getBackComponent().setUi("action-round");
        
//        final br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab tb = new br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab();
//        tb.setId(cb.getBackComponent().hashCode());
//        tb.setText(cb.getLabelText());
//        tb.setImagem(cb.getBackComponent().getUi());
//        tabPanel.getTabs().add(tb);
        
//        final VBox box = new VBox(3);
//        box.getStyleClass().add("tab-vbox");
		
		Pane boxParent = ((Pane)((BorderPane)paneParent).getCenter());
		box.setPrefSize(boxParent.getPrefWidth(), 200);
		
		boxParent.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				box.setPrefHeight(newValue.doubleValue());
            }	            			
		});
        
        paineis.put(cb, box);
        
        cb.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
            public void handle(MouseEvent event) {
				Pane boxParent = ((Pane)((BorderPane)paneParent).getCenter());
				boxParent.getChildren().clear();
				boxParent.getChildren().add(paineis.get(cb));
            }
        	
        });
        
        cb.addLabelChangeListener(new ILabelChangeListener() {
			
			@Override
			public void fireLabelChange(String label) {
				tb.setText(label);
			}
		});
        
        
        box.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
            public void handle(MouseEvent event) {
				int compDesenho = JazzForms.getPaginaDesenhoAtual().getComponenteDesenho();
				if (compDesenho != 0) {
					DesenhaComponenteHandler desenhaComponente = DesenhaComponenteHandler.getInstance();
					desenhaComponente.setDetalhesTab(detalhesTab);
					desenhaComponente.setAvancadoTab(avancadoTab);
					DialogFactory.DialogRetVO voDesenhado = desenhaComponente.desenhaComponente(box, tabPanel, atual);
					if (compDesenho == IComponentesDesenho.FORMPANEL) {
						DragResizer.makeResizable((Region)voDesenhado.root, tabPanel);
                    } 
					
					if (voDesenhado.root instanceof BorderPane && ((BorderPane) voDesenhado.root).getCenter() instanceof JFBorderPane) {
						JFBorderPane pane = (JFBorderPane)((BorderPane) voDesenhado.root).getCenter();
						tb.getItems().add(pane.getBackComponent());
						atual.tabPanel.getItems().clear();
					}
				}
            }	                    	
        });
	}
	
	
	
	public Map<CustomButton, VBox> getPaineis() {
		return paineis;
	}

	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if (container instanceof FieldSet) {
			ret = false;
		} else if (container instanceof FormPanel) {
			ret = false;
		} else if(container instanceof ToolBar) {
			ret = false;
		}else if(container instanceof Panel) {
			ret = true;
		} else if (container instanceof TabPanel) {
			ret = false;
		} else if (container instanceof Carousel) {
			ret = false;
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
		return tabPanel;
	}
	
	@Override
	public Node getCustomComponent() {
		return this;
	}

	@Override
    public void removeComponent(JFBorderPane component) {
		if(centerBox.getChildren().contains(component.getParent())) {
			centerBox.getChildren().remove(component.getParent());
			this.tabPanel.removeComponent(component.getBackComponent());
			if (component instanceof CustomButton) {
				paineis.remove(component);
				Pane boxParent = ((Pane)((BorderPane)paneParent).getCenter());
				boxParent.getChildren().clear();				
			}
		}
		
		if (component instanceof CustomButton) {
			paineis.remove(component);
			Pane boxParent = ((Pane)((BorderPane)paneParent).getCenter());
			boxParent.getChildren().clear();				
		}		
    }

	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
		this.tabPanel.getItems().remove(component.getBackComponent());
		this.tabPanel.getItems().add(component.getBackComponent());
		switch (posicao) {
//		case "left" :
//			leftBox.getChildren().add(component.getParent());
//			break;
//		case "right" :
//			rightBox.getChildren().add(component.getParent());
//			break;
		case "center" :
			centerBox.getChildren().add(component.getParent());
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
	    this.tabPanel = (TabPanel)componente;
	    this.setPrefHeight(componente.getJfxPreferedHeight());
    }

	public TabPanel getTabPanel() {
		return tabPanel;
	}

	public void setTabPanel(TabPanel tabPanel) {
		this.tabPanel = tabPanel;
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
