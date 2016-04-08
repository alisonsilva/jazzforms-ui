package br.com.laminarsoft.jazzforms.ui.dialog;

import br.com.laminarsoft.jazzforms.ui.JazzForms;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JFDialog {
	
	private boolean mainWindowHasEffect = false;
	
	
	public void show(String title, String btn1Lbl){
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
		dialog.setScene(new Scene(
				HBoxBuilder
						.create()
						.styleClass("modal-dialog")
						.children(
								LabelBuilder
										.create()
										.text(title)
										.build(),
								ButtonBuilder
										.create()
										.text(btn1Lbl)
										.cancelButton(true)
										.onAction(
												new EventHandler<ActionEvent>() {
													@Override
													public void handle(ActionEvent actionEvent) {
														if (!mainWindowHasEffect) {
	                                                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
                                                        }
														dialog.close();
													}
												}).build()).build(),
				Color.TRANSPARENT));
		dialog.getScene()
				.getStylesheets()
				.add(getClass().getResource("modal-dialog.css")
						.toExternalForm());

		// allow the dialog to be dragged around.
		final Node root = dialog.getScene().getRoot();
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
		mainWindowHasEffect = JazzForms.PRIMARY_STAGE.getScene().getRoot().getEffect() != null;
		if (!mainWindowHasEffect) {
	        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
        }
		dialog.show();			
	}
	
	/**
	 * Exibe uma tela de diálogo do tipo Ok/Cancela ou Sim/Não
	 * @param title A mensagem a ser exibida para o usuário
	 * @param btn1Lbl O botão Sim ou Ok
	 * @param btn2Lbl O botão Não ou Cancela
	 * @param bnt1Action Essa é a ação executada quando o usuário clica no borão Sim/Ok (deve ser passada). 
	 * Caso o usuário escolha o botão Não/Cancela, o diálogo irá se fechar e não acontecerá nada.
	 */
	public void show(String title, String btn1Lbl, String btn2Lbl, EventHandler<ActionEvent> bnt1Action){
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		final Button btn1 = ButtonBuilder.create().text(btn1Lbl).defaultButton(true).onAction(bnt1Action).build();
		btn1.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent actionEvent) {
				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
				dialog.close();
			}
		});
		
		dialog.setScene(new Scene(
				HBoxBuilder
						.create()
						.styleClass("modal-dialog")
						.children(
								LabelBuilder
										.create()
										.text(title)
										.build(),
								btn1,
								ButtonBuilder
										.create()
										.text(btn2Lbl)
										.cancelButton(true)
										.onAction(
												new EventHandler<ActionEvent>() {
													@Override
													public void handle(ActionEvent actionEvent) {
														if (!mainWindowHasEffect) {
	                                                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
                                                        }
														dialog.close();
													}
												}).build()).build(),
				Color.TRANSPARENT));
		dialog.getScene()
				.getStylesheets()
				.add(getClass().getResource("modal-dialog.css")
						.toExternalForm());

		// allow the dialog to be dragged around.
		final Node root = dialog.getScene().getRoot();
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
		mainWindowHasEffect = JazzForms.PRIMARY_STAGE.getScene().getRoot().getEffect() != null;
		if (!mainWindowHasEffect) {
	        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
        }
		dialog.show();		
	}
	

	class Delta { double x, y; }	
}
