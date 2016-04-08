package br.com.laminarsoft.jazzforms.ui.componentes.toolbar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class ToolbarDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private TextField titulo;
	
	private Pagina pagina = null;
	private CustomToolBar toolBar = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		toolBar.getToolBar().setDescricao(descricao.getText());
		toolBar.getToolBar().setFieldId(id.getText());
		toolBar.getToolBar().setTitle(titulo.getText());
		toolBar.setLabelText(titulo.getText());
		toolBar.getToolBar().setDocked(comboPosicao.getValue());
		
		String posicao = comboPosicao.getValue();
		IJFUIBackComponent cmpPai = toolBar.getContainerPai();
		cmpPai.removeComponent(toolBar);
		cmpPai.adicionaComponent(toolBar, posicao);		
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		id.setText(toolBar.getToolBar().getFieldId());
		descricao.setText(toolBar.getToolBar().getDescricao());
		titulo.setText(toolBar.getToolBar().getTitle());
		String docked = toolBar.getToolBar().getDocked();
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
		toolBar = (CustomToolBar)customComponent.getCustomComponent();
		IJFUIBackComponent parentComponent = toolBar.getContainerPai();
		
		if(toolBar != null) {
			ToolBar backComponent = (ToolBar)toolBar.getBackComponent();

			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorToolbar() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(toolBar.getToolBar().getDescricao());
			titulo.setText(toolBar.getToolBar().getTitle());
			String docked = toolBar.getToolBar().getDocked();
			comboPosicao.getItems().addAll("top", "bottom");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
				parentComponent.removeComponent(toolBar);
				parentComponent.adicionaComponent(toolBar, docked);
			} else {
				parentComponent.removeComponent(toolBar);
				parentComponent.adicionaComponent(toolBar, "top");
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
