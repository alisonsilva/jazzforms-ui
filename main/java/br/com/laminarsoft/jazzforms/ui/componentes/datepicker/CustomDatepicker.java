package br.com.laminarsoft.jazzforms.ui.componentes.datepicker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.DatePicker;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FieldSet;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.FormPanel;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.ToolBar;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.MouseClickEventsCanvas.ISelectedChangeListener;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.SourceDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.event.drag.TargetDragEventHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.componentes.toggle.CustomToggle;
import br.com.laminarsoft.jazzforms.ui.componentes.util.IdentificadorUtil;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class CustomDatepicker extends JFBorderPane implements IDetalhesComponentes, IJFSelectedComponent {
	public static final String FOCUSED_STYLE_NAME = "x-button-selected";
	public static final String UNFOCUSED_STYLE_NAME = "x-button";
	public static final String UI_COMPONENT_ID = "CustomDatePicker";
	
	private DatePicker datePicker;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private Pagina paginaPai;
	private IJFUIBackComponent paneParent;
	private eu.schudt.javafx.controls.calendar.DatePicker calendar;
	private Label label;
	
	protected boolean deleted;
	
	public CustomDatepicker() {
		super();
	}
	
	public CustomDatepicker(Date defaultDate) {
		super();
		
		calendar = new eu.schudt.javafx.controls.calendar.DatePicker();
		calendar.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
		calendar.getCalendarView().todayButtonTextProperty().set("Hoje");
		
		label = new Label("Texto");
		label.getStyleClass().add("x-label");
		label.setAlignment(Pos.CENTER_LEFT);
		
		datePicker = new DatePicker();		
		datePicker.setDocked("left");
		datePicker.setDestroyPickerOnHide(false);
		datePicker.setLabelAlign("top");
		datePicker.setLabel(label.getText());
		datePicker.setFieldId(IdentificadorUtil.getNextIdentificadorDatepicker());
		
		SourceDragEventHandler.getInstance(this, TargetDragEventHandler.ID_COMPONENT);

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		if(defaultDate != null) {
			calendar.setSelectedDate(defaultDate);
			datePicker.setValue(df.format(defaultDate));
		} else {
			calendar.setSelectedDate(new Date());
			datePicker.setValue(df.format(new Date()));
			
		}
		this.setPadding(new Insets(3, 3, 3, 3));
		calendar.setPrefHeight(25);
		
		this.setCenter(calendar);
		this.setTop(label);
		
		final CustomDatepicker atual = this;

		this.setOnMouseEntered(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mouseEvent) {
				atual.setFocusedStyle();
				mouseEvent.consume();
			}			
		});
		
		this.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				IJFSelectedComponent source = (IJFSelectedComponent)mouseEvent.getSource();
				if (!JazzForms.getPaginaDesenhoAtual().containsSelectedNode(source)) {
					atual.setUnfocusedStyle();
					mouseEvent.consume();
				}
			}
		});
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				int compDesenho = JazzForms.getPaginaDesenhoAtual().getComponenteDesenho();
				atual.requestFocus();
				if (compDesenho == 0) {
					if (event.isControlDown() && event.getClickCount() == 1) {
						if (paginaPai.containsSelectedNode(atual)) {
							paginaPai.removeSelectedNode(atual);
						} else {
							paginaPai.addSelectedNode(atual);
						}
						detalhesTab.setContent(null);
						avancadoTab.setContent(null);
					} else if (!event.isControlDown() && event.getClickCount() == 1) {
						paginaPai.clearSelectedNodes();
						paginaPai.addSelectedNode(atual);
						DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
						try {
							DialogFactory.DialogRetVO vo = factory.loadDialog("componentes/datepicker/DatepickerDetalhes.fxml");
							detalhesTab.setContent(null);
							detalhesTab.setContent((Node) vo.root);

							RootController rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizDetalhes(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);

							vo = factory.loadDialog("componentes/datepicker/DatepickerAvancado.fxml");
							avancadoTab.setContent(null);
							avancadoTab.setContent((Node) vo.root);

							rootCntrl = new RootController();
							rootCntrl.controller = (IControllerDetalhes) vo.controller;
							rootCntrl.root = (Parent) vo.root;
							paginaPai.getOnMouseClickedHandler().setRaizAvancado(rootCntrl);
							((IControllerDetalhes) vo.controller).setCustomComponent(atual);
							
							ISelectedChangeListener listener = (ISelectedChangeListener)JazzForms.CENTER_CONTROLLER;
							listener.fireSelectedChange(paginaPai.getSelectedNodes());
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
						}
					} else {
						JFDialog dialog = new JFDialog();
						dialog.show("Um datepicker n�o � container para outros componentes", "Ok");
					}
				}
				event.consume();
			}			
		});
	}
	
	@Override
    public boolean isInsersaoValida(Container container) {
		boolean ret = false;
		if(container instanceof ToolBar) {
			ret = true;
		} else if(container instanceof FormPanel) {
			ret = true;
		} else if(container instanceof FieldSet) {
			ret = true;
		}
	    return ret;
    }


	@Override
	public Component getBackComponent() {
		return datePicker;
	}
	
	@Override
	public Node getCustomComponent() {
		return this;
	}
	
	@Override
    public void setContainerPai(IJFUIBackComponent containerPai) {
		this.paneParent = containerPai;
    }

	@Override
    public IJFUIBackComponent getContainerPai() {
	    return this.paneParent;
    }
	
	@Override
    public boolean isDeleted() {
	    return deleted;
    }

	@Override
    public void setDeleted(boolean deleted) {
		this.deleted = deleted;
    }

	public eu.schudt.javafx.controls.calendar.DatePicker getCalendar() {
		return calendar;
	}

	public void setCalendar(eu.schudt.javafx.controls.calendar.DatePicker calendar) {
		this.calendar = calendar;
	}

	@Override
	public void setFocusedStyle() {
		if (!this.getStyleClass().contains(FOCUSED_STYLE_NAME)) {
			if (this.getStyleClass().contains(UNFOCUSED_STYLE_NAME)) {
				this.getStyleClass().remove(UNFOCUSED_STYLE_NAME);
			}
			this.getStyleClass().add(FOCUSED_STYLE_NAME);
		}
	}

	@Override
	public void setUnfocusedStyle() {
		if(this.getStyleClass().contains(FOCUSED_STYLE_NAME)) {
			this.getStyleClass().remove(FOCUSED_STYLE_NAME);
			this.getStyleClass().add(UNFOCUSED_STYLE_NAME);
		}
	}

	@Override
    public void inicializaCustomComponent(Component componente) {
		DatePicker dPicker = (DatePicker)componente;
		setLabel(dPicker.getLabel());
		setLabelAlign(dPicker.getLabelAlign());
		setUnfocusedStyle();
		this.datePicker = dPicker;
		if (this.datePicker.getValue().trim().length() > 0) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			try {
	            this.calendar.setSelectedDate(format.parse(this.datePicker.getValue()));
            } catch (ParseException e) {
            	this.calendar.setSelectedDate(new Date());
            }
		}
		setId(CustomToggle.UI_COMPONENT_ID);
		
    }

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}
	
	public void setLabel(String text) {
		label.setText(text);
		datePicker.setLabel(text);
	}
	
	public void setLabelAlign(String align) {
		removeLabel();
		switch (align) {
		case "top" :
			this.setTop(label);
			datePicker.setLabelAlign(align);
			this.setPrefWidth(90);
			label.setAlignment(Pos.CENTER_LEFT);
			break;
		case "right" : 
			this.setRight(label);
			datePicker.setLabelAlign(align);
			this.setPrefWidth(130);
			label.setAlignment(Pos.CENTER_RIGHT);
			break;
		case "bottom" :
			this.setBottom(label);
			datePicker.setLabelAlign(align);
			this.setPrefWidth(90);
			label.setAlignment(Pos.CENTER_LEFT);
			break;
		case "left" :
			this.setLeft(label);
			datePicker.setLabelAlign(align);
			label.setAlignment(Pos.CENTER_LEFT);
			this.setPrefWidth(130);
			break;
		}
	}
	
	private void removeLabel() {
		switch (datePicker.getLabelAlign()) {
		case "top" :
			this.setTop(null);
			break;
		case "right" : 
			this.setRight(null);
			break;
		case "bottom" :
			this.setBottom(null);
			break;
		case "left" :
			this.setLeft(null);
			break;
		}
	}	
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDetalhesTab(Tab detalhesTab) {
		this.detalhesTab = detalhesTab;
	}

	@Override
	public void setAvancadoTab(Tab avancadoTab) {
		this.avancadoTab = avancadoTab;
	}

	@Override
	public void setPagina(Pagina pagina) {
		this.paginaPai = pagina;
	}
	
	
}
