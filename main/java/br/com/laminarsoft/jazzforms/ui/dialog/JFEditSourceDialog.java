package br.com.laminarsoft.jazzforms.ui.dialog;

import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.Evento;

public class JFEditSourceDialog {
	private JFEditSourceController controller = null;
	
	public void showJavaScript(String textEdit, String textLabel, IEditSourceJavaScript componente){

		final Stage dialog = montagemDialogo();
		
		controller.setJSEditor(new JavaScriptCodeEditor(textEdit));
		controller.setLabel(textLabel);
		controller.setComponente(componente);
		
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
		dialog.show();					
	}
	
	/**
	 * Exibe a janela de edição de texto para javascript.
	 * @param textEdit O texto pré-carregado a ser exibido
	 * @param textLabel O label a ser exibido na janela de edição
	 * @param componente O back component para o qual está sendo editado o javascript
	 * @param tipoEventoId O id do evento para o qual está sendo editado o javascript
	 * @param evento O evento para o qual está sendo editado o javascript
	 * @param table A tabela onde se encontra listados os eventos e implementações
	 */
	public void showJavaScriptFunction(String textEdit, String textLabel, IEditSourceJavaScript componente, int tipoEventoId, Evento evento, TableView<Evento> table) {
		final Stage dialog = montagemDialogo();
		
		controller.setJSFunctionEditor(new JavaScriptCodeEditor(textEdit), tipoEventoId, evento, table);
		controller.setLabel(textLabel);
		controller.setComponente(componente);
		
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
		dialog.show();					
		
	}

	public void showCSS(String textEdit, String textLabel, IEditSourceJavaScript componente){

		final Stage dialog = montagemDialogo();
		
		controller.setCSSEditor(new CSSCodeEditor(textEdit));
		controller.setLabel(textLabel);
		controller.setComponente(componente);
		
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
		dialog.show();					
	}
	
	
	public void showHTML(String textEdit, String textLabel, IEditSourceJavaScript componente){

		final Stage dialog = montagemDialogo();
		
		controller.setHTMLEditor(new HTMLCodeEditor(textEdit));
		controller.setLabel(textLabel);
		controller.setComponente(componente);
		
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
		dialog.show();					
	}

	private Stage montagemDialogo() {
		URL location = getClass().getResource("JFEditSourceDialog.fxml");

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(location);
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = null; 
		try {
			root = (Parent) fxmlLoader.load(location.openStream());		
			controller = (JFEditSourceController)fxmlLoader.getController();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		final Stage dialog = new Stage(StageStyle.DECORATED);
		dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
            public void handle(WindowEvent windowEvent) {
				
				JFDialog dialog = new JFDialog();
				final Stage source = (Stage) windowEvent.getSource();
				
				EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
						source.close();
					}
				};
				
				dialog.show("Tem certeza que deseja cancelar edição?", "Sim", "Não", act1);	  
				windowEvent.consume();
            }			
		});
		
		dialog.setResizable(true);
		dialog.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	            dialog.setWidth(number2.doubleValue());
	            
	            Pane codeEditor = controller.getCodigoPane();
	            Pane parent = ((Pane)codeEditor.getParent());
	            WebView webview = null;

	            if (codeEditor.getChildren().get(0) instanceof JavaScriptCodeEditor) {
	            	JavaScriptCodeEditor child = (JavaScriptCodeEditor)codeEditor.getChildren().get(0);
	            	webview = child.getWebView();
		            child.setPrefWidth(number2.doubleValue() - 200);
	            } else if (codeEditor.getChildren().get(0) instanceof CSSCodeEditor){
	            	CSSCodeEditor child = (CSSCodeEditor)codeEditor.getChildren().get(0);
	            	webview = child.getWebView();
		            child.setPrefWidth(number2.doubleValue() - 200);
	            } else {
	            	HTMLCodeEditor child = (HTMLCodeEditor)codeEditor.getChildren().get(0);
	            	webview = child.getWebView();
		            child.setPrefWidth(number2.doubleValue() - 200);
	            }
	            
	            
	            codeEditor.setPrefWidth(number2.doubleValue() - 200);
	            parent.setPrefWidth(number2.doubleValue() - 200);
	            webview.setPrefWidth(number2.doubleValue() - 200);
            }
		});
		
		dialog.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	            dialog.setHeight(number2.doubleValue());
	            
	            Pane codeEditor = controller.getCodigoPane();
	            Pane parent = ((Pane)codeEditor.getParent());
	            WebView webview = null;

	            if (codeEditor.getChildren().get(0) instanceof JavaScriptCodeEditor) {
	            	JavaScriptCodeEditor child = (JavaScriptCodeEditor)codeEditor.getChildren().get(0);
	            	webview = child.getWebView();
		            child.setPrefHeight(number2.doubleValue() - 240);
	            } else if (codeEditor.getChildren().get(0) instanceof CSSCodeEditor) {
	            	CSSCodeEditor child = (CSSCodeEditor)codeEditor.getChildren().get(0);
	            	webview = child.getWebView();
		            child.setPrefHeight(number2.doubleValue() - 240);
	            } else {
	            	HTMLCodeEditor child = (HTMLCodeEditor)codeEditor.getChildren().get(0);
	            	webview = child.getWebView();
		            child.setPrefHeight(number2.doubleValue() - 240);
	            }
	            
	            codeEditor.setPrefHeight(number2.doubleValue() - 240);
	            parent.setPrefHeight(number2.doubleValue() - 240);
	            webview.setPrefHeight(number2.doubleValue()- 240);
            }
		});
		
		
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
