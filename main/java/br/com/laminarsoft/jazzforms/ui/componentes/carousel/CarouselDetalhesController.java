package br.com.laminarsoft.jazzforms.ui.componentes.carousel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Carousel;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class CarouselDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> cmbDirection;
	@FXML private CheckBox chkIndicator;
	
	private CustomCarousel fieldSet = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		Carousel backComponent = (Carousel)fieldSet.getBackComponent();
		backComponent.setFieldId(id.getText());
		backComponent.setDescricao(descricao.getText());
		backComponent.setDirection(cmbDirection.getValue());
		backComponent.setIndicator(chkIndicator.isSelected());
		
		fieldSet.btnConfirmaAction(actionEvent);
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		fieldSet = (CustomCarousel)customComponent.getCustomComponent();
		if(fieldSet != null) {
			Carousel backComponent = (Carousel)fieldSet.getBackComponent();
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorCarousel() : fieldId);
			backComponent.setFieldId(fieldId);
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			cmbDirection.setValue(backComponent.getDirection());
			chkIndicator.setSelected(backComponent.getIndicator());
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		
	}

	private void inicializacaoCampos() {
		fieldSet = (CustomCarousel)customComponent.getCustomComponent();
		if(fieldSet != null) {
			Carousel backComponent = (Carousel)fieldSet.getBackComponent();
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());

			cmbDirection.getItems().addAll(new String[]{"horizontal", "vertical"});
			
			cmbDirection.setValue(backComponent.getDirection());
			chkIndicator.setSelected(backComponent.getIndicator());
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}

}
