package br.com.laminarsoft.jazzforms.ui.dialog.equipamento;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEquipamento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.EquipamentoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.GrupoEquipamentoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LocalizacaoVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa.IMapListener;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa.WebMapController;
import br.com.laminarsoft.jazzforms.ui.dialog.message.JFMensagem;

@SuppressWarnings("all")
public class JFMantemEquipamentoController implements Initializable, IMantemEquipamento {

	
	@FXML private TreeView<TreeItemDevice> treeEquipamentos;	
	@FXML private TitledPane errorPane;
	@FXML private TitledPane usuariosErrorPane;	
	@FXML private Label errorLabel;
	@FXML private VBox mainPane;	
	@FXML private ProgressIndicator progressIndicator;
	
	private WebServiceController controller = WebServiceController.getInstance();

	private MenuItem miCopiarEquipamento = new MenuItem("Copiar");
	private MenuItem miColarEquipamento = new MenuItem("Colar");
	private static final DataFormat df = new DataFormat("object/equipamento");

	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		Pagina pagina = JazzForms.getPaginaDesenhoAtual();
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		
		progressIndicator.setVisible(false);
		miColarEquipamento.setDisable(true);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		
		updateData();
	}

	
	
	@Override
    public void atualizaListagemEquipamentos() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateData();
			}
		});
    }



	@Override
    public void adicionaEquipamentoGrupoSucesso(final InfoRetornoEquipamento info) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (info.codigo == 0) {
					updateData();
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});
    }



	@Override
    public void removeGrupoEquipamentoSucesso(final InfoRetornoEquipamento info) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(info.codigo == 0) {
					updateData();
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});
    }



	public void updateData() {
		final JFMantemEquipamentoController me = this;

		TreeItemDevice rt = new TreeItemDevice("Gerenciamento de equipamentos", "", "", TreeItemDevice.NONE);
		TreeItem<TreeItemDevice> rootItem = new TreeItem<TreeItemDevice>(rt, new ImageView(new Image(getClass().getResourceAsStream("./imgs/category_group.png"))));
		rootItem.setExpanded(true);
		treeEquipamentos.setRoot(rootItem);
		treeEquipamentos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);		
		
		final ContextMenu cmGerenciamento = new ContextMenu();
		MenuItem miNovoGrupo = new MenuItem("Novo grupo...");
		miNovoGrupo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
            public void handle(ActionEvent novoGrupoAction) {
				InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
                if (info.codigo == 0) {
                	boolean permissao = info.usuarioPodeInserirGrupo;
                	if(permissao) {
                		TreeItemDevice device = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
                		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
                        DialogFactory.DialogRetVO vo = null;
                        try {
	                        vo = factory.loadDialog("dialog/equipamento/JFGrupoEquipamento.fxml");
	                        JFGrupoEquipamentoController msgController = (JFGrupoEquipamentoController) vo.controller;
	                        msgController.setCallbackAtualizacao(me);
	                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
	                        
	                        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
	                        dialog.initModality(Modality.WINDOW_MODAL);
	                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

	                        dialog.setScene(SceneBuilder.create().width(430).height(350).root((Parent) vo.root).build());
	                        dialog.setResizable(true);
	                        dialog.show();
                        } catch (IOException e) {
	                        e.printStackTrace();
                        }
                    } else {
                        JFDialog dialog = new JFDialog();
                        dialog.show("Usuário não tem permissão para criar grupo", "Ok");
                    }
                }					
            }
		});
		
		cmGerenciamento.getItems().add(miNovoGrupo);
		
		treeEquipamentos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY && !event.isControlDown()) {
                	TreeItem<TreeItemDevice> item = treeEquipamentos.getSelectionModel().getSelectedItem();
                	if (item != null) {
	                    if (item.getValue().tipo == TreeItemDevice.NONE) {
		                    cmGerenciamento.show(treeEquipamentos, event.getScreenX(), event.getScreenY());
	                    } else if (item.getValue().tipo == TreeItemDevice.ALLDEVICES) {
		                    cmGerenciamento.hide();
	                    } else if (item.getValue().tipo == TreeItemDevice.GRUPO) {
		                    cmGerenciamento.hide();
	                    } else if (item.getValue().tipo == TreeItemDevice.DEVICE) {
		                    cmGerenciamento.hide();
	                    } else {
		                    cmGerenciamento.hide();
	                    }
                    } else {
                    	JFDialog dialog = new JFDialog();
                    	dialog.show("É necessário selecionar um item.", "Ok");
                    }
                } else {
                	cmGerenciamento.hide();
                }
            }
		});	 		

		progressIndicator.setVisible(true);
		controller.findEquipamentos(this);
		controller.findGruposEquipamentos(this);
	}
	
	@Override
    public void recuperaEquipamnetosSuccess(final InfoRetornoEquipamento info) {
		JFMantemEquipamentoController me = this;
		Platform.runLater(new Runnable() {
			public void run() {
				progressIndicator.setVisible(false);
				if(info.codigo == 0) {
					Image devicesIcon = new Image(getClass().getResourceAsStream("./imgs/users.png"));
					Image deviceIcon = new Image(getClass().getResourceAsStream("./imgs/mobile_device.png"));
					
					TreeItem<TreeItemDevice> allDevices = new TreeItem<TreeItemDevice>(new TreeItemDevice("Equipamentos", "", "", TreeItemDevice.ALLDEVICES), new ImageView(devicesIcon));
					
					Collections.sort(info.equipamentos);
					
					for(EquipamentoVO equipamentoVo : info.equipamentos) {
						TreeItemDevice tidEquipamento = new TreeItemDevice(equipamentoVo.getDeviceName(), equipamentoVo.getDeviceUUID(), equipamentoVo.getNomeUsuario(), TreeItemDevice.DEVICE);
						tidEquipamento.equipamento = equipamentoVo;
						TreeItem<TreeItemDevice> tiEquipamento = new TreeItem<TreeItemDevice>(tidEquipamento, new ImageView(deviceIcon));
						allDevices.getChildren().add(tiEquipamento);
					}	
					treeEquipamentos.getRoot().getChildren().add(0, allDevices);
					
					final ContextMenu cmEquipamento = new ContextMenu();
	        		MenuItem miEnviarMensagemEquipamento = new MenuItem("Enviar mensagem...");
	        		miEnviarMensagemEquipamento.setOnAction(new EventHandler<ActionEvent>() {

						@Override
	                    public void handle(ActionEvent evtEnviarMensagem) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
	                        if (info.codigo == 0) {
	                        	boolean permissao = info.usuarioPodeInserirGrupo;
	                        	if(permissao) {
	                        		TreeItemDevice device = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
	                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			                        DialogFactory.DialogRetVO vo = null;
			                        try {
				                        vo = factory.loadDialog("dialog/message/JFMensagem.fxml");
				                        JFMensagem msgController = (JFMensagem) vo.controller;
				                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
				                        msgController.setEquipamentoSelecionado(device.equipamento.getId());

				                        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
				                        dialog.initModality(Modality.WINDOW_MODAL);
				                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

				                        dialog.setScene(SceneBuilder.create().width(430).height(550).root((Parent) vo.root).build());
				                        dialog.setResizable(true);
				                        dialog.show();
			                        } catch (IOException e) {
				                        e.printStackTrace();
			                        }
		                        } else {
			                        JFDialog dialog = new JFDialog();
			                        dialog.show("Usuário não tem permissão para enviar mensagem", "Ok");
		                        }
	                        }							
						}
	        		});
	        		
	        		MenuItem miVerMap = new MenuItem("Ver no mapa");
	        		miVerMap.setOnAction(new EventHandler<ActionEvent>() {

						@Override
                        public void handle(ActionEvent mapEvent) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
	                        if (info.codigo == 0) {
	                        	boolean permissao = info.usuarioPodeInserirGrupo;
	                        	if(permissao) {
	                        		final TreeItemDevice device = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
	                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			                        DialogFactory.DialogRetVO vo = null;
			                        try {
				                        vo = factory.loadDialog("dialog/equipamento/mapa/JFWebMap.fxml");
				                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
				                        final WebMapController mapController = (WebMapController)vo.controller;
				                        if (device.equipamento.localizacoes.size() > 0) {
	                                        final LocalizacaoVO loc = device.equipamento.localizacoes.get(device.equipamento.localizacoes.size() - 1);
	                                        mapController.addMapListener(new IMapListener() {
												@Override
												public void executeCommand() {
													mapController.setMapCenter(loc.latitude, loc.longitude);
													EquipamentoVO eqVo = device.equipamento;
													List<LocalizacaoVO> localizacoes = eqVo.getLocalizacoes();
													LocalizacaoVO ultimaLocalizacao = null;
													if (localizacoes.size() > 0) {
														ultimaLocalizacao = localizacoes.get(localizacoes.size() - 1);
													}
													SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
													String content = "Nome: <b>"+eqVo.getNomeUsuario()+"</b><br/>";
													content += "Login: <b>"+eqVo.getLoginUsuario()+"</b><br/>";
													content += "Aparelho: <b>"+eqVo.getDevicePlatform()+"</b><br/>";
													if (ultimaLocalizacao != null) {
	                                                    content += "Data: <b>" + sdf.format(ultimaLocalizacao.getData()) + "</b>";
                                                    }
													mapController.putPin(loc.latitude, loc.longitude, content);
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
	                        	} else {
	                        		JFDialog dialog = new JFDialog();
	                        		dialog.show("Usuário não tem permissão para acessar mapa", "Ok");
	                        	}
	                        }
                        }
					});
	        		
	        		miCopiarEquipamento.setOnAction(new EventHandler<ActionEvent>() {

						@Override
                        public void handle(ActionEvent actionEvent) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
							if(info.codigo == 0) {
	                            boolean permissao = info.usuarioPodeInserirGrupo;
	                            if(permissao) {
	                            	JFDialog dialog = new JFDialog();
	                                final TreeItem<TreeItemDevice> treeItem = treeEquipamentos.getSelectionModel().getSelectedItem();
	                                Clipboard clipboard = Clipboard.getSystemClipboard();
	                                ClipboardContent content = new ClipboardContent();
	                                content.put(df, treeItem.getValue().equipamento);
	                                clipboard.setContent(content);
	                                miColarEquipamento.setDisable(false);
	                            } else {
	                            	JFDialog dialog = new JFDialog();
	                            	dialog.show("Usuário logado não tem permissão para copiar equipamento", "Ok");
	                            }
							} else {
								showErrorMessage(info.mensagem);
							}	                        
                        }
					});
	        		
	        		cmEquipamento.getItems().add(miEnviarMensagemEquipamento);
	        		cmEquipamento.getItems().add(miVerMap);
	        		cmEquipamento.getItems().add(miCopiarEquipamento);
	        		
					treeEquipamentos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

						@Override
	                    public void handle(MouseEvent event) {
		                    if (event.getButton() == MouseButton.SECONDARY && !event.isControlDown()) {
		                    	TreeItem<TreeItemDevice> item = treeEquipamentos.getSelectionModel().getSelectedItem();
		                    	if (item == null) {
		                    		
		                    	} else if (item.getValue().tipo == TreeItemDevice.NONE) {
			                		cmEquipamento.hide();
		                    	} else if (item.getValue().tipo == TreeItemDevice.ALLDEVICES) {
		                    		cmEquipamento.hide();
		                    	} else if(item.getValue().tipo == TreeItemDevice.GRUPO) {
		                    		cmEquipamento.hide();
		                    	} else if(item.getValue().tipo == TreeItemDevice.DEVICE) {
		                    		cmEquipamento.show(treeEquipamentos, event.getScreenX(), event.getScreenY());
		                    	} else {
		                    		cmEquipamento.hide();
		                    	}
		                    } else {
		                    	cmEquipamento.hide();
		                    }
	                    }
						
					});
					
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});	    
    }
	

	@Override
    public void recuperaGruposEquipamentosSucesso(final InfoRetornoEquipamento info) {
		final JFMantemEquipamentoController me = this;
		Platform.runLater(new Runnable() {
			public void run() {
				progressIndicator.setVisible(false);
				if (info.codigo == 0) {
	                Image groupIcon = new Image(getClass().getResourceAsStream("./imgs/device_group.png"));
					Image deviceIcon = new Image(getClass().getResourceAsStream("./imgs/mobile_device.png"));

					Collections.sort(info.gruposEquipamentos);
					
	                for (GrupoEquipamentoVO geVo : info.gruposEquipamentos) {
		                TreeItemDevice tidGroup = new TreeItemDevice(geVo.getNome(), "", "", TreeItemDevice.GRUPO);
		                tidGroup.grupoEquipamento = geVo;
		                TreeItem<TreeItemDevice> tiGroup = new TreeItem<TreeItemDevice>(tidGroup, new ImageView(groupIcon));
		                for(EquipamentoVO equipamentoVo : geVo.getEquipamentos()) {
		                	TreeItemDevice tidEquipamento = new TreeItemDevice(equipamentoVo.getDeviceName(), 
		                			equipamentoVo.getDeviceUUID(), 
		                			equipamentoVo.getNomeUsuario(), TreeItemDevice.DEVICE_ON_GROUP);
							tidEquipamento.equipamento = equipamentoVo;
							TreeItem<TreeItemDevice> tiEquipamento = new TreeItem<TreeItemDevice>(tidEquipamento, new ImageView(deviceIcon));
							tiGroup.getChildren().add(tiEquipamento);
		                }
		                treeEquipamentos.getRoot().getChildren().add(tiGroup);
	                }
	                
	                
	                
	                final ContextMenu cmGrupoEquipamento = new ContextMenu();
	        		MenuItem miEnviarMensagemEquipamento = new MenuItem("Enviar mensagem...");
	        		miEnviarMensagemEquipamento.setOnAction(new EventHandler<ActionEvent>() {

						@Override
	                    public void handle(ActionEvent evtEnviarMensagem) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
	                        if (info.codigo == 0) {
	                        	boolean permissao = info.usuarioPodeInserirGrupo;
	                        	if(permissao) {
	                        		TreeItemDevice device = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
	                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			                        DialogFactory.DialogRetVO vo = null;
			                        try {
				                        vo = factory.loadDialog("dialog/message/JFMensagem.fxml");
				                        JFMensagem msgController = (JFMensagem) vo.controller;
				                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
				                        msgController.setGrupoEquipamentoSelecionado(device.grupoEquipamento.getId());

				                        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
				                        dialog.initModality(Modality.WINDOW_MODAL);
				                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

				                        dialog.setScene(SceneBuilder.create().width(430).height(550).root((Parent) vo.root).build());
				                        dialog.setResizable(true);
				                        dialog.show();
			                        } catch (IOException e) {
				                        e.printStackTrace();
			                        }
		                        } else {
			                        JFDialog dialog = new JFDialog();
			                        dialog.show("Usuário não tem permissão para enviar mensagem", "Ok");
		                        }
	                        }							
						}
	        		});
	        		
	        		MenuItem miAlteraGrupoEquipamento = new MenuItem("Altera...");
	        		miAlteraGrupoEquipamento.setOnAction(new EventHandler<ActionEvent>() {

						@Override
                        public void handle(ActionEvent actionAltera) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
	                        if (info.codigo == 0) {
	                        	boolean permissao = info.usuarioPodeInserirGrupo;
	                        	if(permissao) {
	                        		TreeItemDevice device = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
	                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
	                                DialogFactory.DialogRetVO vo = null;
	                                try {
	        	                        vo = factory.loadDialog("dialog/equipamento/JFGrupoEquipamento.fxml");
	        	                        JFGrupoEquipamentoController msgController = (JFGrupoEquipamentoController) vo.controller;
	        	                        msgController.setCallbackAtualizacao(me);
	        	                        msgController.setGrupoAlterar(device.grupoEquipamento);
	        	                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
	        	                        
	        	                        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
	        	                        dialog.initModality(Modality.WINDOW_MODAL);
	        	                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

	        	                        dialog.setScene(SceneBuilder.create().width(430).height(350).root((Parent) vo.root).build());
	        	                        dialog.setResizable(true);
	        	                        dialog.show();
	                                } catch (IOException e) {
	        	                        e.printStackTrace();
	                                }	                        		
		                        } else {
			                        JFDialog dialog = new JFDialog();
			                        dialog.show("Usuário não tem permissão para alterar grupo", "Ok");
		                        }
	                        }							
                        }
					});
	        		
	        		MenuItem miRemoveGrupoEquipamento = new MenuItem("Remove");
	        		miRemoveGrupoEquipamento.setOnAction(new EventHandler<ActionEvent>() {

						@Override
	                    public void handle(ActionEvent evtEnviarMensagem) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
	                        if (info.codigo == 0) {
	                        	boolean permissao = info.usuarioPodeInserirGrupo;
	                        	if(permissao) {
	                        		JFDialog dialog = new JFDialog();
	                                final TreeItem<TreeItemDevice> treeItem = treeEquipamentos.getSelectionModel().getSelectedItem();

	                            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
	                            		@Override
	                            		public void handle(ActionEvent actionEvent) {
	                            			controller.removeGrupoEquipamento(me, treeItem.getValue().grupoEquipamento.getId());
	                            		}
	                            	};
	                            	dialog.show("Tem certeza que deseja remover o grupo ("+ treeItem.getValue().nome +")?", "Sim", "Não", act1);
		                        } else {
			                        JFDialog dialog = new JFDialog();
			                        dialog.show("Usuário não tem permissão para remover grupo de equipamentos", "Ok");
		                        }
	                        }							
						}
	        		});	        		
	        		
	        		miColarEquipamento.setOnAction(new EventHandler<ActionEvent>() {

						@Override
                        public void handle(ActionEvent event) {
							InfoRetornoLdap infoLdap = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
							if(infoLdap.codigo == 0) {
		                        boolean permissao = infoLdap.usuarioPodeInserirGrupo;
		                        if(permissao) {
	                                final TreeItem<TreeItemDevice> treeItem = treeEquipamentos.getSelectionModel().getSelectedItem();
	                                EquipamentoVO idEquipamento = null;
	                                Clipboard clipboard = Clipboard.getSystemClipboard();
	                                if (clipboard.hasContent(df)) {
	                                	idEquipamento = (EquipamentoVO)clipboard.getContent(df);
	                                	GrupoEquipamentoVO grupoVo =  treeItem.getValue().grupoEquipamento;
                                		boolean grpContemEquipamento = false;
                                		for (EquipamentoVO equipamentoVo : grupoVo.getEquipamentos()) {
                                			if (equipamentoVo.getId().longValue() == idEquipamento.getId().longValue()) {
                                				grpContemEquipamento = true;
                                				break;
                                			}
                                		}
                                		if (!grpContemEquipamento) {
                                			GrupoEquipamentoVO grpVo = new GrupoEquipamentoVO();
                                			grpVo.setId(grupoVo.getId());
                                			grpVo.getEquipamentos().add(idEquipamento);
	                                        controller.adicionarEquipamentoAoGrupo(me, grpVo);
                                        }
                                		miColarEquipamento.setDisable(true);
	                                	clipboard.clear();
	                                }
		                        } else {
		                        	JFDialog dialog = new JFDialog();
		                        	dialog.show("Usuário logado não tem permissão para colar no grupo", "Ok");
		                        }
							} else {
								showErrorMessage(infoLdap.mensagem);
							}
                        }
					});
	        		
	        		cmGrupoEquipamento.getItems().add(miEnviarMensagemEquipamento);
	        		cmGrupoEquipamento.getItems().add(miColarEquipamento);
	        		cmGrupoEquipamento.getItems().add(new SeparatorMenuItem());
	        		cmGrupoEquipamento.getItems().add(miAlteraGrupoEquipamento);
	        		cmGrupoEquipamento.getItems().add(miRemoveGrupoEquipamento);
	        		
	        		final ContextMenu cmEquipamentoOnGrupo = new ContextMenu();
	        		MenuItem miRemoveDoGrupo = new MenuItem("remover do grupo");
	        		miRemoveDoGrupo.setOnAction(new EventHandler<ActionEvent>() {

						@Override
                        public void handle(ActionEvent event) {
							TreeItemDevice selItem = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
							if(selItem != null) {
								final GrupoEquipamentoVO grupoVo = treeEquipamentos.getSelectionModel().getSelectedItem().getParent().getValue().grupoEquipamento;
								final EquipamentoVO equipamentoVo = selItem.equipamento;
								JFDialog dialog = new JFDialog();
                                final TreeItem<TreeItemDevice> treeItem = treeEquipamentos.getSelectionModel().getSelectedItem();

                            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
                            		@Override
                            		public void handle(ActionEvent actionEvent) {
                            			GrupoEquipamentoVO grpEquipVo = new GrupoEquipamentoVO();
                            			grpEquipVo.setId(grupoVo.getId());
                            			grpEquipVo.getEquipamentos().add(equipamentoVo);
                            			controller.removerEquipamentoDoGrupo(me, grpEquipVo);
                            		}
                            	};
                            	dialog.show("Tem certeza que deseja remover o equipamento do grupo?", "Sim", "Não", act1);								
							} else {
								JFDialog dialog = new JFDialog();
								dialog.show("É necessário selecionar um item", "Ok");
							}
                        }
					});
	        		
	        		MenuItem miEnviarMensagemEquipamentoGrupo = new MenuItem("Enviar mensagem...");
	        		miEnviarMensagemEquipamentoGrupo.setOnAction(new EventHandler<ActionEvent>() {

						@Override
	                    public void handle(ActionEvent evtEnviarMensagem) {
							InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
	                        if (info.codigo == 0) {
	                        	boolean permissao = info.usuarioPodeInserirGrupo;
	                        	if(permissao) {
	                        		TreeItemDevice device = treeEquipamentos.getSelectionModel().getSelectedItem().getValue();
	                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			                        DialogFactory.DialogRetVO vo = null;
			                        try {
				                        vo = factory.loadDialog("dialog/message/JFMensagem.fxml");
				                        JFMensagem msgController = (JFMensagem) vo.controller;
				                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
				                        msgController.setEquipamentoSelecionado(device.equipamento.getId());

				                        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
				                        dialog.initModality(Modality.WINDOW_MODAL);
				                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

				                        dialog.setScene(SceneBuilder.create().width(430).height(350).root((Parent) vo.root).build());
				                        dialog.setResizable(true);
				                        dialog.show();
			                        } catch (IOException e) {
				                        e.printStackTrace();
			                        }
		                        } else {
			                        JFDialog dialog = new JFDialog();
			                        dialog.show("Usuário não tem permissão para enviar mensagem", "Ok");
		                        }
	                        }							
						}
	        		});	        		
	        		
	        		cmEquipamentoOnGrupo.getItems().add(miEnviarMensagemEquipamentoGrupo);
	        		cmEquipamentoOnGrupo.getItems().add(new SeparatorMenuItem());
	        		cmEquipamentoOnGrupo.getItems().add(miRemoveDoGrupo);
	                
					treeEquipamentos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

						@Override
	                    public void handle(MouseEvent event) {
		                    if (event.getButton() == MouseButton.SECONDARY && !event.isControlDown()) {
		                    	TreeItem<TreeItemDevice> item = treeEquipamentos.getSelectionModel().getSelectedItem();
		                    	if (item != null) {
	                                if (item.getValue().tipo == TreeItemDevice.NONE) {
		                                cmGrupoEquipamento.hide();
		                                cmEquipamentoOnGrupo.hide();
	                                } else if (item.getValue().tipo == TreeItemDevice.ALLDEVICES) {
	                                	cmEquipamentoOnGrupo.hide();
		                                cmGrupoEquipamento.hide();
	                                } else if (item.getValue().tipo == TreeItemDevice.GRUPO) {
	                                	cmEquipamentoOnGrupo.hide();
		                                cmGrupoEquipamento.show(treeEquipamentos, event.getScreenX(), event.getScreenY());
	                                } else if (item.getValue().tipo == TreeItemDevice.DEVICE) {
	                                	cmEquipamentoOnGrupo.hide();
		                                cmGrupoEquipamento.hide();
	                                } else if (item.getValue().tipo == TreeItemDevice.DEVICE_ON_GROUP) {
	                                	cmGrupoEquipamento.hide();
	                                	cmEquipamentoOnGrupo.show(treeEquipamentos, event.getScreenX(), event.getScreenY());
	                                } else {
	                                	cmEquipamentoOnGrupo.hide();
		                                cmGrupoEquipamento.hide();
	                                }
                                } else {
                                	JFDialog dialog = new JFDialog();
                                	dialog.show("É necessário selecionar um item", "Ok");
                                }
		                    } else {
		                    	cmGrupoEquipamento.hide();
		                    }
	                    }
						
					});	                
                } else {
                	showErrorMessage(info.mensagem);
                }
			}
		});	    
	}

	@Override
    public void recueraInfoError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
			}
		});	    
    }

	private void showErrorMessage(String errorMessage) {
		((Label) errorPane.getContent()).setText(errorMessage);
		errorPane.setVisible(true);
	}	
	
	private void showUsuariosErrorMessage(String errorMessage) {
		((Label) usuariosErrorPane.getContent()).setText(errorMessage);
		usuariosErrorPane.setVisible(true);
	}	


	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		
	}
	
	@FXML
	protected void btnCancelaActionUsuarios(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}
	
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
	
	
	class Delta { double x, y; }	
	
	private class TreeItemDevice {
		public static final int NONE = 0;
		public static final int GRUPO = 1;
		public static final int DEVICE = 2;
		public static final int ALLDEVICES = 3;
		public static final int DEVICE_ON_GROUP = 4;
		
		public boolean basePackage = false;
		public String nome;
		public String uuid;
		public String login;
		public int tipo;
		public boolean selecionado = false;
		
		public EquipamentoVO equipamento;
		public GrupoEquipamentoVO grupoEquipamento;
		
		public TreeItemDevice(){}
		
		public TreeItemDevice(String nome, String uuid, String nomeUsuario, int tipoItem) {
			this.basePackage = basePackage;
			this.nome = nome;
			this.login = nomeUsuario;
			this.tipo = tipoItem;
		}
		
		@Override
		public String toString() {
			if (isDevice()) {
	            return nome + " - " + login;
            }
			return nome;
		}
		
		public boolean isDevice() {
			return tipo == DEVICE || tipo == DEVICE_ON_GROUP;
		}
		
		public boolean isGrupo() {
			return tipo == GRUPO;
		}
	}
}
