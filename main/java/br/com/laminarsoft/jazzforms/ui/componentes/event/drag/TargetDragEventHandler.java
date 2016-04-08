package br.com.laminarsoft.jazzforms.ui.componentes.event.drag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Tab;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TabPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TitleBar;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.center.handler.DesenhaComponenteHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.button.CustomButton;
import br.com.laminarsoft.jazzforms.ui.componentes.carousel.CustomCarousel;
import br.com.laminarsoft.jazzforms.ui.componentes.fieldset.CustomFieldset;
import br.com.laminarsoft.jazzforms.ui.componentes.formpanel.CustomFormPanel;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.tabpanel.CustomTabpanel;
import br.com.laminarsoft.jazzforms.ui.componentes.titlebar.CustomTitleBar;
import br.com.laminarsoft.jazzforms.ui.componentes.toolbar.CustomToolBar;
import br.com.laminarsoft.jazzforms.ui.componentes.util.EquivalenciaComponentesUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;

public class TargetDragEventHandler {
	
	public static final String STYLE_DRAG_ENTERED = "drag-entered";
	public static final String STYLE_DRAG_EXITED = "drag-exited";
	public static final String ID_COMPONENT = "component";
	public static final DataFormat COMPONENT_DATA_FORMAT = new DataFormat("component");
	
	public static TargetDragEventHandler getInstance(final Node target, boolean formulario, JFBorderPane parent) {		
		return new TargetDragEventHandler(target, formulario, parent);		
	}
	
	private TargetDragEventHandler(final Node target, final boolean formulario, final JFBorderPane parent) {

		target.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				if(event.getGestureSource() != target && event.getDragboard().hasContent(COMPONENT_DATA_FORMAT)) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}
				event.consume();
            }			
		});
		
		target.setOnDragEntered(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				if(event.getGestureSource() != target && event.getDragboard().hasContent(COMPONENT_DATA_FORMAT)) {
					if (target.getStyleClass().contains(STYLE_DRAG_EXITED)) {
	                    target.getStyleClass().remove(STYLE_DRAG_EXITED);
                    }
					target.getStyleClass().add(STYLE_DRAG_ENTERED);
				}
				event.consume();
            }
		});
		
		target.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				if(target.getStyleClass().contains(STYLE_DRAG_ENTERED)) {
					target.getStyleClass().remove(STYLE_DRAG_ENTERED);
				}
				target.getStyleClass().add(STYLE_DRAG_EXITED);
				event.consume();
            }			
		});
		
		target.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {

			@Override
            public void handle(MouseDragEvent event) {
				event.consume();
            }
			
		});
		
		target.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				DialogFactory.DialogRetVO vo = null;
				if(event.getTransferMode() == TransferMode.MOVE && event.getDragboard().hasContent(COMPONENT_DATA_FORMAT)) {
					
					Pagina pag = findPaginaParent(parent);
					Container cont = (Container)parent.getBackComponent();
					boolean insereNovoComponentContainer = true;
					if(cont instanceof Carousel) {
						Carousel car = (Carousel)cont;
						CustomCarousel ccar = (CustomCarousel)parent;
						int idxTab = ccar.getPosicaoTab();
						Tab tab = car.getPaginas().get(idxTab);
						List<Component> lstComp = new ArrayList<Component>();
						lstComp.add(((ComponentWrapper)event.getDragboard().getContent(COMPONENT_DATA_FORMAT)).componente);
						tab.setItems(lstComp);
						insereNovoComponentContainer = false;
					}
					vo = criaVisualComponent((Pane)target, (ComponentWrapper)event.getDragboard().getContent(COMPONENT_DATA_FORMAT), parent, ((Container)parent.getBackComponent()), formulario, insereNovoComponentContainer);
					
//					DesenhaComponenteHandler desenhaHandler = DesenhaComponenteHandler.getInstance();
//					vo = desenhaHandler.criaCustomComponent((Pane)target, 
//							(ComponentWrapper)event.getDragboard().getContent(COMPONENT_DATA_FORMAT), 
//							parent,
//							((Container)parent.getBackComponent()),
//							formulario, -1, true);
					if (pag != null) {
						pag.setAtualizada(true);
					}
				}
				event.setDropCompleted(vo != null);
				
				event.consume();
            }	
			
			private Pagina findPaginaParent(Pane pane) {
				Pagina pagina = null;
				if (pane instanceof Pagina) {
					pagina = (Pagina)pane;
				} else {
					return findPaginaParent((Pane)pane.getParent());
				}
				return pagina;
			}
		});
	}
	
	private DialogFactory.DialogRetVO criaVisualComponent(Pane pane, ComponentWrapper wrapper, IJFUIBackComponent parent, Container parentContainer, boolean form, boolean insertIntemContainer) {
		DialogFactory.DialogRetVO vo = DesenhaComponenteHandler.getInstance().criaCustomComponent(pane, wrapper, parent, parentContainer, form, -1, insertIntemContainer);
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
							criaVisualComponent(topPane, wrapperTmp, frmPanel, cont, true, false);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(topPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					} else if(dockPos.equalsIgnoreCase("center")) {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(innerPane, wrapperTmp, frmPanel, cont, true, false);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					} else if(dockPos.equalsIgnoreCase("bottom")) {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(bottomPane, wrapperTmp, frmPanel, cont, true, false);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(bottomPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					} else {
						if(cmpTmp instanceof Container) {
							criaVisualComponent(innerPane, wrapperTmp, frmPanel, cont, true, false);
						} else {
							DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, frmPanel, cont, true, -1, false);
						}
					}
//					DesenhaComponenteHandler.getInstance().criaCustomComponent(innerPane, wrapperTmp, frmPanel, cont, true, -1, false);
				}				
			}  else if(cont instanceof ToolBar) {
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
	                    criaVisualComponent(painelCarousel, wrapperTmp, cCar, cont, false, false);
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
	                    criaVisualComponent(vbox, wrapperTmp, ctabPanel, cont, false, false);
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
}
