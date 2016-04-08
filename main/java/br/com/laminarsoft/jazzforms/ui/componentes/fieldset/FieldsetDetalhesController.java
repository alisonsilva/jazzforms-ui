package br.com.laminarsoft.jazzforms.ui.componentes.fieldset;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class FieldsetDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtTitulo;
	@FXML private TextField txtInstructions;
	@FXML private TextField itemId;

	private CustomFieldset fieldSet = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		FieldSet backComponent = (FieldSet)fieldSet.getBackComponent();
		backComponent.setFieldId(id.getText());
		backComponent.setDescricao(descricao.getText());
		fieldSet.setTitle(txtTitulo.getText());
		fieldSet.setInstruction(txtInstructions.getText());
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		fieldSet = (CustomFieldset)customComponent.getCustomComponent();
		if(fieldSet != null) {
			FieldSet backComponent = (FieldSet)fieldSet.getBackComponent();
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			txtTitulo.setText(backComponent.getTitle());
			txtInstructions.setText(backComponent.getInstructions());
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		
	}

	private void inicializacaoCampos() {
		fieldSet = (CustomFieldset)customComponent.getCustomComponent();
		if(fieldSet != null) {
			FieldSet backComponent = (FieldSet)fieldSet.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorFieldset() : fieldId);
			backComponent.setFieldId(fieldId);

			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			txtTitulo.setText(backComponent.getTitle());
			txtInstructions.setText(backComponent.getInstructions());
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}

}
