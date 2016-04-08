package br.com.laminarsoft.jazzforms.ui.dialog.deploy;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.InstanciaVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ProjetoImplantadoVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.IRefreshAprsesentacao;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.JFDeploymentInstanciaController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

@SuppressWarnings("all")
public class JFDeploymentDataController implements Initializable, IGerenciamentoDeployment, IRefreshAprsesentacao {
	@FXML private TitledPane deploymentsErrorPane;
	@FXML private VBox mainPaneUsuarios;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator deploymentsProgressIndicator;
	@FXML private TableView<InfoDeploymentProperty> tableDeployments;

	
	private WebServiceController controller = WebServiceController.getInstance();

	private PublicaProjetoController pubController;
	private String grupoSelecionado;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}

	public void setGrupoSelecionado(String grupoSel) {
		this.grupoSelecionado = grupoSel;
	}
	
	
	@Override
    public void refreshApresentacao() {
		updateData();
    }
	

	@Override
    public void setRefresher(IRefreshAprsesentacao refresher) {
    }
	

	@Override
    public void retornoRemocaoFisicaDeployment(final InfoRetornoDeployments info) {
		Platform.runLater(new Runnable() {
			public void run() {
    			deploymentsProgressIndicator.setVisible(false);
				if (info.codigo == 0) {
                	JFDialog dialog = new JFDialog();
                	dialog.show(info.mensagem, "Ok");
	                updateData();
                } else {
                	JFDialog dialog = new JFDialog();
                	dialog.show(info.mensagem, "Ok");
                }
			}
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		deploymentsProgressIndicator.visibleProperty().bindBidirectional(mainPaneUsuarios.disableProperty());
		deploymentsProgressIndicator.setVisible(false);
		mainPaneUsuarios.visibleProperty().bind(deploymentsErrorPane.visibleProperty().not());
		
		TableColumn<InfoDeploymentProperty, String> tblColumnNome = new TableColumn<InfoDeploymentProperty, String>("Nome");
		TableColumn<InfoDeploymentProperty, String> tblColumnDescricao = new TableColumn<InfoDeploymentProperty, String>("Descrição");
		TableColumn<InfoDeploymentProperty, String> tblColumnDataUltimaAlteracao = new TableColumn<InfoDeploymentProperty, String>("Data de última alteração");
		TableColumn<InfoDeploymentProperty, String> tblColumnAcaoUltimaAlteracao = new TableColumn<InfoDeploymentProperty, String>("Ação de última alteração");
		TableColumn<InfoDeploymentProperty, String> tblColumnDeploymentId = new TableColumn<InfoDeploymentProperty, String>("Identificador");
		TableColumn<InfoDeploymentProperty, String> tblColumnDataDeployment = new TableColumn<InfoDeploymentProperty, String>("Data de implantação");
		TableColumn<InfoDeploymentProperty, String> tblColumnRemovido = new TableColumn<InfoDeploymentProperty, String>("Removido");
		TableColumn<InfoDeploymentProperty, String> tblColumnAtivo = new TableColumn<InfoDeploymentProperty, String>("Ativo");
		TableColumn<InfoDeploymentProperty, String> tblColumnSubstituido = new TableColumn<InfoDeploymentProperty, String>("Substituido");
		TableColumn<InfoDeploymentProperty, String> tblColumnQuantidade = new TableColumn<InfoDeploymentProperty, String>("Quantidade");
		
		tblColumnNome.setPrefWidth(100);
		tblColumnDescricao.setPrefWidth(200);
		tblColumnDataUltimaAlteracao.setPrefWidth(130); 
		tblColumnAcaoUltimaAlteracao.setPrefWidth(230);
		tblColumnDataDeployment.setPrefWidth(130);
		
		tblColumnNome.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("nomeProjeto"));
		
		tblColumnNome.setCellFactory(createCustomCellFactory(new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent mouseEvent) {
				
				if (mouseEvent.getButton() == MouseButton.PRIMARY) {
					lancaJanelaDetalheProjeto();
				}
            }	            	
        }));
		
		tblColumnDescricao.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("descricaoProjeto"));
		tblColumnDataUltimaAlteracao.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("dataUltimaAlteracao"));
		tblColumnAcaoUltimaAlteracao.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("acaoUltimaAlteracao"));
		tblColumnDeploymentId.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("deploymentId"));
		
		tblColumnDeploymentId.setCellFactory(createCustomCellFactory(new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton() == MouseButton.PRIMARY) {
					lancaJanelaDeployProjeto();
				}
            }			
		}));
		
		tblColumnDataDeployment.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("dataDeployment"));
		tblColumnRemovido.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("removido"));
		tblColumnAtivo.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("ativo"));
		tblColumnSubstituido.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("substituido"));
		tblColumnQuantidade.setCellValueFactory(new PropertyValueFactory<InfoDeploymentProperty, String>("quantidade"));
		
		tblColumnQuantidade.setCellFactory(createCustomCellFactory(new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent arg0) {
				lancaJanelaInstanciasProjeto();
			}
		}));
		
		tableDeployments.getColumns().addAll(tblColumnNome, tblColumnDescricao, tblColumnDataUltimaAlteracao, tblColumnAcaoUltimaAlteracao, 
				tblColumnDeploymentId, tblColumnDataDeployment, tblColumnRemovido, tblColumnAtivo, tblColumnSubstituido, tblColumnQuantidade);
		
		adicionarMenuSuspenso();

		updateData();
	}

	private Callback<TableColumn<InfoDeploymentProperty,String>, TableCell<InfoDeploymentProperty,String>> createCustomCellFactory(final EventHandler<MouseEvent> handler) {
		Callback<TableColumn<InfoDeploymentProperty,String>, TableCell<InfoDeploymentProperty,String>> cellFactory = 
				new Callback<TableColumn<InfoDeploymentProperty,String>, TableCell<InfoDeploymentProperty,String>>() {

			@Override
            public TableCell call(TableColumn arg0) {
	            TableCell cell = new TableCell<InfoDeploymentProperty, String>() {

					@Override
                    protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    setGraphic(null);
	                    if (!empty) {
	                    	setText(getString());
	                    	setTextFill(Color.BLUEVIOLET);
	                    } else {
	                    	setText(null);
	                    }	                    
                    }
	            	
					private String getString() {
						return getItem() == null ? "" : getItem().toString();
					}
	            };
	            
	            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
	            
	            cell.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>(){

					@Override
                    public void handle(MouseEvent mouseEvent) {
						((Node)mouseEvent.getSource()).setCursor(Cursor.HAND);
                    }	            	
	            });
	            
	            cell.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>(){

					@Override
                    public void handle(MouseEvent mouseEvent) {
						((Node)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
                    }	            	
	            });	            
				
	            return cell;
            }			
		};	
		
		return cellFactory;
	}
	
	private void lancaJanelaDetalheProjeto() {
        DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("dialog/deploy/JFDeploymentProjeto.fxml");
			JFDeploymentProjetoController grpController = (JFDeploymentProjetoController)vo.controller;
			grpController.setValoresRotulos(tableDeployments.getSelectionModel().getSelectedItem().projetoImplantado, false);
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			
			final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(JazzForms.PRIMARY_STAGE);
			final Scene scene = SceneBuilder.create().width(450).height(350).root((Parent)vo.root).build();
			scene.setFill(Color.TRANSPARENT);
			
			
			dialog.setScene(scene);
			dialog.setResizable(true);
			dialog.show();    
			
			Parent root = (Parent)vo.root;
			
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
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private void lancaJanelaDeployProjeto() {
        DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("dialog/deploy/JFDeploymentProjeto.fxml");
			JFDeploymentProjetoController grpController = (JFDeploymentProjetoController)vo.controller;
			grpController.setValoresRotulos(tableDeployments.getSelectionModel().getSelectedItem().projetoImplantado, true);
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			
			final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			final Scene scene = SceneBuilder.create().width(450).height(350).root((Parent)vo.root).build();
			scene.setFill(Color.TRANSPARENT);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(JazzForms.PRIMARY_STAGE);
			
			dialog.setScene(scene);
			dialog.setResizable(true);
			dialog.show();    
			
			Parent root = (Parent)vo.root;
			
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
		} catch (IOException e) {
			e.printStackTrace();
		}
    }	
	
	private void lancaJanelaInstanciasProjeto() {
        DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("dialog/deploy/instancia/JFDeploymentInstancia.fxml");
			JFDeploymentInstanciaController grpController = (JFDeploymentInstanciaController)vo.controller;
			if(grpController instanceof IRefreshAprsesentacao) {
				grpController.setRefresher(this);
			}
			grpController.setProjetoSelecionado(tableDeployments.getSelectionModel().getSelectedItem().projetoImplantado.getProjetoId());
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			
			final Stage dialog = new Stage(StageStyle.UTILITY);
			final Scene scene = SceneBuilder.create().width(700).height(450).root((Parent)vo.root).build();
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(JazzForms.PRIMARY_STAGE);
			
			dialog.setScene(scene);
			dialog.setResizable(true);
			dialog.show();    
			
			Parent root = (Parent)vo.root;
			
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void adicionarMenuSuspenso() {
		final JFDeploymentDataController me = this;
		final ContextMenu cmItem = new ContextMenu();
		MenuItem miDetalhesProjeto = new MenuItem("Ver detalhes do projeto");
		miDetalhesProjeto.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent arg0) {
				lancaJanelaDetalheProjeto();				
            }			
		});
		
		MenuItem miDetalhesDeployment = new MenuItem("Ver detalhes da implantação");
		miDetalhesDeployment.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent arg0) {
	           lancaJanelaDeployProjeto();	            
            }			
		});
		
		MenuItem miFormulariosPreenchidos = new MenuItem("Ver formulários preenchidos");
		miFormulariosPreenchidos.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent arg0) {
				lancaJanelaInstanciasProjeto();
			}			
		});
		
		MenuItem miRemoveProjeto = new MenuItem("Remover");
		miRemoveProjeto.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent actionRemover) {
				JFDialog dialog = new JFDialog();

            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
            		@Override
            		public void handle(ActionEvent actionEvent) {
            			InfoDeploymentProperty infoDep = tableDeployments.getSelectionModel().getSelectedItem();
            			deploymentsProgressIndicator.setVisible(true);
            			controller.removeDeploymentFisico(me, Long.valueOf(infoDep.projetoImplantado.getProjetoId()));
            		}
            	};
            	dialog.show("Você está prestes a remover permanentemente a implantação. Você deseja remover a implantação?", "Sim", "Não", act1);		
            }			
		});
		
		cmItem.getItems().add(miDetalhesDeployment);
		cmItem.getItems().add(miDetalhesProjeto);
		cmItem.getItems().add(miFormulariosPreenchidos);
		cmItem.getItems().add(new SeparatorMenuItem());
		cmItem.getItems().add(miRemoveProjeto);
		
	    tableDeployments.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY && !mouseEvent.isControlDown()) {
                	InfoDeploymentProperty item = tableDeployments.getSelectionModel().getSelectedItem();
                	cmItem.show(tableDeployments, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                } else {
                	cmItem.hide();
                }
            }
		});
    }
	
	public void updateData() {
		deploymentsProgressIndicator.setVisible(true);
		controller.findInformacoesImplantacoes(this);
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
    public void recebeInfoDeployments(final InfoRetornoDeployments info) {
		Platform.runLater(new Runnable() {
			public void run() {
				deploymentsProgressIndicator.setVisible(false);
				
				if (info.codigo == 0) {
	                Collections.sort(info.listaDeployments);
	                tableDeployments.getItems().clear();
	                for (ProjetoImplantadoVO vo : info.listaDeployments) {
		                InfoDeploymentProperty implVo = new InfoDeploymentProperty(vo);
		                tableDeployments.getItems().add(implVo);
	                }
                } else {
                	JFDialog dialog = new JFDialog();
                	dialog.show(info.mensagem, "Ok");
                }
			}
		});		
    }

	@Override
    public void onRecuperaDeploymentError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
				deploymentsProgressIndicator.setVisible(false);				
			}
		});		
	}

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		fechaJanela(actionEvent);
	}	
	
	private void fechaJanela(ActionEvent actionEvent) {
	    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
    }	
	
	class Delta { double x, y; }	
	
	public static class InfoDeploymentProperty {
		private final SimpleStringProperty nomeProjeto;
		private final SimpleStringProperty descricaoProjeto;
		private final SimpleStringProperty dataUltimaAlteracao;
		private final SimpleStringProperty acaoUltimaAlteracao;
		private final SimpleStringProperty deploymentId;
		private final SimpleStringProperty dataDeployment;
		private final SimpleStringProperty removido;
		private final SimpleStringProperty ativo;
		private final SimpleStringProperty substituido;
		private final SimpleStringProperty quantidade;
		
		public ProjetoImplantadoVO projetoImplantado;
		
		public InfoDeploymentProperty(ProjetoImplantadoVO vo) {
			this.nomeProjeto = new SimpleStringProperty(vo.getNomeProjeto());
			this.descricaoProjeto = new SimpleStringProperty(vo.getDescProjeto());
			
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			this.dataUltimaAlteracao = new SimpleStringProperty(vo.getUltimaAlteracao() != null ? df.format(vo.getUltimaAlteracao()) : "");
			
			this.acaoUltimaAlteracao = new SimpleStringProperty(vo.getDescUltimaAlteracao());
			this.deploymentId = new SimpleStringProperty(String.valueOf(vo.getDeployId()));
			this.dataDeployment = new SimpleStringProperty(vo.getDhPublicacao() != null ? df.format(vo.getDhPublicacao()) : "");
			this.removido = new SimpleStringProperty(vo.getRemovido() ? "X" : "");
			this.ativo = new SimpleStringProperty(vo.getAtivo() ? "X" : "");
			this.substituido = new SimpleStringProperty(vo.getSubstituido() ? "X" : "");
			this.quantidade = new SimpleStringProperty(String.valueOf(vo.getQtdInstancias()));
			
			this.projetoImplantado = vo;
		}
		
		public void setNomeProjeto(String nomeProjeto) {
			this.nomeProjeto.set(nomeProjeto);
		}

		public String getNomeProjeto() {
			return nomeProjeto.get();
		}

		public void setDescricaoProjeto(String desc) {
			this.descricaoProjeto.set(desc);
		}
		
		public String getDescricaoProjeto() {
			return descricaoProjeto.get();
		}

		public void setDataUltimaAlteracao(String dt) {
			this.dataUltimaAlteracao.set(dt);
		}
		
		public String getDataUltimaAlteracao() {
			return dataUltimaAlteracao.get();
		}
		
		public void setAcaoUltimaAlteracao(String acao) {
			this.acaoUltimaAlteracao.set(acao);
		}

		public String getAcaoUltimaAlteracao() {
			return acaoUltimaAlteracao.get();
		}

		public void setDeploymentId(String id) {
			this.deploymentId.set(id);
		}
		
		public String getDeploymentId() {
			return deploymentId.get();
		}
		
		public void setDataDeployment(String dt) {
			this.dataDeployment.set(dt);
		}

		public String getDataDeployment() {
			return dataDeployment.get();
		}

		public void setRemovido(String rem) {
			this.removido.set(rem);
		}
		
		public String getRemovido() {
			return removido.get();
		}
		
		public void setAtivo(String ativo) {
			this.ativo.set(ativo);
		}

		public String getAtivo() {
			return ativo.get();
		}
		
		public void setSubstituido(String subs) {
			this.substituido.set(subs);
		}
		
		public String getSubstituido() {
			return substituido.get();
		}
		
		public void setQuantidade(String qtd) {
			this.quantidade.set(qtd);
		}

		public String getQuantidade() {
			return quantidade.get();
		}		
	}
}
