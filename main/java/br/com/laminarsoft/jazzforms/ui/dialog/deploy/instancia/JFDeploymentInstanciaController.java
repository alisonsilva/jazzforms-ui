package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import org.apache.cxf.common.util.StringUtils;

import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Option;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.FotoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.IValor;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.InstanciaVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.InstanciasVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LocalizacaoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ValorDataviewVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ValorFormularioVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.reenvio.IReenvioInstanciaUsuario;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa.IMapListener;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa.WebMapController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

@SuppressWarnings("all")
public class JFDeploymentInstanciaController implements Initializable, IGerenciamentoInstancia, IRefreshAprsesentacao {
	@FXML private TitledPane deploymentsErrorPane;
	@FXML private VBox mainPaneUsuarios;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator deploymentsProgressIndicator;
	@FXML private TableView<InstanciaVO> tableInstancias;

	
	private WebServiceController controller = WebServiceController.getInstance();
	private boolean alterado = false;
	private List<InstanciaVO> instanciasAlteradas = new ArrayList<InstanciaVO>();

	private PublicaProjetoController pubController;
	private Long idProjetoSelecionado;
	private IRefreshAprsesentacao refresher;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}

	public void setProjetoSelecionado(Long idProjetoSelecionado) {
		this.idProjetoSelecionado = idProjetoSelecionado;
		updateData();
	}	
	
	@Override
    public void refreshApresentacao() {
		updateData();
    }

	@Override
    public void setRefresher(IRefreshAprsesentacao refresher) {
		this.refresher = refresher;
    }

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		deploymentsProgressIndicator.visibleProperty().bindBidirectional(mainPaneUsuarios.disableProperty());
		deploymentsProgressIndicator.setVisible(false);
		mainPaneUsuarios.visibleProperty().bind(deploymentsErrorPane.visibleProperty().not());
		tableInstancias.setEditable(true);
		
		adicionarMenuSuspenso();

	}
	
	private void adicionarMenuSuspenso() {
		final JFDeploymentInstanciaController me = this;
		
		final ContextMenu cmItem = new ContextMenu();

		final MenuItem miNovoRegistro = new MenuItem("Novo");
		miNovoRegistro.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionNovo) {
				deploymentsProgressIndicator.setVisible(true);
				alterado = true;
				controller.novaInstanciaProjeto(me, idProjetoSelecionado, JazzForms.USUARIO_LOGADO.getUid());
            }			
		});
		
		final MenuItem miDuplicarRegistro = new MenuItem("Duplicar");
		miDuplicarRegistro.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionDuplicar) {
				InstanciaVO vo = tableInstancias.getSelectionModel().getSelectedItem();
				InstanciaVO cloneVo = vo.clone();
				cloneVo.setUserLogin(JazzForms.USUARIO_LOGADO.getUid());
				int pos = tableInstancias.getSelectionModel().getSelectedIndex();
				tableInstancias.getItems().add(pos+1, cloneVo);
				instanciasAlteradas.add(cloneVo);
				alterado = true;
            }			
		});
		
		final MenuItem miRemoveRegistro = new MenuItem("Remover");
		miRemoveRegistro.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionRemover) {
				int pos = tableInstancias.getSelectionModel().getSelectedIndex();
				final InstanciaVO linha = tableInstancias.getSelectionModel().getSelectedItem();
				JFDialog dialog = new JFDialog();

            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
            		@Override
            		public void handle(ActionEvent actionEvent) {
            			deploymentsProgressIndicator.setVisible(true);
            			InstanciaVO instancia = tableInstancias.getSelectionModel().getSelectedItem();
            			controller.removeInstancia(me, instancia.getId());
            		}
            	};
            	dialog.show("Tem certeza que deseja remover o registro selecionado?", "Sim", "Não", act1);
            	actionRemover.consume();						
            }			
		});
		
		final MenuItem miEnviaParaUsuario = new MenuItem("Reenviar ...");
		miEnviaParaUsuario.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionReenviar) {
        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
        		DialogFactory.DialogRetVO vo = null;
        		try {
        			InstanciaVO instancia = tableInstancias.getSelectionModel().getSelectedItem();
        			vo = factory.loadDialog("dialog/deploy/instancia/reenvio/JFReenvioSelecionaUsuario.fxml");
        			IReenvioInstanciaUsuario cntrl = (IReenvioInstanciaUsuario)vo.controller;
        			cntrl.setInstanciaId(me, instancia.getId());
    				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
    				
    				final Stage dialog = new Stage(StageStyle.UTILITY);
    				dialog.initModality(Modality.WINDOW_MODAL);
    				dialog.initOwner(JazzForms.PRIMARY_STAGE);
    				
    				dialog.setScene(SceneBuilder.create().width(600).height(500).root((Parent)vo.root).build());
    				dialog.setResizable(true);
    				dialog.show();                                				
        		} catch (IOException e) {
        			e.printStackTrace();
        		}	            
            }
		});
		
		final MenuItem miCancelarReenvio = new MenuItem("Cancelar reenvio");
		miCancelarReenvio.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionCancelarReenvio) {
				JFDialog dialog = new JFDialog();

            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
            		@Override
            		public void handle(ActionEvent actionEvent) {
            			deploymentsProgressIndicator.setVisible(true);
            			InstanciaVO instancia = tableInstancias.getSelectionModel().getSelectedItem();
            			controller.cancelaReenvioInstanciaUsuario(me, instancia.getId(), JazzForms.USUARIO_LOGADO.getUid());
            		}
            	};
            	dialog.show("Tem certeza que deseja cancelar o reenvio?", "Sim", "Não", act1);				
            }
		});
		
		cmItem.getItems().add(miNovoRegistro);
		cmItem.getItems().add(miDuplicarRegistro);
		cmItem.getItems().add(new SeparatorMenuItem());
		cmItem.getItems().add(miEnviaParaUsuario);
		cmItem.getItems().add(miCancelarReenvio);
		cmItem.getItems().add(new SeparatorMenuItem());
		cmItem.getItems().add(miRemoveRegistro);
		
	    tableInstancias.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY && !mouseEvent.isControlDown()) {
                	InstanciaVO item = tableInstancias.getSelectionModel().getSelectedItem();
                	if (item == null) {
                		miRemoveRegistro.setDisable(true);
                		miDuplicarRegistro.setDisable(true);
                		miEnviaParaUsuario.setDisable(true);
                		miCancelarReenvio.setDisable(true);
                	} else {
                		miRemoveRegistro.setDisable(false);
                		miDuplicarRegistro.setDisable(false);
                		
                		if(item.getReenviado()) {
                    		miCancelarReenvio.setDisable(false);
                    		miEnviaParaUsuario.setDisable(true);
                		} else {
                    		miCancelarReenvio.setDisable(true);
                    		miEnviaParaUsuario.setDisable(false);
                		}
                	}
                		
                	cmItem.show(tableInstancias, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                } else {
                	cmItem.hide();
                }
            }
		});
    }
	
	public void updateData() {
		deploymentsProgressIndicator.setVisible(true);
		controller.findInformacoesInstanciasProjeto(this, this.idProjetoSelecionado);
	}	

	private void showErrorMessage(String errorMessage) {
		((Label) deploymentsErrorPane.getContent()).setText(errorMessage);
		deploymentsErrorPane.setVisible(true);
	}
	
	@FXML
	protected void deploymentsErrorPaneClicked(MouseEvent mouseEvent) {
		deploymentsErrorPane.setVisible(false);
	}		
	

	
	@Override
	public void recebeInfoFotosInstancia(InfoRetornoInstancia info) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void recebeInfoInstancias(final InfoRetornoDeployments info) {
		final JFDeploymentInstanciaController me = this;
		Platform.runLater(new Runnable() {
			public void run() {
				deploymentsProgressIndicator.setVisible(false);
				if (info.codigo == 0) {
					if (info.instancias == null) {
						info.instancias = new ArrayList<InstanciaVO>();
					}
	                if (tableInstancias.getItems().size() == 0 ) {
						tableInstancias.getItems().clear();
	                    ObservableList<InstanciaVO> obsList = FXCollections.observableList(info.instancias);
	                    tableInstancias.setItems(obsList);
	                    if (tableInstancias.getItems().size() > 0) {
	                        tableInstancias.getSelectionModel().clearAndSelect(tableInstancias.getItems().size() - 1);
                        }
						int linha = 1;
		                for (final InstanciaVO vo : info.instancias) {
			                if (linha == 1) {
				                for (ValorFormularioVO vlrVo : vo.getValoresFormulario()) {
					                TableColumn<InstanciaVO, IValor> column = new TableColumn<InstanciaVO, IValor>(vlrVo.getFieldLabel());
					                column.setCellValueFactory(new InstanciaValueFactory<InstanciaVO, IValor>(vlrVo.getFieldId()));
					                
					                column.setCellFactory(new Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>>() {
										@Override
				                        public TableCell<InstanciaVO, IValor> call(TableColumn<InstanciaVO, IValor> column) {
					                        return new EditingCell();
				                        }				                	
									});
					                
					                column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InstanciaVO, IValor>>() {
										@Override
				                        public void handle(CellEditEvent<InstanciaVO, IValor> cell) {
											InstanciaVO linha = cell.getTableView().getItems().get(cell.getTablePosition().getRow());
											IValor valorNovo = cell.getNewValue();
											
											int pos = -1;
											for(ValorFormularioVO vlrVO : linha.getValoresFormulario()) {
												if (vlrVO.getId() == valorNovo.getId()) {
													pos = linha.getValoresFormulario().indexOf(vlrVO);
													break;
												}
											}
											
											if (pos >= 0) {
												linha.getValoresFormulario().get(pos).setValorField(valorNovo.getValorField());
												if(!instanciasAlteradas.contains(linha)) { 
													instanciasAlteradas.add(linha);
												}
												alterado = true;
											}
				                        }
									});				                
					                tableInstancias.getColumns().add(column);
					            }
				                for(FotoVO fotoVo : vo.getValoresFoto()) {
				                	TableColumn<InstanciaVO, IValor> column = new TableColumn<InstanciaVO, IValor>(fotoVo.getFieldId());
				                	column.setCellValueFactory(new InstanciaFotoValueFactory<InstanciaVO, IValor>(fotoVo.getFieldId()));
				                	column.setEditable(false);
				                	column.setResizable(false);
				                	column.setSortable(false);
				                	column.setPrefWidth(90);
				                	tableInstancias.getColumns().add(column);
				                	
				                	final Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>> callback = createCustomFotoCellFactory(me);
				                	column.setCellFactory(callback);		                
				                }
				                for (final ValorDataviewVO dtvVo : vo.getValoresDataview()) {
				                	TableColumn<InstanciaVO, IValor> column = new TableColumn<InstanciaVO, IValor>(dtvVo.getFieldId());
				                	column.setCellValueFactory(new InstanciaValueFactory<InstanciaVO, IValor>(dtvVo.getFieldId()));
				                	column.setEditable(false);
				                	column.setResizable(false);
				                	column.setSortable(false);
				                	column.setPrefWidth(90);
				                	tableInstancias.getColumns().add(column);
				                	
				                	final Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>> callback = createCustomCellFactory(me);
				                	column.setCellFactory(callback);
				                }
			                }
			                
			                linha++;
		                }	                    
	                    
                    } else {
                    	tableInstancias.getItems().clear();
                    	tableInstancias.getItems().setAll(info.instancias);
	                    if (tableInstancias.getItems().size() > 0) {
	                        tableInstancias.getSelectionModel().clearAndSelect(tableInstancias.getItems().size() - 1);
                        }
                    }
                }
			}
		});		
    }
	
	
    private Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>> createCustomCellFactory(final JFDeploymentInstanciaController me) {
		Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>> cellFactory = 
				new Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>>() {

			
			@Override
            public TableCell call(TableColumn arg0) {
	            final TableCell<InstanciaVO,IValor> cell = new TableCell<InstanciaVO,IValor>() {

					@Override
                    protected void updateItem(IValor item, boolean empty) {
	                    super.updateItem(item, empty);
	                    setGraphic(null);
	                    if (!empty) {
	                    	setText(getString());
	                    	
	                    	setStyle("-fx-font-size: 16;-fx-font-weight: bold;-fx-text-fill: BLUE;");
	                    } else {
	                    	setText(null);
	                    }	                    
                    }
	            	
					private String getString() {
						return getItem() == null ? "0" : ((ValorDataviewVO)getItem()).getQtdLinhas() + "";
					}
	            };
	            
	            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	                		
	    			@Override
	    		    public void handle(MouseEvent mouseEvent) {
	    				if (mouseEvent.getButton() == MouseButton.PRIMARY) {
	                        InstanciaVO vo = tableInstancias.getSelectionModel().getSelectedItem();
	                        controller.findValoresDataviewPorId(me, cell.getItem().getId());
                        }
	    		    }			                		
	    		});
	            
	            cell.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>(){

					@Override
                    public void handle(MouseEvent mouseEvent) {
						((Node)mouseEvent.getSource()).setCursor(Cursor.HAND);
                     	cell.setStyle("-fx-font-size: 16;-fx-font-weight: bold;-fx-text-fill: black;");
                    }	            	
	            });
	            
	            cell.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>(){

					@Override
                    public void handle(MouseEvent mouseEvent) {
						((Node)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
                     	cell.setStyle("-fx-font-size: 16;-fx-font-weight: bold;-fx-text-fill: BLUE;");
                    }	            	
	            });	            
				
	            return cell;
            }			
		};	
		
		return cellFactory;
	}    
    
    private Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>> createCustomFotoCellFactory(final JFDeploymentInstanciaController me) {
		Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>> cellFactory = 
				new Callback<TableColumn<InstanciaVO,IValor>, TableCell<InstanciaVO,IValor>>() {

			
			@Override
            public TableCell call(TableColumn arg0) {
	            final TableCell<InstanciaVO,IValor> cell = new TableCell<InstanciaVO,IValor>() {

					@Override
                    protected void updateItem(IValor item, boolean empty) {
	                    super.updateItem(item, empty);
	                    setGraphic(null);
	                    if (!empty) {
	                    	setText(getString());	                    	
	                    	setStyle("-fx-font-size: 16;-fx-font-weight: bold;-fx-text-fill: BLUE;");
	                    } else {
	                    	setText(null);
	                    }	                    
                    }
	            	
					private String getString() {
						return getItem() == null ? "0" : "1";
					}
	            };
	            
	            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {	                		
	    			@Override
	    		    public void handle(MouseEvent mouseEvent) {
	    				if (mouseEvent.getButton() == MouseButton.PRIMARY) {
	                        InstanciaVO vo = tableInstancias.getSelectionModel().getSelectedItem();
	                        if (cell != null && cell.getItem() != null) {
	                        	FotoVO foto = (FotoVO)cell.getItem();
	                        	DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
	                    		DialogFactory.DialogRetVO voFoto = null;
	                    		try {
	                    			voFoto = factory.loadDialog("dialog/deploy/instancia/JFValoresFotos.fxml");
	                    			JFValoresFotosController cntrl = (JFValoresFotosController)voFoto.controller;
	                    			if(cntrl instanceof IRefreshAprsesentacao) {
	                    				((IRefreshAprsesentacao)cntrl).setRefresher(me);
	                    			}
	                    			cntrl.setValorFoto(foto.getFoto());
	                				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
	                				
	                				final Stage dialog = new Stage(StageStyle.UTILITY);
	                				dialog.initModality(Modality.WINDOW_MODAL);
	                				dialog.initOwner(JazzForms.PRIMARY_STAGE);
	                				
	                				dialog.setScene(SceneBuilder.create().width(400).height(400).root((Parent)voFoto.root).build());
	                				dialog.setResizable(true);
	                				dialog.show();                                				
	                    		} catch (IOException e) {
	                    			e.printStackTrace();
	                    		}	                        	
	                        }
                        }
	    		    }			                		
	    		});
	            
	            cell.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>(){

					@Override
                    public void handle(MouseEvent mouseEvent) {
						((Node)mouseEvent.getSource()).setCursor(Cursor.HAND);
                     	cell.setStyle("-fx-font-size: 16;-fx-font-weight: bold;-fx-text-fill: black;");
                    }	            	
	            });
	            
	            cell.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>(){

					@Override
                    public void handle(MouseEvent mouseEvent) {
						((Node)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
                     	cell.setStyle("-fx-font-size: 16;-fx-font-weight: bold;-fx-text-fill: BLUE;");
                    }	            	
	            });	            
				
	            return cell;
            }			
		};	
		
		return cellFactory;
	}    

    
	@Override
    public void recebeInfoValorDataview(final InfoRetornoInstancia info) {
		final JFDeploymentInstanciaController me = this;
		Platform.runLater(new Runnable() {

			@Override
            public void run() {
				if (info.codigo == 0) {
            		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
            		DialogFactory.DialogRetVO vo = null;
            		try {
            			vo = factory.loadDialog("dialog/deploy/instancia/JFValoresDataview.fxml");
            			JFValoresDataviewController cntrl = (JFValoresDataviewController)vo.controller;
            			if(cntrl instanceof IRefreshAprsesentacao) {
            				((IRefreshAprsesentacao)cntrl).setRefresher(me);
            			}
            			cntrl.setValorDataview(info.valorDataview);
        				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
        				
        				final Stage dialog = new Stage(StageStyle.UTILITY);
        				dialog.initModality(Modality.WINDOW_MODAL);
        				dialog.initOwner(JazzForms.PRIMARY_STAGE);
        				
        				dialog.setScene(SceneBuilder.create().width(600).height(500).root((Parent)vo.root).build());
        				dialog.setResizable(true);
        				dialog.show();                                				
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
				}
            }
		});
    }
	

	@Override
    public void cancelaReenvioInstanciaSucesso(final InfoRetornoInstancia info) {
		final JFDeploymentInstanciaController me = this;
		Platform.runLater(new Runnable() {

			@Override
            public void run() {
				deploymentsProgressIndicator.setVisible(false);
				if(info.codigo == 0) {
					JFDialog dialog = new JFDialog();
					me.refreshApresentacao();
					dialog.show("Reenvio cancelado com sucesso", "Ok");
				} else {
					showErrorMessage("Erro cancelando o reenvio de formulário: " + info.mensagem);
				}
			}			
		});	    
    }

	@Override
    public void recebeInfoValorNovaInstancia(final InfoRetornoInstancia info) {
		final JFDeploymentInstanciaController me = this;
		Platform.runLater(new Runnable() {

			@Override
            public void run() {
				deploymentsProgressIndicator.setVisible(false);
				if(info.codigo == 0) {
					refresher.refreshApresentacao();
					tableInstancias.getItems().add(info.instancia);
                    if (tableInstancias.getItems().size() > 0) {
                        tableInstancias.getSelectionModel().clearAndSelect(tableInstancias.getItems().size() - 1);
                    }
					
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
				deploymentsProgressIndicator.setVisible(false);				
			}
		});		
	}

	
	@Override
    public void salvarInstanciaSucesso(final InfoRetornoInstancia info, final ActionEvent actionEvent) {
		Platform.runLater(new Runnable() {
			public void run() {
				deploymentsProgressIndicator.setVisible(false);
		
				if (info.codigo == 0) {
			        alterado = false;
			        fechaJanela(actionEvent);
			        try {
	                    refresher.refreshApresentacao();
                    } catch (Exception e) {
                    }
		        } else {
		        	showErrorMessage(info.mensagem);
		        }
			}
		});
    }

	
	@Override
    public void removeInstanciaSucesso(final InfoRetornoInstancia info) {
		Platform.runLater(new Runnable() {
			public void run() {
				deploymentsProgressIndicator.setVisible(false);
				if(info.codigo == 0) {
        			tableInstancias.getItems().remove(tableInstancias.getSelectionModel().getSelectedIndex());
        			try {
	                    refresher.refreshApresentacao();
                    } catch (Exception e) {
                    }
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});
    }

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		final JFDeploymentInstanciaController me = this;
		if (!alterado) {
	        fechaJanela(actionEvent);
        } else {
			JFDialog dialog = new JFDialog();

        	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
        		@Override
        		public void handle(ActionEvent actionEvent) {
        			deploymentsProgressIndicator.setVisible(true);
        			InstanciasVO instsVo = new InstanciasVO();
        			instsVo.setInstancias(instanciasAlteradas);
               		controller.alterarValoresInstancias(me, instsVo, actionEvent);
        		}
        	};
        	dialog.show("Tem certeza que deseja alterar os valores dos formulários?", "Sim", "Não", act1);        	
        }
	}	
	
	private void fechaJanela(ActionEvent actionEvent) {
	    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
    }	
	
	class Delta { double x, y; }
	
	class EditingCell extends TableCell<InstanciaVO, IValor> {
		private boolean fieldCreated = false;
		
		private TextField textField;
		private eu.schudt.javafx.controls.calendar.DatePicker dtPicker;
		private CheckBox checkBox;
		private ComboBox<Option> comboBox;
		private Button gpsButton;
		private Button fotoButton;
		
		private String fieldType;
		

		public EditingCell() {
		}

		@Override
	    public void startEdit() {
	        super.startEdit();
	        TableRow row = getTableRow();
	        
	        if (!fieldCreated) {
	            createField();
	        }
	        if (row == null || !getTableView().getItems().get(row.getIndex()).getReenviado()) {
	            if (textField != null) {
	                setGraphic(textField);
                } else if(dtPicker != null) {
                	setGraphic(dtPicker);
                } else if(checkBox != null) {
                	setGraphic(checkBox);
                } else if(comboBox != null) {
                	setGraphic(comboBox);
                } else if(gpsButton != null) {
                	setGraphic(gpsButton);
                } else if(fotoButton != null) {
                	setGraphic(fotoButton);
                }
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            Platform.runLater(new Runnable() {
		            @Override
		            public void run() {
			            if (textField != null) {
	                        textField.requestFocus();
	                        textField.selectAll();
                        } else if(dtPicker != null) {
                        	dtPicker.requestFocus();
                        } else if(checkBox != null) {
                        	checkBox.requestFocus();
                        } else if(comboBox != null) {
                        	comboBox.requestFocus();
                        } else if(gpsButton != null) {
                        	gpsButton.requestFocus();
                        } else if(fotoButton != null) {
                        	fotoButton.requestFocus();
                        }
		            }
	            });
            }
	    }
		
		
	    @Override
	    public void cancelEdit() {
	        super.cancelEdit();
	        if (this.fieldType != null && this.fieldType.equalsIgnoreCase("Select")) {
            	ValorFormularioVO selectItem = (ValorFormularioVO)getItem();
	    		String vlr = selectItem.getValor();
	    		for(Option opt : selectItem.getOptions()) {
	    			if(opt.getValue().equalsIgnoreCase(vlr)) {
	    				setText(opt.getText());
	    				break;
	    			}
	    		}	    		
	        } else if(this.fieldType != null && this.fieldType.equalsIgnoreCase("GPSField")) {
	        	setGraphic(gpsButton);
	        } else if(this.fieldType != null && this.fieldType.equalsIgnoreCase("Camera")) {
	        	setGraphic(fotoButton);
	        } else {
		        setText(getString());
	        }
	        setContentDisplay(ContentDisplay.TEXT_ONLY);
	    }    
	    
	    
	    @Override
	    public void updateItem(IValor item, boolean empty) {
	        super.updateItem(item, empty);
	        if (empty) {	        	
	            setText(null);
	            setGraphic(null);
	        } else {
		        TableRow row = getTableRow();
	            if (isEditing()) {
	                if (fieldCreated && textField != null) {
	                    textField.setText(getString());
		                setGraphic(textField);
	                } else if (fieldCreated && dtPicker != null) {
	                	if(getString().contains("/")) {
	                		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	                		try {
	                            dtPicker.setSelectedDate(sdf.parse(getString()));
                            } catch (ParseException e) {
                            	dtPicker.setSelectedDate(new Date());
                            }
	                		setGraphic(dtPicker);
	                	} else {
	                		GregorianCalendar cal = new GregorianCalendar();
	                		cal.setTimeInMillis(Long.valueOf(getString()));
	                		dtPicker.setSelectedDate(cal.getTime());
	                		setGraphic(dtPicker);
	                	}
	                } else if (this.fieldType != null && this.fieldType.equalsIgnoreCase("Select")) {
	                	ValorFormularioVO selectItem = (ValorFormularioVO)getItem();
	    	    		String vlr = selectItem.getValor();
	    	    		for(Option opt : selectItem.getOptions()) {
	    	    			if(opt.getValue().equalsIgnoreCase(vlr)) {
	    	    				setText(opt.getText());
	    	    				break;
	    	    			}
	    	    		}
	    	    	} else if (fieldType != null && fieldType.equalsIgnoreCase("GPSField")) {
	    	    		setGraphic(gpsButton);
	    	    	} else if(fieldType != null && fieldType.equalsIgnoreCase("Camera")) {
	    	    		setGraphic(fotoButton);
	    	    	} else if(fieldCreated && checkBox != null) {
	                	String valor = getString();
	                	if(valor != null && valor.trim().length() > 0 && valor.equalsIgnoreCase("X")) {
	                		checkBox.setSelected(true);
	                	} else {
	                		checkBox.setSelected(false);
	                	}
	                	setGraphic(checkBox);
	                }
	                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            } else {
	            	if(this.fieldType != null && this.fieldType.equalsIgnoreCase("datepicker")) {
	    	    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    	    		if(getString() != null && !getString().contains("/")) {
	    	    			try{
	    	    				GregorianCalendar gc = new GregorianCalendar();
	    	    				gc.setTimeInMillis(Long.parseLong(getString()));
	    	    				setText(sdf.format(gc.getTime()));
	    	    			} catch(Exception e) {	    	    				
	    	    			}
	    	    		} else {
	    	    			setText(getString());
	    	    		}
	    	    	} else if(this.fieldType != null && (
	    	    				this.fieldType.equalsIgnoreCase("CheckBox") || 
	    	    				this.fieldType.equalsIgnoreCase("Toggle"))) {
	    	    		setText(getString());
	    	    	} else if (this.fieldType != null && this.fieldType.equalsIgnoreCase("Select")) {
	    	    		ValorFormularioVO selectItem = (ValorFormularioVO)getItem();
	    	    		String vlr = selectItem.getValor();
	    	    		for(Option opt : selectItem.getOptions()) {
	    	    			if(opt.getValue().equalsIgnoreCase(vlr)) {
	    	    				setText(opt.getText());
	    	    				break;
	    	    			}
	    	    		}
	    	    	} else if (fieldType != null && fieldType.equalsIgnoreCase("GPSField")) {
	    	    		setGraphic(gpsButton);
	    	    	} else if(fieldType != null && fieldType.equalsIgnoreCase("Camera")) {
	    	    		setGraphic(fotoButton);
	    	    	} else {
	    	    		setText(getString());
	    	    	}
	                setContentDisplay(ContentDisplay.TEXT_ONLY);
	            }
	            if(row != null) {
	    	        this.fieldType = item.getFieldType();
		        	InstanciaVO vo = getTableView().getItems().get(row.getIndex());
		        	if(vo.getReenviado()) {
		        		setStyle("-fx-font-size: 16;-fx-text-fill: RED;");
		        	} else {
		        		setStyle("");
		        	}
		        }

	        }
	    }
	    
	    
	    private void createField() {
	    	if(this.fieldType != null && this.fieldType.equalsIgnoreCase("datepicker")) {
	    		createDatePicker();
	    	} else if(this.fieldType != null && (
    				this.fieldType.equalsIgnoreCase("CheckBox") || 
    				this.fieldType.equalsIgnoreCase("Toggle"))) {
	    		createCheckBox();
	    	} else if (this.fieldType != null && this.fieldType.equalsIgnoreCase("Select")) {
	    		createComboBox();
	    	} else if (this.fieldType != null && this.fieldType.equalsIgnoreCase("GPSField")) {
	    		createGPSButton();
	    	} else if(this.fieldType != null && this.fieldType.equalsIgnoreCase("Camera")) {
	    		createFotoButton();
	    	} else {
	    		createTextField();
	    	}
	    }
	    
	    private void createDatePicker() {
	    	dtPicker = new eu.schudt.javafx.controls.calendar.DatePicker();
	    	dtPicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
	    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    	dtPicker.setDateFormat(sdf);
	    	dtPicker.getCalendarView().todayButtonTextProperty().set("Hoje");
	    	
	    	String strDate = getString();

	    	if (strDate != null && strDate.trim().length() > 0 && strDate.contains("/")) {
	            try {
	                dtPicker.setSelectedDate(sdf.parse(strDate));
                } catch (ParseException e) {
                }
            }
			dtPicker.setOnKeyPressed(new EventHandler<KeyEvent>() {

				@Override
                public void handle(KeyEvent keyEvent) {
					if(keyEvent.getCode() == KeyCode.ENTER) {						
						getItem().setValorField(dtPicker.getSelectedDate().getTime() + "");
	                    commitEdit(getItem());
					} else if(keyEvent.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					} else if (keyEvent.getCode() == KeyCode.TAB) {
						getItem().setValorField(dtPicker.getSelectedDate().getTime() + "");
						commitEdit(getItem());
	                    TableColumn nextColumn = getNextColumn(!keyEvent.isShiftDown());
	                    if (nextColumn != null) {
	                        getTableView().edit(getTableRow().getIndex(), nextColumn);
	                    }
					}
                }
			});
	    }
	    
	    private void createComboBox() {
	    	comboBox = getCombo();
	    }
	    
	    private void createGPSButton() {
	    	gpsButton = new Button();
            
	    	gpsButton.getStyleClass().add("btn_gps_centralizado");
	    	gpsButton.setTooltip(new Tooltip("Clique para visualizar no mapa"));
	    	gpsButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
            		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
                    DialogFactory.DialogRetVO vo = null;
                    try {
                        vo = factory.loadDialog("dialog/equipamento/mapa/JFWebMap.fxml");
                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
                        final WebMapController mapController = (WebMapController)vo.controller;
                    	String latlong = getItem().getValorField();
                    	
                        if (!StringUtils.isEmpty(latlong) && latlong.length() > 4) {
                        	StringTokenizer tok = new StringTokenizer(latlong, ";");
                        	String latitude = tok.nextToken();
                        	String longitude = tok.nextToken();
							final LocalizacaoVO loc = new LocalizacaoVO();
							loc.setLatitude(latitude);
							loc.setLongitude(longitude);
							mapController.addMapListener(new IMapListener() {
								@Override
								public void executeCommand() {
									mapController.setMapCenter(loc.latitude, loc.longitude);
									mapController.putPin(loc.latitude, loc.longitude, "");
								}
							});
						}
						final Stage dialog = new Stage(StageStyle.UTILITY);
                        dialog.initModality(Modality.WINDOW_MODAL);
                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

                        dialog.setScene(SceneBuilder.create().width(800).height(700).root((Parent) vo.root).build());
                        dialog.setResizable(true);
                        dialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }                	
                }
            });	    	
	    }

	    private void createFotoButton() {
	    	fotoButton = new Button();
            
	    	fotoButton.getStyleClass().add("btn_picture_centralizado");
	    	fotoButton.setTooltip(new Tooltip("Clique para visualizar as fotos"));
	    	fotoButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
//            		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
//                    DialogFactory.DialogRetVO vo = null;
//                    try {
//                        vo = factory.loadDialog("dialog/equipamento/mapa/JFWebMap.fxml");
//                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
//                        final WebMapController mapController = (WebMapController)vo.controller;
//                    	String latlong = getItem().getValorField();
//                    	
//                        if (!StringUtils.isEmpty(latlong) && latlong.length() > 4) {
//                        	StringTokenizer tok = new StringTokenizer(latlong, ";");
//                        	String latitude = tok.nextToken();
//                        	String longitude = tok.nextToken();
//							final LocalizacaoVO loc = new LocalizacaoVO();
//							loc.setLatitude(latitude);
//							loc.setLongitude(longitude);
//							mapController.addMapListener(new IMapListener() {
//								@Override
//								public void executeCommand() {
//									mapController.setMapCenter(loc.latitude, loc.longitude);
//									mapController.putPin(loc.latitude, loc.longitude, "");
//								}
//							});
//						}
//						final Stage dialog = new Stage(StageStyle.UTILITY);
//                        dialog.initModality(Modality.WINDOW_MODAL);
//                        dialog.initOwner(JazzForms.PRIMARY_STAGE);
//
//                        dialog.setScene(SceneBuilder.create().width(800).height(700).root((Parent) vo.root).build());
//                        dialog.setResizable(true);
//                        dialog.show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }                	
                }
            });	    	
	    }	    
	    
	    private void createCheckBox() {
	    	checkBox = new CheckBox();
	    	String strValor = getString();
	    	if(strValor != null && strValor.trim().length() > 0 && strValor.equalsIgnoreCase("X")) {
	    		checkBox.setSelected(true);
	    	}
	    	
	    	checkBox.setOnKeyPressed(new EventHandler<KeyEvent>(){

				@Override
                public void handle(KeyEvent keyEvent) {
					if(keyEvent.getCode() == KeyCode.ENTER) {
						getItem().setValorField(checkBox.isSelected()  + "");
						commitEdit(getItem());
					} else if(keyEvent.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					} else if(keyEvent.getCode() == KeyCode.TAB) {
						getItem().setValorField(checkBox.isSelected()  + "");
						commitEdit(getItem());
	                    TableColumn nextColumn = getNextColumn(!keyEvent.isShiftDown());
	                    if (nextColumn != null) {
	                        getTableView().edit(getTableRow().getIndex(), nextColumn);
	                    }
					}
                }
	    	});	    	
	    }
	    
	    private void createTextField() {
	        textField = new TextField(getString());
	        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
	        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
	            @Override
	            public void handle(KeyEvent t) {
	                if (t.getCode() == KeyCode.ENTER) {
	                	getItem().setValorField(textField.getText());
	                    commitEdit(getItem());
	                } else if (t.getCode() == KeyCode.ESCAPE) {
	                    cancelEdit();
	                } else if (t.getCode() == KeyCode.TAB) {
	                	getItem().setValorField(textField.getText());
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
	                	getItem().setValorField(textField.getText());
	                    commitEdit(getItem());
	                }
	            }
	        });
	    }
	    
	    private ComboBox getCombo() {
	    	ComboBox<Option> combo = new ComboBox<Option>();
	    	combo.setMinWidth(100);
	    	combo.setPrefWidth(150);
	    	final IValor item = getItem();
	    	if(item instanceof ValorFormularioVO) {
	    		ValorFormularioVO vlr = (ValorFormularioVO)item;
	    		Option selOption = null;
	    		for(Option opt : vlr.getOptions()) {
	    			if (opt.getValue().equalsIgnoreCase(((ValorFormularioVO) item).getValor())){
	    				selOption = opt;
	    				break;
	    			}
	    		}
	    		combo.getItems().addAll(vlr.getOptions());
	    		combo.getSelectionModel().select(selOption);
	    	}
	    	
	    	combo.valueProperty().addListener(new ChangeListener<Option>() {

				@Override
				public void changed(ObservableValue<? extends Option> ov, Option anterior, Option nova) {
					ValorFormularioVO vlr = (ValorFormularioVO)item;
					vlr.setValor(nova.getValue());
					setText(nova.getText());
					commitEdit(getItem());
				}	    		
			});
	    	
	    	return combo;
	    }
	    
	    private String getString() {
	    	IValor item = getItem();
	        return item == null ? "" : item.toString();
	    }
	    /**
	     *
	     * @param forward true gets the column to the right, false the column to the left of the current column
	     * @return
	     */
	    private TableColumn<InstanciaVO, ?> getNextColumn(boolean forward) {
	        List<TableColumn<InstanciaVO, ?>> columns = new ArrayList<>();
	        for (TableColumn<InstanciaVO, ?> column : getTableView().getColumns()) {
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
	     
	    private List<TableColumn<InstanciaVO, ?>> getLeaves(TableColumn<InstanciaVO, ?> root) {
	        List<TableColumn<InstanciaVO, ?>> columns = new ArrayList<>();
	        if (root.getColumns().isEmpty()) {
	            //We only want the leaves that are editable.
	            if (root.isEditable()) {
	                columns.add(root);
	            }
	            return columns;
	        } else {
	            for (TableColumn<InstanciaVO, ?> column : root.getColumns()) {
	                columns.addAll(getLeaves(column));
	            }
	            return columns;
	        }
	    }		
	}
}
