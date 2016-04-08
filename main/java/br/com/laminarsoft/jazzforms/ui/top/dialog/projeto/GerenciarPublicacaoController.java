package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.Grupo;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.Usuario;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerDeploymentResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

@SuppressWarnings("all")
public class GerenciarPublicacaoController implements Initializable, IServerResponseHandler, IServerDeploymentResponseHandler {

	@FXML private TitledPane errorPane;	
	@FXML private Label errorLabel;	
	@FXML private ProgressIndicator progressIndicator;	
	@FXML private VBox mainPane;	
	@FXML private TableView<ProjetoTblElement> tableGerenPublicacoes;
	@FXML private Button btnCancel;
	
	WebServiceController controller = WebServiceController.getInstance();
	private CheckBox currentCheckBox;
	
	private ICenterDispatcher centerDispathcer;
	
	public void setCenterDispatcher(ICenterDispatcher dispatcher) {
		this.centerDispathcer = dispatcher;
		AwesomeDude.setIcon(btnCancel, AwesomeIcon.CHECK_CIRCLE, "16px", ContentDisplay.RIGHT);
	}
	
	@Override
    public void initialize(URL url, ResourceBundle bundle) {
	    TableColumn colProjeto = new TableColumn("Projeto");
	    TableColumn colDhPublicacao = new TableColumn("Data/Hora de publicação");
	    TableColumn colAtivo = new TableColumn("Ativo");
	    TableColumn colAcao = new TableColumn("Ação");
	    
	    tableGerenPublicacoes.getColumns().addAll(colProjeto, colDhPublicacao, colAtivo, colAcao);
	   
	    progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		tableGerenPublicacoes.getColumns().clear();
		updateData();
	}
	
