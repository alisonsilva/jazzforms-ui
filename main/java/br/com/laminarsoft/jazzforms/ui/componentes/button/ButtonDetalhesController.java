package br.com.laminarsoft.jazzforms.ui.componentes.button;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Icon;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerIconsResponseHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFIconComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.numbertextfield.NumberSpinner;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;

public class ButtonDetalhesController implements Initializable, IControllerDetalhes, IJFIconComponent {
	@FXML private TextField id;
	@FXML private TextArea descricao;
	@FXML private ComboBox<String> comboPosicao;
	@FXML private ComboBox<String> comboPosicaoIcone;
	@FXML private ComboBox<String> comboUI;
	@FXML private TextField text;
	@FXML private NumberSpinner pressedDelay;
	@FXML private TextField txtIcone;
	@FXML private Button btnIcones;
	@FXML private Button btnCleanIcon;
	
	private CustomButton button = null;
	private IDetalhesComponentes customComponent; 
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		button = (CustomButton)customComponent.getCustomComponent();
		
		if(button != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button)button.getBackComponent();
			backComponent.setFieldId(id.getText());
			backComponent.setDescricao(descricao.getText());
			button.setLabelText(text.getText());
			backComponent.setPressedDelay(pressedDelay.getNumber().intValue());
			backComponent.setIconCls(txtIcone.getText());
			backComponent.setDocked(comboPosicao.getValue());
			
			if(button instanceof IDetalhesComponentes) {
				IJFUIBackComponent pai = ((IDetalhesComponentes)button).getContainerPai();
				switch (comboPosicao.getValue()) {
				case "right":
					pai.removeComponent(button);
					pai.adicionaComponent(button, "right");
					break;
				case "left" :
					pai.removeComponent(button);
					pai.adicionaComponent(button, "left");
					break;
				}
			}
			backComponent.setUi(comboUI.getValue());
			button.setDefaultUnfocusedStyle(comboUI.getValue());

		}
	}

	
	
	@Override
    public void setIcon(Icon icon) {
		button.setIcon(icon);
		txtIcone.setText(icon.getNome());
    }

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		button = (CustomButton)customComponent.getCustomComponent();
		
		if(button != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button)button.getBackComponent();
			id.setText(button.getBackComponent().getFieldId());
			descricao.setText(button.getBackComponent().getDescricao());
			text.setText(button.getLabelText());
			pressedDelay.setNumber(new BigDecimal(backComponent.getPressedDelay()));
			txtIcone.setText(backComponent.getIconCls());
			
			String docked = backComponent.getDocked();
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}
			
			String iconPos = backComponent.getIconAlign();
			comboPosicaoIcone.getItems().addAll("top", "right", "bottom", "left", "center");
			if (iconPos != null && iconPos.trim().length() > 0) {
				comboPosicaoIcone.setValue(iconPos);
			} else {
				comboPosicaoIcone.setValue("center");
			}
			
			comboUI.setValue(backComponent.getUi());
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		AwesomeDude.setIcon(btnCleanIcon, AwesomeIcon.TIMES_CIRCLE, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnIcones, AwesomeIcon.PLUS_CIRCLE, "16px", ContentDisplay.CENTER);
	}

	private void inicializacaoCampos() {
		button = (CustomButton)customComponent.getCustomComponent();
		
		if(button != null) {
			br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button backComponent = 
					(br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Button)button.getBackComponent();
			String fieldId = button.getBackComponent().getFieldId();
			fieldId = (fieldId.trim().length() == 0 ? IdentificadorUtil.getNextIdentificadorButton() : fieldId);
			button.getBackComponent().setFieldId(fieldId);
			id.setText(fieldId);
			descricao.setText(button.getBackComponent().getDescricao());
			text.setText(button.getLabelText());
			pressedDelay.setNumber(new BigDecimal(backComponent.getPressedDelay()));
			txtIcone.setText(backComponent.getIconCls());
			
			String docked = backComponent.getDocked();
			comboPosicao.getItems().addAll("top", "right", "bottom", "left", "center");
			if (docked != null && docked.trim().length() > 0) {
				comboPosicao.setValue(docked);
			} else {
				comboPosicao.setValue("top");
			}
			
			String iconPos = backComponent.getIconAlign();
			comboPosicaoIcone.getItems().addAll("top", "right", "bottom", "left", "center");
			if (iconPos != null && iconPos.trim().length() > 0) {
				comboPosicaoIcone.setValue(iconPos);
			} else {
				comboPosicaoIcone.setValue("center");
			}
			
			comboUI.getItems().addAll("normal", "back", "forward", "round", "action", "decline", "confirm", "action-round", "decline-round", "confirm-round");
			comboUI.setValue(backComponent.getUi());
			
			comboUI.setDisable(!button.isUiEnabled());
			comboPosicao.setDisable(!button.isPosicaoEnabled());
			txtIcone.setDisable(!button.isIconClsEnabled());
		}
	}

	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		this.customComponent = customComponente;
		inicializacaoCampos();
	}
	
	@FXML
	protected void btnIconesAction(ActionEvent action) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		try {
			DialogFactory.DialogRetVO vo = factory.loadDialog("top/dialog/projeto/JFCarregaImagensDialog.fxml");
			final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(JazzForms.PRIMARY_STAGE);
			dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent windowEvent) {
				    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
					Stage stage = (Stage) windowEvent.getSource();
					stage.close();
				}
			});
			
			((IServerIconsResponseHandler)vo.controller).setShowingElement(this);
			Scene scene = new Scene((Parent)vo.root);
			scene.setFill(Color.TRANSPARENT);
			dialog.setScene(scene);
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.show();		
			
			final Delta dragDelta = new Delta();
			
			((Parent)vo.root).setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					// record a delta distance for the drag and drop
					// operation.
					dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
					dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
				}
			});
			((Parent)vo.root).setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
					dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
				}
			});				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}		
	}

	@FXML
	protected void btnClearIconAction(ActionEvent action) {
		txtIcone.setText("");
		button.setIcon(null);
	}
	
	class Delta { double x, y; }		
}
