package br.com.laminarsoft.jazzforms.ui.componentes.titlebar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.TitleBar;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class TitlebarDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private TextField titulo;
	
	private CustomTitleBar titleBar = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		titleBar.getTitleBar().setDescricao(descricao.getText());
		titleBar.getTitleBar().setFieldId(id.getText());
		titleBar.getTitleBar().setTitle(titulo.getText());
		titleBar.setLabelText(titulo.getText());
		titleBar.getTitleBar().setDocked(comboPosicao.getValue());
		
		String posicao = comboPosicao.getValue();
		IJFUIBackComponent cmpPai = titleBar.getContainerPai();;
		cmpPai.removeComponent(titleBar);
		cmpPai.adicionaComponent(titleBar, posicao);		
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		id.setText(titleBar.getTitleBar().getFieldId());
		descricao.setText(titleBar.getTitleBar().getDescricao());
		titulo.setText(titleBar.getTitleBar().getTitle());
		String docked = titleBar.getTitleBar().getDocked();
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
		
//		inicializacaoCampos();
		
	}

	private void inicializacaoCampos() {
		titleBar = (CustomTitleBar)customComponent.getCustomComponent();
		IJFUIBackComponent parentComponent = titleBar.getContainerPai();
		
		if(titleBar != null) {
			TitleBar backComponent = (TitleBar)titleBar.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorTitlebar() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(titleBar.getTitleBar().getDescricao());
			titulo.setText(titleBar.getTitleBar().getTitle());
			String docked = titleBar.getTitleBar().getDocked();
			comboPosicao.getItems().addAll("top", "bottom");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
				parentComponent.removeComponent(titleBar);
				parentComponent.adicionaComponent(titleBar, docked);
			} else {
				parentComponent.removeComponent(titleBar);
				parentComponent.adicionaComponent(titleBar, "top");
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
