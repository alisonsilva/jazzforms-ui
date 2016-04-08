package br.com.laminarsoft.jazzforms.ui.dialog.equipamento;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEquipamento;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.GrupoEquipamentoVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class JFGrupoEquipamentoController implements Initializable, IGrupoEquipamento {
	@FXML private TextField nomeGrupo;
	@FXML private TextArea descricaoGrupo;
	@FXML private TitledPane errorPane;
	@FXML private Label lblRotulo;
	@FXML private VBox mainPane;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	

	private WebServiceController webController;
	private boolean alterar = false;
	private GrupoEquipamentoVO grupoAlterar;
	private IMantemEquipamento callback;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		
		nomeGrupo.setText("");
		descricaoGrupo.setText("");
		webController = WebServiceController.getInstance();
	}
	
	public void setGrupoAlterar(GrupoEquipamentoVO grupoAlterar) {
		this.grupoAlterar = grupoAlterar;
		nomeGrupo.setText(grupoAlterar.getNome());
		descricaoGrupo.setText(grupoAlterar.getDescricao());
		alterar = true;
	}

	private void showErrorMessage(String errorMessage) {
		((Label) errorPane.getContent()).setText(errorMessage);
		errorPane.setVisible(true);
	}
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
	

	@Override
    public void setCallbackAtualizacao(IMantemEquipamento callback) {
	    this.callback = callback;
    }

	@Override
    public void manipulacaoGrupoEquipamentoSucesso(final InfoRetornoEquipamento info) {
		Platform.runLater(new Runnable() {
			public void run() {
				progressIndicator.setVisible(false);
				if(info.codigo == 0) {
					callback.atualizaListagemEquipamentos();
					nomeGrupo.setText("");
					descricaoGrupo.setText("");
					JFDialog dialog = new JFDialog();
					dialog.show("Grupo enviado com sucesso", "Ok");
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});
    }

	@Override
    public void manipulacaoGrupoEquipamentoErro(final Exception excp) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(excp.getMessage());
			}
		});
    }

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		if (nomeGrupo == null || nomeGrupo.getText().trim().length() == 0) {
			showErrorMessage("O nome do grupo deve ser válido");
		} else {
			progressIndicator.setVisible(true);
			if (!alterar) {
	            GrupoEquipamentoVO grupoEquipamento = new GrupoEquipamentoVO();
	            grupoEquipamento.setDescricao(this.descricaoGrupo.getText());
	            grupoEquipamento.setNome(this.nomeGrupo.getText());
	            webController.novoGrupoEquipamento(this, grupoEquipamento);
            } else {
            	this.grupoAlterar.setNome(nomeGrupo.getText());
            	this.grupoAlterar.setDescricao(descricaoGrupo.getText());
            	webController.alteraGrupoEquipamento(this, grupoAlterar);
            }
		}
		
	}	
	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}	
	
}
