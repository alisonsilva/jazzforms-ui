package br.com.laminarsoft.jazzforms.ui.dialog.ldap;

import java.net.URL;
import java.util.ResourceBundle;

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
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

public class JFGrupo implements Initializable{
	@FXML private TextField nomeGrupo;
	@FXML private TextArea descricao;
	@FXML private TitledPane errorPane;
	@FXML private VBox mainPane;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	
	private PublicaProjetoController pubController;
	private WebServiceController webserviceController;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		webserviceController = WebServiceController.getInstance();
		nomeGrupo.setText("");
		descricao.setText("");
	}

	private void showErrorMessage(String errorMessage) {
		((Label) errorPane.getContent()).setText(errorMessage);
		errorPane.setVisible(true);
	}
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
		

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		if (nomeGrupo == null || nomeGrupo.getText().trim().length() < 6 || nomeGrupo.getText().trim().contains(" ")) {
			showErrorMessage("O nome do grupo deve ser válido (maior ou igual a seis caracteres e sem espaços)");
		} else if (descricao == null || descricao.getText().trim().length() == 0) {
			showErrorMessage("A descrição dever ser válida");
		} else {
			progressIndicator.setVisible(true);
			LdapGrupoVO grupo = webserviceController.getGrupo(nomeGrupo.getText().trim()).grupo;
			if (grupo != null) {
				this.showErrorMessage("Grupo já existe.");
			} else {
				LdapGrupoVO grpVo = new LdapGrupoVO();
				grpVo.setNome(nomeGrupo.getText().trim());
				grpVo.setDescription(descricao.getText().trim());
				InfoRetornoLdap info = webserviceController.adicionaGrupo(grpVo);
				pubController.updateData();
				if (info.codigo == 0) {
	                JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
	                Node source = (Node) actionEvent.getSource();
	                Stage stage = (Stage) source.getScene().getWindow();
	                stage.close();
                } else 	{
                	showErrorMessage(info.mensagem);
                }
			}
			progressIndicator.setVisible(false);
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
