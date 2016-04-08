package br.com.laminarsoft.jazzforms.ui.componentes.fieldset;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import br.com.laminarsoft.jazzforms.ui.componentes.formpanel.FRMVBox;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;

public class CustomFieldset extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "fieldset-focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "fieldset-unfocused-style";
	public static final String UI_COMPONENT_ID = "CustomFieldset";
	
	private FieldSet fieldSet;
	private IJFUIBackComponent paneParent;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private FRMVBox centerBox;
	private Label titleLabel;
	private Label instructionsLabel;
	
	protected boolean deleted = false;
	
	public CustomFieldset() {
		super();
		
		centerBox = new FRMVBox("center", 3);		
		this.getStyleClass().add(UNFOCUSED_STYLE_NAME);
		this.setPadding(new Insets(5, 5, 5, 5));
		centerBox.setId("fieldset-centerbox");
		
		fieldSet = new FieldSet();
		fieldSet.setFieldId(IdentificadorUtil.getNextIdentificadorFieldset());
		this.setCenter(centerBox);
		fieldSet.setJfxPreferedHeight(this.getPrefHeight());
		final CustomFieldset atual = this;

		FormTargetDragEventHandler.getInstance(centerBox, this);		
		
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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/fieldset/FieldsetDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/fieldset/FieldsetAvancado.fxml");
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
					desenhaComponente.desenhaComponente(centerBox, fieldSet, atual);
				}
				event.consume();
			}			
		});
	}
	
	
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if (container instanceof FormPanel) {
			ret = true;
		} else if(container instanceof Panel) {
			ret = false;
		} else if(container instanceof TabPanel) {
			ret = false;
		} else if(container instanceof Carousel) {
			ret = false;
		}
		return ret;
    }



	@Override
	public Component getBackComponent() {
		return fieldSet;
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
			this.fieldSet.getItems().remove(component.getBackComponent());
		} 		
    }

	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
		this.fieldSet.getItems().remove(component.getBackComponent());
		this.fieldSet.getItems().add(component.getBackComponent());
		switch (posicao) {
		case "center" :
			centerBox.getChildren().add(component.getParent());
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
		this.fieldSet = (FieldSet)componente;
		this.setPrefHeight(componente.getJfxPreferedHeight());
		this.titleLabel.setText(fieldSet.getTitle());
		this.instructionsLabel.setText(fieldSet.getInstructions());
    }


	public FieldSet getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(FieldSet fieldSet) {
		this.fieldSet = fieldSet;
	}

	public void setTitle(String text) {
		fieldSet.setTitle(text);
		titleLabel.setText(text);
	}
	
	public void setTitleLabel(Label label) {
		this.titleLabel = label;
	}
	
	public void setInstructionsLabel(Label label) {
		this.instructionsLabel = label;
		this.instructionsLabel.setText(label.getText());
	}
	
	public String getTitle() {
		return fieldSet.getTitle();
	}
	
	public void setInstruction(String instruction) {
		fieldSet.setInstructions(instruction);
		instructionsLabel.setText(instruction);
	}
	
	public String getInstruction() {
		return fieldSet.getInstructions();
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
