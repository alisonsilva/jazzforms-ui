package br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;


public class WebMap {

    private BorderPane bp;
    private WebView webView = new WebView();
    private WebEngine webEngine = webView.getEngine();
    
    private List<IMapListener> listeners = new ArrayList<IMapListener>();
    
    public BorderPane getMapPane() {
    	if (bp == null) {
    		bp = createMap();
    	}
    	return bp;
    }
    
    public void addListener(IMapListener listener) {
    	listeners.add(listener);
    }
    
    public void sendMessage() {
    	JFDialog dialog = new JFDialog();
    	dialog.show("Mensagem sendo enviada", "Ok");
    }
    
    public void callListeners() {
    	for(IMapListener listener : listeners) {
    		listener.executeCommand();
    	}
    }
    
    public void setCenterPosition(String latitude, String longitude) {
    	webEngine.executeScript("document.setMapCenter(" + latitude+ "," + longitude+ ")");
    }
    
    public void putPin(String latitude, String longitude, String content) {
    	webEngine.executeScript("document.putPin(" + latitude+ "," + longitude+ ",'" + content + "')");
    }
    
    private BorderPane createMap() {
    	
    	final WebMap me = this;
        // create web engine and view
        //final WebEngine webEngine = new WebEngine(getClass().getResource("googlemap.html").toString());
        
        
        webEngine.load(getClass().getResource("googlemap.html").toString());
        webEngine.setJavaScriptEnabled(true);
        // create map type buttons
        final ToggleGroup mapTypeGroup = new ToggleGroup();
        final ToggleButton road = new ToggleButton("Estrada");
        road.setSelected(true);
        road.setToggleGroup(mapTypeGroup);
        final ToggleButton satellite = new ToggleButton("Satélite");
        satellite.setToggleGroup(mapTypeGroup);
        final ToggleButton hybrid = new ToggleButton("Hibrido");
        hybrid.setToggleGroup(mapTypeGroup);
        final ToggleButton terrain = new ToggleButton("Terreno");
        terrain.setToggleGroup(mapTypeGroup);
        mapTypeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle toggle1) {
                if (road.isSelected()) {
                    webEngine.executeScript("document.setMapTypeRoad()");
                } else if (satellite.isSelected()) {
                    webEngine.executeScript("document.setMapTypeSatellite()");
                } else if (hybrid.isSelected()) {
                    webEngine.executeScript("document.setMapTypeHybrid()");
                } else if (terrain.isSelected()) {
                    webEngine.executeScript("document.setMapTypeTerrain()");
                }
            }
        });
 
        Button zoomIn = new Button("Zoom In");
        zoomIn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) { webEngine.executeScript("document.zoomIn()"); }
        });
        Button zoomOut = new Button("Zoom Out");
        zoomOut.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) { webEngine.executeScript("document.zoomOut()"); }
        });
        // create toolbar
        ToolBar toolBar = new ToolBar();
        toolBar.getStyleClass().add("map-toolbar");
        toolBar.getItems().addAll(
                road, satellite, hybrid, terrain,
                createSpacer(),
                //google, yahoo, bing,
                createSpacer(),
                new Label("Localização:"), //searchBox, 
                zoomIn, zoomOut);
        
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			public void changed(ObservableValue<? extends State> p, State oldState, State newState) {
				if (newState == Worker.State.SUCCEEDED) {
					JSObject win = (JSObject) webEngine.executeScript("window");
					win.setMember("javaObj", me);
					callListeners();
				}
			}
		});    

        
        // create root
        BorderPane root = new BorderPane();
        root.getStyleClass().add("map");
        root.setCenter(webView);
        root.setTop(toolBar);

        bp = root;
        return root;
    }

    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    
    static { // use system proxy settings when standalone application    
        System.setProperty("java.net.useSystemProxies", "true");
    }
    
    public static void main(String[] args){
        Application.launch(args);
    }
}