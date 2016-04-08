package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.reenvio;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import br.com.laminarsoft.jazzforms.persistencia.model.Usuario;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.IRefreshAprsesentacao;
import br.com.laminarsoft.jazzforms.ui.dialog.ldap.JFUsuario;
import br.com.laminarsoft.jazzforms.ui.entidade.UIUsuario;

public class JFReenvioSelecionaUsuarioController implements Initializable, IReenvioInstanciaUsuario {
	@FXML private TitledPane usuariosErrorPane;
	@FXML private VBox mainPaneUsuarios;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private TreeView<TreeItemLDAP> treeUsuarios;
	
	
	private WebServiceController controller = WebServiceController.getInstance();
	private Long instanciaId;
	private IRefreshAprsesentacao refresher;

	@Override
    public void setInstanciaId(IRefreshAprsesentacao refresher, Long instanciaId) {
		this.instanciaId = instanciaId;
		this.refresher = refresher;
		updateData();
    }

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.visibleProperty().bindBidirectional(mainPaneUsuarios.disableProperty());
		progressIndicator.setVisible(false);
		mainPaneUsuarios.visibleProperty().bind(usuariosErrorPane.visibleProperty().not());
		
//		updateData();
	}
	
	public void updateData() {
		progressIndicator.setVisible(true);
		controller.recuperaUsuariosReenvioInstancia(this, this.instanciaId);
	}	

	private void showErrorMessage(String errorMessage) {
		((Label) usuariosErrorPane.getContent()).setText(errorMessage);
		usuariosErrorPane.setVisible(true);
	}
	
	@FXML
	protected void usuariosErrorPaneClicked(MouseEvent mouseEvent) {
		usuariosErrorPane.setVisible(false);
	}		

	@Override
    public void recebeResultadoReenvioInstancia(InfoRetornoInstancia info) {
		Platform.runLater(new Runnable() {
			public void run() {
				refresher.refreshApresentacao();
				progressIndicator.setVisible(false);
				JFDialog dialog = new JFDialog();
				dialog.show("Formulário enviado com sucesso para o usuário especificado", "Ok");
			}
		});
    }

	@Override
    public void recebeUsuariosPossiveis(final InfoRetornoInstancia info) {
		final JFReenvioSelecionaUsuarioController me = this;
		Platform.runLater(new Runnable() {
			public void run() {	
                me.progressIndicator.setVisible(false);
				if (info.codigo == 0) {
	                List<Usuario> usrs = info.usuariosReenvioInstancia;
	                final List<UIUsuario> usuarios = new ArrayList<UIUsuario>();
	                for (Usuario usr : usrs) {
		                usuarios.add(convertUsuario(usr));
	                }
	                TreeItem<TreeItemLDAP> root = treeUsuarios.getRoot();
	                if (root != null) {
		                root.getChildren().clear();
	                }
	                TreeItem<TreeItemLDAP> rootItem = new TreeItem<TreeItemLDAP>(new TreeItemLDAP("Usuários possíveis", "", "", "", true, TreeItemLDAP.NONE),
	                        new ImageView(new Image(getClass().getResourceAsStream("./imgs/folder-home.png"))));
	                Image peopleIcon = new Image(getClass().getResourceAsStream("./imgs/user.png"));
	                Collections.sort(usuarios);
	                for (UIUsuario usuario : usuarios) {
		                TreeItemLDAP treeItmUsr = new TreeItemLDAP(usuario.getNome(), usuario.getUid(), usuario.getDn(), "", false, TreeItemLDAP.USUARIO);
		                treeItmUsr.uid = usuario.getUid();
		                TreeItem<TreeItemLDAP> itUser = new TreeItem<TreeItemLDAP>(treeItmUsr, new ImageView(peopleIcon));
		                rootItem.getChildren().add(itUser);
	                }
	                rootItem.setExpanded(true);
	                treeUsuarios.setRoot(rootItem);
	                treeUsuarios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                } else {
                	showErrorMessage("Erro ao recuperar usuários possíveis: " + info.mensagem); 
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

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		JFReenvioSelecionaUsuarioController me = this;
		progressIndicator.setVisible(true);
		TreeItem<TreeItemLDAP> selecionado =  treeUsuarios.getSelectionModel().getSelectedItem();
		if (selecionado != null) {
			progressIndicator.setVisible(true);
			controller.reenviaInstanciaUsuario(me, instanciaId, selecionado.getValue().uid, JazzForms.USUARIO_LOGADO.getUid());
		} else {
			progressIndicator.setVisible(false);
			JFDialog dialog = new JFDialog();
			dialog.show("É necessário selecionar ao menos um usuário", "Ok");
		}
		
	}	
	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		fechaJanela(actionEvent);
	}
	
	private UIUsuario convertUsuario(Usuario usuario) {
		UIUsuario usrConvertido = new UIUsuario();
		usrConvertido.setCn(usuario.getNome());
		usrConvertido.setUid(usuario.getLogin());
		usrConvertido.setNome(usuario.getNome());
		return usrConvertido;
	}
	
	@FXML
	protected void btnNovoUsuario(ActionEvent actionEvent) {
		DialogFactory factory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
		DialogFactory.DialogRetVO vo = null;
		try {
			vo = factory.loadDialog("dialog/ldap/JFUsuario.fxml");
			JFUsuario usrController = (JFUsuario)vo.controller;
//			usrController.setPublicadorController(pubController);
//			usrController.setGrupoSelecionado(grupoSelecionado);
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
		
		public TreeItemLDAP(String nome, String login, String dn, String descricao, boolean basePackage, int tipoItem) {
			this.basePackage = basePackage;
			this.nome = nome;
			this.dn = dn;
			this.descricao = descricao;
			this.tipo = tipoItem;
			this.uid = login;
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
