package br.com.laminarsoft.jazzforms.ui.top;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.ITopReceiver;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.IProjetoController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.JFGerenciarProjetoDialog;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.SalvaProjetoController;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

public class TopController implements Initializable, ITopReceiver {

	
	@FXML private VBox vboxMenu;
	@FXML private Button btnNewProject;
	@FXML private Button btnBarSalvar;
	@FXML private Button btnBarPublicar;
	@FXML private Button btnBarCarregar;
	@FXML private Button btnBarDeletar;
	
	private ICenterDispatcher centerDispathcer;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		AwesomeDude.setIcon(btnNewProject, AwesomeIcon.FILE_TEXT_ALT, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnBarPublicar, AwesomeIcon.COGS, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnBarSalvar, AwesomeIcon.SAVE, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnBarCarregar, AwesomeIcon.FOLDER_OPEN_ALT, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnBarDeletar, AwesomeIcon.TRASH_ALT, "16px", ContentDisplay.CENTER);
		

		ListCell<Label> buttonCell = new ListCell<Label>() {

			@Override
			protected void updateItem(Label item, boolean isEmpty) {
				super.updateItem(item, isEmpty);
				
				ImageView img = null;
				if (item != null) {
					switch (item.getText().toUpperCase()) {
					case "LAYOUT":
						img = null;
						break;
					case "GRID LAYOUT":
						img = new ImageView(new Image(getClass()
								.getResourceAsStream("imgs/grid.png")));
						break;
					case "BORDER LAYOUT":
						img = new ImageView(new Image(getClass()
								.getResourceAsStream("imgs/borderlayout.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					case "AGRUPAMENTO" :
						img = new ImageView(new Image(getClass().getResourceAsStream("imgs/Grouping.png")));
						img.setFitHeight(16);
						img.setFitWidth(16);
						break;
					}
				}
				setGraphic(img);
				//if (item != null) setAlignment(Pos.CENTER);
				setText(item == null ? "Layout" : item.getText());
			}
		};
		
	}


	
	@FXML
	protected void btnCarregar(ActionEvent event) {
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			
			DialogFactory dialogFactory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			DialogFactory.DialogRetVO vo = dialogFactory.loadDialog("top/dialog/projeto/JFCarregaProjetoDialog.fxml");
			
			root = (StackPane)vo.root;

			IProjetoController carregaProjetoController = (IProjetoController)vo.controller;
			carregaProjetoController.setCenterDispathcer(this.centerDispathcer);
			
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(500).height(400).root(root).build());
			dialog.show();
		}
	}
	
	
	
	@Override
	public void setCenterDispatcher(ICenterDispatcher dispatcher) {
		this.centerDispathcer = dispatcher;
	}
	
	@FXML
	protected void btnNovo(ActionEvent event) {
		this.centerDispathcer.novoProjeto();
	}

	@FXML
	protected void btnSalvar(ActionEvent event) {
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			DialogFactory dialogFactory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			DialogFactory.DialogRetVO vo = dialogFactory.loadDialog("top/dialog/projeto/JFSalvaProjetoDialog.fxml");
			
			root = (StackPane)vo.root;
			CenterController.ProjetoContainer projeto = centerDispathcer.getProjetoComponentesContainer();
			SalvaProjetoController salvaProjetoController = (SalvaProjetoController)vo.controller;
			salvaProjetoController.setCenterDispathcer(centerDispathcer);
			salvaProjetoController.setProjetoLocal(projeto);
			salvaProjetoController.exibeDialogo();
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(500).height(400).root(root).build());
			dialog.show();
		}
	}
	
	@FXML
	protected void menuGerenciarDados(ActionEvent event) {
		final Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
            public void handle(WindowEvent windowEvent) {
			    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
				Stage stage = (Stage) windowEvent.getSource();
				stage.close();
            }			
		});
		
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			DialogFactory dialogFactory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			DialogFactory.DialogRetVO vo = dialogFactory.loadDialog("dialog/deploy/JFDeploymentData.fxml");
			
			root = (Parent)vo.root;
			
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
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(1000).height(500).root(root).build());
			dialog.show();
		}
	}
	
	@FXML
	protected void menuGerenciarEquipamentos(ActionEvent actionEvent) {
		final Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
            public void handle(WindowEvent windowEvent) {
			    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
				Stage stage = (Stage) windowEvent.getSource();
				stage.close();
            }			
		});
		
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			DialogFactory dialogFactory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			DialogFactory.DialogRetVO vo = dialogFactory.loadDialog("dialog/equipamento/JFMantemEquipamentoDialog.fxml");
			
			root = (Parent)vo.root;
			
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
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(1000).height(500).root(root).build());
			dialog.show();
		}
	}
	
	@FXML
	protected void menuGerenciarUsuariosAction(ActionEvent event) {
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			URL location = this.getClass().getResource("dialog/projeto/JFPublicaProjetoDialog.fxml");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(location);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			
			root = (TabPane)loader.load();

			CenterController.ProjetoContainer projeto = centerDispathcer.getProjetoComponentesContainer();
			PublicaProjetoController carregaProjetoController = (PublicaProjetoController)loader.getController();
			carregaProjetoController.setCenterDispathcer(this.centerDispathcer);
			carregaProjetoController.setProjetoLocal(projeto);	
			carregaProjetoController.setProcessoNegocioTabVisible(false);
			
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
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(600).height(630).root(root).build());
			dialog.setResizable(true);
			dialog.show();
		}		
	}
	
	@FXML
	protected void btnPublicar(ActionEvent event) {
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			URL location = this.getClass().getResource("dialog/projeto/JFPublicaProjetoDialog.fxml");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(location);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			
			root = (TabPane)loader.load();

			CenterController.ProjetoContainer projeto = centerDispathcer.getProjetoComponentesContainer();
			IProjetoController carregaProjetoController = (IProjetoController)loader.getController();
			carregaProjetoController.setCenterDispathcer(this.centerDispathcer);
			carregaProjetoController.setProjetoLocal(projeto);	
			
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
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(600).height(630).root(root).build());
			dialog.setResizable(true);
			dialog.show();
		}		
	}
	
	@FXML
	protected void menuGerenciarProjetosAction() {
		JFGerenciarProjetoDialog sourceEditDiag = new JFGerenciarProjetoDialog();
		sourceEditDiag.setCenterDispatcher(this.centerDispathcer);
		sourceEditDiag.showDialog();		
	}
	
	class Delta { double x, y; }
}
