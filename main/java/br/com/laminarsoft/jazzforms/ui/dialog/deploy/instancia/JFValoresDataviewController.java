package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.org.apache.bcel.internal.generic.IREM;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.Campo;
import br.com.laminarsoft.jazzforms.persistencia.model.Linha;
import br.com.laminarsoft.jazzforms.persistencia.model.ValorDataview;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.IValor;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ValorDataviewVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

@SuppressWarnings("all")
public class JFValoresDataviewController implements Initializable, IGerenciamentoDataview, IRefreshAprsesentacao {
	@FXML private TitledPane valoresDataviewErrorPane;
	@FXML private VBox mainPanelValoresDataview;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator valoresDataviewProgressIndicator;
	@FXML private TableView<Linha> tableValoresDataview;

	
	private WebServiceController controller = WebServiceController.getInstance();
	private boolean alterado = false;

	private PublicaProjetoController pubController;
	private ValorDataview valorDataview;
	private IRefreshAprsesentacao refresher;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}

	public void setValorDataview(ValorDataview valorDataview) {
		this.valorDataview = valorDataview;
		updateData(null);
	}
	
	
	
	@Override
    public void refreshApresentacao() {
    }

	@Override
    public void setRefresher(IRefreshAprsesentacao refresher) {
		this.refresher = refresher;
    }

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		valoresDataviewProgressIndicator.visibleProperty().bindBidirectional(mainPanelValoresDataview.disableProperty());
		valoresDataviewProgressIndicator.setVisible(false);
		mainPanelValoresDataview.visibleProperty().bind(valoresDataviewErrorPane.visibleProperty().not());
		tableValoresDataview.setEditable(true);
		adicionarMenuSuspenso();
	}
	
	
	
	@Override
    public void recebeInfoRemoveLinhaInstanciaDataview(InfoRetornoInstancia info) {
		Platform.runLater(new Runnable() {
			public void run() {
				int selectedIndex = tableValoresDataview.getSelectionModel().getSelectedIndex();
				tableValoresDataview.getItems().remove(selectedIndex);
				valoresDataviewProgressIndicator.setVisible(false);
			}
		});
    }

	@Override
    public void recebeInfoAlteraInstanciaDataview(final InfoRetornoInstancia info, final ActionEvent action) {
		Platform.runLater(new Runnable() {
			public void run() {
				valoresDataviewProgressIndicator.setVisible(false);
				if (info.codigo == 0) {
					alterado = false;
                } else {
                	showErrorMessage("Erro ao alterar dados: " + info.mensagem);
                }
			}
		});
    }
	
	
	
	@Override
    public void recebeInfoNovaLinhaDataview(final InfoRetornoInstancia info) {
		Platform.runLater(new Runnable() {
			public void run() {
				valoresDataviewProgressIndicator.setVisible(false);
				if (info.codigo == 0) {
					valorDataview = info.valorDataview;
					updateData(tableValoresDataview.getItems());
                    alterado = true;
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});
    }

	@Override
    public void onRecuperaInstanciasError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
				valoresDataviewProgressIndicator.setVisible(false);				
			}
		});			
    }


	private void adicionarMenuSuspenso() {
		final JFValoresDataviewController me = this;
		final ContextMenu cmItem = new ContextMenu();
		final MenuItem miApagarDTVRow = new MenuItem("Remover");
		miApagarDTVRow.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionApagar) {
				final Linha linha = tableValoresDataview.getSelectionModel().getSelectedItem();
				JFDialog dialog = new JFDialog();

            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
            		@Override
            		public void handle(ActionEvent actionEvent) {
            			valoresDataviewProgressIndicator.setVisible(true);
                   		controller.removeLinhaInstanciaDataview(me, linha.getId());
            		}
            	};
            	dialog.show("Tem certeza que deseja remover a linha selecionada ("+ linha.getNumero() +")?", "Sim", "Não", act1);
            	actionApagar.consume();				
            }			
		});
		
		final MenuItem miDuplicarRegistro = new MenuItem("Duplicar");
		miDuplicarRegistro.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionDuplicar) {
				Linha linhaSelecionada = tableValoresDataview.getSelectionModel().getSelectedItem();
				Linha novalinha = linhaSelecionada.clone();
				int posLinhaNova = tableValoresDataview.getSelectionModel().getSelectedIndex() + 1;
				tableValoresDataview.getItems().add(posLinhaNova, novalinha);
				alterado = true;
            }			
		});
		
		final MenuItem miAdicionarRegistro = new MenuItem("Novo");
		miAdicionarRegistro.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionNovo) {
				if (alterado) {
					JFDialog dialog = new JFDialog();

	            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
	            		@Override
	            		public void handle(ActionEvent actionEvent) {
	            			valoresDataviewProgressIndicator.setVisible(true);
	                   		controller.alteraValoresDataview(me, valorDataview, actionEvent);
	            		}
	            	};
	            	dialog.show("É necessário salvar as alterações antes. Deseja salvar agora?", "Sim", "Não", act1);					
				} else {
					valoresDataviewProgressIndicator.setVisible(true);
					controller.novaLinhaDataview(me, valorDataview.getId());
				}
            }
		});
		
		cmItem.getItems().add(miAdicionarRegistro);
		cmItem.getItems().add(miDuplicarRegistro);
		cmItem.getItems().add(new SeparatorMenuItem());
		cmItem.getItems().add(miApagarDTVRow);
		
	    tableValoresDataview.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY && !mouseEvent.isControlDown()) {
                	Linha item = tableValoresDataview.getSelectionModel().getSelectedItem();
                	if (item == null) {
                		miDuplicarRegistro.setDisable(true);
                		miDuplicarRegistro.setDisable(true);
                    } else {
                		miDuplicarRegistro.setDisable(false);
                		miDuplicarRegistro.setDisable(false);
                    }
                    cmItem.show(tableValoresDataview, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                } else {
                	cmItem.hide();
                }
            }
		});
    }
	
	private void updateData(ObservableList<Linha> obsList) {
		int linha = 1;
		tableValoresDataview.getColumns().clear();
		tableValoresDataview.getItems().clear();
		boolean novaList = false;
		if (obsList == null) {
	        obsList = FXCollections.observableList(valorDataview.getRows());
	        novaList = true;
        }
		for (Linha row : valorDataview.getRows()) {
			if (linha == 1) {
				for(Campo cmp : row.getCampos()) {
					TableColumn<Linha, Campo> column = new TableColumn<Linha, Campo>(cmp.getNomeCampo());
	                column.setCellValueFactory(new DataviewValueFactory<Linha, Campo>(cmp.getNomeCampo()));
	                column.setCellFactory(new Callback<TableColumn<Linha,Campo>, TableCell<Linha,Campo>>() {

						@Override
                        public TableCell<Linha, Campo> call(TableColumn<Linha, Campo> column) {
	                        return new EditingCell();
                        }
	                	
					});
	                column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Linha,Campo>>() {
						@Override
                        public void handle(CellEditEvent<Linha, Campo> cell) {
							Linha linha = cell.getTableView().getItems().get(cell.getTablePosition().getRow());
							Campo valorNovo = cell.getNewValue();
							if (linha.getCampos().contains(valorNovo)) {
								int pos = linha.getCampos().indexOf(valorNovo);
								linha.getCampos().get(pos).setValorCampo(valorNovo.getValorCampo());
								alterado = true;
							}
                        }
					});
	                tableValoresDataview.getColumns().add(column);
				}
			}
			linha++;
		}
		if (novaList) {
	        tableValoresDataview.setItems(obsList);
        } else {
        	tableValoresDataview.getItems().addAll(valorDataview.getRows());
        }
		if(tableValoresDataview.getItems().size() > 0) {
			tableValoresDataview.getSelectionModel().clearAndSelect(tableValoresDataview.getItems().size() - 1);
		}
	}	

	private void showErrorMessage(String errorMessage) {
		((Label) valoresDataviewErrorPane.getContent()).setText(errorMessage);
		valoresDataviewErrorPane.setVisible(true);
	}
	
	@FXML
	protected void deploymentsErrorPaneClicked(MouseEvent mouseEvent) {
		valoresDataviewErrorPane.setVisible(false);
	}		
	


	@FXML
	protected void btnOkAction(final ActionEvent actionEvent) {
		final JFValoresDataviewController me = this;
		if (!alterado) {
			try {
	            refresher.refreshApresentacao();
            } catch (Exception e) {
            }
	        fechaJanela(actionEvent);
        } else {        	
        	valorDataview.getRows();
			JFDialog dialog = new JFDialog();
        	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
        		@Override
        		public void handle(ActionEvent actionEvent) {
        			valoresDataviewProgressIndicator.setVisible(true);
               		controller.alteraValoresDataview(me, valorDataview, actionEvent);
        		}
        	};
        	dialog.show("Tem certeza que deseja alterar os valores de dados?", "Sim", "Não", act1);
        }
	}	
	
	private void fechaJanela(ActionEvent actionEvent) {
	    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
    }	
	
	class Delta { double x, y; }
	
	class EditingCell extends TableCell<Linha, Campo> {
		private TextField textField;
		public EditingCell() {
			
		}
		
		@Override
	    public void startEdit() {
	        super.startEdit();
	        if (textField == null) {
	            createTextField();
	        }
	        setGraphic(textField);
	        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	        Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	                textField.requestFocus();
	                textField.selectAll();
	            }
	        });
	    }
		
		
	    @Override
	    public void cancelEdit() {
	        super.cancelEdit();
	        setText((String) getItem().getValorCampo());
	        setContentDisplay(ContentDisplay.TEXT_ONLY);
	    }    
	    
	    
	    @Override
	    public void updateItem(Campo item, boolean empty) {
	        super.updateItem(item, empty);
	        if (empty) {
	            setText(null);
	            setGraphic(null);
	        } else {
	            if (isEditing()) {
	                if (textField != null) {
	                    textField.setText(getString());
	                }
	                setGraphic(textField);
	                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            } else {
	                setText(getString());
	                setContentDisplay(ContentDisplay.TEXT_ONLY);
	            }
	        }
	    }
	    
	    
	    private void createTextField() {
	        textField = new TextField(getString());
	        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
	        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
	            @Override
	            public void handle(KeyEvent t) {
	                if (t.getCode() == KeyCode.ENTER) {
	                	getItem().setValorCampo(textField.getText());
	                    commitEdit(getItem());
	                } else if (t.getCode() == KeyCode.ESCAPE) {
	                    cancelEdit();
	                } else if (t.getCode() == KeyCode.TAB) {
	                	getItem().setValorCampo(textField.getText());
	                    commitEdit(getItem());
	                    TableColumn nextColumn = getNextColumn(!t.isShiftDown());
	                    if (nextColumn != null) {
	                        getTableView().edit(getTableRow().getIndex(), nextColumn);
	                    }
	                }
	            }
	        });
	        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	            @Override
	            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	                if (!newValue && textField != null) {
	                	getItem().setValorCampo(textField.getText());
	                    commitEdit(getItem());
	                }
	            }
	        });
	    }
	    
	    
	    private String getString() {
	        return getItem() == null ? "" : getItem().toString();
	    }
	    /**
	     *
	     * @param forward true gets the column to the right, false the column to the left of the current column
	     * @return
	     */
	    private TableColumn<Linha, ?> getNextColumn(boolean forward) {
	        List<TableColumn<Linha, ?>> columns = new ArrayList<>();
	        for (TableColumn<Linha, ?> column : getTableView().getColumns()) {
	            columns.addAll(getLeaves(column));
	        }
	        //There is no other column that supports editing.
	        if (columns.size() < 2) {
	            return null;
	        }
	        int currentIndex = columns.indexOf(getTableColumn());
	        int nextIndex = currentIndex;
	        if (forward) {
	            nextIndex++;
	            if (nextIndex > columns.size() - 1) {
	                nextIndex = 0;
	            }
	        } else {
	            nextIndex--;
	            if (nextIndex < 0) {
	                nextIndex = columns.size() - 1;
	            }
	        }
	        return columns.get(nextIndex);
	    }
	     
	    private List<TableColumn<Linha, ?>> getLeaves(TableColumn<Linha, ?> root) {
	        List<TableColumn<Linha, ?>> columns = new ArrayList<>();
	        if (root.getColumns().isEmpty()) {
	            //We only want the leaves that are editable.
	            if (root.isEditable()) {
	                columns.add(root);
	            }
	            return columns;
	        } else {
	            for (TableColumn<Linha, ?> column : root.getColumns()) {
	                columns.addAll(getLeaves(column));
	            }
	            return columns;
	        }
	    }		
	}
}
