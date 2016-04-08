package br.com.laminarsoft.jazzforms.ui.componentes.formpanel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class FormPanelDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> comboMetodo;
	@FXML private TextField txtUrl;
	@FXML private CheckBox chkScrollable;
	@FXML private CheckBox chkSubmitOnAction;
	@FXML private CheckBox chkTrackResetOnLoad;
	
	
	private CustomFormPanel formPanel = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		FormPanel backComponent = (FormPanel)formPanel.getBackComponent();
		backComponent.setFieldId(id.getText());
		backComponent.setDescricao(descricao.getText());
		backComponent.setUrl(txtUrl.getText());
		backComponent.setSubmitOnAction(chkSubmitOnAction.isSelected());
		backComponent.setScrollable(chkScrollable.isSelected());
		backComponent.setTrackResetOnLoad(chkTrackResetOnLoad.isSelected());
		backComponent.setMethod(comboMetodo.getValue());
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		FormPanel backComponent = (FormPanel)formPanel.getBackComponent();
		id.setText(backComponent.getFieldId());
		descricao.setText(backComponent.getDescricao());
		txtUrl.setText(backComponent.getUrl());
		chkScrollable.setSelected(backComponent.getScrollable());
		chkSubmitOnAction.setSelected(backComponent.getSubmitOnAction());
		chkTrackResetOnLoad.setSelected(backComponent.getTrackResetOnLoad());
		comboMetodo.getItems().addAll("post", "get");
		if (backComponent.getMethod() != null && backComponent.getMethod().trim().length() > 0) {
            comboMetodo.setValue(backComponent.getMethod());
        } else {
        	comboMetodo.setValue("post");
        }
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
	}

	private void inicializacaoCampos() {
		formPanel = (CustomFormPanel)customComponent.getCustomComponent();
		if(formPanel != null) {
			FormPanel backComponent = (FormPanel)formPanel.getBackComponent();

			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorFormpanel() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			txtUrl.setText(backComponent.getUrl());
			chkScrollable.setSelected(backComponent.getScrollable());
			chkSubmitOnAction.setSelected(backComponent.getSubmitOnAction());
			chkTrackResetOnLoad.setSelected(backComponent.getTrackResetOnLoad());
			comboMetodo.getItems().addAll("post", "get");
			if (backComponent.getMethod() != null && backComponent.getMethod().trim().length() > 0) {
	            comboMetodo.setValue(backComponent.getMethod());
            } else {
            	comboMetodo.setValue("post");
            }
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}

}
