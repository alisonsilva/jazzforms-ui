package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Icon;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoIcon;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController.ProjetoContainer;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerIconsResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFIconComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

@SuppressWarnings("all")
public class CarregaImagensController implements Initializable, IServerIconsResponseHandler {

	private static final String FOCUSED_STYLE_NAME = "line-focused-style";
	private static final String UNFOCUSED_STYLE_NAME = "line-unfocused-style";

	@FXML private VBox gridImagens;
	@FXML private TitledPane errorPane;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private VBox mainPane;
	
	@FXML private Button btnOk;
	@FXML private Button btnCancela;

	private WebServiceController controller;
	private HBox selectedBox;
	private IJFIconComponent showingElement;

	private ICenterDispatcher centerDispatcher;

	public void setProjetoLocal(ProjetoContainer projeto) {
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		controller = JazzForms.WELD_CONTAINER.instance().select(WebServiceController.class).get();
		Pagina pagina = JazzForms.getPaginaDesenhoAtual();
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		
		AwesomeDude.setIcon(btnOk, AwesomeIcon.CHECK_CIRCLE, "14px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnCancela, AwesomeIcon.TIMES_CIRCLE, "14px", ContentDisplay.RIGHT);
		updateData();
	}

	private void updateData() {
		progressIndicator.setVisible(true);
		controller.findIconsWithName(this);
	}

	@Override
    public void setShowingElement(IJFIconComponent showingElement) {
		this.showingElement = showingElement;
    }

	@Override
	public void onIconRetrieve(final InfoRetornoIcon infoIcon) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (infoIcon == null) {
					showErrorMessage("Erro ao recuperar icones.");
				} else if (infoIcon.codigo > 0) {
					showErrorMessage(infoIcon.mensagem);
				} else if (infoIcon != null && infoIcon.icons != null && infoIcon.icons.size() > 0) {
					gridImagens.getScene().getStylesheets().add("br/com/laminarsoft/jazzforms/ui/css/focused-style.css");
					for (Icon projeto : infoIcon.icons) {
						Image img = new Image(new ByteArrayInputStream(projeto.getIcon24()));
						ImageView imgView = new ImageView();
						imgView.setImage(img);

						ListItem lbl = new ListItem(projeto.getNome(), projeto);

						final HBox hbox = new HBox();
						hbox.setSpacing(10);
						hbox.setPrefHeight(40);
						hbox.getChildren().add(imgView);
						hbox.getChildren().add(lbl);

						hbox.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent mouseEvent) {
								setFocusedStyle(hbox);
								if (selectedBox != null) {
									setUnfocusedStyle(selectedBox);
								}
								selectedBox = hbox;
							}
						});

						gridImagens.getChildren().add(hbox);
					}
				}
				progressIndicator.setVisible(false);
			}
		});
	}

	public void setFocusedStyle(HBox box) {
		if (!box.getStyleClass().contains(FOCUSED_STYLE_NAME)) {
			if (box.getStyleClass().contains(UNFOCUSED_STYLE_NAME)) {
				box.getStyleClass().remove(UNFOCUSED_STYLE_NAME);
			}
			box.getStyleClass().add(FOCUSED_STYLE_NAME);
		}
	}

	public void setUnfocusedStyle(HBox box) {
		if (box.getStyleClass().contains(FOCUSED_STYLE_NAME)) {
			box.getStyleClass().remove(FOCUSED_STYLE_NAME);
			box.getStyleClass().add(UNFOCUSED_STYLE_NAME);
		}
	}

	@Override
	public void onServerError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
				progressIndicator.setVisible(false);
			}
		});
	}

	private void showErrorMessage(String errorMessage) {
		((Label) errorPane.getContent()).setText(errorMessage);
		errorPane.setVisible(true);
	}

	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		if(selectedBox == null) {
			JFDialog dialog = new JFDialog();
			dialog.show("É necessário selecionar um ícone", "Ok");
		} else {
			Icon icon = null;
			Iterator iter = selectedBox.getChildren().iterator();
			while(iter.hasNext()) {
				Object oj = iter.next();
				if(oj instanceof ListItem) {
					icon = ((ListItem)oj).icon;
					break;
				}
			}
			if (icon != null) {
	            showingElement.setIcon(icon);
            }
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
			Node source = (Node) actionEvent.getSource();
			Stage stage = (Stage) source.getScene().getWindow();
			stage.close();			
		}
	}

	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}

	private class ListItem extends Label{
		public String nome;
		public Icon icon;

		public ListItem(String nome, Icon icon) {
			this.nome = nome;
			this.icon = icon;
			super.setText(nome);
		}

		@Override
		public String toString() {
			return nome;
		}
	}
}
