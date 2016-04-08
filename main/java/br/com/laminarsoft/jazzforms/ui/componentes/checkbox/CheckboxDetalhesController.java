package br.com.laminarsoft.jazzforms.ui.componentes.checkbox;

import java.net.URL;
import java.util.ResourceBundle;

import org.drools.core.util.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFFormComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CheckboxDetalhesController implements Initializable, IControllerDetalhes {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtLabel;
	@FXML private ComboBox<String> comboAlignLabel;
	@FXML private CheckBox chkLabelWrap;
	@FXML private CheckBox chkToggled;
	
	private CustomCheckbox customCheckbox = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		customCheckbox = (CustomCheckbox)customComponent.getCustomComponent();
		limpaCampos();
		if (validaEntrada()) {
	        if (customCheckbox != null) {
		        br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.CheckBox backComponent = 
		        		(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.CheckBox) customCheckbox.getBackComponent();
		        backComponent.setFieldId(id.getText());
		        backComponent.setDescricao(descricao.getText());

		        backComponent.setLabelWrap(chkLabelWrap.isSelected());
		        
		        backComponent.setLabel(txtLabel.getText());
		        customCheckbox.setLabel(txtLabel.getText());
		        customCheckbox.setLabelAlign(comboAlignLabel.getValue());
		        backComponent.setChecked(chkToggled.isSelected());
		        ((CheckBox)customCheckbox.getLocalCheck()).setSelected(chkToggled.isSelected());
		        
		        if (customCheckbox instanceof IJFFormComponent) {
		        	IJFFormComponent txtForm = (IJFFormComponent)customComponent;
		        	txtForm.setLabelText(txtLabel.getText());
		        }
			}
		} else {
			JFDialog dialog = new JFDialog();
			dialog.show("Erro ao validar informações", "Ok");
		}
	}	
	
	private void limpaCampos() {

	}
	
	private boolean validaEntrada() {
		boolean ret = true;
		return ret;		
	}

	
	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		customCheckbox = (CustomCheckbox)customComponent.getCustomComponent();
		
		if(customCheckbox != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.CheckBox backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.CheckBox)customCheckbox.getBackComponent();
			id.setText(customCheckbox.getBackComponent().getFieldId());
			descricao.setText(customCheckbox.getBackComponent().getDescricao());
			
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkToggled.setSelected(backComponent.getChecked());
			
			txtLabel.setText(backComponent.getLabel());

			comboAlignLabel.getItems().addAll("top", "right", "bottom", "left");
			if(backComponent.getLabelAlign() != null && backComponent.getLabelAlign().trim().length() > 0) {
				comboAlignLabel.setValue(backComponent.getLabelAlign());
			} else {
				comboAlignLabel.setValue("left");
			}
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
	}

	private void inicializacaoCampos() {
		customCheckbox = (CustomCheckbox)customComponent.getCustomComponent();
		
		if(customCheckbox != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.CheckBox backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.CheckBox)customCheckbox.getBackComponent();
			String fieldId = backComponent.getFieldId();
			fieldId = (StringUtils.isEmpty(fieldId) ? IdentificadorUtil.getNextIdentificadorCehckbox() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(customCheckbox.getBackComponent().getFieldId());
			descricao.setText(customCheckbox.getBackComponent().getDescricao());

			txtLabel.setText(backComponent.getLabel());
			chkToggled.setSelected(backComponent.getChecked());
			
			
			comboAlignLabel.getItems().addAll("top", "right", "bottom", "left");
			if(backComponent.getLabelAlign() != null && backComponent.getLabelAlign().trim().length() > 0) {
				comboAlignLabel.setValue(backComponent.getLabelAlign());
			} else {
				comboAlignLabel.setValue("left");
			}
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}
}
