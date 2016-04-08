package br.com.laminarsoft.jazzforms.ui.componentes.camera;

import java.io.IOException;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Camera;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Text;
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

public class CustomCameraForm extends CustomCamera implements IJFFormComponent {
	public static final String FOCUSED_STYLE_NAME = "x-button-selected";
	public static final String UNFOCUSED_STYLE_NAME = "x-button";
	public static final String UI_COMPONENT_ID = "CustomTextFieldForm";
	
	private Camera camera;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private IJFUIBackComponent paneParent;
	private Button button;
	private Label label;
	
	private HBox leftBox;
	private HBox centerBox;
	
	public CustomCameraForm() {
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
		
		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);
		
		label = new Label();
		label.setId("formLabel");

		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f, 0.25f));
		label.setEffect(ds);
		
		button = new Button();
		AwesomeDude.setIcon(button, AwesomeIcon.CAMERA, "16px", ContentDisplay.CENTER);
		
		camera = new Camera();
		camera.setDocked("center");
		camera.setLabel("Foto");
		camera.setFieldId(IdentificadorUtil.getNextIdentificadorCamera());
		
		label.setText("0 fotos de " + camera.getQuantity());
		label.setWrapText(true);
		leftBox.getChildren().add(label);
		this.setLeft(leftBox);
		

		this.setPadding(new Insets(3, 3, 3, 3));
		
		centerBox.getChildren().add(button);
		this.setCenter(centerBox);
		
		final CustomCameraForm atual = this;

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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/camera/CameraDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/camera/CameraAvancado.fxml");
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
						dialog.show("Um campo camera não é container para outros componentes", "Ok");
					}
				}
				event.consume();
			}			
		});
	}
	

	@Override
    public void setSetupTabs() throws IOException {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();

		DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/camera/CameraDetalhes.fxml");
		detalhesTab.setContent(null);
		detalhesTab.setContent((Node) vo.root);

		RootController rootCntrl = new RootController();
		rootCntrl.controller = (IControllerDetalhes) vo.controller;
		rootCntrl.root = (Parent) vo.root;
		paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
		((IControllerDetalhes) vo.controller).setCustomComponent(this);

		vo = factory.loadDialog("componentes/camera/CameraAvancado.fxml");
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
    }

	public void setLabel(String label) {
		this.label.setText(label);
		this.camera.setLabel(label);
	}
	
	public void setLabelAlign(String align) {
		//this.text.setLabelAlign(align);
	}	

	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if(container instanceof ToolBar) {
			ret = true;
		} else if(container instanceof FormPanel) {
			ret = true;
		} 
	    return ret;
    }

	@Override
    public void inicializaCustomComponent(Component componente) {
		Camera text = (Camera)componente;
		setLabel(text.getLabel());
		setUnfocusedStyle();
		this.camera = text;
		setId(CustomCamera.UI_COMPONENT_ID);
		detalhesTab.setContent(null);
		avancadoTab.setContent(null);
    }
	
	@Override
	public Component getBackComponent() {
		return camera;
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
	
	public Button getButton() {
		return button;
	}

	public void setButton(Button calendar) {
		this.button = calendar;
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

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
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
