package br.com.laminarsoft.jazzforms.ui.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.ui.IJFSystemControll;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.PropertiesServiceController;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.entidade.UIUsuario;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.IProjetoController;

public class JFLoginController implements Initializable, ILDAPLoginResponseHandler {

	@FXML private TextField login;
	@FXML private PasswordField senha;
	@FXML private TitledPane errorPane;
	@FXML private VBox mainPane;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private Button btnCancel;
	@FXML private Button btnOk;
	
	
	private IJFSystemControll controll;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());

		AwesomeDude.setIcon(btnCancel, AwesomeIcon.TIMES_CIRCLE, "16px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnOk, AwesomeIcon.CHECK_CIRCLE, "16px", ContentDisplay.RIGHT);
		
		login.setText("asilva");
		senha.setText("123455");
	}

	

	@Override
	public void setApplicationDispatcher(IJFSystemControll controller) {
		this.controll = controller;
	}



	@Override
	public void receiveUserByLogin(UIUsuario usuario) {
		progressIndicator.setVisible(false);
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
		progressIndicator.setVisible(false);
		errorPane.setVisible(true);
	}
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
		

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		boolean validado = false;
		if (login == null || login.getText().trim().length() == 0) {
			showErrorMessage("O login deve ser válido");
		} else if (senha == null || senha.getText().trim().length() < 6) {
			showErrorMessage("A senha deve ser informada e com no mínimo 6 caracteres");
		} else {
			progressIndicator.setVisible(true);
				
			InfoRetornoLdap result = null;
			try {
				result = WebServiceController.getInstance().autenticaUsuario(login.getText(), senha.getText());
			} catch (Exception e) {
				showErrorMessage("Não foi possível realizar chamada: erro na conexão");
				return;
			}
			if (result.codigo != 0) {
				showErrorMessage("Não foi possível realizar a autenticação: " + result.mensagem);
			} else {
				for (LdapGrupoVO grupo : result.usuario.getGrupos()) { 
					if (grupo.getNome().equalsIgnoreCase(PropertiesServiceController.getInstance().getProperty(IProjetoController.GRUPO_JAZZFORMS_ADMIN))) {
						JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
						Node source = (Node) actionEvent.getSource();
						Stage stage = (Stage) source.getScene().getWindow();
						stage.close();
						UIUsuario usuarioLogado = new UIUsuario();
						usuarioLogado.setCn(result.usuario.getCn());
						usuarioLogado.setDn(result.usuario.getDn());
						usuarioLogado.setGroupDn(result.usuario.getGroupDn());
						usuarioLogado.setNome(result.usuario.getNome());
						usuarioLogado.setUid(result.usuario.getUid());
						usuarioLogado.setSenha(senha.getText());
						for(LdapGrupoVO grp : result.usuario.getGrupos()) {
							usuarioLogado.getPapeis().add(grp.getNome());
						}
						
						controll.setUsuarioLogado(usuarioLogado);
						validado = true;
						break;
					} 
				} 
			}
			progressIndicator.setVisible(false);
			if (!validado) {
				showErrorMessage("Credenciais do usuário não são válidas");
			}
		}
		
	}	
	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		controll.finalizarAplicacao();
	}	
	
}
