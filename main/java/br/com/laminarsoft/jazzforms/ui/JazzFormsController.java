package br.com.laminarsoft.jazzforms.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import br.com.laminarsoft.jazzforms.ui.center.CenterController;


public class JazzFormsController implements Initializable {

	@FXML private BorderPane telaPrincipal;
	
	
	private void inicializaAplicacao(final JazzFormsController controller) {
		URL locationTop = this.getClass().getResource("./top/TopArea.fxml");
		FXMLLoader fxmlLoaderTop = new FXMLLoader();
		fxmlLoaderTop.setLocation(locationTop);
		fxmlLoaderTop.setBuilderFactory(new JavaFXBuilderFactory());
		
		URL locationCenter = this.getClass().getResource("./center/CenterArea.fxml");
		FXMLLoader fxmlLoaderCenter = new FXMLLoader();
		fxmlLoaderCenter.setLocation(locationCenter);
		fxmlLoaderCenter.setBuilderFactory(new JavaFXBuilderFactory());
		
		try {
			telaPrincipal.setTop((VBox)fxmlLoaderTop.load(locationTop.openStream()));
			ITopReceiver receiver = (ITopReceiver)fxmlLoaderTop.getController();
			
			telaPrincipal.setCenter((SplitPane)fxmlLoaderCenter.load(locationCenter.openStream()));
			CenterController dispatcher = (CenterController)fxmlLoaderCenter.getController();
			JazzForms.CENTER_CONTROLLER = dispatcher;
			
			receiver.setCenterDispatcher(dispatcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void initialize(URL locationInicializacao, ResourceBundle resources) {
		inicializaAplicacao(this);		
	}

	
}
