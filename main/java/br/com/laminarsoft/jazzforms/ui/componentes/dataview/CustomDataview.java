package br.com.laminarsoft.jazzforms.ui.componentes.dataview;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DataView;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Panel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TabPanel;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CustomDataview extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "fieldset-focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "fieldset-unfocused-style";
	public static final String UI_COMPONENT_ID = "CustomDataview";
	
	private DataView dataView;
	private IJFUIBackComponent paneParent;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private VBox centerBox;
	private Label titleLabel;
	private Label instructionsLabel;
	
	
	
	
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    
	protected boolean deleted = false;
	
	
	
String strHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""+
    " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en-US\">" +
"<head>" +
"<title>LaminarSoft - The mobile solution</title>"+
 "<meta http-equiv=\"content-type\" content=\"application/xhtml; charset=UTF-8\" />" +
 "<style media=\"screen\" type=\"text/css\">"+
	".latest_news{border-bottom:1px dashed #dedede;margin-top:10px;padding-bottom:7px;}"+
	".latest_news p{margin-top:3px;}"+
	".latest_news_link a{color:#1a75bc;font-size:12px;font-weight:bold;}"+
	".latest_news_link a:hover{color:#1a75bc;font-size:12px;font-weight:bold;text-decoration:underline;}"+ 
	"#right_content{float:left;width:277px;color:#666666;font-size:11px;text-align:left;margin-left:10px;padding:22px 5px 15px 27px;} "+
 "</style>"+
 "<body>"+
"<!-- News1 -->"+
 "<div id=\"right_content\">"+
"<div class=\"latest_news\">"+
"<p>15 Ago 20XX</p>"+
"<p class=\"latest_news_link\"><a href=\"#\" target=\"_new\">Manchete da not&iacute;cia 1</a></p>"+
"<p>Aqui vai uma breve descri&ccedil;&atilde;o da not&iacute;cia 1</p>"+
"</div>"+
"<div class=\"latest_news\">"+
"<p>15 Ago 20XX</p>"+
"<p class=\"latest_news_link\"><a href=\"#\" target=\"_new\">Manchete da not&iacute;cia 2</a></p>"+
"<p>Aqui vai uma breve descri&ccedil;&atilde;o da not&iacute;cia 2</p>"+
"</div>"+
"</div>"+
" </body>"+
"</head>";
	
	
	public CustomDataview() {
		super();
		
		centerBox = new VBox(3);
		this.getStyleClass().add(UNFOCUSED_STYLE_NAME);
		this.setPadding(new Insets(5, 5, 5, 5));
		centerBox.setId("fieldset-centerbox");
		
		dataView = new DataView();		
		dataView.setFieldId(IdentificadorUtil.getNextIdentificadorDataview());
		dataView.setJfxPreferedHeight(this.getPrefHeight());
		
		webEngine.loadContent(strHtml);
		centerBox.getChildren().add(browser);
		centerBox.setPrefHeight(500);
		this.setCenter(centerBox);
		
		
		
		final CustomDataview atual = this;

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
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/dataview/DataviewDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/dataview/DataviewAvancado.fxml");
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
//					if (centerBox.getChildren().size() == 0) { 
//						DesenhaComponenteHandler desenhaComponente = DesenhaComponenteHandler.getInstance();
//						desenhaComponente.setDetalhesTab(detalhesTab);
//						desenhaComponente.setAvancadoTab(avancadoTab);
//						desenhaComponente.desenhaComponente(centerBox, dataView, atual);
//					} else {
//						JFDialog dialog = new JFDialog();
//						dialog.show("Fieldset só pode conter um componente por vez", "Ok");
//					}
					JFDialog dialog = new JFDialog();
					dialog.show("O componente Dataview não pode conter outros componentes", "Ok");
					
				}
				event.consume();
			}			
		});
	}
	
	
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if (container instanceof FormPanel) {
			ret = false;
		} else if(container instanceof Panel) {
			ret = true;
		} else if(container instanceof TabPanel) {
			ret = true;
		} else if(container instanceof Carousel) {
			ret = true;
		}
		return ret;
    }



	@Override
	public Component getBackComponent() {
		return dataView;
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
    }

	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
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
		this.dataView = (DataView)componente;
    }


	public DataView getDataView() {
		return dataView;
	}

	public void setDataView(DataView dataView) {
		this.dataView = dataView;
	}

	public void setTitle(String text) {
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
		return titleLabel.getText();
	}
	
	public void setInstruction(String instruction) {
		instructionsLabel.setText(instruction);
	}
	
	public String getInstruction() {
		return instructionsLabel.getText();
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

class Browser extends Region {
	 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final ScrollPane scroll = new ScrollPane();
     
    public Browser() {
        //apply the styles
        // load the web page
        webEngine.load("http://www.oracle.com/products/index.html");
        //add the web view to the scene
        browser.setPrefHeight(500);
        browser.setPrefWidth(500);
        
        scroll.setContent(browser);
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setStyle("-fx-background-color:transparent;");

        getChildren().add(scroll);
        
        this.setPrefHeight(Double.MAX_VALUE);
 
		webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {

			@Override
            public WebEngine call(PopupFeatures param) {
	            return null;
            }			
		});        
    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 750;
    }
} 
