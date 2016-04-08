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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle;
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
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CustomToggle extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "x-button-selected";
	public static final String UNFOCUSED_STYLE_NAME = "x-button";
	public static final String UI_COMPONENT_ID = "CustomTextField";
	
	private Toggle toggle;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private IJFUIBackComponent paneParent;
	private ImageView imageView;
	private Label label;
	
	protected boolean deleted = false;
	
	public CustomToggle(String dumb) {
		super();
	}
	
	public CustomToggle() {
		super();
		
		imageView = new ImageView();
		toggle = new Toggle();
		toggle.setValue("0");
		toggle.setDocked("left");
		
		label = new Label();
		label.setText("Texto");
		toggle.setLabel("Texto");
		label.getStyleClass().add("x-label");
		label.setAlignment(Pos.CENTER_LEFT);
		this.setPadding(new Insets(3, 3, 3, 3));
		
		Image img = new Image(this.getClass().getResourceAsStream("../imgs/ToggleOff.png"));
		imageView.setImage(img);
		this.setCenter(imageView);
		this.setTop(label);
		toggle.setLabelAlign("top");
		toggle.setFieldId(IdentificadorUtil.getNextIdentificadorToggle());
		final CustomToggle atual = this;
		
		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);


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
						dialog.show("Um campo de texto não é container para outros componentes", "Ok");
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
    public IJFUIBackComponent getContainerPai() {
	    return this.paneParent;
    }
	
	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imgView) {
		this.imageView = imgView;
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
		Toggle toggle = (Toggle)componente;
		setLabel(toggle.getLabel());
		setToggled((toggle.getValue().equalsIgnoreCase("0") ? false : true));
		setLabelAlign(toggle.getLabelAlign());
		setUnfocusedStyle();
		this.toggle = toggle;
		setId(CustomToggle.UI_COMPONENT_ID);
    }
	
	public void setToggled(boolean toggled) {
		if(toggled) {
			Image img = new Image(this.getClass().getResourceAsStream("../imgs/ToggleOn.png"));
			this.imageView.setImage(img);
			toggle.setValue("1");
		} else {
			Image img = new Image(this.getClass().getResourceAsStream("../imgs/ToggleOff.png"));
			this.imageView.setImage(img);
			toggle.setValue("0");
		}
	}
	
	public void setLabel(String label) {
		this.label.setText(label);
		this.toggle.setLabel(label);
	}
	
	public void setLabelAlign(String align) {
		removeLabel();
		switch (align) {
		case "top" :
			this.setTop(label);
			toggle.setLabelAlign(align);
			this.setPrefWidth(90);
			label.setAlignment(Pos.CENTER_LEFT);
			break;
		case "right" : 
			this.setRight(label);
			toggle.setLabelAlign(align);
			this.setPrefWidth(130);
			label.setAlignment(Pos.CENTER_RIGHT);
			break;
		case "bottom" :
			this.setBottom(label);
			toggle.setLabelAlign(align);
			this.setPrefWidth(90);
			label.setAlignment(Pos.CENTER_LEFT);
			break;
		case "left" :
			this.setLeft(label);
			toggle.setLabelAlign(align);
			label.setAlignment(Pos.CENTER_LEFT);
			this.setPrefWidth(130);
			break;
		}
	}
	
	private void removeLabel() {
		switch (toggle.getLabelAlign()) {
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

	public Toggle getToggle() {
		return toggle;
	}

	public void setToggle(Toggle toggle) {
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
