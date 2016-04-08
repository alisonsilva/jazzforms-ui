package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.net.URL;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;

public class JFGerenciarProjetoDialog {
	private GerenciarPublicacaoController controller;
	private ICenterDispatcher centerDispathcer;
	
	public void showDialog() {
		final Stage dialog = montagemDialogo();
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
		dialog.show();	
	}
	
	public void setCenterDispatcher(ICenterDispatcher centerDisp) {
		this.centerDispathcer = centerDisp;
	}
	
	public GerenciarPublicacaoController getController() {
		return controller;
	}
	
	public Stage montagemDialogo() {
		URL location = getClass().getResource("JFGerenciarPublicacao.fxml");

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(location);
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = null;
		try {
			root = (Parent) fxmlLoader.load(location.openStream());		
			controller = (GerenciarPublicacaoController)fxmlLoader.getController();
			controller.setCenterDispatcher(this.centerDispathcer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
		dialog.setScene(new Scene(root));
		final Delta dragDelta = new Delta();
		
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// record a delta distance for the drag and drop
				// operation.
				dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
				dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
				dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
			}
		});
		return dialog;		
	}
	
	class Delta { double x, y; }	
}
