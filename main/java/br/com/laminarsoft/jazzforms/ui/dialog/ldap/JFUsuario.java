package br.com.laminarsoft.jazzforms.ui.dialog.ldap;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.commons.codec.binary.Base64;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapUsuarioVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

public class JFUsuario implements Initializable{
	@FXML private TextField loginUsuario;
	@FXML private TextField nomeUsuario;
	@FXML private TitledPane errorPane;
	@FXML private VBox mainPane;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	
	private PublicaProjetoController pubController;
	private String grupoSelecionado;
	private WebServiceController webController;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}
	
	public void setGrupoSelecionado(String grupoSel) {
		this.grupoSelecionado = grupoSel;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		
		loginUsuario.setText("");
		nomeUsuario.setText("");
		webController = WebServiceController.getInstance();
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
		if (loginUsuario == null || loginUsuario.getText().trim().length() < 6 || loginUsuario.getText().trim().contains(" ")) {
			showErrorMessage("O login do usuário deve ser válido (maior ou igual a seis caracteres e sem espaços)");
		} else if (nomeUsuario == null || nomeUsuario.getText().trim().length() == 0) {
			showErrorMessage("O nome do usuário deve ser válido");
		} else {
			progressIndicator.setVisible(true);
			String password = "123455";
			try {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(password.getBytes());
	            byte byteData[] = md.digest();

	            // convert the byte to hex format method 1
	            StringBuffer sb = new StringBuffer();
	            for (int i = 0; i < byteData.length; i++) {
	            	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	            }

	            LdapUsuarioVO usuario = new LdapUsuarioVO();
	            usuario.setCn(nomeUsuario.getText().trim());
	            usuario.setNome(nomeUsuario.getText().trim());
	            usuario.setUid(loginUsuario.getText().trim());
	            usuario.setGroupDn(grupoSelecionado);
	            usuario.setPasswd("{SHA}" + Base64.encodeBase64String(byteData));
	            
	            InfoRetornoLdap info = webController.adicionaUsuario(usuario);
            	progressIndicator.setVisible(false);
	            if (info.codigo == 0) {
	                pubController.updateData();
	                JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
	                Node source = (Node) actionEvent.getSource();
	                Stage stage = (Stage) source.getScene().getWindow();
	                stage.close();
	            } else {
	            	showErrorMessage(info.mensagem);
	            }
            } catch (NoSuchAlgorithmException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
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
