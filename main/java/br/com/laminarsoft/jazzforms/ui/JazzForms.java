package br.com.laminarsoft.jazzforms.ui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.jboss.weld.environment.se.WeldContainer;

import br.com.laminarsoft.jazzforms.ui.center.CenterController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.ILDAPLoginResponseHandler;
import br.com.laminarsoft.jazzforms.ui.entidade.UIUsuario;

public class JazzForms extends Application implements IJFSystemControll {
//	public static ApplicationContext SPRING_FACTORY = null;
	public static WeldContainer WELD_CONTAINER = null;
	
	public static Stage PRIMARY_STAGE;
	
	public static UIUsuario USUARIO_LOGADO;
	public static CenterController CENTER_CONTROLLER;
	public static Map<String, Pagina> paginas = new HashMap<String, Pagina>(); 
	
	static {
		Font.loadFont(JazzForms.class.getResource("/font-awesome/fonts/fontawesome-webfont.ttf").toExternalForm(), 10);
	}
	
	
	public static Pagina getPaginaDesenhoAtual() {
		Pagina pg = null;
		pg = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup(Pagina.ID_PAGINA_ATUAL);
		if (pg == null) {
			pg = paginas.get(Pagina.ID_PAGINA_ATUAL);
		}
		return pg;
	}
	
	public static Parent getSceneRoot() {
		return PRIMARY_STAGE.getScene().getRoot();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        PRIMARY_STAGE = primaryStage;
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        
        Parent root = FXMLLoader.load(getClass().getResource("JazzForms.fxml"));
        
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("imgs/jazzicon32.png")));
        
        primaryStage.setTitle("Jazz Forms");
        
        primaryStage.setScene(new Scene(root, 1300, 700));
        primaryStage.getScene().getStylesheets().add("br/com/laminarsoft/jazzforms/ui/css/focused-style.css");
        
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        
        primaryStage.show();	
        
        Stage login = startLoginScreen();
        login.sizeToScene();
        login.show();
        
	}
	
	public Stage startLoginScreen() {
		
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			URL location = this.getClass().getResource("dialog/JFLoginDialog.fxml");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(location);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			
			root = (StackPane)loader.load();

			ILDAPLoginResponseHandler loginController = (ILDAPLoginResponseHandler)loader.getController();
			loginController.setApplicationDispatcher(this);
			
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(400).height(300).root(root).build());
			dialog.setResizable(true);
		}	
		return dialog;
	}

	@Override
	public UIUsuario getUsuarioLogado() {
		return USUARIO_LOGADO;
	}

	@Override
	public void setUsuarioLogado(UIUsuario usuario) {
		JazzForms.USUARIO_LOGADO = usuario;
		
	}

	@Override
	public void finalizarAplicacao() {
		Platform.exit();
	}	
	
	public static void main(String[] args) {
		launch(args);
	}	
	
	public void run(String[] args, WeldContainer cont) {
		WELD_CONTAINER = cont;
		launch(args);
	}
	
//	public void run(@Observes ContainerInitialized event) {
//		launch(new String[0]);
//	}
	
}
