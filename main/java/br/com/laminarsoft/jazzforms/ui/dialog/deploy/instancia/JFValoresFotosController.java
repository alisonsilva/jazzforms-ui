package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.ValorDataview;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

@SuppressWarnings("all")
public class JFValoresFotosController implements Initializable, IRefreshAprsesentacao {
	@FXML private ImageView imageView;
	@FXML private ScrollPane scrollImage;
	@FXML private StackPane valoresFotos;

	
	private WebServiceController controller = WebServiceController.getInstance();
	private boolean alterado = false;

	private PublicaProjetoController pubController;
	private ValorDataview valorDataview;
	private IRefreshAprsesentacao refresher;
	private byte[] foto;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}

	public void setValorFoto(byte[] foto) {
		this.foto = foto;
		scrollImage.setFitToHeight(true);
		scrollImage.setFitToWidth(true);
		updateData();
	}
	
	@Override
    public void refreshApresentacao() {
    }

	@Override
    public void setRefresher(IRefreshAprsesentacao refresher) {
		this.refresher = refresher;
    }

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
//		valoresFotosProgressIndicator.visibleProperty().bindBidirectional(mainPanelValoresFotos.disableProperty());
//		valoresFotosProgressIndicator.setVisible(false);
//		mainPanelValoresFotos.visibleProperty().bind(valoresFotosErrorPane.visibleProperty().not());
	}

	
	private void updateData() {
        Image image = new Image(new ByteArrayInputStream(foto));
        imageView.setImage(image);
        //imageView.setFitWidth(350);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
	}	

	private void showErrorMessage(String errorMessage) {
//		((Label) valoresFotosErrorPane.getContent()).setText(errorMessage);
//		valoresFotosErrorPane.setVisible(true);
	}
	
	@FXML
	protected void deploymentsErrorPaneClicked(MouseEvent mouseEvent) {
//		valoresFotosErrorPane.setVisible(false);
	}		

	@FXML
	protected void btnOkAction(final ActionEvent actionEvent) {
		final JFValoresFotosController me = this;
        fechaJanela(actionEvent);
	}	
	
	private void fechaJanela(ActionEvent actionEvent) {
	    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
    }	
	
	class Delta { double x, y; }
	
	
}
