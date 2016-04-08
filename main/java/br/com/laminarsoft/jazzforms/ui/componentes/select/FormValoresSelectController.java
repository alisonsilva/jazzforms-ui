package br.com.laminarsoft.jazzforms.ui.componentes.select;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Option;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class FormValoresSelectController implements Initializable {
	@FXML private TextField txtTexto;
	@FXML private TextField txtValor;
	
	private Option opcaoSelecionada = null;
	private TableView<Option> tblOpcoes;
	
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		
	}

	public void setTxtTexto(String texto) {
		this.txtTexto.setText(texto);
	}
	
	public void setTxtValor(String valor) {
		this.txtValor.setText(valor);
	}
	
	public void setOpcao(Option option) {
		this.opcaoSelecionada = option;
	}
	
	public void setTblOpcoes(TableView<Option> tbl) {
		this.tblOpcoes = tbl;
	}
	
	@FXML
	public void cancelaAction(ActionEvent actionEvent) {
		JFDialog dialog = new JFDialog();
		final Node source = (Node) actionEvent.getSource();
		
		EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
				Stage stage = (Stage) source.getScene().getWindow();
				stage.close();
			}
		};
		
		dialog.show("Tem certeza que deseja cancelar edição?", "Sim", "Não", act1);				
	}
	
	@FXML
	public void confirmaAction(ActionEvent actionEvent) {
		if (txtTexto.getText().trim().length() == 0 || 
				txtValor.getText().trim().length() == 0) {
			JFDialog dialog = new JFDialog();
			dialog.show("É necessário preencher texto e valor", "Ok");
		} else {
			final Node source = (Node) actionEvent.getSource();
			if (opcaoSelecionada != null) {
				opcaoSelecionada.setText(txtTexto.getText());
				opcaoSelecionada.setValue(txtValor.getText());
			} else {			
				opcaoSelecionada = new Option();
				opcaoSelecionada.setText(txtTexto.getText());
				opcaoSelecionada.setValue(txtValor.getText());
				tblOpcoes.getItems().add(opcaoSelecionada);
			}
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
			Stage stage = (Stage) source.getScene().getWindow();
			stage.close();
		}
	}
}
