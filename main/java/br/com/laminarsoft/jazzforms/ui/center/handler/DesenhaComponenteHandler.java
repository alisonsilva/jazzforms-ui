package br.com.laminarsoft.jazzforms.ui.center.handler;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javax.enterprise.inject.Alternative;

import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IComponentesDesenho;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.button.CustomButton;
import br.com.laminarsoft.jazzforms.ui.componentes.camera.CustomCamera;
import br.com.laminarsoft.jazzforms.ui.componentes.carousel.CustomCarousel;
import br.com.laminarsoft.jazzforms.ui.componentes.chart.CustomChart;
import br.com.laminarsoft.jazzforms.ui.componentes.checkbox.CustomCheckbox;
import br.com.laminarsoft.jazzforms.ui.componentes.dataview.CustomDataview;
import br.com.laminarsoft.jazzforms.ui.componentes.datepicker.CustomDatepicker;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.ComponentWrapper;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.DragResizer;
import br.com.laminarsoft.jazzforms.ui.componentes.fieldset.CustomFieldset;
import br.com.laminarsoft.jazzforms.ui.componentes.formpanel.CustomFormPanel;
import br.com.laminarsoft.jazzforms.ui.componentes.formpanel.FRMVBox;
import br.com.laminarsoft.jazzforms.ui.componentes.gps.CustomGPS;
import br.com.laminarsoft.jazzforms.ui.componentes.numberfield.CustomNumberfield;
import br.com.laminarsoft.jazzforms.ui.componentes.select.CustomSelect;
import br.com.laminarsoft.jazzforms.ui.componentes.tabpanel.CustomTabpanel;
import br.com.laminarsoft.jazzforms.ui.componentes.textfield.CustomTextfield;
import br.com.laminarsoft.jazzforms.ui.componentes.titlebar.CustomTitleBar;
import br.com.laminarsoft.jazzforms.ui.componentes.toggle.CustomToggle;
import br.com.laminarsoft.jazzforms.ui.componentes.toolbar.CustomToolBar;
import br.com.laminarsoft.jazzforms.ui.componentes.toolbar.TBHBox;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

@Alternative
public class DesenhaComponenteHandler {
	
	private static DesenhaComponenteHandler INSTANCE = null;
	
