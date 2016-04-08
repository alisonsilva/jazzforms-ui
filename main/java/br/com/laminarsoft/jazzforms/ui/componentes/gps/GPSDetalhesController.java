package br.com.laminarsoft.jazzforms.ui.componentes.gps;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class GPSDetalhesController implements Initializable, IControllerDetalhes {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtLabel;
//	@FXML private NumberSpinner qtdFotos;
//	@FXML private ComboBox<String> cmbDestination;
//	@FXML private ComboBox<String> cmbEncoding;
//	@FXML private ComboBox<String> cmbFonte;
//	@FXML private NumberSpinner pictWidth;
//	@FXML private NumberSpinner pictHeight;

	private CustomGPS customGPS = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		customGPS = (CustomGPS)customComponent.getCustomComponent();
		limpaCampos();
		if (validaEntrada()) {
	        if (customGPS != null) {
		        br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.GPSField backComponent = 
		        		(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.GPSField) customGPS.getBackComponent();
		        backComponent.setFieldId(id.getText());
		        backComponent.setDescricao(descricao.getText());
//		        backComponent.setDocked("bottom");

		        customGPS.setLabel(txtLabel.getText());
//		        customGPS.setLabelAlign(cmbEncoding.getValue());
		        backComponent.setLabel(txtLabel.getText());
		        
//		        backComponent.setPictHeight(pictHeight.getNumber().intValue());
//		        backComponent.setPictWidth(pictWidth.getNumber().intValue());
//		        backComponent.setQuantity(qtdFotos.getNumber().intValue());
		        
//		        String destination = cmbDestination.getSelectionModel().getSelectedItem();
//		        backComponent.setDestination(destination);
		        
//		        String encoding = cmbEncoding.getSelectionModel().getSelectedItem();
//		        backComponent.setEncoding(encoding);
		        
//		        String source = cmbFonte.getSelectionModel().getSelectedItem();
//		        backComponent.setSource(source);
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
		customGPS = (CustomGPS)customComponent.getCustomComponent();
		
		if(customGPS != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.GPSField backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.GPSField)customGPS.getBackComponent();
			id.setText(customGPS.getBackComponent().getFieldId());
			descricao.setText(customGPS.getBackComponent().getDescricao());
			txtLabel.setText(backComponent.getLabel());

//			String destination = backComponent.getDestination();
//			String encoding = backComponent.getEncoding();
//			String source = backComponent.getSource();
//			cmbDestination.getSelectionModel().select(destination);
//			cmbEncoding.getSelectionModel().select(encoding);
//			cmbFonte.getSelectionModel().select(source);
//			
//			qtdFotos.setNumber(new BigDecimal(backComponent.getQuantity()));
//			pictWidth.setNumber(new BigDecimal(backComponent.getPictWidth()));
//			pictHeight.setNumber(new BigDecimal(backComponent.getPictHeight()));
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
	}

	private void inicializacaoCampos() {
		customGPS = (CustomGPS)customComponent.getCustomComponent();
		
		if(customGPS != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.GPSField backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.GPSField)customGPS.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorCamera() : fieldId);
			backComponent.setFieldId(fieldId);
			
			id.setText(backComponent.getFieldId());
			descricao.setText(customGPS.getBackComponent().getDescricao());
			txtLabel.setText(backComponent.getLabel());
			
//			String destination = backComponent.getDestination();
//			String encoding = backComponent.getEncoding();
//			String source = backComponent.getSource();
//			cmbDestination.getSelectionModel().select(destination);
//			cmbEncoding.getSelectionModel().select(encoding);
//			cmbFonte.getSelectionModel().select(source);
//			
//			qtdFotos.setNumber(new BigDecimal(backComponent.getQuantity()));
//			pictWidth.setNumber(new BigDecimal(backComponent.getPictWidth()));
//			pictHeight.setNumber(new BigDecimal(backComponent.getPictHeight()));
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}
}
