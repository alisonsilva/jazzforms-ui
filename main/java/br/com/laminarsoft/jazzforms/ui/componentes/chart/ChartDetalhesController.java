package br.com.laminarsoft.jazzforms.ui.componentes.chart;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Chart;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;

public class ChartDetalhesController implements Initializable, IControllerDetalhes{
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField itemId;
	@FXML private CheckBox chkShadow;
	@FXML private TextField txtTema;
	@FXML private CheckBox chkAnimate;
	@FXML private CheckBox chkPolarChart;
	@FXML private CheckBox chkScrollable;
	@FXML private TextField txtInsetPadding;
	@FXML private CheckBox chkFlipXY;

	private CustomChart customChart = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		Chart backComponent = (Chart)customChart.getBackComponent();
		backComponent.setFieldId(id.getText());
		backComponent.setDescricao(descricao.getText());
		backComponent.setShadow(chkShadow.isSelected());
		backComponent.setTheme(txtTema.getText());
		backComponent.setAnimate(chkAnimate.isSelected());
		backComponent.setPolarChart(chkPolarChart.isSelected());
		backComponent.setChartScrollable(chkScrollable.isSelected());
		backComponent.setFlipXY(chkFlipXY.isSelected());
		String insPadding = txtInsetPadding.getText();
		if(StringUtils.isNotEmpty(insPadding) && StringUtils.isNumeric(insPadding)) {
			backComponent.setInsetPadding(Integer.valueOf(insPadding));
		} else {
			backComponent.setInsetPadding(null);
		}
		
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		customChart = (CustomChart)customComponent.getCustomComponent();
		if(customChart != null) {
			Chart backComponent = (Chart)customChart.getBackComponent();
			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			chkShadow.setSelected(backComponent.getShadow());
			txtTema.setText(backComponent.getTheme());
			chkAnimate.setSelected(backComponent.getAnimate());
			chkFlipXY.setSelected(backComponent.getFlipXY());
			chkPolarChart.setSelected(backComponent.getPolarChart() == null || !backComponent.getPolarChart()? false : backComponent.getPolarChart());
			chkScrollable.setSelected(backComponent.getChartScrollable() == null || !backComponent.getChartScrollable() ? false : true);
			if (backComponent.getInsetPadding() != null && backComponent.getInsetPadding() > 0) {
				txtInsetPadding.setText(backComponent.getInsetPadding() + "");
			}
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		
	}

	private void inicializacaoCampos() {
		customChart = (CustomChart)customComponent.getCustomComponent();
		if(customChart != null) {
			Chart backComponent = (Chart)customChart.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorChart() : fieldId);
			backComponent.setFieldId(fieldId);

			id.setText(backComponent.getFieldId());
			descricao.setText(backComponent.getDescricao());
			chkShadow.setSelected(backComponent.getShadow());
			txtTema.setText(backComponent.getTheme());
			chkAnimate.setSelected(backComponent.getAnimate());
			chkFlipXY.setSelected(backComponent.getFlipXY());
			chkPolarChart.setSelected(backComponent.getPolarChart() == null ? false : backComponent.getPolarChart());
			chkPolarChart.setSelected(backComponent.getPolarChart() == null || !backComponent.getPolarChart()? false : backComponent.getPolarChart());
			chkScrollable.setSelected(backComponent.getChartScrollable() == null || !backComponent.getChartScrollable() ? false : true);
			if (backComponent.getInsetPadding() != null && backComponent.getInsetPadding() > 0) {
				txtInsetPadding.setText(backComponent.getInsetPadding() + "");
			}
			
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}

}
