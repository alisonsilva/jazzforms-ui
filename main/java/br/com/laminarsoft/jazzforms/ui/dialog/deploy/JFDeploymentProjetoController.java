package br.com.laminarsoft.jazzforms.ui.dialog.deploy;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ProjetoImplantadoVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;

public class JFDeploymentProjetoController implements Initializable{

	@FXML private Label lblNome;
	@FXML private TextArea descricaoProjeto;
	@FXML private Label lblDataCriacao;
	@FXML private Label lblUsuario;
	@FXML private Label lblId;
	
	@FXML private Label lblRotDataCriacao;
	@FXML private Label lblRotUsuarioCriacao;
	
		
	public void setValoresRotulos(ProjetoImplantadoVO vo, boolean deploy) {

		lblNome.setText(vo.getNomeProjeto());
		lblNome.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		
        lblDataCriacao.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        lblUsuario.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        descricaoProjeto.getStyleClass().add("textarea-modal");
		descricaoProjeto.setText(vo.getDescProjeto());
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		if (!deploy) {
			lblRotDataCriacao.setText("Data de alteração");
			lblRotDataCriacao.setTooltip(new Tooltip("Data de alteração"));
	        if (vo.getDadosUsuario() != null && vo.getDadosUsuario().getDhAlteracaoDeployment() != null) {
	            lblDataCriacao.setText(df.format(vo.getDadosUsuario().getDhAlteracaoProjeto()));
            } else {
            	lblDataCriacao.setText("não especificado");
            }
			if (vo.getDadosUsuario() != null) {
	            lblUsuario.setText(vo.getDadosUsuario().getNomeUsuarioProjeto());
            } else {
            	lblUsuario.setText("não especificado");
            }
			lblId.setText(vo.getProjetoId() + "");
			lblId.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        } else {
			lblRotDataCriacao.setText("Data de implantação");
			lblRotDataCriacao.setTooltip(new Tooltip("Data de implantação"));
	        if (vo.getDadosUsuario() != null && vo.getDadosUsuario().getDhAlteracaoDeployment() != null) {
	            lblDataCriacao.setText(df.format(vo.getDadosUsuario().getDhAlteracaoDeployment()));
            } else {
            	lblDataCriacao.setText("não especificado");
            }
			if (vo.getDadosUsuario() != null && vo.getDadosUsuario().getNomeUsuarioDeployment() != null) {
	            lblUsuario.setText(vo.getDadosUsuario().getNomeUsuarioDeployment());
            } else {
            	lblUsuario.setText("não especificado");
            }
			lblId.setText(vo.getDeployId() + "");
			lblId.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        }
	}


	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
    }

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		fechaJanela(actionEvent);
	}	
	
	private void fechaJanela(ActionEvent actionEvent) {
	    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
    }	
}
