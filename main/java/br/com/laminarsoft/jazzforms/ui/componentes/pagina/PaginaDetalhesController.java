package br.com.laminarsoft.jazzforms.ui.componentes.pagina;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Layout;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class PaginaDetalhesController implements Initializable, IControllerDetalhes {

	@FXML private TextField nome;	
	@FXML private TextField id;
	@FXML private TextField parentId;
	@FXML private TextArea descricao;	
	@FXML private ColorPicker backGroundCP;	
	@FXML private CheckBox checkRolagemHor;
	@FXML private CheckBox checkRolagemVert;
	@FXML private CheckBox checkModal;
	@FXML private TextField activeItem;
	@FXML private ComboBox<String> comboLayout;
	
	private IDetalhesComponentes customComponent;

	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		Pagina pagina = JazzForms.getPaginaDesenhoAtual();
		List<IJFSelectedComponent> selNode = pagina.getSelectedNodes();
		if(selNode.size() > 0 && selNode.get(0) instanceof Pagina) {
			Pagina titledPane = (Pagina)selNode.get(0);
			titledPane.setNome(nome.getText());
			titledPane.setDescricao(descricao.getText());
			titledPane.setStyleBackgroundColor(backGroundCP.getValue());
			
			String backGroundColor =  Math.floor(backGroundCP.getValue().getRed()*255) + "," +
					Math.floor(backGroundCP.getValue().getGreen()*255) + "," +
					Math.floor(backGroundCP.getValue().getBlue()*255);
			
			titledPane.setBackgroundColor(backGroundColor);
			titledPane.getPaginaModel().setXtype(id.getText());
			titledPane.getPaginaModel().setParentXtype(parentId.getText());
			
			titledPane.getPaginaModel().setNome(nome.getText());
			titledPane.getPaginaModel().setDescricao(descricao.getText());
			titledPane.setBackgroundColor(backGroundColor);
			titledPane.getPaginaModel().getContainer().setActiveItem(activeItem.getText());
			titledPane.getPaginaModel().getContainer().setModal(checkModal.isSelected());
			titledPane.getPaginaModel().getContainer().setVscrollable(checkRolagemVert.isSelected());
			titledPane.getPaginaModel().getContainer().setHscrollable(checkRolagemHor.isSelected());
			titledPane.getPaginaModel().getContainer().getLayout().setNome(comboLayout.getValue());
		}			
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		List<IJFSelectedComponent> selNode = pagina.getSelectedNodes();
		if(selNode.size() > 0 && selNode.get(0) instanceof Pagina) {
			Pagina titledPane = (Pagina)selNode.get(0);
			nome.setText(titledPane.getNome());
			descricao.setText(titledPane.getDescricao());
			backGroundCP.setValue(titledPane.getStyleBackgroundColor());
		}	
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		List<IJFSelectedComponent> selNode = pagina.getSelectedNodes();
		if(selNode.size() > 0 && selNode.get(0) instanceof Pagina) {
			Pagina titledPane = (Pagina)selNode.get(0);
			
			String pgNome = titledPane.getNome();
			pgNome = (pgNome.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorPagina() : pgNome);
			titledPane.setNome(pgNome);
			
			nome.setText(titledPane.getNome());
			descricao.setText(titledPane.getDescricao());
			backGroundCP.setValue(titledPane.getStyleBackgroundColor());
			
			for(String cmbOption : Layout.getTypes()) {
				comboLayout.getItems().add(cmbOption);
			}
			comboLayout.setValue(titledPane.getPaginaModel().getContainer().getLayout().getNome());
			activeItem.setText(titledPane.getPaginaModel().getContainer().getActiveItem());
			id.setText(titledPane.getPaginaModel().getXtype());
			parentId.setText(titledPane.getPaginaModel().getParentXtype());
			checkModal.setSelected(titledPane.getPaginaModel().getContainer().getModal());
			checkRolagemVert.setSelected(titledPane.getPaginaModel().getContainer().getVscrollable());
			checkRolagemHor.setSelected(titledPane.getPaginaModel().getContainer().getHscrollable());
		}
	}	
	
	@FXML
	protected void rolagemVertAction(ActionEvent actionEvent) {
		
	}
	
	@FXML
	protected void rolagemHorAction(ActionEvent actionEvent) {
	}
	
	@FXML
	protected void modalAction(ActionEvent actionEvent) {
		
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
	}

	
}