	private Tab detalhesTab;
	private Tab avancadoTab;
	
	
	public static DesenhaComponenteHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DesenhaComponenteHandler();
		}
		return INSTANCE;
	}

	private int qtd = 0;
	
	private DesenhaComponenteHandler(){		
	}	
	
	public void setQtd(int qtd) {
		this.qtd = qtd;
	}
	
	public int getQtd() {
		return qtd;
	}
	
	
	public Tab getDetalhesTab() {
		return detalhesTab;
	}

	public void setDetalhesTab(Tab detalhesTab) {
		this.detalhesTab = detalhesTab;
	}

	public Tab getAvancadoTab() {
		return avancadoTab;
	}

	public void setAvancadoTab(Tab avancadoTab) {
		this.avancadoTab = avancadoTab;
	}

	/**
	 * Desenha o componente especificado na página
	 * @param pane O painel onde o componente será desenhado
	 * @param container Objecto de entidade Container onde o componente será armazenado 
	 * @param parentContainer Container que realmente abriga o componente
	 */
	public DialogFactory.DialogRetVO desenhaComponente(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory.DialogRetVO vo = null;
		switch (JazzForms.getPaginaDesenhoAtual().getComponenteDesenho()) {
		case IComponentesDesenho.COMBOBOX:
			vo = desenhaCombo(pane, container, parentContainer);
			break;
		case IComponentesDesenho.FIELDSET:
			vo = desenhaFieldSet(pane, container, parentContainer);
			break;
		case IComponentesDesenho.CAMPOTEXTO:
			vo = desenhaCampoTexto(pane, container, parentContainer);
			break;
		case IComponentesDesenho.CAMPONUMERO:
			vo = desenhaCampoNumerico(pane, container, parentContainer);
			break;
		case IComponentesDesenho.BOTAO:
			vo = desenhaButton(pane, container, parentContainer);
			break;
		case IComponentesDesenho.TOGGLE:
			vo = desenhaToggle(pane, container, parentContainer);
			break;			
		case IComponentesDesenho.TABPANEL:
			vo = desenhaTabPanel(pane, container, parentContainer);
			break;
		case IComponentesDesenho.CHECKBUTTON:
			vo = desenhaCheckbox(pane, container, parentContainer);
			break;
		case IComponentesDesenho.DATA:
			vo = desenhaDatePicker(pane, container, parentContainer);
			break;
		case IComponentesDesenho.AREATEXTO:
//			desenhaTextArea(pg, evt);
			break;
		case IComponentesDesenho.PICTURE:
//			desenhaPictureBox(pg, evt);
			break;
		case IComponentesDesenho.CHART:
			vo = desenhaChart(pane, container, parentContainer);
			break;
		case IComponentesDesenho.PHOTO :
			vo = desenhaPhoto(pane, container, parentContainer);
			break;
		case IComponentesDesenho.GPS :
			vo = desenhaGPS(pane, container, parentContainer);
			break;
		case IComponentesDesenho.DATAVIEW:
			vo = desenhaDataView(pane, container, parentContainer);
			break;
		case IComponentesDesenho.TITLEBAR :
			vo = desenhaTitleBar(pane, container, parentContainer);
			break;
		case IComponentesDesenho.TOOLBAR :
			vo = desenhaToolBar(pane, container, parentContainer);
			break;
		case IComponentesDesenho.FORMPANEL :
			vo = desenhaFormPanel(pane, container, parentContainer);
			break;		
		case IComponentesDesenho.CAROUSEL :
			vo = desenhaCarousel(pane, container, parentContainer);
			break;
		}
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaTitleBar(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/titlebar/TitleBar.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	if (pane instanceof VBox) {
	        		VBox top = (VBox)((BorderPane)parentContainer).getTop();
	        		top.getChildren().add((Node)vo.root);
	        	} else if (pane instanceof BorderPane) {
	        		((VBox)((BorderPane)pane).getTop()).getChildren().add((Node)vo.root);			
	        	}
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
				((Node)vo.root).addEventFilter(KeyEvent.KEY_PRESSED, JazzForms.getPaginaDesenhoAtual().getKeyListener());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um TitleBar nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}
	
	private DialogFactory.DialogRetVO desenhaDataView(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/dataview/Dataview.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	DragResizer.makeResizable((BorderPane)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
				((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");					
	        	if (pane instanceof BorderPane) {
					((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);	
	        	} else if(pane instanceof VBox) {
	        		((VBox)pane).getChildren().add((Node)vo.root);
	        	}	        	
	        	
//				((VBox)pane).getChildren().add((Node)vo.root);			
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
				((Node)vo.root).addEventFilter(KeyEvent.KEY_PRESSED, JazzForms.getPaginaDesenhoAtual().getKeyListener());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um DataView nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}	

	private DialogFactory.DialogRetVO desenhaToolBar(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/toolbar/ToolBar.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	if (pane instanceof BorderPane) {
	        		((VBox)((BorderPane)pane).getTop()).getChildren().add((Node)vo.root);
	        	} else if (parentContainer instanceof BorderPane) {
	        		((VBox)((BorderPane)parentContainer).getTop()).getChildren().add((Node)vo.root);
	        	} else {
	        		JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir uma ToolBar nesse container", "Ok");	        		
	        		return null;
	        	}
	        	
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir uma ToolBar nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaTabPanel(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/tabpanel/Tabpanel.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	if (((BorderPane)pane).getTop() == null) {
	        		VBox vbox = new VBox();
	        		((BorderPane)pane).setTop(vbox);
	        	}
				((VBox)((BorderPane)pane).getTop()).getChildren().add((Node)vo.root);			
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
				((Node)vo.root).addEventFilter(KeyEvent.KEY_PRESSED, JazzForms.getPaginaDesenhoAtual().getKeyListener());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir uma TabPanel nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaFieldSet(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/fieldset/Fieldset.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
				((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");
	        	if (pane instanceof BorderPane) {
					((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
	        	} else if(pane instanceof VBox) {
	        		((VBox)pane).getChildren().add((Node)vo.root);
	        	}
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um FieldSet nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}	

	private DialogFactory.DialogRetVO desenhaChart(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/chart/Chart.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
				((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");
	        	if (pane instanceof BorderPane) {
					((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
	        	} else if(pane instanceof VBox) {
	        		((VBox)pane).getChildren().add((Node)vo.root);
	        	}
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um component Gráfico nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {			
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
		}
		return vo;
	}		
	
	private DialogFactory.DialogRetVO desenhaCarousel(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/carousel/Carousel.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
				((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");
	        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	        	if (pane instanceof BorderPane) {
					((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
	        	} else if(pane instanceof VBox) {
	        		((VBox)pane).getChildren().add((Node)vo.root);
	        	}
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um Carousel nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaFormPanel(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/formpanel/FormPanel.fxml");
			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
            	((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");                	
	        	if (pane instanceof VBox) {
	                pane.getChildren().add((Node) vo.root);
                } else if (pane instanceof BorderPane) {
                	((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
                }
	        	final BorderPane criado = (BorderPane)vo.root;
	        	if (parentContainer instanceof CustomFieldset) {
	        		((JFBorderPane)parentContainer).heightProperty().addListener(new ChangeListener<Number>() {

						@Override
						public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
							criado.setPrefHeight(arg2.doubleValue());
						}	        			
	        		});
	        	}
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parentContainer);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
				((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
				container.getItems().add(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	        } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um FormPanel nesse container", "Ok");
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
		}
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaButton(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
	        vo = factory.loadDialog("componentes/button/Button.fxml");
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um botão nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}
	
	private DialogFactory.DialogRetVO desenhaCheckbox(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("componentes/checkbox/CheckboxForm.fxml");
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um checkbox nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaToggle(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			if(container instanceof FormPanel || container instanceof FieldSet) {
				vo = factory.loadDialog("componentes/toggle/ToggleForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/toggle/Toggle.fxml");
	        }			
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um toggle nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaDatePicker(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			if(container instanceof FormPanel ||
					container instanceof FieldSet) {
				vo = factory.loadDialog("componentes/datepicker/DatepickerForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/datepicker/Datepicker.fxml");
	        }
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um datepicker nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaPhoto(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			if(container instanceof FormPanel || container instanceof FieldSet) {
				vo = factory.loadDialog("componentes/camera/CameraForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/camera/Camera.fxml");
	        }
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um componente de câmera nesse container", "Ok");
            }
			
		} catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}
	
	private DialogFactory.DialogRetVO desenhaGPS(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			if(container instanceof FormPanel || container instanceof FieldSet) {
				vo = factory.loadDialog("componentes/gps/GPSForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/gps/GPS.fxml");
	        }
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um componente de GPS nesse container", "Ok");
            }
			
		} catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaCombo(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			if(container instanceof FormPanel || container instanceof FieldSet) {
				vo = factory.loadDialog("componentes/select/SelectForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/Select/Select.fxml");
	        }
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um seletor nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}	
	
	private DialogFactory.DialogRetVO desenhaCampoTexto(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
        DialogFactory.DialogRetVO vo = null;
		try {
	        if (container instanceof FormPanel || container instanceof FieldSet) {
	        	vo = factory.loadDialog("componentes/textfield/TextfieldForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/textfield/Textfield.fxml");
	        }
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um textfield nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}	
	
	
	private DialogFactory.DialogRetVO desenhaCampoNumerico(Pane pane, Container container, IJFUIBackComponent parentContainer) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
        DialogFactory.DialogRetVO vo = null;
		try {
	        if (container instanceof FormPanel || container instanceof FieldSet) {
	        	vo = factory.loadDialog("componentes/numberfield/NumberfieldForm.fxml");
	        } else {
	        	vo = factory.loadDialog("componentes/numberfield/Numberfield.fxml");
	        }
	        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
	        if (insersaoValida) {
	            pane.getChildren().add((Node) vo.root);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parentContainer);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
	            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
	            container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
            } else {
            	JFDialog dialog = new JFDialog();
            	dialog.show("Não é possível inserir um campo numérico nesse container", "Ok");
            }
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
			JazzForms.getPaginaDesenhoAtual().setComponenteDesenho(IComponentesDesenho.ARROW);
			JazzForms.getPaginaDesenhoAtual().setCursor(Cursor.DEFAULT);
			JazzForms.getPaginaDesenhoAtual().setUnfocused();
			if (JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres() != null) {
	            JazzForms.getPaginaDesenhoAtual().getBtnBarComboContaineres().getSelectionModel().select(0);
            }
        }
		return vo;
	}		
	
	/**
	 * Método utilizado para criar um novo componente de UI com base em um componente de negócio pré-existente.
	 *  
	 * @param pane É o container JFX que irá abrigar o Componente UI sendo criado
	 * @param wrapper Contém o componente de negócio sendo utilizado bem como os nomes de seu respectivo componente UI (ambos formulário e não formulário)
	 * @param parent É o componente UI pai do componente a ser criado (p.e. a CustomToolBar seria um parent para CustomButton)
	 * @param container É o objeto Container de negócio ao qual o componente pertence (p.e. a ToolBar contém um Button - ToolBar é o container)
	 * @param form O componente de negócio será do tipo formulário? true caso seja, false caso não seja.
	 * @param posicao A posição dentro da lista de filhos de Pane. Caso seja o último da fila, posição deverá ser -1
	 * @param insertItemContainer Flag indicando se o Componente sendo criado será inserido no container passado. No geral sempre será, mas no momento da recuperação das informações salvas, os itens já 
	 * deverão estar inseridos a priori
	 * @return DialogFactory.DialogRetVO Componente de interface montado a partir dos parâmetros passados. Caso haja um erro na montagem do componente, retornará null.
	 */
	public DialogFactory.DialogRetVO criaCustomComponent(Pane pane, ComponentWrapper wrapper, IJFUIBackComponent parent, Container container, boolean form, int posicao, boolean insertItemContainer) {
		DialogFactory.DialogRetVO vo = null;
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		switch(wrapper.tipoComponente) {
		case "CustomButton" :
			try {
		        vo = factory.loadDialog("componentes/button/Button.fxml");
		        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		        	pane.getChildren().add((Node)vo.root);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomButton)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
	                    	container.getItems().add(posicao, ((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
	                    } else {
	                    	container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
	                    }
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um botão nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }			
			break;
		case "CustomToggle" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/toggle/Toggle.fxml");
				} else {
					vo = factory.loadDialog("componentes/toggle/ToggleForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
					((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomToggle)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
							container.getItems().add(posicao, ((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
							container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um botão nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;
		case "CustomSelect" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/select/Select.fxml");
				} else {
					vo = factory.loadDialog("componentes/select/SelectForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomSelect)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
							container
									.getItems()
									.add(posicao, ((IJFUIBackComponent) ((IControllerComponent) vo.controller)
											.getBackComponent())
											.getBackComponent());
						} else {
							container
							.getItems()
							.add(((IJFUIBackComponent) ((IControllerComponent) vo.controller)
									.getBackComponent())
									.getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um botão nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;			
		case "CustomDatepicker" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/datepicker/Datepicker.fxml");
				} else {
					vo = factory.loadDialog("componentes/datepicker/DatepickerForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomDatepicker)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao, ((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um botão nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;			
		case "CustomTextfield" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/textfield/Textfield.fxml");
				} else {
					vo = factory.loadDialog("componentes/textfield/TextfieldForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomTextfield)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
							container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
							container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um botão nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;	
		case "CustomNumberfield" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/numberfield/Numberfield.fxml");
				} else {
					vo = factory.loadDialog("componentes/numberfield/NumberfieldForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomNumberfield)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um campo numérico nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;				
		case "CustomCamera" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/camera/Camera.fxml");
				} else {
					vo = factory.loadDialog("componentes/camera/CameraForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomCamera)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um component câmera nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;							
		case "CustomGPS" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/gps/GPS.fxml");
				} else {
					vo = factory.loadDialog("componentes/gps/GPSForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomGPS)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um component GPS nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;							
		case "CustomCheckbox" :
			try {
		        vo = null;
		        if (!form) {
					vo = factory.loadDialog("componentes/checkbox/Checkbox.fxml");
				} else {
					vo = factory.loadDialog("componentes/checkbox/CheckboxForm.fxml");
				}
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (posicao < 0) {
						pane.getChildren().add((Node) vo.root);
					} else {
						pane.getChildren().add(posicao, (Node)vo.root);
					}
		        	if (pane instanceof TBHBox) {
		        		TBHBox tbhbox = (TBHBox)pane;
		        		wrapper.componente.setDocked(tbhbox.position);
		        	} else if (pane instanceof FRMVBox) {
		        		wrapper.componente.setDocked(((FRMVBox)pane).position);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomCheckbox)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um checkbox nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;	
		case "CustomFormPanel" :
			try {
		        vo = factory.loadDialog("componentes/formpanel/FormPanel.fxml");
		        
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (pane instanceof VBox) {
		        		pane.getChildren().add((Node) vo.root);
		        	} else if(pane instanceof BorderPane) {
		        		if(container.getDocked().equalsIgnoreCase("top")) {
		        			((VBox)((BorderPane)pane).getTop()).getChildren().add((Node)vo.root);
		        		} else if(container.getDocked().equalsIgnoreCase("center")) {
		        			((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
		        		} else if(container.getDocked().equalsIgnoreCase("bottom")) {
		        			((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
		        		}
		        	}
					
					final BorderPane criado = (BorderPane)vo.root;
		        	if (parent instanceof CustomFieldset) {
		        		((JFBorderPane)parent).heightProperty().addListener(new ChangeListener<Number>() {

							@Override
							public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
								criado.setPrefHeight(arg2.doubleValue());
							}	        			
		        		});
		        	}					
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomFormPanel)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
					Region reg = (Region)vo.root;
					reg.setMinHeight(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().getJfxPreferedHeight());
					reg.setPrefHeight(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().getJfxPreferedHeight());
		        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um form panel nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;
		case "CustomToolBar" :
			try {
		        vo = factory.loadDialog("componentes/toolbar/ToolBar.fxml");
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
					if(wrapper.componente.getDocked().equals("center")) {
						if (pane instanceof VBox) {
							pane.getChildren().add((Node) vo.root);
						} else if (pane instanceof BorderPane) {
							((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
						}
					} else if(wrapper.componente.getDocked().equals("top")) {
						if(pane instanceof BorderPane) {
							((VBox)((BorderPane)pane).getTop()).getChildren().add((Node)vo.root);
						} else if(pane instanceof VBox) {
							((VBox)pane).getChildren().add((Node)vo.root);
						}
					} else if(wrapper.componente.getDocked().equals("bottom")) {
						if(pane instanceof BorderPane) {
							((VBox)((BorderPane)pane).getBottom()).getChildren().add((Node)vo.root);
						} else if(pane instanceof VBox) {
							((VBox)pane).getChildren().add((Node)vo.root);
						}
					}
		            
					((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomToolBar)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um toolbar nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;
		case "CustomFieldset" :
			try {
		        vo = factory.loadDialog("componentes/fieldset/Fieldset.fxml");
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (pane instanceof BorderPane) {
						((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);			
		        	} else if(pane instanceof VBox) {
		        		((VBox)pane).getChildren().add((Node)vo.root);
		        	}
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomFieldset)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
					Region reg = (Region)vo.root;
					reg.setMinHeight(0);
					reg.setPrefHeight(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().getJfxPreferedHeight());
		        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um fieldset nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;
		case "CustomDataview" :
			try {
		        vo = factory.loadDialog("componentes/dataview/Dataview.fxml");
				boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (pane instanceof BorderPane) {
						((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);			
		        	} else if(pane instanceof VBox) {
		        		((VBox)pane).getChildren().add((Node)vo.root);
		        	}	        	
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setContainerPai(parent);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
		            ((IDetalhesComponentes) ((IControllerComponent) vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomDataview)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
		            if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
					Region reg = (Region)vo.root;
					reg.setMinHeight(0);
					reg.setPrefHeight(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().getJfxPreferedHeight());
		        	DragResizer.makeResizable((BorderPane)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
	            } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um dataview nesse container", "Ok");
	            }
	        } catch (IOException e) {
		        e.printStackTrace();
	        }
			break;
		case "CustomCarousel" :
			try {
				vo = factory.loadDialog("componentes/carousel/Carousel.fxml");
				
		        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	if (pane instanceof BorderPane) {
						((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
						((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");
		        	} else if(pane instanceof VBox) {
		        		((VBox)pane).getChildren().add((Node)vo.root);
		        	}
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parent);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomCarousel)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
					if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
					Region reg = (Region)vo.root;
					reg.setMinHeight(0);
					reg.setPrefHeight(((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().getJfxPreferedHeight());
		        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} 
			break;
		case "CustomTitleBar" :
			try {
				vo = factory.loadDialog("componentes/titlebar/TitleBar.fxml");
				
		        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	
		        	if(wrapper.componente.getDocked().equals("center")) {
						if (pane instanceof VBox) {
							pane.getChildren().add((Node) vo.root);
						} else if (pane instanceof BorderPane) {
							((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
						}
					} else if(wrapper.componente.getDocked().equals("top")) {
						if(pane instanceof BorderPane) {
							((VBox)((BorderPane)pane).getTop()).getChildren().add((Node)vo.root);
						} else if(pane instanceof VBox) {
							((VBox)pane).getChildren().add((Node)vo.root);
						}
					} else if(wrapper.componente.getDocked().equals("bottom")) {
						if(pane instanceof BorderPane) {
							((VBox)((BorderPane)pane).getBottom()).getChildren().add((Node)vo.root);
						} else if(pane instanceof VBox) {
							((VBox)pane).getChildren().add((Node)vo.root);
						}
					} else if (pane instanceof VBox) {
		        		((VBox)pane).getChildren().add((Node)vo.root);
		        	}
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parent);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomTitleBar)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
					if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "CustomChart" :
			try {
				vo = factory.loadDialog("componentes/chart/Chart.fxml");
				
		        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
		        	DragResizer.makeResizable((Region)vo.root, ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent());
					((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).getBackComponent().setDocked("center");
		        	if (pane instanceof BorderPane) {
						((VBox)((BorderPane)pane).getCenter()).getChildren().add((Node)vo.root);
		        	} else if(pane instanceof VBox) {
		        		((VBox)pane).getChildren().add((Node)vo.root);
		        	}
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parent);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
					((CustomChart)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
					if(insertItemContainer){
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
					}
		        } else {
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Não é possível inserir um component Gráfico nesse container", "Ok");
	            }
			} catch (IOException e) {
				e.printStackTrace();
			} 
			break;
		case "CustomTabpanel" :
			try {
				vo = factory.loadDialog("componentes/tabpanel/Tabpanel.fxml");
				
		        boolean insersaoValida = ((IJFUIBackComponent)((IControllerComponent)vo.controller).getBackComponent()).isInsersaoValida(container);
		        if (insersaoValida) {
					((VBox)pane).getChildren().add((Node)vo.root);			
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setContainerPai(parent);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setDetalhesTab(detalhesTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setAvancadoTab(avancadoTab);
					((IDetalhesComponentes)((IControllerComponent)vo.controller).getBackComponent()).setPagina(JazzForms.getPaginaDesenhoAtual());
		            ((CustomTabpanel)((IControllerComponent) vo.controller).getBackComponent()).inicializaCustomComponent(wrapper.componente);
					if (insertItemContainer) {
	                    if (posicao >= 0) {
		                    container.getItems().add(posicao,((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						} else {
		                    container.getItems().add(((IJFUIBackComponent) ((IControllerComponent) vo.controller).getBackComponent()).getBackComponent());
						}
                    }
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;			
		}
		return vo;
	}
}
