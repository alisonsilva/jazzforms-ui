package br.com.laminarsoft.jazzforms.ui.componentes.toggle;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFFormComponent;
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
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CustomToggleForm extends CustomToggle implements IJFFormComponent {
	public static final String FOCUSED_STYLE_NAME = "x-button-selected";
	public static final String UNFOCUSED_STYLE_NAME = "x-button";
	public static final String UI_COMPONENT_ID = "CustomTextFieldForm";
	
	private Toggle toggle;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private IJFUIBackComponent paneParent;
	private ImageView imgView;
	private Label label;
	
	private HBox leftBox;
	private HBox centerBox;
	
	public CustomToggleForm() {
		super("dumb");
		
		leftBox = new HBox();
		leftBox.setId("labelBox");
		leftBox.setPrefWidth(IJFFormComponent.LEFT_PREF_WIDTH);
		leftBox.setMaxWidth(IJFFormComponent.LEFT_PREF_WIDTH);
		
		leftBox.setAlignment(Pos.CENTER_RIGHT);
		leftBox.setPadding(new Insets(0, 20, 0, 0));
		
		centerBox = new HBox();
		centerBox.setId("fieldBox");
//		centerBox.setPrefWidth(IJFFormComponent.CENTER_PREF_WIDTH);
		centerBox.setAlignment(Pos.CENTER_LEFT);
		
		label = new Label();
		label.setId("formLabel");

		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f, 0.25f));
		label.setEffect(ds);
		
		imgView = new ImageView();
		
		Image img = new Image(this.getClass().getResourceAsStream("../imgs/ToggleOff.png"));
		imgView.setImage(img);
		
		toggle = new Toggle();
		toggle.setDocked("center");
		toggle.setLabel("Label:");
		toggle.setValue("0");
		toggle.setFieldId(IdentificadorUtil.getNextIdentificadorToggle());
		
		label.setText("Label:");
		label.setWrapText(true);
		leftBox.getChildren().add(label);
		this.setLeft(leftBox);
		
		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);
		

		this.setPadding(new Insets(3, 3, 3, 3));
		
		centerBox.getChildren().add(imgView);
		this.setCenter(centerBox);
		
		final CustomToggleForm atual = this;

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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/toggle/ToggleDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/toggle/ToggleAvancado.fxml");
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
						dialog.show("Um campo toggle não é container para outros componentes", "Ok");
					}
				}
				event.consume();
			}			
		});
	}
	

	@Override
    public void setSetupTabs() throws IOException {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();

		DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/toggle/ToggleDetalhes.fxml");
		detalhesTab.setContent(null);
		detalhesTab.setContent((Node) vo.root);

		RootController rootCntrl = new RootController();
		rootCntrl.controller = (IControllerDetalhes) vo.controller;
		rootCntrl.root = (Parent) vo.root;
		paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
		((IControllerDetalhes) vo.controller).setCustomComponent(this);

		vo = factory.loadDialog("componentes/toggle/ToggleAvancado.fxml");
		avancadoTab.setContent(null);
		avancadoTab.setContent((Node) vo.root);

		rootCntrl = new RootController();
		rootCntrl.controller = (IControllerDetalhes) vo.controller;
		rootCntrl.root = (Parent) vo.root;
		paginaPai.getOnMouseClickedHandler().setRaizAvancado(rootCntrl);
		((IControllerDetalhes) vo.controller).setCustomComponent(this);
		
		ISelectedChangeListener listener = (ISelectedChangeListener)JazzForms.CENTER_CONTROLLER;
		listener.fireSelectedChange(paginaPai.getSelectedNodes());	    
    }

	@Override
    public void setLabelText(String label) {
		this.label.setText(label);
		this.toggle.setLabel(label);
    }

	public void setLabel(String label) {
		this.label.setText(label);
		this.toggle.setLabel(label);
	}
	
	public void setLabelAlign(String align) {
		this.toggle.setLabel(align);
	}	

	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if(container instanceof ToolBar) {
			ret = true;
		} else if(container instanceof FormPanel) {
			ret = true;
		} else if(container instanceof FieldSet) {
			ret = true;
		}
	    return ret;
    }

	@Override
	public void setToggled(boolean toggled) {
		if(toggled) {
			Image img = new Image(this.getClass().getResourceAsStream("../imgs/ToggleOn.png"));
			this.imgView.setImage(img);
			toggle.setValue("1");
		} else {
			Image img = new Image(this.getClass().getResourceAsStream("../imgs/ToggleOff.png"));
			this.imgView.setImage(img);
			toggle.setValue("0");
		}		
	}
	
	@Override
    public void inicializaCustomComponent(Component componente) {
		Toggle toggle = (Toggle)componente;
		setLabel(toggle.getLabel());
		setToggled((toggle.getValue().equalsIgnoreCase("0") ? false : true));
		setLabelAlign(toggle.getLabelAlign());
		setUnfocusedStyle();
		this.toggle = toggle;
		setId(CustomToggle.UI_COMPONENT_ID);
    }	

	@Override
	public Component getBackComponent() {
		return toggle;
	}
	
	@Override
	public Node getCustomComponent() {
		return this;
	}
	
	@Override
    public void setContainerPai(IJFUIBackComponent containerPai) {
		this.paneParent = containerPai;
		FormTargetDragEventHandler.getInstance(this.getParent(), (JFBorderPane)this.paneParent, this);
    }

	@Override
    public IJFUIBackComponent getContainerPai() {
	    return this.paneParent;
    }
	
	public ImageView getImgView() {
		return imgView;
	}

	public void setImgView(ImageView imgView) {
		this.imgView = imgView;
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

	public Toggle getToggle() {
		return toggle;
	}

	public void setDatePicker(Toggle toggle) {
		this.toggle = toggle;
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
