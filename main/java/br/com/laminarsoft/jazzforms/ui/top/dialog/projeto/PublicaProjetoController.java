package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.drools.core.util.StringUtils;

import br.com.laminarsoft.jazzforms.persistencia.model.Deployment;
import br.com.laminarsoft.jazzforms.persistencia.model.Grupo;
import br.com.laminarsoft.jazzforms.persistencia.model.Historico;
import br.com.laminarsoft.jazzforms.persistencia.model.ProcessModel;
import br.com.laminarsoft.jazzforms.persistencia.model.ProcessNode;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.Usuario;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProcessModel;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapUsuarioVO;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.IGuvnorResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.ILDAPResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.IPublicaResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.JFValoresFotosController;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa.WebMapController;
import br.com.laminarsoft.jazzforms.ui.dialog.ldap.JFGrupo;
import br.com.laminarsoft.jazzforms.ui.dialog.ldap.JFSelecionaUsuarioController;
import br.com.laminarsoft.jazzforms.ui.dialog.message.JFMensagem;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

@SuppressWarnings("all")
public class PublicaProjetoController extends ProjetoController implements Initializable, 
		IGuvnorResponseHandler, ILDAPResponseHandler, IPublicaResponseHandler {

	@FXML private TabPane publicaProjetoTab;
	
	@FXML private TreeView<TreeItemBPM> treeProcessos;
	@FXML private TreeView<TreeItemLDAP> treeUsuarios;
	
	@FXML private TitledPane errorPane;
	@FXML private TitledPane usuariosErrorPane;
	
	@FXML private Tab processoNegocioTab;
	@FXML private Tab usuariosTab;
	
	@FXML private Button btnUsuariosOk;	
	@FXML private Button btnUsuariosCancel;
	@FXML private Button btnCancel;
	@FXML private Button btnOk;
	
	@FXML private Label errorLabel;
	
	@FXML private ProgressIndicator progressIndicator;
	@FXML private ProgressIndicator uruariosProgressIndicator;
	
	@FXML private VBox mainPane;
	@FXML private VBox mainPaneUsuarios;
	
	@FXML private TextField txtFiltro;
	
	private WebServiceController controller = WebServiceController.getInstance();
	
	private ICenterDispatcher centerDispatcher;
	private TreeItem<TreeItemBPM> selectedTreeItem;
	private TreeItem<TreeItemLDAP> selectedTreeItemUsuario;
	
	private List<String> usrGrpSelecionados = new ArrayList<String>();
	private List<String> actvtSelecionadas = new ArrayList<String>();
	private Projeto alterProjeto = null;
	private WebServiceController webController;
	
	private Map<LdapGrupoVO, List<LdapUsuarioVO>> mapaGrpUsr = new HashMap<LdapGrupoVO, List<LdapUsuarioVO>>();

	@Override
	public void setCenterDispathcer(ICenterDispatcher dispatcher) {
		this.centerDispatcher = dispatcher;
	}

	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		Pagina pagina = JazzForms.getPaginaDesenhoAtual();
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		uruariosProgressIndicator.visibleProperty().bindBidirectional(mainPaneUsuarios.disableProperty());
		
		progressIndicator.setVisible(false);
		uruariosProgressIndicator.setVisible(false);
		
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		mainPaneUsuarios.visibleProperty().bind(usuariosErrorPane.visibleProperty().not());
		webController = WebServiceController.getInstance();
		
		AwesomeDude.setIcon(btnUsuariosOk, AwesomeIcon.CHECK_CIRCLE, "14px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnUsuariosCancel, AwesomeIcon.TIMES_CIRCLE, "14px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnOk, AwesomeIcon.CHECK_CIRCLE, "14px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnCancel, AwesomeIcon.TIMES_CIRCLE, "14px", ContentDisplay.RIGHT);
		updateData();
	}

	
	@FXML 
	protected void handleKeyInput(KeyEvent event) {
		final String text = ((TextField)event.getTarget()).getText();
		Map<LdapGrupoVO, List<LdapUsuarioVO>> mapLst = new HashMap<LdapGrupoVO, List<LdapUsuarioVO>>();
		
		Iterator<LdapGrupoVO> grpKeyIterator = mapaGrpUsr.keySet().iterator();
		while(grpKeyIterator.hasNext()) {
			LdapGrupoVO key = grpKeyIterator.next();
			List<LdapUsuarioVO> lst = mapaGrpUsr.get(key);
			List<LdapUsuarioVO> filtrados = Lists.newArrayList(Collections2.filter(lst, new Predicate<LdapUsuarioVO>(){
				
				@Override
			    public boolean apply(LdapUsuarioVO p) {
					boolean ret = true;
					if(StringUtils.isEmpty(text)) {
						ret = true;
					} else if(p.getNome() != null && p.getNome().toUpperCase().contains(text.toUpperCase())) {
						ret = true;
					} else {
						ret = false;
					}
					return ret;
			    }			
			}));
			mapLst.put(key, filtrados);
		}
		
		Image groupIcon = new Image(getClass().getResourceAsStream("./imgs/group.png"));
		Image peopleIcon = new Image(getClass().getResourceAsStream("./imgs/user.png"));

		grpKeyIterator = mapLst.keySet().iterator();
		TreeItem<TreeItemLDAP> rootItem = treeUsuarios.getRoot();
		rootItem.getChildren().clear();
		while(grpKeyIterator.hasNext()) {
			LdapGrupoVO key = grpKeyIterator.next();
			TreeItemLDAP grpItem = new TreeItemLDAP(key.getNome(), key.getDn(), key.getDescription(), false, TreeItemLDAP.GRUPO);					
			TreeItem<TreeItemLDAP> item = new TreeItem<TreeItemLDAP>( grpItem, new ImageView(groupIcon));
			rootItem.getChildren().add(item);
			
			List<LdapUsuarioVO> usuarios = mapLst.get(key);
			Collections.sort(usuarios);
			for(LdapUsuarioVO user : usuarios) {						
				TreeItemLDAP treeItmUsr = new TreeItemLDAP(user.getNome(), user.getDn(), "", false, TreeItemLDAP.USUARIO);
				treeItmUsr.uid = user.getLogin();
				TreeItem<TreeItemLDAP> itUser = new TreeItem<TreeItemLDAP>(treeItmUsr, new ImageView(peopleIcon));
				item.getChildren().add(itUser);

			}			
		}
		
	} 	
	
	public void updateData() {
		progressIndicator.setVisible(true);
		uruariosProgressIndicator.setVisible(true);
		controller.getPackages(this);
		webController.getGrupos(this);
	}
	
	public void setSelectedNodes(List<String> selectedUsrGrp, List<String> selectedActivities, Projeto projeto) {
		this.usrGrpSelecionados = selectedUsrGrp;
		this.actvtSelecionadas = selectedActivities;
		this.alterProjeto = projeto;
	}

	public void setProcessoNegocioTabVisible(boolean visible) {
		processoNegocioTab.setDisable(!visible);
		if (!visible) {
	        publicaProjetoTab.getTabs().remove(0);
	        btnUsuariosOk.setDisable(true);	        
        }
	}
	

	@Override
	public void receivePackages(final InfoRetornoProcessModel doc) {
		final PublicaProjetoController me = this;
		Platform.runLater(new Runnable() {
			public void run() {
				if (doc != null && doc.codigo == 0) {
					List<ProcessModel> feed = doc.processModels;			
					TreeItem<TreeItemBPM> rootItem = new TreeItem<TreeItemBPM>(new TreeItemBPM("Processos", "", null), 
							new ImageView(new Image(getClass().getResourceAsStream("./imgs/folder-home.png"))));
					rootItem.setExpanded(true);
					treeProcessos.setRoot(rootItem);
					treeProcessos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

					for(ProcessModel model : feed) {
						Image kogWheels = new Image(getClass().getResourceAsStream("./imgs/kservices.png"));
						TreeItemBPM trItemInterno = new TreeItemBPM(model.getProcessId(), model.getProcessId(), model);
						trItemInterno.setProcess(true);
						TreeItem<TreeItemBPM> trItem = new TreeItem<PublicaProjetoController.TreeItemBPM>(trItemInterno, new ImageView(kogWheels));
						rootItem.getChildren().add(trItem);
						trItem.setExpanded(true);
						
						model.getNodes().removeAll(Collections.singleton(null));
						
						for(ProcessNode node : model.getNodes()) {
							Image notes = new Image(getClass().getResourceAsStream("./imgs/cellphone.png"));
							TreeItemBPM trItemNode = new TreeItemBPM(node.getNodeName(), node.getNodeId(), null);
							trItemNode.setProcess(true);
							trItemNode.setActivity(true);
							TreeItem<TreeItemBPM> trNode = new TreeItem<PublicaProjetoController.TreeItemBPM>(trItemNode,
									new ImageView(notes));
							trItem.getChildren().add(trNode);
							trNode.setExpanded(true);							
						}
						
		        		final ContextMenu cmCriarGrupo = new ContextMenu();
		        		MenuItem crGrpMenuItem = new MenuItem("Visualizar modelo");
		        		crGrpMenuItem.setOnAction(new EventHandler<ActionEvent>() {

							@Override
		                    public void handle(ActionEvent evtCrGrupo) {
								TreeItem<TreeItemBPM> item = treeProcessos.getSelectionModel().getSelectedItem();
		                        DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		                        DialogFactory.DialogRetVO voFoto = null;
		                        try {
		                        	voFoto = factory.loadDialog("dialog/deploy/instancia/JFValoresFotos.fxml");
	                    			JFValoresFotosController cntrl = (JFValoresFotosController)voFoto.controller;
	                    			cntrl.setValorFoto(item.getValue().model.getProcessImage());
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
		        		});
		        		
		        		cmCriarGrupo.getItems().add(crGrpMenuItem);
		        		
		        		treeProcessos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

							@Override
		                    public void handle(MouseEvent event) {
			                    if (event.getButton() == MouseButton.SECONDARY && !event.isControlDown()) {
			                    	TreeItem<TreeItemBPM> item = treeProcessos.getSelectionModel().getSelectedItem();
			                    	if (item.getValue().model != null) {
				                		cmCriarGrupo.show(treeProcessos, event.getScreenX(), event.getScreenY());
			                    	} else {
			                    		cmCriarGrupo.hide();
			                    	}
			                    } else {
			                    	cmCriarGrupo.hide();
			                    }
		                    }
							
						});		        		
					}
					
					
					treeProcessos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

						@Override
						public void changed(ObservableValue observable, Object oldValue, Object newValue) {

						}
						
					});
				} else if(doc.codigo != 0) {
					me.showErrorMessage(doc.mensagem);
				}
				progressIndicator.setVisible(false);
			}
		});		
	}

	
	
	
	@Override
	public void receiveUsers(List<LdapUsuarioVO> usuarios) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveGroups(final List<LdapGrupoVO> grupos) {
		final PublicaProjetoController me = this;
		Platform.runLater(new Runnable() {
			public void run() {	
				TreeItem<TreeItemLDAP> root = treeUsuarios.getRoot();
				if (root != null) {
					root.getChildren().clear();
				}
				List<TreeItem<TreeItemLDAP>> selectedItems = new ArrayList<TreeItem<TreeItemLDAP>>(); 
				TreeItem<TreeItemLDAP> rootItem = new TreeItem<TreeItemLDAP>(new TreeItemLDAP("Grupos", "", "", true, TreeItemLDAP.NONE), 
						new ImageView(new Image(getClass().getResourceAsStream("./imgs/folder-home.png"))));
				Image groupIcon = new Image(getClass().getResourceAsStream("./imgs/group.png"));
				Image peopleIcon = new Image(getClass().getResourceAsStream("./imgs/user.png"));
						
				me.mapaGrpUsr.clear();
				for(LdapGrupoVO grupo : grupos) {
					TreeItemLDAP grpItem = new TreeItemLDAP(grupo.getNome(), grupo.getDn(), grupo.getDescription(), false, TreeItemLDAP.GRUPO);					
					TreeItem<TreeItemLDAP> item = new TreeItem<TreeItemLDAP>( grpItem, new ImageView(groupIcon));
					
					rootItem.getChildren().add(item);
					
					for(String nomeGrupo : usrGrpSelecionados) {
						if (nomeGrupo.equalsIgnoreCase(grupo.getNome())) {
							grpItem.selecionado = true;
							selectedItems.add(item);
							break;
						}
					}
					
					Collections.sort(grupo.getUsuarios());
					for(LdapUsuarioVO user : grupo.getUsuarios()) {						
						TreeItemLDAP treeItmUsr = new TreeItemLDAP(user.getNome(), user.getDn(), "", false, TreeItemLDAP.USUARIO);
						treeItmUsr.uid = user.getLogin();
						if(user.getLogin().toUpperCase().contains("T318203")) {
							System.out.println("Eu");
						}
						TreeItem<TreeItemLDAP> itUser = new TreeItem<TreeItemLDAP>(treeItmUsr, 
								new ImageView(peopleIcon));
						item.getChildren().add(itUser);
						
						for(String nomeUsr : usrGrpSelecionados) {
							if (nomeUsr.equalsIgnoreCase(treeItmUsr.uid)) {
								treeItmUsr.selecionado = true;
								selectedItems.add(itUser);
							}
						}
					}
					me.mapaGrpUsr.put(grupo, grupo.getUsuarios());
				}	
				rootItem.setExpanded(true);
				treeUsuarios.setRoot(rootItem);
				treeUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
				
				
        		final ContextMenu cmCriarGrupo = new ContextMenu();
        		MenuItem crGrpMenuItem = new MenuItem("Criar grupo...");
        		crGrpMenuItem.setOnAction(new EventHandler<ActionEvent>() {

					@Override
                    public void handle(ActionEvent evtCrGrupo) {
						InfoRetornoLdap info = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());						
                        if (info.codigo == 0) {
	                        boolean permissao = info.usuarioPodeInserirGrupo;
	                        if (permissao) {
		                        DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		                        DialogFactory.DialogRetVO vo = null;
		                        try {
			                        vo = factory.loadDialog("dialog/ldap/JFGrupo.fxml");
			                        JFGrupo grpController = (JFGrupo) vo.controller;
			                        grpController.setPublicadorController(me);
			                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());

			                        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			                        dialog.initModality(Modality.WINDOW_MODAL);
			                        dialog.initOwner(JazzForms.PRIMARY_STAGE);

			                        dialog.setScene(SceneBuilder.create().width(600).height(500).root((Parent) vo.root).build());
			                        dialog.setResizable(true);
			                        dialog.show();
		                        } catch (IOException e) {
			                        e.printStackTrace();
		                        }
	                        } else {
		                        JFDialog dialog = new JFDialog();
		                        dialog.show("Usuário não tem permissão para criar grupo", "Ok");
	                        }
                        } else {
                        	showErrorMessage(info.mensagem);
                        }                        
                    }
        		});
        		
        		cmCriarGrupo.getItems().add(crGrpMenuItem);
        		
        		final ContextMenu cmCrUsrDelGrp = new ContextMenu();
        		MenuItem delGrp = new MenuItem("Remover");
        		delGrp.setOnAction(new EventHandler<ActionEvent>(){

					@Override
                    public void handle(ActionEvent event) {
						InfoRetornoLdap info = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if(info.codigo == 0) {
                            boolean permissao = info.usuarioPodeInserirGrupo;
                            if(permissao) {
                            	JFDialog dialog = new JFDialog();
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();

                                InfoRetornoLdap infoDeployGrupos = webController.grupoTemDeployment(treeItem.getValue().nome);
                                
                            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
                            		@Override
                            		public void handle(ActionEvent actionEvent) {
                                    	uruariosProgressIndicator.setVisible(true);
                                        InfoRetornoLdap infoRemGrupo = webController.removeGrupo(treeItem.getValue().nome);
                                        if (infoRemGrupo.codigo == 0) {
	                                        me.updateData();
	                                    	uruariosProgressIndicator.setVisible(false);
	                                        txtFiltro.clear();
                                        } else {
                                        	showErrorMessage(infoRemGrupo.mensagem);
                                        }
                            		}
                            	};
                            	dialog.show("Tem certeza que deseja remover o grupo selecionado ("+ treeItem.getValue().nome +")?" +
                            			(infoDeployGrupos.grupoTemDeployment ? " Há implantações relacionadas ao grupo." : ""), "Sim", "Não", act1);
                            	event.consume();
                            } else {
                            	JFDialog dialog = new JFDialog();
                            	dialog.show("Usuário logado não tem permissão para apagar grupo", "Ok");
                            }
						} else {
							showErrorMessage(info.mensagem);
						}
                    }        			
        		});

        		MenuItem crtUser = new MenuItem("Adicionar usuário...");
        		crtUser.setOnAction(new EventHandler<ActionEvent>(){

					@Override
                    public void handle(ActionEvent event) {
						InfoRetornoLdap infoRet = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if (infoRet.codigo == 0) {
	                        boolean permissao = infoRet.usuarioPodeInserirGrupo;
	                        if(permissao) {
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
                        		DialogFactory.DialogRetVO vo = null;
                        		try {
                        			//vo = factory.loadDialog("dialog/ldap/JFUsuario.fxml");
                        			vo = factory.loadDialog("dialog/ldap/JFSelecionaUsuario.fxml");
                        			JFSelecionaUsuarioController usrController = (JFSelecionaUsuarioController)vo.controller;
                        			usrController.setPublicadorController(me);
                        			usrController.setGrupoSelecionado(treeItem.getValue().nome);
                    				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
                    				
                    				final Stage dialog = new Stage(StageStyle.TRANSPARENT);
                    				dialog.initModality(Modality.WINDOW_MODAL);
                    				dialog.initOwner(JazzForms.PRIMARY_STAGE);
                    				
                    				dialog.setScene(SceneBuilder.create().width(500).height(650).root((Parent)vo.root).build());
                    				dialog.setResizable(true);
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
                        		}
	                        } else {
	                        	JFDialog dialog = new JFDialog();
	                        	dialog.show("Usuário não tem permissão para criar outro usuário", "Ok");
	                        }
						} else {
							showErrorMessage(infoRet.mensagem);
						}
                    }
        		});
        		
        		final MenuItem crtColaUsuario = new MenuItem("Colar");
        		crtColaUsuario.setOnAction(new EventHandler<ActionEvent>() {

					@Override
                    public void handle(ActionEvent arg0) {
						InfoRetornoLdap infoLdap = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if(infoLdap.codigo == 0) {
	                        boolean permissao = infoLdap.usuarioPodeInserirGrupo;
	                        if(permissao) {
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                                String uid = "";
                                Clipboard clipboard = Clipboard.getSystemClipboard();
                                if (clipboard.hasString()) {
                                	uid = clipboard.getString();
                                	LdapUsuarioVO usr = webController.getUsuario(uid).usuario;
                                	if (usr == null) {
                                		JFDialog diag = new JFDialog();
                                		diag.show("Não há um usuário na área de transferência", "Ok");
                                	} else {
                                		LdapGrupoVO grp = webController.getGrupo(treeItem.getValue().nome).grupo;
                                		boolean grpContemUsuario = false;
                                		for (LdapGrupoVO grupoUsr : webController.getGruposUsuario(usr.getUid()).grupos) {
                                			if (grupoUsr.getNome().equalsIgnoreCase(treeItem.getValue().nome)) {
                                				grpContemUsuario = true;
                                				break;
                                			}
                                		}
                                		if (!grpContemUsuario) {
	                                        webController.adicionaUsuarioAoGrupo(uid, treeItem.getValue().nome);
	                                        me.updateData();
											txtFiltro.clear();
                                        }
										crtColaUsuario.setDisable(true);
                                	}
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
        		
        		MenuItem miEnviarMensagemGrupo = new MenuItem("Enviar mensagem...");
        		miEnviarMensagemGrupo.setOnAction(new EventHandler<ActionEvent>() {

					@Override
                    public void handle(ActionEvent enviarMsgAction) {
						InfoRetornoLdap infoRet = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if (infoRet.codigo == 0) {
	                        boolean permissao = infoRet.usuarioPodeInserirGrupo;
	                        if(permissao) {
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
                        		DialogFactory.DialogRetVO vo = null;
                        		try {
                        			//vo = factory.loadDialog("dialog/ldap/JFUsuario.fxml");
                        			vo = factory.loadDialog("dialog/message/JFMensagem.fxml");
                        			JFMensagem msgController = (JFMensagem)vo.controller;
                        			msgController.setGrupoSelecionado(treeItem.getValue().nome);
                    				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
                    				
                    				final Stage dialog = new Stage(StageStyle.TRANSPARENT);
                    				dialog.initModality(Modality.WINDOW_MODAL);
                    				dialog.initOwner(JazzForms.PRIMARY_STAGE);
                    				
                    				dialog.setScene(SceneBuilder.create().width(500).height(550).root((Parent)vo.root).build());
                    				dialog.setResizable(true);
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
                        		}
	                        } else {
	                        	JFDialog dialog = new JFDialog();
	                        	dialog.show("Usuário não tem permissão para enviar mensagem", "Ok");
	                        }
						} else {
							showErrorMessage(infoRet.mensagem);
						}						
                    }
				});        		
        		
        		cmCrUsrDelGrp.getItems().add(delGrp);
        		cmCrUsrDelGrp.getItems().add(crtUser);
        		cmCrUsrDelGrp.getItems().add(crtColaUsuario);
        		cmCrUsrDelGrp.getItems().add(new SeparatorMenuItem());
        		cmCrUsrDelGrp.getItems().add(miEnviarMensagemGrupo);
        		
        		crtColaUsuario.setDisable(true);
        		
        		
        		final ContextMenu cmUsuario = new ContextMenu();
        		MenuItem miRemoveUsuario = new MenuItem("Remover");
        		miRemoveUsuario.setOnAction(new EventHandler<ActionEvent>(){

					@Override
                    public void handle(ActionEvent event) {
						InfoRetornoLdap info = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if(info.codigo == 0) {
                            boolean permissao = info.usuarioPodeInserirGrupo;
                            if(permissao) {
                            	JFDialog dialog = new JFDialog();
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                                final TreeItem<TreeItemLDAP> treeItemGrupo = treeItem.getParent();

                            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
                            		@Override
                            		public void handle(ActionEvent actionEvent) {
                                		String nomeGrupo = treeItemGrupo.getValue().nome;
                                        webController.removeUsuarioDoGrupo(treeItem.getValue().uid, nomeGrupo);
                                        me.updateData();
                            		}
                            	};
                            	dialog.show("Tem certeza que deseja remover o usuário selecionado ("+ treeItem.getValue().nome +")?", "Sim", "Não", act1);
//                            	event.consume();
                            } else {
                            	JFDialog dialog = new JFDialog();
                            	dialog.show("Usuário logado não tem permissão para apagar usuáiro", "Ok");
                            }
						} else {
							showErrorMessage(info.mensagem);
						}
                    }        			
        		});
        		
        		MenuItem miCopiaUsuario = new MenuItem("Copiar");
        		miCopiaUsuario.setOnAction(new EventHandler<ActionEvent>() {

					@Override
                    public void handle(ActionEvent event) {
						InfoRetornoLdap info = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if(info.codigo == 0) {
                            boolean permissao = info.usuarioPodeInserirGrupo;
                            if(permissao) {
                            	JFDialog dialog = new JFDialog();
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                                Clipboard clipboard = Clipboard.getSystemClipboard();
                                ClipboardContent content = new ClipboardContent();
                                content.putString(treeItem.getValue().uid);
                                clipboard.setContent(content);
                                crtColaUsuario.setDisable(false);
//                            	event.consume();
                            } else {
                            	JFDialog dialog = new JFDialog();
                            	dialog.show("Usuário logado não tem permissão para copiar usuáiro", "Ok");
                            }
						} else {
							
						}
                    }
        			
				});
        		
        		MenuItem miEnviarMensagem = new MenuItem("Enviar mensagem...");
        		miEnviarMensagem.setOnAction(new EventHandler<ActionEvent>() {

					@Override
                    public void handle(ActionEvent enviarMsgAction) {
						InfoRetornoLdap infoRet = webController.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if (infoRet.codigo == 0) {
	                        boolean permissao = infoRet.usuarioPodeInserirGrupo;
	                        if(permissao) {
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
                        		DialogFactory.DialogRetVO vo = null;
                        		try {
                        			//vo = factory.loadDialog("dialog/ldap/JFUsuario.fxml");
                        			vo = factory.loadDialog("dialog/message/JFMensagem.fxml");
                        			JFMensagem msgController = (JFMensagem)vo.controller;
                        			msgController.setLoginSelecionado(treeItem.getValue().uid);
                    				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
                    				
                    				final Stage dialog = new Stage(StageStyle.TRANSPARENT);
                    				dialog.initModality(Modality.WINDOW_MODAL);
                    				dialog.initOwner(JazzForms.PRIMARY_STAGE);
                    				
                    				dialog.setScene(SceneBuilder.create().width(500).height(550).root((Parent)vo.root).build());
                    				dialog.setResizable(true);
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
                        		}
	                        } else {
	                        	JFDialog dialog = new JFDialog();
	                        	dialog.show("Usuário não tem permissão para enviar mensagem", "Ok");
	                        }
						} else {
							showErrorMessage(infoRet.mensagem);
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
                        		final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();
                        		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		                        DialogFactory.DialogRetVO vo = null;
		                        try {
			                        vo = factory.loadDialog("dialog/equipamento/mapa/JFWebMap.fxml");
			                        JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			                        final WebMapController mapController = (WebMapController)vo.controller;
			                        controller.findLocalizacoesUsuario(mapController, treeItem.getValue().uid);
			                        /*if (device.equipamento.localizacoes.size() > 0) {
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
                                    }*/
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
        		
        		cmUsuario.getItems().add(miRemoveUsuario);
        		cmUsuario.getItems().add(miCopiaUsuario);
        		cmUsuario.getItems().add(new SeparatorMenuItem());
        		cmUsuario.getItems().add(miEnviarMensagem);
        		cmUsuario.getItems().add(new SeparatorMenuItem());
        		cmUsuario.getItems().add(miVerMap);
        		
        		
        		treeUsuarios.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

					@Override
                    public void handle(MouseEvent event) {
	                    if (event.getButton() == MouseButton.SECONDARY && !event.isControlDown()) {
	                    	TreeItem<TreeItemLDAP> item = treeUsuarios.getSelectionModel().getSelectedItem();
	                    	if (item.getValue().tipo == TreeItemLDAP.NONE) {
		                		cmCriarGrupo.show(treeUsuarios, event.getScreenX(), event.getScreenY());
		                		cmCrUsrDelGrp.hide();
		                		cmUsuario.hide();
	                    	} else if(item.getValue().tipo == TreeItemLDAP.GRUPO) { 
	                    		cmCrUsrDelGrp.show(treeUsuarios, event.getScreenX(), event.getScreenY());
	                    		cmCriarGrupo.hide();
	                    		cmUsuario.hide();
	                    	} else if (item.getValue().tipo == TreeItemLDAP.USUARIO) {
	                    		cmUsuario.show(treeUsuarios, event.getScreenX(), event.getScreenY());
	                    		cmCriarGrupo.hide();
	                    		cmCrUsrDelGrp.hide();
	                    	} else {
	                    		cmCriarGrupo.hide();
	                    		cmCrUsrDelGrp.hide();
	                    		cmUsuario.hide();
	                    	}
	                    } else {
	                    	cmCriarGrupo.hide();
	                    	cmCrUsrDelGrp.hide();
	                    	cmUsuario.hide();
	                    }
                    }
					
				});

				
				for (TreeItem<TreeItemLDAP> treeItem : selectedItems) {
					treeUsuarios.getSelectionModel().select(treeItem);					
				}
				
				uruariosProgressIndicator.setVisible(false);
			}
		});
	}
	
	

	@Override
	public void onProjetoDeployed(final Projeto projeto, final InfoRetornoProjeto infoProjeto) {
		Platform.runLater(new Runnable(){
			public void run() {
            	uruariosProgressIndicator.setVisible(false);
				
				if (infoProjeto != null && infoProjeto.codigo == 0) {
					JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);					
					JFDialog dialog = new JFDialog();
					dialog.show("Projeto publicado com sucesso", "Ok");					
					if (projeto.getDeployment().getNomeAtividadeNegocio() == null || projeto.getDeployment().getNomeAtividadeNegocio().length() == 0) {
						Stage stage = (Stage) usuariosErrorPane.getScene().getWindow();
						stage.close();
					} else {
						Stage stage = (Stage) errorPane.getScene().getWindow();
						stage.close();
					}
				} else if (infoProjeto.mensagem != null && infoProjeto.codigo != 0) {
					if (projeto.getDeployment().getNomeAtividadeNegocio() == null || projeto.getDeployment().getNomeAtividadeNegocio().length() == 0) {
						showUsuariosErrorMessage(infoProjeto.mensagem);
					} else {
						showErrorMessage(infoProjeto.mensagem);
					}
				} else {
					if (projeto.getDeployment().getNomeAtividadeNegocio() == null || projeto.getDeployment().getNomeAtividadeNegocio().length() == 0) {
						showUsuariosErrorMessage("Não foi possível publicar o projeto");
					} else {
						showErrorMessage("Não foi possível publicar o projeto");
					}
				}
			}
		});
		
	}

	@Override
	public void receivePackageAssets(Document<Feed> assets) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onServerDeploymentError(final Projeto projeto, final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (projeto.getDeployment().getNomeAtividadeNegocio() == null || projeto.getDeployment().getNomeAtividadeNegocio().length() == 0) {
					showUsuariosErrorMessage(e.getMessage());
					e.printStackTrace();
					uruariosProgressIndicator.setVisible(false);
				} else {
					showErrorMessage(e.getMessage());
					progressIndicator.setVisible(false);
				}
			}
		});		
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
		TreeItem<TreeItemBPM> selectedItem = treeProcessos.getSelectionModel().getSelectedItem();
		
		if (selectedItem == null || !selectedItem.getValue().isActivity()) {
			JFDialog dialog = new JFDialog();
			dialog.show("Favor selecionar uma atividade de processo de negócio válida", "Ok");
		} else {
			try {
	            progressIndicator.setVisible(true);
	            
	            ObservableList<TreeItem<TreeItemBPM>> selecionados = treeProcessos.getSelectionModel().getSelectedItems();
	            if (selecionados != null && selecionados.size() > 0) {
	            	TreeItem<TreeItemBPM> item = selecionados.get(0);
	            	TreeItem<TreeItemBPM> parent = item.getParent();
	            	Projeto projetoPersistencia = parseCriacaoProjeto("Publicação do projeto");
	            	if (item.getValue().isActivity()) {
	            		if (projetoPersistencia.getId() > 0) {
	            			Deployment deployment = new Deployment();
	            			projetoPersistencia.setDeployment(deployment);
	            			deployment.setProjeto(projetoPersistencia);
	            			deployment.setNomeAtividadeNegocio(item.getValue().href);
	            			deployment.setNomeProcessoNegocio(parent.getValue().href);
	            			controller.deployProjeto(projetoPersistencia, this);
	            		} else {
	    		            progressIndicator.setVisible(false);
	            			JFDialog dialog = new JFDialog();
	            			dialog.show("O projeto ainda não foi salvo. É necessário salvá-lo.", "Ok");
	            		}
	            	} else {
			            progressIndicator.setVisible(false);
	            		JFDialog dialog = new JFDialog();
	            		dialog.show("É necessário selecionar uma atividade.", "Ok");
	            	}
	            } else {
		            progressIndicator.setVisible(false);
	            	JFDialog dialog = new JFDialog();
	            	dialog.show("Favor selecionar uma atividade válida", "Ok");
	            }
            } catch (PreenchimentoException e) {
	            e.printStackTrace();
            }			
		}
	}
	
	@FXML
	protected void btnCancelaActionUsuarios(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	protected void btnOkActionUsuarios(ActionEvent actionEvent) {
		ObservableList<TreeItem<TreeItemLDAP>> objsItems = treeUsuarios.getSelectionModel().getSelectedItems();
		if (objsItems == null || objsItems.size() == 0) {
			JFDialog dialog = new JFDialog();
			dialog.show("Favor selecionar um usuáiro ou grupo válido", "Ok");
		} else {
			try {
				if(centerDispatcher != null && centerDispatcher.isProjetoNaoSalvo()) {
					btnCancelaActionUsuarios(actionEvent);
					JFDialog dialog = new JFDialog();
					dialog.show("Existem alterações no projeto não salvas. É necessário salvá-las antes de publicar.", "Ok");
				} else if (this.alterProjeto == null){
		            boolean usrgrpSelecionado = false; // o projeto está sendo publicado pela primeira vez
		            Projeto projetoPersistencia = parseCriacaoProjeto("Publicação do projeto");
		            if (projetoPersistencia.getId() > 0) {// se o projeto que está sendo publicado foi salvo
		            	uruariosProgressIndicator.setVisible(true);
		            	Deployment deployment = new Deployment();
		            	projetoPersistencia.setDeployment(deployment);
		            	deployment.setProjeto(projetoPersistencia);
		            	
		            	Historico hist = new Historico();
		            	hist.setDescricao("Deployment do projeto");
		            	hist.setDhAlteracao(new Date());
		            	hist.setCodigo(Historico.CODIGO_DEPLOY);
		            	Usuario usuario = new Usuario();
		            	usuario.setLogin(JazzForms.USUARIO_LOGADO.getUid());
		            	hist.setUsuario(usuario);

		            	projetoPersistencia.getHistoricos().add(hist);
		            	for (TreeItem<TreeItemLDAP> item : objsItems) {
		            		TreeItemLDAP itg = item.getValue();
		            		if (itg.isGrupo()) {//adiciona os grupos selecionados para serem responsaveis
		            			Grupo grupo = new Grupo();
		            			grupo.setDescricao(itg.descricao);
		            			grupo.setNome(itg.nome);
		            			deployment.getGruposPossiveis().add(grupo);
		            			usrgrpSelecionado = true;
		            		} else if (itg.isUser()) {// adiciona os usuarios selecionados para serem responsáveis
		            			usrgrpSelecionado = true;
		            			Usuario usr = new Usuario();
		            			usr.setNome(itg.nome);
		            			usr.setLogin(itg.uid);
		            			InfoRetornoLdap info = webController.getGruposUsuario(usr.getLogin());
		            			if(info.codigo == 0) {
		            				List<LdapGrupoVO> usrGrps = info.grupos;
		            				for (LdapGrupoVO usrGrp : usrGrps) {//recupera os grupos dos usuários selecionados
		            					Grupo grpTmp = new Grupo();
		            					grpTmp.setDescricao(usrGrp.getDescription());
		            					grpTmp.setNome(usrGrp.getNome());
		            					usr.getGrupos().add(grpTmp);
		            				}
		            			} else {
		            				showErrorMessage(info.mensagem);
		            			}

		            			deployment.getUsuariosPossiveis().add(usr);
		            			usr.getDeployment().add(deployment);
		            		}
		            	}
		            	if (!usrgrpSelecionado) {
			            	uruariosProgressIndicator.setVisible(false);
		            		JFDialog dialog = new JFDialog();
		            		dialog.show("Favor selecionar um usuáiro ou grupo válido", "Ok");
		            	} else {
		            		controller.deployProjeto(projetoPersistencia, this);
		            	}
		            } else { // caso o projeto que está sendo publicado ainda não foi salvo
		            	uruariosProgressIndicator.setVisible(false);
		            	JFDialog dialog = new JFDialog();
		            	dialog.show("O projeto ainda não foi salvo. É necessário salvá-lo.",
		            			"Ok");				
		            }
				} else {// a publicação do projeto está sendo alterada
					boolean usrgrpSelecionado = false;
	            	uruariosProgressIndicator.setVisible(true);
	            	alterProjeto.getDeployment().setProjeto(alterProjeto);
	            	Deployment deployment = alterProjeto.getDeployment();
	            	deployment.getInstancias().clear();
	            	deployment.getUsuariosPossiveis().clear();
	            	deployment.getGruposPossiveis().clear();
	            	alterProjeto.getHistoricos().clear();
	            	
	            	Historico hist = new Historico();
	            	hist.setDescricao("Alteração de destinatários do deployment");
	            	hist.setDhAlteracao(new Date());
	            	hist.setCodigo(Historico.CODIGO_ALTERACAO_DEPLOYMENT);
	            	Usuario usuario = new Usuario();
	            	usuario.setLogin(JazzForms.USUARIO_LOGADO.getUid());
	            	hist.setUsuario(usuario);
	            	
	            	alterProjeto.getHistoricos().add(hist);
	            	
	            	for (TreeItem<TreeItemLDAP> item : objsItems) {
	            		TreeItemLDAP itg = item.getValue();
	            		if (itg.isGrupo()) {//adiciona os grupos selecionados para serem responsaveis
	            			Grupo grupo = new Grupo();
	            			grupo.setDescricao(itg.descricao);
	            			grupo.setNome(itg.nome);
	            			deployment.getGruposPossiveis().add(grupo);
	            			usrgrpSelecionado = true;
	            		} else if (itg.isUser()) {// adiciona os usuarios selecionados para serem responsáveis
	            			usrgrpSelecionado = true;
	            			Usuario usr = new Usuario();
	            			usr.setNome(itg.nome);
	            			usr.setLogin(itg.uid);
	            			InfoRetornoLdap info = webController.getGruposUsuario(usr.getLogin());
	            			if(info.codigo == 0) {
	            				List<LdapGrupoVO> usrGrps = info.grupos;
	            				for (LdapGrupoVO usrGrp : usrGrps) {//recupera os grupos dos usuários selecionados
	            					Grupo grpTmp = new Grupo();
	            					grpTmp.setDescricao(usrGrp.getDescription());
	            					grpTmp.setNome(usrGrp.getNome());
	            					usr.getGrupos().add(grpTmp);
	            				}
	            			} else {
	            				
	            			}
	            			deployment.getUsuariosPossiveis().add(usr);
	            			usr.getDeployment().add(deployment);
	            		}
	            	}
	            	if (!usrgrpSelecionado) {
		            	uruariosProgressIndicator.setVisible(false);
	            		JFDialog dialog = new JFDialog();
	            		dialog.show("Favor selecionar um usuáiro ou grupo válido", "Ok");
	            	} else {
	            		controller.alteraDestinatarios(alterProjeto, this);
	            	}
	            }
            } catch (PreenchimentoException e) {
	            e.printStackTrace();
            }
		}
	}	
	
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
	
	@FXML
	protected void usuariosErrorPaneClicked(MouseEvent mouseEvent) {
		usuariosErrorPane.setVisible(false);
	}	
	
	class Delta { double x, y; }	
	
	private class TreeItemBPM {
		public String title;
		public String href;
		ProcessModel model;
		private boolean process = false;
		private boolean activity = false;
		
		public TreeItemBPM(String title, String href, ProcessModel model) {
			this.title = title;
			this.href = href;
			this.model = model;
		}
		
		@Override
		public String toString() {
			return title;
		}
		
		public boolean isProcess() {
			return process;
		}
		
		public void setProcess(boolean process) {
			this.process = process;
		}

		public boolean isActivity() {
			return activity;
		}

		public void setActivity(boolean activity) {
			this.activity = activity;
		}		
	}
	
	private class TreeItemLDAP {
		public static final int NONE = 0;
		public static final int GRUPO = 1;
		public static final int USUARIO = 2;
		
		public boolean basePackage = false;
		public String nome;
		public String dn;
		public String uid;
		public String descricao;
		public int tipo;
		
		public boolean selecionado = false;
		
		public TreeItemLDAP(){}
		
		public TreeItemLDAP(String nome, String dn, String descricao, boolean basePackage, int tipoItem) {
			this.basePackage = basePackage;
			this.nome = nome;
			this.dn = dn;
			this.descricao = descricao;
			this.tipo = tipoItem;
		}
		
		@Override
		public String toString() {
			return nome;
		}
		
		public boolean isUser() {
			return tipo == USUARIO;
		}
		
		public boolean isGrupo() {
			return tipo == GRUPO;
		}
	}
}
