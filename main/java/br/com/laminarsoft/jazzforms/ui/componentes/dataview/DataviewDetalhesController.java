package br.com.laminarsoft.jazzforms.ui.componentes.dataview;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.apache.commons.lang.StringUtils;

import br.com.laminarsoft.jazzforms.persistencia.model.ImplementacaoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DataView;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.numbertextfield.NumberSpinner;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.IEditSourceJavaScript;
import br.com.laminarsoft.jazzforms.ui.dialog.JFEditSourceDialog;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

public class DataviewDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtItemTpl;
	@FXML private TextField txtStore;
	@FXML private TextField txtObjeto;
	@FXML private TextField txtEmptyText;
	@FXML private TextField txtItemClass;
	@FXML private TextField txtDataViewLoadingText;
	@FXML private NumberSpinner txtMaxItemCache;
	@FXML private TextField txtDataViewPressedCls;
	@FXML private TextField txtDefaultType;
	@FXML private TextField txtDataViewSelectedCls;
	@FXML private ComboBox<String> cmbViewMode;
	@FXML private ComboBox<String> cmbScroll;
	@FXML private ComboBox<String> cmbTplWriteMode;
	@FXML private ComboBox<String> cmbTriggerEvent;
	@FXML private CheckBox chkScrollVertical;
	@FXML private CheckBox chkDisableSelection;
	@FXML private CheckBox chkUseComponents;
	
	@FXML private Button editJSTPL;
	@FXML private Button editJSStore;

	private CustomDataview dataView = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		DataView backComponent = (DataView)dataView.getBackComponent();
		backComponent.setFieldId(id.getText());
		backComponent.setDescricao(descricao.getText());
		
		//backComponent.setItemTpl(txtItemTpl.getText());		
		//backComponent.setStore(txtStore.getText());
		backComponent.setObject(txtObjeto.getText());
		backComponent.setEmptyText(txtEmptyText.getText());
		backComponent.setItemCls(txtItemClass.getText());
		backComponent.setDataViewLoadingText(txtDataViewLoadingText.getText());
		backComponent.setMaxItemCache((txtMaxItemCache.getNumber() != null ? txtMaxItemCache.getNumber().intValue() : backComponent.getMaxItemCache()));
		backComponent.setDataViewPressedCls(txtDataViewPressedCls.getText());
		backComponent.setDataViewSelectedCls(txtDataViewSelectedCls.getText());
		backComponent.setDtViewMode(cmbViewMode.getValue());
		backComponent.setDataViewScrollable(cmbScroll.getValue());
		backComponent.setTplWriteMode(cmbTplWriteMode.getValue());
		backComponent.setTriggerEvent(cmbTriggerEvent.getValue());
		backComponent.setDefaultType(txtDefaultType.getText());
		
		backComponent.setVscrollable(chkScrollVertical.isSelected());
		backComponent.setDisableSelection(chkDisableSelection.isSelected());
		backComponent.setUseComponents(chkUseComponents.isSelected());
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		dataView = (CustomDataview)customComponent.getCustomComponent();
		if(dataView != null) {
			DataView backComponent = (DataView)dataView.getBackComponent();
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			
			if(!StringUtils.isEmpty(backComponent.getItemTpl())) {
				txtItemTpl.setText("Preenchido...");
			}
			if (!StringUtils.isEmpty(backComponent.getStore())) {
				txtStore.setText("Preenchido...");
			}

			txtObjeto.setText(backComponent.getObject());
			txtEmptyText.setText(backComponent.getEmptyText());
			txtItemClass.setText(backComponent.getItemCls());
			txtDataViewLoadingText.setText(backComponent.getDataViewLoadingText());
			txtMaxItemCache.setNumber(new BigDecimal(backComponent.getMaxItemCache()));
			txtDataViewPressedCls.setText(backComponent.getDataViewPressedCls());
			txtDataViewSelectedCls.setText(backComponent.getDataViewSelectedCls());
			txtDefaultType.setText(backComponent.getDefaultType());
			cmbViewMode.setValue(backComponent.getDtViewMode());
			cmbScroll.setValue(backComponent.getDataViewScrollable());
			cmbTplWriteMode.setValue(backComponent.getTplWriteMode());
			cmbTriggerEvent.setValue(backComponent.getTriggerEvent());

			chkScrollVertical.setSelected(backComponent.getVscrollable());
			chkDisableSelection.setSelected(backComponent.getDisableSelection());
			chkUseComponents.setSelected(backComponent.getUseComponents());
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		AwesomeDude.setIcon(editJSTPL, AwesomeIcon.PENCIL, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(editJSStore, AwesomeIcon.PENCIL, "16px", ContentDisplay.CENTER);
	}

	private void inicializacaoCampos() {
		dataView = (CustomDataview)customComponent.getCustomComponent();
		if(dataView != null) {
			DataView backComponent = (DataView)dataView.getBackComponent();

			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorDataview() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			if (!StringUtils.isEmpty(backComponent.getItemTpl())) {
				txtItemTpl.setText("preenchido...");
			}
			if (!StringUtils.isEmpty(new String(backComponent.getDataViewStore()))) {
	            txtStore.setText("preenchido...");
            }
			txtObjeto.setText(backComponent.getObject());
			txtEmptyText.setText(backComponent.getEmptyText());
			txtItemClass.setText(backComponent.getItemCls());
			txtDataViewLoadingText.setText(backComponent.getDataViewLoadingText());
			txtMaxItemCache.setNumber(new BigDecimal(backComponent.getMaxItemCache()));
			txtDataViewPressedCls.setText(backComponent.getDataViewPressedCls());
			txtDataViewSelectedCls.setText(backComponent.getDataViewSelectedCls());
			txtDefaultType.setText(backComponent.getDefaultType());

			cmbViewMode.getItems().addAll("SINGLE","SIMPLE", "MULTI");
			cmbViewMode.setValue(backComponent.getDtViewMode());
			cmbScroll.getItems().addAll("horizontal","vertical", "both");
			cmbScroll.setValue(backComponent.getDataViewScrollable());
			cmbTplWriteMode.getItems().addAll("append", "insertAfter", "insertBefore", "insertFirst", "overwrite");
			cmbTplWriteMode.setValue(backComponent.getTplWriteMode());
			cmbTriggerEvent.getItems().addAll("itemtap", "itemsingletap", "itemdoubletap", "itemswipe", "itemtaphold");
			cmbTriggerEvent.setValue(backComponent.getTriggerEvent());

			chkScrollVertical.setSelected(backComponent.getVscrollable());
			chkDisableSelection.setSelected(backComponent.getDisableSelection());
			chkUseComponents.setSelected(backComponent.getUseComponents());
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}

	
	@FXML
	protected void editJSItemTPLAction(ActionEvent actionEvent) {
		JFEditSourceDialog sourceEditDiag = new JFEditSourceDialog();
		byte[] codigo = ((DataView)customComponent.getBackComponent()).getItemTpl().getBytes();
		String cdStr = (codigo != null && codigo.length > 0 ? new String(codigo) : "");
		sourceEditDiag.showHTML(cdStr, "Editando html", new IEditSourceJavaScript() {
			
			@Override
			public void setupJavaScriptCode(byte[] codigo) {
				((DataView)customComponent.getBackComponent()).setItemTpl(new String(codigo));
				txtItemTpl.setText("preenchido...");
			}
			
			@Override
			public void removeImplementacao(ImplementacaoEvento implementacao) {
				customComponent.getBackComponent().getImplementacoes().remove(implementacao);
				
			}
			
			@Override
			public List<ImplementacaoEvento> getImplementacoes() {
				return customComponent.getBackComponent().getImplementacoes();
			}
			
			@Override
			public void addImplementacao(ImplementacaoEvento implementacao) {
				customComponent.getBackComponent().getImplementacoes().add(implementacao);
			}
		});
		
	}	
	
	@FXML
	protected void editJSBibliotecaAction(ActionEvent actionEvent) {
		JFEditSourceDialog sourceEditDiag = new JFEditSourceDialog();
		byte[] codigo = ((DataView)customComponent.getBackComponent()).getDataViewStore();
		String cdStr = (codigo != null && codigo.length > 0 ? new String(codigo) : "");
		sourceEditDiag.showJavaScript(cdStr, "Editando javascript", new IEditSourceJavaScript() {
			
			@Override
			public void setupJavaScriptCode(byte[] codigo) {
				((DataView)customComponent.getBackComponent()).setDataViewStore(codigo);
				txtStore.setText("preenchido...");
			}
			
			@Override
			public void removeImplementacao(ImplementacaoEvento implementacao) {
				customComponent.getBackComponent().getImplementacoes().remove(implementacao);
				
			}
			
			@Override
			public List<ImplementacaoEvento> getImplementacoes() {
				return customComponent.getBackComponent().getImplementacoes();
			}
			
			@Override
			public void addImplementacao(ImplementacaoEvento implementacao) {
				customComponent.getBackComponent().getImplementacoes().add(implementacao);
			}
		});
		
	}	
}
