package br.com.laminarsoft.jazzforms.ui.componentes.button;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Icon;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TabPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TitleBar;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFIconComponent;
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

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class CustomButton extends JFBorderPane implements IDetalhesComponentes, IJFIconComponent, IJFSelectedComponent, Serializable {
	public static final long serialVersionUID = 1121l;
	
	public static final String FOCUSED_STYLE_NORMAL = "x-button-normal-focused";
	public static final String FOCUSED_STYLE_ROUND = "x-button-round-focused";
	public static final String FOCUSED_STYLE_CONFIRM = "x-button-confirm-focused";
	public static final String FOCUSED_STYLE_CONFIRM_ROUND = "x-button-confirm-round-focused";	
	public static final String FOCUSED_STYLE_ACTION = "x-button-action-focused";
	public static final String FOCUSED_STYLE_DECLINE = "x-button-decline-focused";
	public static final String FOCUSED_STYLE_DECLINE_ROUND = "x-button-decline-round-focused";
	public static final String FOCUSED_STYLE_ACTION_ROUND = "x-button-action-round-focused";
	
	public static final String UNFOCUSED_STYLE_NORMAL = "x-button-normal";
	public static final String UNFOCUSED_STYLE_ROUND = "x-button-round";
	public static final String UNFOCUSED_STYLE_CONFIRM = "x-button-confirm";
	public static final String UNFOCUSED_STYLE_CONFIRM_ROUND = "x-button-confirm-round";
	public static final String UNFOCUSED_STYLE_ACTION = "x-button-action";
	public static final String UNFOCUSED_STYLE_ACTION_ROUND = "x-button-action-round";
	public static final String UNFOCUSED_STYLE_DECLINE = "x-button-decline";
	public static final String UNFOCUSED_STYLE_DECLINE_ROUND = "x-button-decline-round";
	public static final String UI_COMPONENT_ID = "CustomButton";
	
	public static final int DEFAULT_WIDTH = 60;
	public static final int DEFAULT_HEIGHT = 24;
	
	private String defaultUnfocusedStyle = UNFOCUSED_STYLE_NORMAL;
	private String defaultFocusedStyle = FOCUSED_STYLE_NORMAL;
	private String defaultLabel = "x-label-normal";
	
	private Button button;
	private Label label;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;	
	private IJFUIBackComponent paneParent;
	
	protected boolean deleted = false;
	
	private boolean uiEnabled = true;
	private boolean posicaoEnabled = true;
	private boolean dockedEnabled = true;
	private boolean iconClsEnabled = true;
	
	private List<ILabelChangeListener> labelListeners = new ArrayList<ILabelChangeListener>();
	
	public CustomButton() {
		super();
		label = new Label();
		label.setText("Label");
		label.getStyleClass().add(defaultLabel);
		this.setCenter(label);
		
		button = new Button();
		button.setDocked("left");
		button.setButtonText(label.getText());
		button.setFieldId(IdentificadorUtil.getNextIdentificadorButton());
		
		button.setUi("normal");
		
		setButtonWidth();
	
		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);
		
		final CustomButton atual = this;
		
		if (button.getIcon() != null) {
			setIcon(button.getIcon());
		}

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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/button/ButtonDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/button/ButtonAvancado.fxml");
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
						dialog.show("Um botão não é container para outros componentes", "Ok");
					}
				}
				event.consume();
			}			
		});
	}
	
	
	@Override
    public void setIcon(Icon icon) {
		if (icon != null) {
            Image img = new Image(new ByteArrayInputStream(icon.getIcon24()));
            ImageView imgView = new ImageView();
            imgView.setImage(img);
			
			this.setCenter(imgView);
			this.setBottom(null);
			Pane parent = ((Pane)this.getParent());
			parent.setPrefWidth(DEFAULT_WIDTH);
			this.button.setIcon(icon);
			this.button.setIconName(icon.getNome());
		} else {
			this.setCenter(null);
			this.setCenter(label);
			this.button.setIcon(null);
			this.button.setIconName("");
			setButtonWidth();
		}
    }
	
	private void setButtonWidth() {
        FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(label.getFont());
        float width = fm.computeStringWidth(label.getText()) + 40;
        if (width > DEFAULT_WIDTH) {
        	Pane parent = ((Pane)this.getParent()); 
        	if (parent != null) {
				parent.setPrefWidth(width);
			} else {
				this.setPrefWidth(DEFAULT_WIDTH);
			}
        } else {
        	this.setPrefWidth(DEFAULT_WIDTH);
        }
	}


	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if(container instanceof TitleBar) {
			ret = true;
		} else if(container instanceof ToolBar) {
			ret = true;
		} else if(container instanceof TabPanel) {
			ret = true;
		}
	    return ret;
    }


	@Override
	public Component getBackComponent() {
		return button;
	}
	
	@Override
	public Node getCustomComponent() {
		return this;
	}
	
	
	
	@Override
    public boolean isDeleted() {
	    return this.deleted;
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
	
	public void setDefaultUnfocusedStyle(String style) {
		String previousUnfocusedStyle = defaultUnfocusedStyle;
		String previousFocusedStyle = defaultFocusedStyle;
		String previousLabel = defaultLabel;
		switch(style) {
		case "normal" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_NORMAL;
			defaultFocusedStyle = FOCUSED_STYLE_NORMAL;
			defaultLabel = "x-label-normal";
			break;
		case "back" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_NORMAL;
			defaultFocusedStyle = FOCUSED_STYLE_NORMAL;
			defaultLabel = "x-label-normal";
			break;
		case "forward" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_NORMAL;
			defaultFocusedStyle = FOCUSED_STYLE_NORMAL;
			defaultLabel = "x-label-normal";
			break;
		case "round" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_ROUND;
			defaultFocusedStyle = FOCUSED_STYLE_ROUND;
			defaultLabel = "x-label-normal";
			break;
		case "plain" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_NORMAL;
			defaultFocusedStyle = FOCUSED_STYLE_NORMAL;
			defaultLabel = "x-label-normal";
			break;
		case "action" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_ACTION;
			defaultFocusedStyle = FOCUSED_STYLE_ACTION;
			defaultLabel = "x-label-action";
			break;
		case "action-round" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_ACTION_ROUND;
			defaultFocusedStyle = FOCUSED_STYLE_ACTION_ROUND;
			defaultLabel = "x-label-action";
			break;
		case "decline" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_DECLINE;
			defaultFocusedStyle = FOCUSED_STYLE_DECLINE;
			defaultLabel = "x-label-action";
			break;
		case "decline-round" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_DECLINE_ROUND;
			defaultFocusedStyle = FOCUSED_STYLE_DECLINE_ROUND;
			defaultLabel = "x-label-action";
			break;
		case "confirm" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_CONFIRM;
			defaultFocusedStyle = FOCUSED_STYLE_CONFIRM;
			defaultLabel = "x-label-action";
			break;
		case "confirm-round" :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_CONFIRM_ROUND;
			defaultFocusedStyle = FOCUSED_STYLE_CONFIRM_ROUND;
			defaultLabel = "x-label-action";
			break;
		default :
			defaultUnfocusedStyle = UNFOCUSED_STYLE_NORMAL;
			defaultFocusedStyle = FOCUSED_STYLE_NORMAL;
			defaultLabel = "x-label-normal";
		}
		
		if(this.getStyleClass().contains(previousUnfocusedStyle)) {
			this.getStyleClass().remove(previousUnfocusedStyle);
		}
		if(label.getStyleClass().contains(previousLabel)) {
			label.getStyleClass().remove(previousLabel);
		}
		if(this.getStyleClass().contains(previousFocusedStyle)) {
			this.getStyleClass().remove(previousFocusedStyle);
		}
		this.getStyleClass().add(defaultUnfocusedStyle);
		label.getStyleClass().add(defaultLabel);
		setFocusedStyle();
	}
	
	
	public boolean isUiEnabled() {
		return uiEnabled;
	}


	public void setUiEnabled(boolean uiEnabled) {
		this.uiEnabled = uiEnabled;
	}


	public boolean isPosicaoEnabled() {
		return posicaoEnabled;
	}


	public void setPosicaoEnabled(boolean posicaoEnabled) {
		this.posicaoEnabled = posicaoEnabled;
	}


	public boolean isDockedEnabled() {
		return dockedEnabled;
	}


	public void setDockedEnabled(boolean dockedEnabled) {
		this.dockedEnabled = dockedEnabled;
	}


	public boolean isIconClsEnabled() {
		return iconClsEnabled;
	}


	public void setIconClsEnabled(boolean iconClsEnabled) {
		this.iconClsEnabled = iconClsEnabled;
	}


	@Override
	public void setFocusedStyle() {
		if (!this.getStyleClass().contains(defaultFocusedStyle)) {
			if (this.getStyleClass().contains(defaultUnfocusedStyle)) {
				this.getStyleClass().remove(defaultUnfocusedStyle);
			}
			this.getStyleClass().add(defaultFocusedStyle);
		}
	}

	@Override
	public void setUnfocusedStyle() {
		if(this.getStyleClass().contains(defaultFocusedStyle)) {
			this.getStyleClass().remove(defaultFocusedStyle);
			this.getStyleClass().add(defaultUnfocusedStyle);
		}
	}

	@Override
    public void inicializaCustomComponent(Component componente) {
		this.button = (Button)componente;
		this.setUnfocusedStyle();
		this.setId(CustomButton.UI_COMPONENT_ID);
		this.detalhesTab.setContent(null);
		this.avancadoTab.setContent(null);
		if (this.button.getIcon() != null) {
	        this.setIcon(this.button.getIcon());
        }
		this.label.setText(this.button.getButtonText());
		setDefaultUnfocusedStyle(this.button.getUi());
		setButtonWidth();
    }


	public Button getTitleBar() {
		return button;
	}

	public void setTitleBar(Button button) {
		this.button = button;
	}

	public void setLabelText(String text) {
		label.setText(text);
		button.setButtonText(text);
		setButtonWidth();
		for(ILabelChangeListener listener : this.labelListeners) {
			listener.fireLabelChange(text);
		}
	}
	
	public String getLabelText() {
		return label.getText();
	}
	
	public void addLabelChangeListener(ILabelChangeListener listener) {
		this.labelListeners.add(listener);
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
