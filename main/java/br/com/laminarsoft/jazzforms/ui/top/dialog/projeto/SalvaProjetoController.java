package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.commons.lang.StringUtils;

import br.com.laminarsoft.jazzforms.persistencia.model.Pagina;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController.ProjetoContainer;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

@SuppressWarnings("all")
public class SalvaProjetoController extends ProjetoController implements Initializable, IServerResponseHandler {

	@FXML private TitledPane errorPane;	
	@FXML private Label errorLabel;	
	@FXML private ProgressIndicator progressIndicator;
	@FXML private ComboBox<String> cmbTipoAplicacao;
	@FXML private VBox mainPane;	
	@FXML private TextField nome;	
	@FXML private TextArea descricao;
	@FXML private TextArea descricaoVersao;
	@FXML private CheckBox chkControleVersao;
	
	private WebServiceController controller = WebServiceController.getInstance();
	private SalvaProjetoController atual;
	private ICenterDispatcher centerDispatcher;
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {

//		updateData();
	}
	
	public void exibeDialogo() {
		br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina pagina = 
				(br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		Projeto projeto = ((Pagina)pagina.getPaginaModel()).getProjeto();
		
		if (projeto == null && centerDispatcher.getProjetoComponentesContainer() != null) {
			cmbTipoAplicacao.getSelectionModel().select(centerDispatcher.getProjetoComponentesContainer().aplicacao == null || 
					centerDispatcher.getProjetoComponentesContainer().aplicacao ? 1 : 0);
			chkControleVersao.setSelected(centerDispatcher.getProjetoComponentesContainer().versionControl);
		} else if (projeto != null) {
			chkControleVersao.setSelected(projeto.versionControl);
		}
		
		chkControleVersao.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mouseEvent) {
				if(chkControleVersao.isSelected()) {
					descricaoVersao.setEditable(true);
				} else {
					descricaoVersao.setEditable(false);
				}
			}			
		});
		
		if(projeto != null && projeto.getAplicacao() != null && projeto.getAplicacao()) {
			cmbTipoAplicacao.getSelectionModel().select(1);
		} else if (projeto != null){
			cmbTipoAplicacao.getSelectionModel().select(0);
		}
	}
	
	private void updateData() {
		progressIndicator.setVisible(true);
		controller.findProjetoAll(this);
	}
	
	@Override
	public void onEventosPorNomeComponente(InfoRetornoEvento infoEvento) {
	}

	@Override
	public void onProjetosRetrieveAll(final InfoRetornoProjeto infoProjeto) {
	}
	
	@Override
	public void onProjetoSVNVersions(InfoRetornoSVN infoSVN) {
	}

	@Override
	public void setCenterDispathcer(ICenterDispatcher dispatcher) {
		this.centerDispatcher = dispatcher;
	}

	@Override
	public void onProjetoRetrieve(InfoRetornoProjeto infoProjeto) {
	}

	@Override
	public void onProjetoCreation(final InfoRetornoProjeto infoProjeto) {
		atual = this;
		Platform.runLater(new Runnable(){
			public void run() {
				progressIndicator.setVisible(false);
				if (infoProjeto != null && infoProjeto.codigo == 0) {
					JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
					JFDialog dialog = new JFDialog();
					dialog.show("Projeto salvo com sucesso", "Ok");
					Stage stage = (Stage) errorPane.getScene().getWindow();
					stage.close();
					projetoLocal.projetoId = infoProjeto.projeto.getId();
					atual.limpaAtualizacaoPaginas();
				} else if (infoProjeto != null && infoProjeto.codigo != 0) {
					showErrorMessage(infoProjeto.mensagem);
				} else {
					showErrorMessage("Não foi possível salvar o projeto");
				}
			}
		});
	}
	
	
	@Override
	public void setProjetoLocal(ProjetoContainer projeto) {
		super.setProjetoLocal(projeto);
		this.nome.setText(projeto.nome);
		this.descricao.setText(projeto.descricao);
		this.descricaoVersao.setText(StringUtils.isEmpty(projeto.versionControlDescription) ? "" : projeto.versionControlDescription );
	}


	@Override
	public void onServerError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage("Não foi possível estabelecer comunicação com o servidor");
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
		progressIndicator.setVisible(true);

		projetoLocal.nome = nome.getText();
		projetoLocal.descricao = descricao.getText();
		projetoLocal.aplicacao = cmbTipoAplicacao.getSelectionModel().getSelectedIndex() == 0 ? false : true;
		projetoLocal.versionControlDescription = descricaoVersao.getText();
		
		try {
	        Projeto projetoPersistencia = parseCriacaoProjeto("Criação/Alteração do processo");
	        projetoPersistencia.setAplicacao(cmbTipoAplicacao.getSelectionModel().getSelectedIndex() == 0 ? false : true);
	        projetoPersistencia.versionControl = chkControleVersao.isSelected();
	        controller.createProjeto(projetoPersistencia, this);	        
        } catch (PreenchimentoException e) {
        	//TODO melhnorar a abordagem de tratamento de erro
        	showErrorMessage(e.getMessage());
	        e.printStackTrace();
        }
	}
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
	
	private class ListItem {
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
