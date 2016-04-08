package br.com.laminarsoft.jazzforms.ui.componentes.toggle;

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
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class ToggleDetalhesController implements Initializable, IControllerDetalhes {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtLabel;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private ComboBox<String> comboAlignLabel;
	@FXML private CheckBox chkLabelWrap;
	@FXML private CheckBox chkObrigatorio;
	@FXML private CheckBox chkToggled;
	
	private CustomToggle customToggle = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		customToggle = (CustomToggle)customComponent.getCustomComponent();
		limpaCampos();
		if (validaEntrada()) {
	        if (customToggle != null) {
		        br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle backComponent = 
		        		(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle) customToggle.getBackComponent();
		        backComponent.setFieldId(id.getText());
		        backComponent.setDescricao(descricao.getText());
		        String posicaoAnterior = backComponent.getDocked();
		        backComponent.setDocked(comboPosicao.getValue());

		        backComponent.setLabelWrap(chkLabelWrap.isSelected());
		        backComponent.setRequired(chkObrigatorio.isSelected());
		        
		        backComponent.setLabel(txtLabel.getText());
		        customToggle.setLabel(txtLabel.getText());
		        customToggle.setLabelAlign(comboAlignLabel.getValue());
		        backComponent.setValue((chkToggled.isSelected() ? "1" : "0"));
		        customToggle.setToggled(chkToggled.isSelected());
		        
		        if (customToggle instanceof IJFFormComponent) {
		        	IJFFormComponent txtForm = (IJFFormComponent)customComponent;
		        	txtForm.setLabelText(txtLabel.getText());
		        }
		        
		        
		        if (customToggle instanceof IDetalhesComponentes && !comboPosicao.getValue().equalsIgnoreCase(posicaoAnterior)) {
			        IJFUIBackComponent pai = ((IDetalhesComponentes) customToggle).getContainerPai();
			        switch (comboPosicao.getValue()) {
					case "right":
						pai.removeComponent(customToggle);
						pai.adicionaComponent(customToggle, "right");
						break;
					case "left":
						pai.removeComponent(customToggle);
						pai.adicionaComponent(customToggle, "left");
						break;
					case "center":
						pai.removeComponent(customToggle);
						pai.adicionaComponent(customToggle, "center");
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
		customToggle = (CustomToggle)customComponent.getCustomComponent();
		
		if(customToggle != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle)customToggle.getBackComponent();
			id.setText(customToggle.getBackComponent().getFieldId());
			descricao.setText(customToggle.getBackComponent().getDescricao());
			
			String docked = backComponent.getDocked();
			
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			chkToggled.setSelected((backComponent.getValue().equalsIgnoreCase("0") ? false : true));

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
		customToggle = (CustomToggle)customComponent.getCustomComponent();
		
		if(customToggle != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Toggle)customToggle.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorToggle() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(customToggle.getBackComponent().getDescricao());
			
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			

			txtLabel.setText(backComponent.getLabel());
			chkToggled.setSelected((backComponent.getValue().equalsIgnoreCase("0") ? false : true));
			
			String docked = backComponent.getDocked();
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}

			if (customToggle instanceof IJFFormComponent) {
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
