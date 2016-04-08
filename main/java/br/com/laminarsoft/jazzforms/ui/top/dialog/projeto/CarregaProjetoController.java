package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.SVNProjetoVO;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController;
import br.com.laminarsoft.jazzforms.ui.center.CenterController.ProjetoContainer;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.message.JFMensagem;

@SuppressWarnings("all")
public class CarregaProjetoController implements Initializable, IServerResponseHandler, IProjetoController {

	@FXML private ListView<Object> lvProjetos;	
	@FXML private TitledPane errorPane;	
	@FXML private Label errorLabel;	
	@FXML private ProgressIndicator progressIndicator;	
	@FXML private VBox mainPane;
	@FXML private Button btnCancel;
	@FXML private Button btnOk;
	@FXML private Button btnRepo;
	
	WebServiceController controller = WebServiceController.getInstance();
	
	private ICenterDispatcher centerDispatcher;
	private JFMensagem mensagemController;
	
	@Override
	public void setProjetoLocal(ProjetoContainer projeto) {
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
		AwesomeDude.setIcon(btnRepo, AwesomeIcon.CLOUD_DOWNLOAD, "16px", ContentDisplay.RIGHT);
		updateData();
	}
	
	private void updateData() {
		progressIndicator.setVisible(true);
		controller.findProjetoAll(this);
	}
	
	@Override
	public void onProjetosRetrieveAll(final InfoRetornoProjeto infoProjeto) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (infoProjeto != null && infoProjeto.projetos != null && infoProjeto.projetos.size() > 0) {
					for (Projeto projeto : infoProjeto.projetos) {
						lvProjetos.getItems().add(new ListItem(projeto.getId(), projeto.getNome(), projeto.getDescricao()));
					}
				} 
				progressIndicator.setVisible(false);
			}
		});		
	}


	@Override
	public void onProjetoRetrieve(final InfoRetornoProjeto infoProjeto) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (infoProjeto != null && infoProjeto.projeto != null && infoProjeto.codigo == 0)  {
					centerDispatcher.carregaProjeto(infoProjeto.projeto);
					JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
					JFDialog dialog = new JFDialog();
					dialog.show("Projeto carregado com sucesso", "Ok");
					Stage stage = (Stage) errorPane.getScene().getWindow();
					stage.close();					
				} else {
					String errorMsg = "";
					if (infoProjeto == null || infoProjeto.projeto == null) {
						errorMsg = "Não foi encontrado nenhum projeto com essa identificação";
					} else if (infoProjeto.codigo > 0) {
						errorMsg = infoProjeto.mensagem;
					}
					showErrorMessage(errorMsg);
				}
				progressIndicator.setVisible(false);
			}
		});	
	}

	@Override
	public void onProjetoCreation(InfoRetornoProjeto infoProjeto) {
		
	}

	@Override
	public void onEventosPorNomeComponente(InfoRetornoEvento infoEvento) {
		
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
		ListItem selectedItem = (ListItem)lvProjetos.getSelectionModel().getSelectedItem();
		if (selectedItem != null && centerDispatcher != null) {
	        long projetoId = selectedItem.projetoId;
	        progressIndicator.setVisible(true);
	        controller.findProjetoById(this, projetoId);
        } else if(selectedItem != null && mensagemController != null) {
			progressIndicator.setVisible(false);
        	mensagemController.setProjetoMensagem(selectedItem);
    		Node source = (Node) actionEvent.getSource();
    		Stage stage = (Stage) source.getScene().getWindow();
    		stage.close();
    	}
	}
	
	@FXML
	protected void btnRepositorio(ActionEvent actionEvent) {
		ListItem selectedItem = (ListItem)lvProjetos.getSelectionModel().getSelectedItem();
		if(selectedItem == null) {
			JFDialog dialog = new JFDialog();
			dialog.show("É necessário selecionar um projeto", "Ok");
		} else {
			progressIndicator.setVisible(true);
			String nomeProjeto = selectedItem.nome;
			controller.findInformacoesSVNProjeto(this, nomeProjeto);
		}
		
	}
	
	
	
	@Override
	public void onProjetoSVNVersions(final InfoRetornoSVN infoSVN) {
		final CarregaProjetoController me = this;
		Platform.runLater(new Runnable() {
			public void run() {
				progressIndicator.setVisible(false);		

				final Stage dialog = new Stage(StageStyle.TRANSPARENT);
				dialog.initModality(Modality.WINDOW_MODAL);
				dialog.initOwner(JazzForms.PRIMARY_STAGE);
				
		        Parent root = null;
		        boolean excpt = false;
				try {					
					DialogFactory dialogFactory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
					DialogFactory.DialogRetVO vo = dialogFactory.loadDialog("top/dialog/projeto/JFCarregaProjetoSVNVersionsDialog.fxml");					
					root = (StackPane)vo.root;
					CarregaProjetoSVNController carregaProjetoController = (CarregaProjetoSVNController)vo.controller;
					carregaProjetoController.setInfoSVN(infoSVN);
					carregaProjetoController.setCenterDispathcer(me.centerDispatcher);
					carregaProjetoController.renderSVNVersions();
				} catch (IOException e) {
					excpt = true;
				}
		        
				if (!excpt) {
					JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
					dialog.setScene(SceneBuilder.create().width(500).height(400).root(root).build());
					dialog.show();
					Stage stage = (Stage)btnRepo.getScene().getWindow();
					stage.close();
				}
			}
		});			
	}

	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
	
	public class ListItem {
		public long projetoId;
		public String nome;
		public String descricao;
		
		public ListItem(long id, String nome, String descricao) {
			this.projetoId = id;
			this.nome = nome;
			this.descricao = descricao;
		}
		
		@Override
		public String toString() {
			return nome;
		}
	}
}
