package br.com.laminarsoft.jazzforms.ui.componentes.select;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Option;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.Evento;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFFormComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.numbertextfield.NumberSpinner;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class SelectDetalhesController implements Initializable, IControllerDetalhes {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private TextField txtLabel;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private ComboBox<String> comboAlignLabel;
	@FXML private TableView<Option> tblOpcoes;
	@FXML private CheckBox chkLabelWrap;
	@FXML private CheckBox chkObrigatorio;
	
	@FXML private CheckBox chkAutoCapitalize;
	@FXML private CheckBox chkAutoComplete;
	@FXML private CheckBox chkAutoCorrect;
	@FXML private CheckBox chkSomenteLeitura;
	@FXML private CheckBox chkAutoSelect;
	@FXML private CheckBox chkUsePicker;
	
	@FXML private TextField txtPlaceHolder;
	@FXML private NumberSpinner maxLength;
	
	@FXML private Button btnApaga;
	@FXML private Button btnEdita;
	@FXML private Button btnAdicionaOpcao;
	
	private CustomSelect customSelect = null;
	private IDetalhesComponentes customComponent;
	
	private TableColumn clTexto;
	private TableColumn clValor;
	
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		customSelect = (CustomSelect)customComponent.getCustomComponent();
		limpaCampos();
		if (validaEntrada()) {
	        if (customSelect != null) {
		        br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select backComponent = 
		        		(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select) customSelect.getBackComponent();
		        ComboBox<Option> comboBox = customSelect.getComboBox();
		        backComponent.setFieldId(id.getText());
		        backComponent.setDescricao(descricao.getText());
		        String posicaoAnterior = backComponent.getDocked();
		        backComponent.setDocked(comboPosicao.getValue());

		        backComponent.setRequired(chkObrigatorio.isSelected());
		        
		        customSelect.setLabel(txtLabel.getText());
		        customSelect.setLabelAlign(comboAlignLabel.getValue());
		        
	        	backComponent.getOptions().clear();
	        	comboBox.getItems().clear();
		        for(Option option : tblOpcoes.getItems()) {
		        	backComponent.getOptions().add(option);
		        	comboBox.getItems().add(option);
		        }
		        
		        backComponent.setLabelWrap(chkLabelWrap.isSelected());

		        backComponent.setMaxLength(maxLength.getNumber().intValue());
		        backComponent.setPlaceHolder(txtPlaceHolder.getText());
		        backComponent.setAutoCapitalize(chkAutoCapitalize.isSelected());
		        backComponent.setAutoComplete(chkAutoComplete.isSelected());
		        backComponent.setAutoCorrect(chkAutoCorrect.isSelected());
		        backComponent.setReadOnly(chkSomenteLeitura.isSelected());
		        backComponent.setLabel(txtLabel.getText());
		        
		        backComponent.setAutoSelect(chkAutoSelect.isSelected());
		        backComponent.setUsePicker(chkUsePicker.isSelected());
		        
		        if (customSelect instanceof IJFFormComponent) {
		        	IJFFormComponent txtForm = (IJFFormComponent)customComponent;
		        	txtForm.setLabelText(txtLabel.getText());
		        }
		        
		        customSelect.getComboBox().setEditable(!chkSomenteLeitura.isSelected());
		        
		        if (customSelect instanceof IDetalhesComponentes && !comboPosicao.getValue().equalsIgnoreCase(posicaoAnterior)) {
			        IJFUIBackComponent pai = ((IDetalhesComponentes) customSelect).getContainerPai();
			        switch (comboPosicao.getValue()) {
					case "right":
						pai.removeComponent(customSelect);
						pai.adicionaComponent(customSelect, "right");
						break;
					case "left":
						pai.removeComponent(customSelect);
						pai.adicionaComponent(customSelect, "left");
						break;
					case "center":
						pai.removeComponent(customSelect);
						pai.adicionaComponent(customSelect, "center");
					}
				}
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
		customSelect = (CustomSelect)customComponent.getCustomComponent();
		
		if(customSelect != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select)customSelect.getBackComponent();
			id.setText(customSelect.getBackComponent().getFieldId());
			descricao.setText(customSelect.getBackComponent().getDescricao());
			
			tblOpcoes.getItems().clear();
			tblOpcoes.setItems(FXCollections.observableArrayList(backComponent.getOptions()));
			
			
			String docked = backComponent.getDocked();
			
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			
			maxLength.setNumber(new BigDecimal(backComponent.getMaxLength().intValue()));
			txtPlaceHolder.setText(backComponent.getPlaceHolder());
			chkAutoCapitalize.setSelected(backComponent.getAutoCapitalize());
			chkAutoComplete.setSelected(backComponent.getAutoComplete());
			chkAutoCorrect.setSelected(backComponent.getAutoCorrect());
			chkSomenteLeitura.setSelected(backComponent.getReadOnly());
			chkAutoSelect.setSelected(backComponent.getAutoSelect());
			chkUsePicker.setSelected(backComponent.getUsePicker());
			txtLabel.setText(backComponent.getLabel());			
			
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
		AwesomeDude.setIcon(btnApaga, AwesomeIcon.TIMES_CIRCLE, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnAdicionaOpcao, AwesomeIcon.PLUS_CIRCLE, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnEdita, AwesomeIcon.PENCIL, "16px", ContentDisplay.CENTER);
	}

	@SuppressWarnings("all")
	private void inicializacaoCampos() {
		customSelect = (CustomSelect)customComponent.getCustomComponent();
		
		if(customSelect != null) {
			double tblWidth = tblOpcoes.getPrefWidth();
			double middle = tblWidth / 2d;
			clTexto = new TableColumn("Texto");
			clTexto.setPrefWidth(middle);
			clTexto.setEditable(true);
			clValor = new TableColumn("Valor");
			clValor.setPrefWidth(middle);
			clValor.setEditable(true);
			
			clTexto.setCellValueFactory(new PropertyValueFactory<Option, String>("text"));
			clValor.setCellValueFactory(new PropertyValueFactory<Evento, String>("value"));
			tblOpcoes.getColumns().addAll(clTexto, clValor);			
			

			
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Select)customSelect.getBackComponent();
			
			tblOpcoes.getItems().clear();
			tblOpcoes.setItems(FXCollections.observableArrayList(backComponent.getOptions()));

			String fieldId = backComponent.getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorSelect() : fieldId);
			backComponent.setFieldId(fieldId);

			id.setText(backComponent.getFieldId());
			descricao.setText(customSelect.getBackComponent().getDescricao());

			maxLength.setNumber(new BigDecimal(backComponent.getMaxLength().intValue()));
			txtPlaceHolder.setText(backComponent.getPlaceHolder());
			chkAutoCapitalize.setSelected(backComponent.getAutoCapitalize());
			chkAutoComplete.setSelected(backComponent.getAutoComplete());
			chkAutoCorrect.setSelected(backComponent.getAutoCorrect());
			chkSomenteLeitura.setSelected(backComponent.getReadOnly());
			chkAutoSelect.setSelected(backComponent.getAutoSelect());
			chkUsePicker.setSelected(backComponent.getUsePicker());
			chkLabelWrap.setSelected(backComponent.getLabelWrap());
			chkObrigatorio.setSelected(backComponent.getRequired());
			txtLabel.setText(backComponent.getLabel());
			
			String docked = backComponent.getDocked();
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}
			
			if (customSelect instanceof IJFFormComponent) {
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
	
	@FXML 
	protected void btnApagaTextoAction(ActionEvent event) {
		final Option opcaoSelecionada = tblOpcoes.getSelectionModel().getSelectedItem();
		if(opcaoSelecionada == null) {
			JFDialog diag = new JFDialog();
			diag.show("É necessário selecionar uma opção", "Ok");
		} else {
			JFDialog dialog = new JFDialog();
			
			EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
		            tblOpcoes.getItems().remove(opcaoSelecionada);
				}
			};
			
			
			dialog.show("Tem certeza que deseja excluir a implementação?", "Sim", "Não", act1);
		}
	}
	
	@FXML 
	protected void btnEditaTextoAction(ActionEvent event) {
		final Option opcaoSelecionada = tblOpcoes.getSelectionModel().getSelectedItem();
		if(opcaoSelecionada == null) {
			JFDialog diag = new JFDialog();
			diag.show("É necessário selecionar uma opção", "Ok");
		} else {
			DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			try {
		        DialogFactory.DialogRetVO vo = null;
	        	vo = factory.loadDialog("componentes/select/FormValoresSelect.fxml");
	        	
	        	FormValoresSelectController controller = (FormValoresSelectController)vo.controller;
	        	controller.setTxtTexto(opcaoSelecionada.getText());
	        	controller.setTxtValor(opcaoSelecionada.getValue());
	        	controller.setOpcao(opcaoSelecionada);
	        	controller.setTblOpcoes(tblOpcoes);
	        	
	    		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
	    		dialog.initModality(Modality.WINDOW_MODAL);
	    		dialog.initOwner(JazzForms.PRIMARY_STAGE);
	    		
	    		dialog.setScene(new Scene((Parent)vo.root));
	    		
	    		// allow the dialog to be dragged around.
	    		final Node root = dialog.getScene().getRoot();
	    		final Delta dragDelta = new Delta();
	    		root.setOnMousePressed(new EventHandler<MouseEvent>() {
	    			@Override
	    			public void handle(MouseEvent mouseEvent) {
	    				// record a delta distance for the drag and drop
	    				// operation.
	    				dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
	    				dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
	    			}
	    		});
	    		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	    			@Override
	    			public void handle(MouseEvent mouseEvent) {
	    				dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
	    				dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
	    			}
	    		});
	    		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
	    		dialog.show();	    		
	        } catch (IOException e) {
		        e.printStackTrace();
	        } finally {        	
	        }
		}
	}
	
	@FXML
	protected void btnAdicionaTextoAction(ActionEvent event) {

		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		try {
	        DialogFactory.DialogRetVO vo = null;
        	vo = factory.loadDialog("componentes/select/FormValoresSelect.fxml");
        	
        	FormValoresSelectController controller = (FormValoresSelectController)vo.controller;
        	controller.setTblOpcoes(tblOpcoes);
        	
    		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
    		dialog.initModality(Modality.WINDOW_MODAL);
    		dialog.initOwner(JazzForms.PRIMARY_STAGE);
    		
    		dialog.setScene(new Scene((Parent)vo.root));
    		
    		// allow the dialog to be dragged around.
    		final Node root = dialog.getScene().getRoot();
    		final Delta dragDelta = new Delta();
    		root.setOnMousePressed(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent mouseEvent) {
    				// record a delta distance for the drag and drop
    				// operation.
    				dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
    				dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
    			}
    		});
    		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent mouseEvent) {
    				dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
    				dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
    			}
    		});
    		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
    		dialog.show();	    		
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {        	
        }
	}

	class Delta { double x, y; }
}
