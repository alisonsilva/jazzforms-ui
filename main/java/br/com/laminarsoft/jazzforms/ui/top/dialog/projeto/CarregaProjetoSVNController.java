package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.SVNProjetoVO;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController.ProjetoContainer;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.message.JFMensagem;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

@SuppressWarnings("all")
public class CarregaProjetoSVNController implements Initializable, IProjetoController {

	@FXML private ListView<SVNProjetoVO> lvVersoes;	
	@FXML private TitledPane errorPane;	
	@FXML private Label errorLabel;	
	@FXML private ProgressIndicator progressIndicator;	
	@FXML private VBox mainPane;
	@FXML private Button btnCancel;
	@FXML private Button btnOk;
	
	WebServiceController controller = WebServiceController.getInstance();
	
	private ICenterDispatcher centerDispatcher;
	private JFMensagem mensagemController;
	private InfoRetornoSVN infoSVN;
	
	@Override
	public void setProjetoLocal(ProjetoContainer projeto) {
	}
	
	public void setInfoSVN(InfoRetornoSVN info) {
		this.infoSVN = info;
	}

	@Override
	public ProjetoContainer getProjetoLocal() {
		return null;
	}

	@Override
	public void setCenterDispathcer(ICenterDispatcher dispatcher) {
		this.centerDispatcher = dispatcher;
	}
	
	public void setMensagemController(JFMensagem mensagemController){
		this.mensagemController = mensagemController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		AwesomeDude.setIcon(btnCancel, AwesomeIcon.TIMES_CIRCLE, "16px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnOk, AwesomeIcon.CHECK_CIRCLE, "16px", ContentDisplay.RIGHT);
	}
	
	public void renderSVNVersions() {
		ObservableList<SVNProjetoVO> data = FXCollections.observableArrayList(infoSVN.lstProjetos);
		lvVersoes.setItems(data);
		lvVersoes.setCellFactory(new Callback<ListView<SVNProjetoVO>, ListCell<SVNProjetoVO>>() {
			
			@Override
			public ListCell<SVNProjetoVO> call(ListView<SVNProjetoVO> list) {
				return new SVNVersionCell();
			}
		});
	}
	
	static class SVNVersionCell extends ListCell<SVNProjetoVO> {

		@Override
		protected void updateItem(SVNProjetoVO item, boolean empty) {
			super.updateItem(item, empty);
			if(item != null) {
				GridPane grid = new GridPane();
				grid.setPadding(new Insets(10, 10, 10, 10));
				grid.setVgap(10);
				grid.setHgap(10);
				Label lblVersao = new Label("Versão: ");
				Label lblData = new Label("Data: ");
				Label lblDescricao = new Label("Descrição: ");
				
				DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dataAtualizacao = null;
				try {
					dataAtualizacao = dtf.parse(item.getDataAtualizacao().substring(0, item.getDataAtualizacao().indexOf(".")).replace("T", " "));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Label lblVersaoTxt = new Label(item.getVersao());
				Label lblDataTxt = new Label(dataAtualizacao == null ? "" : dtf.format(dataAtualizacao));
				Label lblDescricaoTxt = new Label(item.getDescricao());
				
				grid.add(lblVersao, 0, 0);
				grid.add(lblVersaoTxt, 1, 0);
				grid.add(lblData, 0, 1);
				grid.add(lblDataTxt, 1, 1);
				grid.add(lblDescricao, 0, 2);
				grid.add(lblDescricaoTxt, 1, 2);
				
				setGraphic(grid);
			}
		}
		
	}
	
	public void onServerError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
				progressIndicator.setVisible(false);				
			}
		});		
	}
	
	public void onProjetoRecuperado(final InfoRetornoSVN svnProjeto) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				progressIndicator.setVisible(false);
				if(svnProjeto.codigo != 0) {
					showErrorMessage(svnProjeto.mensagem);
				} else {
					Projeto prj = svnProjeto.projeto;
					centerDispatcher.carregaProjeto(prj);

					JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
					JFDialog dialog = new JFDialog();
					dialog.show("Projeto carregado com sucesso", "Ok");
					Stage stage = (Stage) errorPane.getScene().getWindow();
					stage.close();					
				}
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
		SVNProjetoVO selectedItem = (SVNProjetoVO)lvVersoes.getSelectionModel().getSelectedItem();
		if (selectedItem != null && centerDispatcher != null) {
	        progressIndicator.setVisible(true);
	        controller.checkoutSVNProjeto(this, selectedItem.getNome(), selectedItem.getVersao());
        } else if(selectedItem == null) {
			JFDialog dialog = new JFDialog();
			dialog.show("É necessário selecionar uma versão", "Ok");    	
		}
	}
	

	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
}
