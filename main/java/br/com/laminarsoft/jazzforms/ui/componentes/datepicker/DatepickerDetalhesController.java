package br.com.laminarsoft.jazzforms.ui.componentes.datepicker;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFFormComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import eu.schudt.javafx.controls.calendar.DatePicker;

public class DatepickerDetalhesController implements Initializable, IControllerDetalhes {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private ComboBox<String> comboAlignLabel;
	@FXML private DatePicker vlrInicial;
	@FXML private TextField anoInicial;
	@FXML private TextField anoFinal;
	@FXML private TextField yearText;
	@FXML private TextField monthText;
	@FXML private TextField dayText;
	@FXML private TextField txtLabel;
	@FXML private CheckBox chkLabelWrap;
	@FXML private CheckBox chkObrigatorio;
	
	private CustomDatepicker customDatePicker = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		customDatePicker = (CustomDatepicker)customComponent.getCustomComponent();
		limpaWarningCampos();
		if (validaEntrada()) {
	        if (customDatePicker != null) {
		        br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker backComponent = (br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker) customDatePicker
		                .getBackComponent();
		        backComponent.setFieldId(id.getText());
		        backComponent.setDescricao(descricao.getText());
		        
		        String posicaoAnterior = backComponent.getDocked();
		        backComponent.setDocked(comboPosicao.getValue());

		        backComponent.setMonthText(monthText.getText());
		        backComponent
		                .setYearFrom((anoInicial.getText() != null && anoInicial.getText().trim().length() > 0 ? Integer.valueOf(anoInicial.getText()) : 0));
		        backComponent.setYearTo((anoFinal.getText() != null && anoFinal.getText().trim().length() > 0 ? Integer.valueOf(anoFinal.getText()) : 0));
		        backComponent.setYearText(yearText.getText());
		        backComponent.setMonthText(monthText.getText());
		        
		        customDatePicker.setLabel(txtLabel.getText());
		        customDatePicker.setLabelAlign(comboAlignLabel.getValue());
		        
		        if(customDatePicker instanceof IJFFormComponent) {
		        	IJFFormComponent formComponent = (IJFFormComponent)customComponent;
		        	formComponent.setLabelText(txtLabel.getText());
		        }

		        if (vlrInicial.getSelectedDate() != null) {
			        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			        backComponent.setValue(sdf.format(vlrInicial.getSelectedDate()));
			        DatePicker dtpk = customDatePicker.getCalendar();
			        dtpk.setSelectedDate(vlrInicial.getSelectedDate());		        
		        }

		        backComponent.setLabelWrap(chkLabelWrap.isSelected());
		        backComponent.setRequired(chkObrigatorio.isSelected());
		        
		        if (customDatePicker instanceof IDetalhesComponentes && !comboPosicao.getValue().equalsIgnoreCase(posicaoAnterior)) {
			        IJFUIBackComponent pai = ((IDetalhesComponentes) customDatePicker).getContainerPai();
			        switch (comboPosicao.getValue()) {
					case "right":
						pai.removeComponent(customDatePicker);
						pai.adicionaComponent(customDatePicker, "right");
						break;
					case "left":
						pai.removeComponent(customDatePicker);
						pai.adicionaComponent(customDatePicker, "left");
						break;
					case "center":
						pai.removeComponent(customDatePicker);
						pai.adicionaComponent(customDatePicker, "center");
					}
				}
			}
		} else {
			JFDialog dialog = new JFDialog();
			dialog.show("Erro ao validar informações", "Ok");
		}
	}	
	
	private void limpaWarningCampos() {
		anoInicial.getStyleClass().remove("text-error");
		anoFinal.getStyleClass().remove("text-error");
		anoInicial.setTooltip(null);
		anoFinal.setTooltip(null);
	}
	
	private boolean validaEntrada() {
		boolean ret = true;
		String anoInicialTxt = anoInicial.getText();
		String anoFinalTxt = anoFinal.getText();
		boolean val1 = validaAno(anoInicialTxt);
		boolean val2 = validaAno(anoFinalTxt);
		if (val1 && val2) {
			Integer int1 = (anoInicialTxt.trim().length() > 0 ? Integer.valueOf(anoInicialTxt) : 0);
			Integer int2 = (anoFinalTxt.trim().length() > 0 ? Integer.valueOf(anoFinalTxt) : 0);
			if (int2 < int1) {
				ret = false;
				anoFinal.getStyleClass().add("text-error");
				anoFinal.setTooltip(new Tooltip("Ano final deve ser maior que ano inicial"));
			} else {
				Calendar cal = new GregorianCalendar();
				cal.setTime(new Date());
				int anoAtual = cal.get(Calendar.YEAR);
				if (int2 != 0 && anoAtual > int2) {
					anoFinal.getStyleClass().add("text-error");
					anoFinal.setTooltip(new Tooltip("Ano final deve ser maior ou igual ao ano atual"));
					ret = false;
				}
			}
		} else {
			ret = false;
		}
		return ret;		
	}

	private boolean validaAno(String valorAno) {
		boolean ret = true;
	    if (valorAno != null && !valorAno.equalsIgnoreCase("0") && valorAno.trim().length() > 0) {
			if(!validaAnoCorreto(valorAno)) {
				anoInicial.getStyleClass().add("text-error");
				anoInicial.setTooltip(new Tooltip("Ano com formato incorreto (dddd)"));
				ret = false;
			}			
		}
	    return ret;
    }
	
	private boolean validaAnoCorreto(String txtAno) {
		boolean val = true;
		if(txtAno.length() != 4) {
			val = false;
		} else {
			try {
	            Integer.valueOf(txtAno);
            } catch (NumberFormatException e) {
            	val = false;
            }
		}
		return val;
	}
	
	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		customDatePicker = (CustomDatepicker)customComponent.getCustomComponent();
		
		if(customDatePicker != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker)customDatePicker.getBackComponent();
			
			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorDatepicker() : fieldId);
			backComponent.setFieldId(fieldId);

			id.setText(backComponent.getFieldId());
			descricao.setText(customDatePicker.getBackComponent().getDescricao());
			txtLabel.setText(backComponent.getLabel());
			
			String docked = backComponent.getDocked();
			monthText.setText(backComponent.getMonthText());
			yearText.setText(backComponent.getYearText());
			dayText.setText(backComponent.getDayText());
			anoInicial.setText((backComponent.getYearFrom() == null ? "" : backComponent.getYearFrom().toString()));
			anoFinal.setText((backComponent.getYearTo() == null ? "" : backComponent.getYearTo().toString()));

			DatePicker dtpk = customDatePicker.getCalendar();
			if (dtpk.getSelectedDate() != null) {
				vlrInicial.setSelectedDate(dtpk.getSelectedDate());
			}
			
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			
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
		customDatePicker = (CustomDatepicker)customComponent.getCustomComponent();
		
		if(customDatePicker != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker)customDatePicker.getBackComponent();
			id.setText(backComponent.getFieldId());
			descricao.setText(customDatePicker.getBackComponent().getDescricao());
			txtLabel.setText(backComponent.getLabel());

			String docked = backComponent.getDocked();
			
			monthText.setText(backComponent.getMonthText());
			yearText.setText(backComponent.getYearText());
			dayText.setText(backComponent.getDayText());
			anoInicial.setText((backComponent.getYearFrom() == null ? "" : backComponent.getYearFrom().toString()));
			anoFinal.setText((backComponent.getYearTo() == null ? "" : backComponent.getYearTo().toString()));
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());

			DatePicker dtpk = customDatePicker.getCalendar();
			if (dtpk.getSelectedDate() != null) {
				vlrInicial.setSelectedDate(dtpk.getSelectedDate());
			}
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}
			
			if (customDatePicker instanceof IJFFormComponent) {
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
