package br.com.laminarsoft.jazzforms.ui.dialog.ldap;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.drools.core.util.StringUtils;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapUsuarioVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.ILDAPResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.message.JFMensagem;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.PublicaProjetoController;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

public class JFSelecionaUsuarioController implements Initializable, ILDAPResponseHandler {
	@FXML private TitledPane usuariosErrorPane;
	@FXML private VBox mainPaneUsuarios;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator uruarioProgressIndicator;
	@FXML private TreeView<TreeItemLDAP> treeUsuarios;
	@FXML private TextField txtFiltro;
	@FXML private Button btnUsuarioNovo;
	@FXML private Button btnUsuariosOk;
	@FXML private Button btnUsuariosCancel;
	
	
	private WebServiceController controller = WebServiceController.getInstance();

	private PublicaProjetoController pubController;
	private String grupoSelecionado;
	
	private List<LdapUsuarioVO> listaUsuarios;
	
	public void setPublicadorController(PublicaProjetoController pubController) {
		this.pubController = pubController;
	}

	public void setGrupoSelecionado(String grupoSel) {
		this.grupoSelecionado = grupoSel;
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		uruarioProgressIndicator.visibleProperty().bindBidirectional(mainPaneUsuarios.disableProperty());
		uruarioProgressIndicator.setVisible(false);
		mainPaneUsuarios.visibleProperty().bind(usuariosErrorPane.visibleProperty().not());
		AwesomeDude.setIcon(btnUsuarioNovo, AwesomeIcon.PLUS_CIRCLE, "16px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnUsuariosOk, AwesomeIcon.CHECK_CIRCLE, "16px", ContentDisplay.RIGHT);
		AwesomeDude.setIcon(btnUsuariosCancel, AwesomeIcon.TIMES_CIRCLE, "16px", ContentDisplay.RIGHT);
		updateData();
	}
	
	public void updateData() {
		uruarioProgressIndicator.setVisible(true);
		controller.getUsuarios(this);
	}	

	private void showErrorMessage(String errorMessage) {
		((Label) usuariosErrorPane.getContent()).setText(errorMessage);
		usuariosErrorPane.setVisible(true);
	}
	
	@FXML
	protected void usuariosErrorPaneClicked(MouseEvent mouseEvent) {
		usuariosErrorPane.setVisible(false);
	}		
	
	
	@FXML 
	protected void handleKeyInput(KeyEvent event) {
		final String text = ((TextField)event.getTarget()).getText();
		List<LdapUsuarioVO> filtrados = Lists.newArrayList(Collections2.filter(listaUsuarios, new Predicate<LdapUsuarioVO>(){
			
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
		
		Collections.sort(filtrados);
		Image peopleIcon = new Image(getClass().getResourceAsStream("./imgs/user.png"));
		TreeItem<TreeItemLDAP> rootItem = treeUsuarios.getRoot();
		rootItem.getChildren().clear();

		for (LdapUsuarioVO usuario : filtrados) {
			TreeItemLDAP treeItmUsr = new TreeItemLDAP(usuario.getNome(), usuario.getDn(), "", false, TreeItemLDAP.USUARIO);
			treeItmUsr.uid = usuario.getUid();
			TreeItem<TreeItemLDAP> itUser = new TreeItem<TreeItemLDAP>(treeItmUsr, new ImageView(peopleIcon));
			rootItem.getChildren().add(itUser);					
		}		
	} 
	
	@Override
    public void receiveUsers(final List<LdapUsuarioVO> usuarios) {
		final JFSelecionaUsuarioController me = this;
		Platform.runLater(new Runnable() {
			public void run() {	
				me.listaUsuarios = usuarios;
				
				TreeItem<TreeItemLDAP> root = treeUsuarios.getRoot();
				if (root != null) {
					root.getChildren().clear();
				}
				TreeItem<TreeItemLDAP> rootItem = new TreeItem<TreeItemLDAP>(new TreeItemLDAP("Usuários", "", "", true, TreeItemLDAP.NONE), 
						new ImageView(new Image(getClass().getResourceAsStream("./imgs/folder-home.png"))));
				Image peopleIcon = new Image(getClass().getResourceAsStream("./imgs/user.png"));
				Collections.sort(usuarios);
				for (LdapUsuarioVO usuario : usuarios) {
					TreeItemLDAP treeItmUsr = new TreeItemLDAP(usuario.getNome(), usuario.getDn(), "", false, TreeItemLDAP.USUARIO);
					treeItmUsr.uid = usuario.getUid();
					TreeItem<TreeItemLDAP> itUser = new TreeItem<TreeItemLDAP>(treeItmUsr, new ImageView(peopleIcon));
					rootItem.getChildren().add(itUser);					
				}
				rootItem.setExpanded(true);
				treeUsuarios.setRoot(rootItem);
				treeUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				me.uruarioProgressIndicator.setVisible(false);
				
        		final ContextMenu cmUsuario = new ContextMenu();
        		MenuItem miRemoveUsuario = new MenuItem("Remove");
        		miRemoveUsuario.setOnAction(new EventHandler<ActionEvent>(){

					@Override
                    public void handle(ActionEvent event) {
						InfoRetornoLdap info = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
						if(info.codigo == 0) {
                            boolean permissao = info.usuarioPodeInserirGrupo;
                            if(permissao) {
                            	JFDialog dialog = new JFDialog();
                                final TreeItem<TreeItemLDAP> treeItem = treeUsuarios.getSelectionModel().getSelectedItem();

                            	EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
                            		@Override
                            		public void handle(ActionEvent actionEvent) {
                            			InfoRetornoLdap info = controller.removeUsuario(treeItem.getValue().uid);
                                        if (info.codigo == 0) {
                                            me.updateData();
                                            pubController.updateData();
                                        } else {
                                        	showErrorMessage(info.mensagem);
                                        }
                            		}
                            	};
                            	dialog.show("Tem certeza que deseja remover o usuário selecionado ("+ treeItem.getValue().nome +")?", "Sim", "Não", act1);
                            	event.consume();
                            } else {
                            	JFDialog dialog = new JFDialog();
                            	dialog.show("Usuário logado não tem permissão para apagar usuáiros", "Ok");
                            }
						} else {
							showErrorMessage(info.mensagem);
						}
					}        			
        		});
        		MenuItem miEnviarMensagem = new MenuItem("Enviar mensagem...");
        		miEnviarMensagem.setOnAction(new EventHandler<ActionEvent>() {

					@Override
                    public void handle(ActionEvent enviarMsgAction) {
						InfoRetornoLdap infoRet = controller.usuarioPodeInserirGrupo(JazzForms.USUARIO_LOGADO.getUid());
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
                    				
                    				dialog.setScene(SceneBuilder.create().width(430).height(550).root((Parent)vo.root).build());
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
        		
        		cmUsuario.getItems().add(miRemoveUsuario);
        		cmUsuario.getItems().add(new SeparatorMenuItem());
        		cmUsuario.getItems().add(miEnviarMensagem);     		
        		
        		
        		treeUsuarios.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

					@Override
                    public void handle(MouseEvent event) {
	                    if (event.getButton() == MouseButton.SECONDARY && !event.isControlDown()) {
	                    	TreeItem<TreeItemLDAP> item = treeUsuarios.getSelectionModel().getSelectedItem();
	                    	if (item.getValue().tipo == TreeItemLDAP.USUARIO) {
	                    		cmUsuario.show(treeUsuarios, event.getScreenX(), event.getScreenY());
	                    	} else {
	                    		cmUsuario.hide();
	                    	}
	                    } else {
	                    	cmUsuario.hide();
	                    }
                    }
					
				});        		
			}
		});
	}

	@Override
    public void receiveGroups(List<LdapGrupoVO> grupos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onServerError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
				uruarioProgressIndicator.setVisible(false);				
			}
		});		
	}

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		uruarioProgressIndicator.setVisible(true);
		List<TreeItem<TreeItemLDAP>> selecionados =  treeUsuarios.getSelectionModel().getSelectedItems();
		if (selecionados.size() > 0) {
            	boolean usuarioJaEmGrupo = false;
            	String usuarios = "";
                for(TreeItem<TreeItemLDAP> sel : selecionados) {
                	TreeItemLDAP usuario = sel.getValue();
                	InfoRetornoLdap info = controller.getGruposUsuario(usuario.uid);
                	if (info.codigo == 0) {
	                    List<LdapGrupoVO> gruposUsuario = info.grupos;
	                    for (LdapGrupoVO grp : gruposUsuario) {
		                    if (grp.getNome().equalsIgnoreCase(grupoSelecionado)) {
			                    usuarioJaEmGrupo = true;
			                    usuarios += usuario.nome + ", ";
		                    }
	                    }
                    } else {
                    	showErrorMessage(info.mensagem);
                    }                	 
                }
            	if (!usuarioJaEmGrupo) {
            		for(TreeItem<TreeItemLDAP> sel : selecionados) {
                    	TreeItemLDAP usuario = sel.getValue();
                    	InfoRetornoLdap info = controller.adicionaUsuarioAoGrupo(usuario.uid, grupoSelecionado);
                    	if(info.codigo != 0) {
                    		showErrorMessage(info.mensagem);
                    	}
                	} 
            		pubController.updateData();
            		fechaJanela(actionEvent);
                } else {
            		JFDialog dialog = new JFDialog();
            		dialog.show("Usuário(s) (" + usuarios.substring(0, usuarios.length()-2) + ") já está(ão) no grupo " + grupoSelecionado, "Ok");
            	}
		} else {
			JFDialog dialog = new JFDialog();
			dialog.show("É necessário selecionar ao menos um usuário", "Ok");
		}
		uruarioProgressIndicator.setVisible(false);
		
	}	
	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		fechaJanela(actionEvent);
	}
	
	@FXML
	protected void btnNovoUsuario(ActionEvent actionEvent) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("dialog/ldap/JFUsuario.fxml");
			JFUsuario usrController = (JFUsuario)vo.controller;
			usrController.setPublicadorController(pubController);
			usrController.setGrupoSelecionado(grupoSelecionado);
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			
			final Stage dialog = new Stage(StageStyle.TRANSPARENT);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(JazzForms.PRIMARY_STAGE);
			
			dialog.setScene(SceneBuilder.create().width(400).height(200).root((Parent)vo.root).build());
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
		
		
		fechaJanela(actionEvent);
	}

	private void fechaJanela(ActionEvent actionEvent) {
	    JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
    }	
	
	class Delta { double x, y; }	
	
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
