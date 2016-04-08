package br.com.laminarsoft.jazzforms.ui.componentes.pagina;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Layout;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Panel;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJazzFormsUIComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.componentes.util.KeyListenerCanvas;

public class Pagina extends JFBorderPane implements IJazzFormsUIComponent, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "focused-style";
	public static final String UNFOCUSED_STYLE_NAME = "unfocused-style";
	public static final String ID_PAGINA_ATUAL = "#paginaDesenhoAtual";
	
	private int componenteDesenho;
	private ComboBox<Label> btnBarComboContaineres;
	private List<IJFSelectedComponent> selectedNodes = new ArrayList<IJFSelectedComponent>();
	private MouseClickEventsCanvas mouseClickedEventHandler;
	
	private String FOCUSED_STYLE= "-fx-border-style: solid ; "
			+ "-fx-border-width: 2; "
			+ "-fx-border-color: rgb(64,0,128);" +
			"-fx-border-radius:10;" +
			"-fx-background-radius: 10;";
	private String UNFOCUSED_STYLE = "-fx-border-style: solid ; "
						+ "-fx-border-width: 1;"
						+ "-fx-border-color: rgb(0,0,0);" +
						"-fx-border-radius: 10;" +
						"-fx-background-radius: 10;";
	
	private String nome  = "";
	private String descricao = "";
	private Color styleBackgroundColor;
	
	private String backgroundColor;
	private byte[] codigoCustomizado;
	
	private br.com.laminarsoft.jazzforms.persistencia.model.Pagina paginaModel;
	private KeyListenerCanvas keyListener;
	
	private boolean atualizada;
	
	public Pagina(){
		super();
		styleBackgroundColor = Color.rgb(211, 206, 190);
		formatBackgroundColor(styleBackgroundColor);
		this.setPrefSize(1000, 2000);
		
		Container container = new Panel();
		Layout layout = new Layout();
		layout.setNome(Layout.LAYOUT_FIT);
		container.setLayout(layout);
		container.setFieldId(IdentificadorUtil.getNextIdentificadorPanel());
		this.paginaModel = new br.com.laminarsoft.jazzforms.persistencia.model.Pagina();
		this.paginaModel.setContainer(container);
		this.paginaModel.setNome(IdentificadorUtil.getNextIdentificadorPagina());
		VBox vboxtop = new VBox(3);
		vboxtop.setPadding(new Insets(10, 15, 5, 10));
		
		VBox vboxbottom = new VBox(3);
		vboxbottom.setPadding(new Insets(10, 15, 5, 10));
		
		VBox vboxcenter = new VBox(3);
		vboxcenter.setPadding(new Insets(10, 15, 5, 10));

		this.setBottom(vboxbottom);
		this.setTop(vboxtop);
		this.setCenter(vboxcenter);
		
		keyListener = new KeyListenerCanvas(this);		
		this.setOnKeyPressed(keyListener);
		
	}
	
	
	public KeyListenerCanvas getKeyListener() {
		return keyListener;
	}
	
	
	
	public boolean isAtualizada() {
		return atualizada;
	}


	public void setAtualizada(boolean atualizada) {
		this.atualizada = atualizada;
	}


	@Override
    public void inicializaCustomComponent(Component componente) {
	    this.paginaModel.setContainer((Container)componente);
    }


	@Override
    public void removeComponent(JFBorderPane component) {
		if(this.topProperty().get() == component.getParent()) {
			this.setTop(null);
		} else if(this.leftProperty().get() == component.getParent()) {
			this.setLeft(null);
		} else if(this.rightProperty().get() == component.getParent()) {
			this.setRight(null);
		} else if(this.bottomProperty().get() == component.getParent()) {
			this.setBottom(null);
		} else if(this.centerProperty().get() == component.getParent()) {
			this.setCenter(null);
		}
    }



	@Override
    public void adicionaComponent(JFBorderPane component, String posicao) {
		VBox topBox = (VBox)this.getTop();
		VBox bottomBox = (VBox)this.getBottom();
		VBox centerBox = (VBox)this.getCenter();
		switch(posicao) {
		case "top" :
			if (bottomBox.getChildren().contains(component.getParent())) {
				bottomBox.getChildren().remove(component.getParent());
			}
			if (!topBox.getChildren().contains(component.getParent())) {
				topBox.getChildren().add(component.getParent());
			}
			break;
		case "bottom" :
			if (topBox.getChildren().contains(component.getParent())) {
				topBox.getChildren().remove(component.getParent());
			}
			if (!bottomBox.getChildren().contains(component.getParent())) {
				bottomBox.getChildren().add(component.getParent());
			}
			break;
		case "left" :
			this.setLeft(component.getParent());
			break;
		case "right" :
			this.setRight(component.getParent());
			break;
		case "center" :
			if(centerBox.getChildren().contains(component.getParent())) {
				centerBox.getChildren().remove(component.getParent());
			}
			if (!centerBox.getChildren().contains(component.getParent())) {
				centerBox.getChildren().add(component.getParent());
			}
			break;
		}
		if (!this.getPaginaModel().getContainer().getItems().contains(component.getBackComponent())) {
			this.getPaginaModel().getContainer().getItems().add(component.getBackComponent());
		}
    }


	@Override
    public boolean isInsersaoValida(Container container) {
	    return true;
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
	public Component getBackComponent() {
		return paginaModel.getContainer();
	}



	public br.com.laminarsoft.jazzforms.persistencia.model.Pagina getPaginaModel() {
		return paginaModel;
	}


	public void setPaginaModel(
			br.com.laminarsoft.jazzforms.persistencia.model.Pagina paginaModel) {
		this.paginaModel = paginaModel;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}


	public String getBackgroundColor() {
		return this.backgroundColor;
	}

	public void setBackgroundColor(String color) {
		this.backgroundColor = color;
		formatBackgroundColor(color);
		if(this.isFocused()) {
			this.setStyle(FOCUSED_STYLE);
		} else {
			this.setStyle(UNFOCUSED_STYLE);
		}		
	}

	public byte[] getBackgroundImage() {
		return null;
	}

	public void setBackgroundImage(byte[] image) {
	}

	public byte[] getPacoteCodigoCustomizado() {
		return codigoCustomizado;
	}

	public void setPacoteCodigoCustomizado(byte[] codigo) {
		this.codigoCustomizado = codigo;
	}


	@Override
	public Node clone() {
		return this;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	
	public Color getStyleBackgroundColor() {
		return styleBackgroundColor;
	}

	
	public void setStyleBackgroundColor(Color styleBackgroundColor) {
		this.styleBackgroundColor = styleBackgroundColor;
		formatBackgroundColor(styleBackgroundColor);
		if(this.isFocused()) {
			this.setStyle(FOCUSED_STYLE);
		} else {
			this.setStyle(UNFOCUSED_STYLE);
		}
	}
	public String getUnfocusedtStyle() {
		return UNFOCUSED_STYLE;
	}

	public String getFocusedStyle() {
		return FOCUSED_STYLE;
	}
	

	@Override
    public boolean isDeleted() {
	    // TODO Auto-generated method stub
	    return false;
    }



	@Override
    public void setDeleted(boolean deleted) {
	    // TODO Auto-generated method stub
	    
    }



	@Override
	public void setFocused() {
		this.setStyle(FOCUSED_STYLE);
	}
	
	@Override
	public void setUnfocused() {
		this.setStyle(UNFOCUSED_STYLE);
	}
	
	private void formatBackgroundColor(Color styleBackgroundColor) {
		String red = Math.floor(styleBackgroundColor.getRed()*255) + "";
		String green = Math.floor(styleBackgroundColor.getGreen()*255) + "";
		String blue = Math.floor(styleBackgroundColor.getBlue()*255) + "";
		
		String colorComplement = "-fx-background-color: rgb("+ red + "," +
				 green + "," +
				 blue + ");";
		FOCUSED_STYLE += colorComplement;
		UNFOCUSED_STYLE += colorComplement;
		
		backgroundColor = red + "," + green + "," + blue; 
	}
	
	private void formatBackgroundColor(String color) {
		String colorComplement = "-fx-background-color: rgb(" + color + ");";		
		FOCUSED_STYLE += colorComplement;
		UNFOCUSED_STYLE += colorComplement;
		if (color != null) {
			StringTokenizer tok = new StringTokenizer(color, ",");
			if (tok.countTokens() == 3) {
				styleBackgroundColor = Color.rgb(Double.valueOf(tok.nextToken()).intValue(),
						Double.valueOf(tok.nextToken()).intValue(), 
						Double.valueOf(tok.nextToken()).intValue());
			}
		}
	}
	
	
	public void setOnMouseClickedHandler(MouseClickEventsCanvas handler){
		this.mouseClickedEventHandler = handler;
	}
	
	public MouseClickEventsCanvas getOnMouseClickedHandler() {
		return this.mouseClickedEventHandler;
	}
	
	@Override
	public RootController getContentDetalhes() {
		RootController ret = new RootController();
		URL location = getClass().getResource("PaginaDetalhes.fxml");

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(location);
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = null;
		try {
			root = (Parent) fxmlLoader.load(location.openStream());		
			ret.root = root;
			ret.controller = fxmlLoader.getController();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}


	@Override
	public RootController getContentAvancado() {
		RootController ret = new RootController();
		URL location = getClass().getResource("PaginaAvancado.fxml");

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(location);
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = null;
		try {
			root = (Parent) fxmlLoader.load(location.openStream());		
			ret.root = root;
			ret.controller = fxmlLoader.getController();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public boolean containsSelectedNode(IJFSelectedComponent node) {
		return selectedNodes.contains(node);
	}
	
	public void addSelectedNode(IJFSelectedComponent node) {
		node.setFocusedStyle();
		selectedNodes.add(node);
	}
	
	public void addAllSelectedNodes(List<IJFSelectedComponent> nodes) {
		for(IJFSelectedComponent node : nodes) {
			node.setFocusedStyle();
		}
		selectedNodes.addAll(nodes);
	}
	
	public void removeSelectedNode(IJFSelectedComponent node) {
		node.setUnfocusedStyle();
		selectedNodes.remove(node);
	}
	
	public void clearSelectedNodes() {
		for(IJFSelectedComponent node : selectedNodes) {
			node.setUnfocusedStyle();
		}
		selectedNodes.clear();
	}
	
	public void deleteSelectedNodes() {
		for(int i = 0; i < selectedNodes.size() && !selectedNodes.get(i).isDeleted(); i++) {
			IJFSelectedComponent nd = (IJFSelectedComponent)selectedNodes.get(i);
			deleteNode(nd);
			this.atualizada = true;
		}
		mouseClickedEventHandler.getAvancadoTab().setContent(null);
		mouseClickedEventHandler.getDetalhesTab().setContent(null);
		selectedNodes.clear();
		this.getPaginaModel().getContainer();
	}
	
	private void deleteNode(IJFSelectedComponent node) {
		node.setDeleted(true);
		JFBorderPane pane = (JFBorderPane)node;
		for(Node son : pane.getChildren()) {
			if (son instanceof IJFSelectedComponent) {
				deleteNode((IJFSelectedComponent)son);
			}
		}
		if (pane.getBackComponent() instanceof Container) {
			Container cont = (Container)pane.getBackComponent();
			cont.getItems().clear();
		}
		pane.getChildren().clear();
		Pane paneParent = (Pane)pane.getParent();
		paneParent.getChildren().remove(pane);		
		((Pane)paneParent.getParent()).getChildren().remove(paneParent);
		
		IDetalhesComponentes det = (IDetalhesComponentes)node;
		JFBorderPane parentPane = (JFBorderPane)det.getContainerPai();
		if (parentPane.getBackComponent() instanceof Container) {
			Container cont = (Container)parentPane.getBackComponent();
			cont.removeComponent(pane.getBackComponent());
		}
		parentPane.removeComponent((JFBorderPane)node);
	}
	
	public List<IJFSelectedComponent> getSelectedNodes() {
		return selectedNodes;
	}
	
	public int getComponenteDesenho() {
		return componenteDesenho;
	}


	public void setComponenteDesenho(int componenteDesenho) {
		this.componenteDesenho = componenteDesenho;
	}


	public ComboBox<Label> getBtnBarComboContaineres() {
		return btnBarComboContaineres;
	}

	public void setBtnBarComboContaineres(ComboBox<Label> btnBarComboLayouts) {
		this.btnBarComboContaineres = btnBarComboLayouts;
	}
}


