package br.com.laminarsoft.jazzforms.ui.componentes.select;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Option;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Text;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.SourceDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.TargetDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.toggle.CustomToggle;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CustomSelect extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "x-button-selected";
	public static final String UNFOCUSED_STYLE_NAME = "x-button";
	public static final String UI_COMPONENT_ID = "CustomTextField";
	
	private Select select;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private IJFUIBackComponent paneParent;
	private ComboBox<Option> comboBox;
	private Label label;
	
	protected boolean deleted = false;
	
	public CustomSelect(String dumb) {
		super();
	}
	
	public CustomSelect() {
		super();
		
		comboBox = new ComboBox<Option>();
		select = new Select();
		select.setDocked("left");
		
		label = new Label("Texto");
		select.setLabel("Texto");
		select.setLabelAlign("top");
		select.setFieldId(IdentificadorUtil.getNextIdentificadorSelect());
		label.getStyleClass().add("x-label");
		label.setAlignment(Pos.CENTER_LEFT);
		comboBox.setPrefWidth(80);
		this.setPadding(new Insets(3, 3, 3, 3));
		
		this.setCenter(comboBox);
		this.setTop(label);
		
		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);
		
		final CustomSelect atual = this;

		this.setOnMouseEntered(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mouseEvent) {
				atual.setFocusedStyle();
				mouseEvent.consume();
			}			
		});
		
		this.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				IJFSelectedComponent source = (IJFSelectedComponent)mouseEvent.getSource();
				if (!JazzForms.getPaginaDesenhoAtual().containsSelectedNode(source)) {
					atual.setUnfocusedStyle();
					mouseEvent.consume();
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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/select/SelectDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/select/SelectAvancado.fxml");
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
					} else {
						JFDialog dialog = new JFDialog();
						dialog.show("Um campo de seleção não é container para outros componentes", "Ok");
					}
				}
				event.consume();
			}			
		});
	}
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if(container instanceof ToolBar) {
			ret = true;
		}
	    return ret;
    }

	public void setLabel(String label) {
		this.label.setText(label);
		this.select.setLabel(label);
	}
	
	public void setLabelAlign(String align) {
		removeLabel();
		switch (align) {
		case "top" :
			this.setTop(label);
			select.setLabelAlign(align);
			this.setPrefWidth(90);
			label.setAlignment(Pos.CENTER_LEFT);
			break;
		case "right" : 
			this.setRight(label);
			select.setLabelAlign(align);
			this.setPrefWidth(130);
			label.setAlignment(Pos.CENTER_RIGHT);
			break;
		case "bottom" :
			this.setBottom(label);
			select.setLabelAlign(align);
			this.setPrefWidth(90);
			label.setAlignment(Pos.CENTER_LEFT);
			break;
		case "left" :
			this.setLeft(label);
			select.setLabelAlign(align);
			label.setAlignment(Pos.CENTER_LEFT);
			this.setPrefWidth(130);
			break;
		}
	}
	
	private void removeLabel() {
		switch (select.getLabelAlign()) {
		case "top" :
			this.setTop(null);
			break;
		case "right" : 
			this.setRight(null);
			break;
		case "bottom" :
			this.setBottom(null);
			break;
		case "left" :
			this.setLeft(null);
			break;
		}
	}	
	

	@Override
	public Component getBackComponent() {
		return select;
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
    public void setContainerPai(IJFUIBackComponent containerPai) {
		this.paneParent = containerPai;
    }

	@Override
    public IJFUIBackComponent getContainerPai() {
	    return this.paneParent;
    }
	
	public ComboBox<Option> getComboBox() {
		return comboBox;
	}

	public void setComboBox(ComboBox<Option> comboBox) {
		this.comboBox = comboBox;
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
		Select select = (Select)componente;
		setLabel(select.getLabel());
		setLabelAlign(select.getLabelAlign());
		setUnfocusedStyle();
		this.select = select;
		comboBox.getItems().addAll(this.select.getOptions());
		setId(CustomToggle.UI_COMPONENT_ID);
    }

	public Text getText() {
		return select;
	}

	public void setSelect(Select select) {
		this.select = select;
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
