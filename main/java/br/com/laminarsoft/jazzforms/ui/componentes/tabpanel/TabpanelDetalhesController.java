package br.com.laminarsoft.jazzforms.ui.componentes.tabpanel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class TabpanelDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> comboPosicao;
	
	private Pagina pagina = null;
	private CustomTabpanel tabPanel = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		tabPanel.getTabPanel().setDescricao(descricao.getText());
		tabPanel.getTabPanel().setFieldId(id.getText());
		tabPanel.getTabPanel().setDocked(comboPosicao.getValue());
		
		String posicao = comboPosicao.getValue();
		IJFUIBackComponent cmpPai = pagina;
		cmpPai.removeComponent(tabPanel);
		cmpPai.adicionaComponent(tabPanel, posicao);		
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		id.setText(tabPanel.getTabPanel().getFieldId());
		descricao.setText(tabPanel.getTabPanel().getDescricao());
		String docked = tabPanel.getTabPanel().getDocked();
		comboPosicao.getItems().clear();
		comboPosicao.getItems().addAll("top", "bottom");
		if (docked != null && docked.trim().length() > 0) {
			comboPosicao.setValue(docked);
		} else {
			comboPosicao.setValue("top");
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		pagina = JazzForms.getPaginaDesenhoAtual();
		
//		inicializacaoCampos();
		
	}

	private void inicializacaoCampos() {
		tabPanel = (CustomTabpanel)customComponent.getCustomComponent();
		
		if(tabPanel != null) {
			String fieldId = tabPanel.getTabPanel().getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorTabpanel() : fieldId);
			tabPanel.getTabPanel().setFieldId(fieldId);
			
			id.setText(tabPanel.getTabPanel().getFieldId());
			descricao.setText(tabPanel.getTabPanel().getDescricao());
			String docked = tabPanel.getTabPanel().getDocked();
			comboPosicao.getItems().addAll("top", "bottom");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
				this.pagina.removeComponent(tabPanel);
				this.pagina.adicionaComponent(tabPanel, docked);
			} else {
				this.pagina.removeComponent(tabPanel);
				this.pagina.adicionaComponent(tabPanel, "top");
				comboPosicao.setValue("top");
			}
			
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}

}
