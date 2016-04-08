package br.com.laminarsoft.jazzforms.ui.componentes.numberfield;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

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
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.numbertextfield.NumberSpinner;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class NumberfieldDetalhesController implements Initializable, IControllerDetalhes {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtLabel;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private ComboBox<String> comboAlignLabel;
	@FXML private CheckBox chkLabelWrap;
	@FXML private CheckBox chkObrigatorio;
	
	@FXML private CheckBox chkAutoCapitalize;
	@FXML private CheckBox chkAutoComplete;
	@FXML private CheckBox chkAutoCorrect;
	@FXML private CheckBox chkSomenteLeitura;
	@FXML private TextField txtPlaceHolder;
	@FXML private TextField txtValorMaximo;
	@FXML private TextField txtValorMinimo;
	@FXML private NumberSpinner maxLength;
	
	private CustomNumberfield customTextField = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		customTextField = (CustomNumberfield)customComponent.getCustomComponent();
		limpaCampos();
		if (validaEntrada()) {
	        if (customTextField != null) {
		        br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Number backComponent = 
		        		(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Number) customTextField.getBackComponent();
		        backComponent.setFieldId(id.getText());
		        backComponent.setDescricao(descricao.getText());
		        String posicaoAnterior = backComponent.getDocked();
		        backComponent.setDocked(comboPosicao.isDisabled() || comboPosicao.getValue().equalsIgnoreCase("center") ? "" : comboPosicao.getValue());

		        backComponent.setLabelWrap(chkLabelWrap.isSelected());
		        backComponent.setRequired(chkObrigatorio.isSelected());
		        
		        backComponent.setMaxValue(maxLength.getNumber().intValue());
		        backComponent.setPlaceHolder(txtPlaceHolder.getText());
		        backComponent.setAutoCapitalize(chkAutoCapitalize.isSelected());
		        backComponent.setAutoComplete(chkAutoComplete.isSelected());
		        backComponent.setAutoCorrect(chkAutoCorrect.isSelected());
		        backComponent.setReadOnly(chkSomenteLeitura.isSelected());

		        customTextField.setLabel(txtLabel.getText());
		        customTextField.setLabelAlign(comboAlignLabel.getValue());
		        
		        if (customTextField instanceof IJFFormComponent) {
		        	IJFFormComponent txtForm = (IJFFormComponent)customComponent;
		        	txtForm.setLabelText(txtLabel.getText());
		        }
		        
		        customTextField.getTextField().setEditable(!chkSomenteLeitura.isSelected());
		        
		        if (customTextField instanceof IDetalhesComponentes && !comboPosicao.getValue().equalsIgnoreCase(posicaoAnterior)) {
			        IJFUIBackComponent pai = ((IDetalhesComponentes) customTextField).getContainerPai();
			        switch (comboPosicao.getValue()) {
					case "right":
						pai.removeComponent(customTextField);
						pai.adicionaComponent(customTextField, "right");
						break;
					case "left":
						pai.removeComponent(customTextField);
						pai.adicionaComponent(customTextField, "left");
						break;
					case "center":
						pai.removeComponent(customTextField);
						pai.adicionaComponent(customTextField, "center");
					}
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
		customTextField = (CustomNumberfield)customComponent.getCustomComponent();
		
		if(customTextField != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Number backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Number)customTextField.getBackComponent();
			id.setText(customTextField.getBackComponent().getFieldId());
			descricao.setText(customTextField.getBackComponent().getDescricao());
			
			String docked = backComponent.getDocked();
			
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			
			maxLength.setNumber(new BigDecimal(backComponent.getMaxValue().intValue()));
			txtPlaceHolder.setText(backComponent.getPlaceHolder());
			chkAutoCapitalize.setSelected(backComponent.getAutoCapitalize());
			chkAutoComplete.setSelected(backComponent.getAutoComplete());
			chkAutoCorrect.setSelected(backComponent.getAutoCorrect());
			chkSomenteLeitura.setSelected(backComponent.getReadOnly());
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			txtLabel.setText(backComponent.getLabel());
			
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}
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
		customTextField = (CustomNumberfield)customComponent.getCustomComponent();
		
		if(customTextField != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Number backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Number)customTextField.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorTextfield() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(customTextField.getBackComponent().getDescricao());

			maxLength.setNumber(new BigDecimal(backComponent.getMaxValue() == null ? 0 :backComponent.getMaxValue()));
			txtPlaceHolder.setText(backComponent.getPlaceHolder());
			chkAutoCapitalize.setSelected(backComponent.getAutoCapitalize());
			chkAutoComplete.setSelected(backComponent.getAutoComplete());
			chkAutoCorrect.setSelected(backComponent.getAutoCorrect());
			chkSomenteLeitura.setSelected(backComponent.getReadOnly());
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			txtLabel.setText(backComponent.getLabel());
			
			String docked = backComponent.getDocked();
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}

			if (customTextField instanceof IJFFormComponent) {
				comboPosicao.setDisable(true);
			}			
			
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