	private void updateData() {
		
		final GerenciarPublicacaoController atual = this;
		progressIndicator.setVisible(true);
		controller.findProjetosPublicados(this);
		
		tableGerenPublicacoes.setEditable(true);
		TableColumn<ProjetoTblElement, String> nomeProjeto = new TableColumn<ProjetoTblElement, String>("Nome");
		nomeProjeto.setCellValueFactory(new PropertyValueFactory<ProjetoTblElement, String>("nomeProjeto"));
		nomeProjeto.setPrefWidth(350);
		
		TableColumn<ProjetoTblElement, String> dataPublicacaoProjeto = new TableColumn<ProjetoTblElement, String>("Data de publicação");
		dataPublicacaoProjeto.setCellValueFactory(new PropertyValueFactory<ProjetoTblElement, String>("dataPublicacao"));
		dataPublicacaoProjeto.setPrefWidth(200);
		
		
		TableColumn<ProjetoTblElement, Boolean> projetoAtivo = new TableColumn<ProjetoTblElement, Boolean>("Ativo");
		projetoAtivo.setCellValueFactory(new PropertyValueFactory<ProjetoTblElement, Boolean>("ativo"));
//		projetoAtivo.setCellFactory(CheckBoxTableCell.forTableColumn(projetoAtivo));
		projetoAtivo.setCellFactory(new Callback<TableColumn<ProjetoTblElement, Boolean>, TableCell<ProjetoTblElement, Boolean>>() {
			public TableCell<ProjetoTblElement, Boolean> call(TableColumn<ProjetoTblElement, Boolean> p) {
				final TableCell<ProjetoTblElement, Boolean> cell = new TableCell<ProjetoTblElement, Boolean>() {
					@Override
					public void updateItem(final Boolean item, boolean empty) {
						if (item == null) {
							return;
						}
						super.updateItem(item, empty);
						if (!isEmpty()) {
							final ProjetoTblElement projeto = getTableView().getItems().get(getIndex());
							final CheckBox checkBox = new CheckBox();
							checkBox.selectedProperty().bindBidirectional(projeto.getAtivo());
							checkBox.setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent event) {
									
									EventHandler<ActionEvent> actOk = new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent actionEvent) {
											controller.deactivateDeployedProject(atual, 
													projeto.projeto.getId(), 
													!checkBox.isSelected(),
													JazzForms.USUARIO_LOGADO.getUid());
										}
									};
									JFDialog dialog = new JFDialog();
									dialog.show("Tem certeza que deseja alterar o status da implantação?", "Ok", "Cancela", actOk);
									((CheckBox)event.getSource()).setSelected(!((CheckBox)event.getSource()).isSelected());
									currentCheckBox = checkBox;
									event.consume();
								}
							});
							setGraphic(checkBox);
						}
					}
				};
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});
		

		TableColumn<ProjetoTblElement, String> acao = new TableColumn<>("Ação");
		acao.setCellValueFactory(new PropertyValueFactory<ProjetoTblElement, String>("nomeProjeto"));
		
		Callback<TableColumn<ProjetoTblElement, String>, TableCell<ProjetoTblElement, String>> acaoColumnCellFactory = 
                new Callback<TableColumn<ProjetoTblElement, String>, TableCell<ProjetoTblElement, String>>() {

            @Override
            public TableCell call(final TableColumn param) {
                final TableCell cell = new TableCell() {

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(5);
                        	final Button btnRefresh = new Button();
                    		AwesomeDude.setIcon(btnRefresh, AwesomeIcon.REFRESH, "14px", ContentDisplay.RIGHT);

                            btnRefresh.getStyleClass().add("btn_refresh_centralizado");
                            btnRefresh.setTooltip(new Tooltip("Atualiza deployment com última versão do projeto"));
                            btnRefresh.setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    param.getTableView().getSelectionModel().select(getIndex());
                                    final ProjetoTblElement item = tableGerenPublicacoes.getSelectionModel().getSelectedItem();
                                    if (item != null) {
                                    	JFDialog dialog = new JFDialog();

                        				EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
                        					@Override
                        					public void handle(ActionEvent actionEvent) {
                                            	progressIndicator.setVisible(true);
                        						controller.changeDeploymentProject(atual, item.projeto.getDeployment().getId(), JazzForms.USUARIO_LOGADO.getUid());
                        					}
                        				};
                        				dialog.show("Tem certeza que deseja substituir o projeto desse deployment pelo mais atual?", "Sim", "Não", act1);
                        				event.consume();
                        			}
                                }
                            });
                            
                            final Button btnEdita = new Button();
                            AwesomeDude.setIcon(btnEdita, AwesomeIcon.PENCIL, "14px", ContentDisplay.RIGHT);
                            btnEdita.getStyleClass().add("btn_edit_centralizado");
                            btnEdita.setTooltip(new Tooltip("Edita o deployment"));
                            btnEdita.setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    param.getTableView().getSelectionModel().select(getIndex());
                                    final ProjetoTblElement item = tableGerenPublicacoes.getSelectionModel().getSelectedItem();
                                    if (item != null) {
                                		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
                                		DialogFactory.DialogRetVO vo = null;
                                		try {
                                			Long deployId = item.projeto.getDeployment().getId();
                                			vo = factory.loadDialog("top/dialog/projeto/JFPublicaProjetoDialog.fxml");
                                			PublicaProjetoController cntrl = (PublicaProjetoController)vo.controller;
                                			cntrl.setCenterDispathcer(centerDispathcer);
                                			InfoRetornoDeployments infoDep = controller.getDeploymentById(deployId);
                                			if (infoDep.deployment != null) {
                                				List<Usuario> usuarios = infoDep.deployment.getUsuariosPossiveis();
                                				List<Grupo> grupos = infoDep.deployment.getGruposPossiveis();
                                				List<String> nomeUsuarios = new ArrayList<String>();
                                				for(Usuario usr : usuarios) {
                                					nomeUsuarios.add(usr.getLogin());
                                				}
                                				for(Grupo grp : grupos) {
                                					nomeUsuarios.add(grp.getNome());
                                				}
                                				cntrl.setSelectedNodes(nomeUsuarios, new ArrayList<String>(), item.projeto);
                                				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
                                				
                                				final Stage dialog = new Stage(StageStyle.TRANSPARENT);
                                				dialog.initModality(Modality.WINDOW_MODAL);
                                				dialog.initOwner(JazzForms.PRIMARY_STAGE);
                                				
                                				dialog.setScene(SceneBuilder.create().width(600).height(630).root((Parent)vo.root).build());
                                				dialog.setResizable(true);
                                				dialog.show();                                				
                                			}
                                		} catch (IOException e) {
                                			e.printStackTrace();
                                		}
                                    	
                        				event.consume();
                        			}
                                }
                            });                                
                            
                            final Button btnDelete = new Button();
                            AwesomeDude.setIcon(btnDelete, AwesomeIcon.TRASH_ALT, "14px", ContentDisplay.RIGHT);
                            
                            btnDelete.getStyleClass().add("btn_delete_centralizado");
                            btnDelete.setTooltip(new Tooltip("Remove o deployment"));
                            btnDelete.setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    param.getTableView().getSelectionModel().select(getIndex());
                                    final ProjetoTblElement item = tableGerenPublicacoes.getSelectionModel().getSelectedItem();
                                    if (item != null) {
                                    	JFDialog dialog = new JFDialog();

                        				EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
                        					@Override
                        					public void handle(ActionEvent actionEvent) {
                        						controller.removeDeploymentProject(atual, item.projeto.getDeployment().getId(), JazzForms.USUARIO_LOGADO.getUid());
                        					}
                        				};
                        				dialog.show("Tem certeza que deseja remover o deployment?", "Sim", "Não", act1);
                        				event.consume();
                        			}
                                }
                            });                            
                            
                            box.getChildren().add(btnRefresh);
                            box.getChildren().add(btnEdita);                         
                            box.getChildren().add(btnDelete);
                            setGraphic(box);
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        }
                    }
                };
                return cell;
            }
        };

        acao.setCellFactory(acaoColumnCellFactory);
        acao.setPrefWidth(120);
		projetoAtivo.setEditable(true);
		projetoAtivo.setPrefWidth(100);
		
		tableGerenPublicacoes.getColumns().addAll(nomeProjeto, dataPublicacaoProjeto, projetoAtivo, acao);
	}	
	

	
	@Override
    public void onDeploymentProjetoRefreshed(final InfoRetornoDeployments infoDeployment) {
		Platform.runLater(new Runnable() {
			public void run() {		
				progressIndicator.setVisible(false);
				if(infoDeployment.codigo == 0) {
					JFDialog dialog = new JFDialog();
					dialog.show("Deployment atualizado com sucesso", "Ok");
				} else {
					JFDialog dialog = new JFDialog();
					dialog.show("Não foi possível atualizar deployment: " + infoDeployment.mensagem, "Ok");
				}
			}
		});
    }
	
	
	@Override
    public void onDeploymentRemoved(final InfoRetornoDeployments infoDeployment) {
		final GerenciarPublicacaoController atual = this;
		Platform.runLater(new Runnable() {
			public void run() {		
				if(infoDeployment.codigo == 0) {
					JFDialog dialog = new JFDialog();
					dialog.show("Remoção do deployment realizado com sucesso", "Ok");
					controller.findProjetosPublicados(atual);
				} else {
					JFDialog dialog = new JFDialog();
					dialog.show("Não foi possível remover o deployment: " + infoDeployment.mensagem, "Ok");
				}
			}
		});
    }

	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}

	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
	}
	
	@FXML
	protected void btnEditaAction(ActionEvent actionEvent) {
		
	}

	
	
	
	@Override
	public void onProjetoSVNVersions(InfoRetornoSVN infoSVN) {
		
	}

	@Override
    public void onProjetosRetrieveAll(final InfoRetornoProjeto infoProjeto) {
		Platform.runLater(new Runnable() {
			public void run() {
				tableGerenPublicacoes.getItems().clear();
				if (infoProjeto.projetos != null) {
	                for (Projeto projeto : infoProjeto.projetos) {
	                	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	                	String dtPublicacao = df.format(projeto.getDeployment().getDhPublicacao());
	                	ProjetoTblElement elem = new ProjetoTblElement(projeto.getNome(), dtPublicacao, projeto.getDeployment().getAtivo(), projeto);
	                	tableGerenPublicacoes.getItems().add(elem);
	                }
                }
				progressIndicator.setVisible(false);
			}
		});	
	}

	@Override
    public void onProjetoRetrieve(final InfoRetornoProjeto infoProjeto) {
		Platform.runLater(new Runnable() {
			public void run() {
				progressIndicator.setVisible(false);
				if (infoProjeto.mensagem.length() > 0) {
					showErrorMessage(infoProjeto.mensagem);
                } else {
                	JFDialog dialog = new JFDialog();
                	dialog.show("Status alterado com sucesso", "Ok");
                	if (currentCheckBox != null) {
						currentCheckBox.setSelected(!currentCheckBox.isSelected());
						currentCheckBox = null;
					}
                }
			}
		});	
    }

	@Override
    public void onEventosPorNomeComponente(InfoRetornoEvento infoEvento) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onProjetoCreation(InfoRetornoProjeto infoProjeto) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onServerError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
				progressIndicator.setVisible(false);				
			}
		});		    
    }
	
	private void showErrorMessage(String errorMessage) {
		((Label) errorPane.getContent()).setText(errorMessage);
		errorPane.setVisible(true);
	}	
	
	public static class ProjetoTblElement {
		private final SimpleStringProperty nomeProjeto;
		private final SimpleStringProperty dataPublicacao;
		private final SimpleBooleanProperty ativo;
		private Projeto projeto;
		
		public ProjetoTblElement(String nomeProjeto, String dataPublicacao, Boolean ativo, Projeto projeto) {
			this.nomeProjeto = new SimpleStringProperty(nomeProjeto);
			this.dataPublicacao = new SimpleStringProperty(dataPublicacao);
			this.ativo = new SimpleBooleanProperty(ativo.booleanValue());
			this.projeto = projeto;
		}

		public String getNomeProjeto() {
			return nomeProjeto.get();
		}

		public void setNomeProjeto(String nomeProjeto) {
			this.nomeProjeto.set(nomeProjeto);;
		}

		public BooleanProperty ativoProperty() {
			return ativo;
		}
		
		public String getDataPublicacao() {
			return dataPublicacao.get();
		}

		public void setDataPublicacao(String dataPublicacao) {
			this.dataPublicacao.set(dataPublicacao);
		}

		public BooleanProperty getAtivo() {
			return ativo;
		}

		public void setAtivo(Boolean ativo) {
			this.ativo.set(ativo);
		}

		public Projeto getProjeto() {
			return projeto;
		}

		public void setProjeto(Projeto projeto) {
			this.projeto = projeto;
		}
	}
}


