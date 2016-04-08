package br.com.laminarsoft.jazzforms.ui.dialog.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoMensagem;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.AnexoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.MensagemVO;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.CarregaProjetoController;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.CarregaProjetoController.ListItem;
import eu.medsea.mimeutil.MimeUtil2;

public class JFMensagem implements Initializable, IMensagem {
	@FXML private TextField tituloMensagem;
	@FXML private TextArea conteudoMensagem;
	@FXML private TitledPane errorPane;
	@FXML private Label lblRotulo;
	@FXML private VBox mainPane;
	@FXML private Label errorLabel;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private HBox boxAnexos;
	@FXML private Label lblAnexos;
	@FXML private Label lblProjeto;
	@FXML private Label lblURL;
	@FXML private TextField txtLink;
	
	private String loginUsuarioSelecionado;
	private String nomeGrupo;
	private Long idEquipamento;
	private Long idGrupoEquipamento;
	private int destinatario = 1;
	private WebServiceController webController;
	private ListItem projetoSelecionado;
	
	private Map<String, AnexoVO> arquivosAnexos = new HashMap<String, AnexoVO>();
	
	public void setLoginSelecionado(String loginSelecionado) {
		this.loginUsuarioSelecionado = loginSelecionado;
		this.lblRotulo.setText("Enviar mensagem para usuário: " + loginSelecionado);
		this.destinatario = IMensagem.MENSAGEM_USUARIO;
	}
	
	public void setGrupoSelecionado(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
		this.lblRotulo.setText("Enviar mensagem para grupo: " + nomeGrupo);
		this.destinatario = IMensagem.MENSAGEM_GRUPO_USUARIOS;
	}
	
	public void setGrupoEquipamentoSelecionado(Long idGrupoEquipamento) {
		this.idGrupoEquipamento = idGrupoEquipamento;
		this.lblRotulo.setText("Enviar mensagem para grupo de equipamentos");
		this.destinatario = IMensagem.MENSAGEM_GRUPO_EQUIPAMENTOS;
	}
	
	public void setEquipamentoSelecionado(Long idEquipamento) {
		this.idEquipamento = idEquipamento;
		this.lblRotulo.setText("Enviar mensagem para equipamento");
		this.destinatario = IMensagem.MENSAGEM_EQUIPAMENTO;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.visibleProperty().bindBidirectional(mainPane.disableProperty());
		progressIndicator.setVisible(false);
		mainPane.visibleProperty().bind(errorPane.visibleProperty().not());
		
		tituloMensagem.setText("");
		conteudoMensagem.setText("");
		webController = WebServiceController.getInstance();
	}

	private void showErrorMessage(String errorMessage) {
		((Label) errorPane.getContent()).setText(errorMessage);
		errorPane.setVisible(true);
	}
	
	@FXML
	protected void errorPaneClicked(MouseEvent mouseEvent) {
		errorPane.setVisible(false);
	}
		

	@Override
    public void mensagemEnviadaSucesso(final InfoRetornoMensagem info) {
		Platform.runLater(new Runnable() {
			public void run() {
				progressIndicator.setVisible(false);
				if(info.codigo == 0) {
					tituloMensagem.setText("");
					conteudoMensagem.setText("");
					JFDialog dialog = new JFDialog();
					dialog.show("Mensagem enviada com sucesso", "Ok");
				} else {
					showErrorMessage(info.mensagem);
				}
			}
		});
    }

	@Override
    public void mensagemEnviadaError(final Exception excp) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(excp.getMessage());
			}
		});
    }

	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		if (tituloMensagem == null || tituloMensagem.getText().trim().length() == 0) {
			showErrorMessage("O título da mensagem deve ser válido");
		} else if (conteudoMensagem == null || conteudoMensagem.getText().trim().length() == 0) {
			showErrorMessage("O conteúdo da mensagem deve ser válido");
		} else {
			progressIndicator.setVisible(true);
			MensagemVO vo = new MensagemVO();
			vo.conteudo = conteudoMensagem.getText();
			vo.setTitulo(tituloMensagem.getText());
			vo.getGrupos().add(nomeGrupo);
			vo.setDestinatarioUID(loginUsuarioSelecionado);
			vo.setRemetenteUID(JazzForms.USUARIO_LOGADO.getUid());
			
			Iterator<String> arquivosAnexosIter = arquivosAnexos.keySet().iterator();
			while(arquivosAnexosIter.hasNext()) {
				String nomeArquivo = arquivosAnexosIter.next();
				AnexoVO anexo = arquivosAnexos.get(nomeArquivo);
				vo.getAnexos().add(anexo);
			}
			if (lblURL.getText().trim().length() > 0 && arquivosAnexos.keySet().size() > 0) {
				AnexoVO anexo = new AnexoVO();
				anexo.urlSite = lblURL.getText();
				anexo.dhInclusao = new Date();
				vo.getAnexos().add(anexo);
			}
			if (projetoSelecionado != null) {
				vo.idProjeto = projetoSelecionado.projetoId;
			}
			
            if (destinatario == IMensagem.MENSAGEM_USUARIO) {
	            webController.novaMensagemUsuario(this, vo);
            } else if (destinatario == IMensagem.MENSAGEM_GRUPO_USUARIOS) {
            	webController.novaMensagemGrupo(this, vo);
            } else if (destinatario == IMensagem.MENSAGEM_GRUPO_EQUIPAMENTOS) {
            	vo.getGrupoEquipamentos().add(idGrupoEquipamento);
            	webController.novaMensagemGrupoEquipamento(this, vo);
            } else if(destinatario == IMensagem.MENSAGEM_EQUIPAMENTO) {
            	vo.getEquipamentos().add(idEquipamento);
            	webController.novaMensagemEquipamento(this, vo);
            }
		}		
	}	
	
	@FXML
	protected void lblAnexosClick(ActionEvent actionEvent) {
		
	}
	
	@FXML
	protected void lblURLClick(ActionEvent actionEvent) {
		
	}	
	
	@FXML
	protected void btnAnexarAction(ActionEvent actionEvent) {
		progressIndicator.setVisible(true);
		Platform.runLater(new Runnable() {
			public void run() {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos a anexar", "*.pdf", "*.png", "*.jpg");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(JazzForms.PRIMARY_STAGE);
				if (file != null) {
					try {
						Path path = Paths.get(file.toURI());
						byte[] data = Files.readAllBytes(path);
						
						AnexoVO anexo = new AnexoVO();
						
						anexo.arqAnexo = data;
						anexo.nomeArquivo = file.getName();
						anexo.dhInclusao = new Date();		
						
						MimeUtil2 mimeUtil = new MimeUtil2();
						mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
						String mimeType = MimeUtil2.getMostSpecificMimeType(mimeUtil.getMimeTypes(file)).toString();
						anexo.type = mimeType;
						
						arquivosAnexos.put(file.getName(), anexo);
						Iterator<String> arquivos = arquivosAnexos.keySet().iterator();
						String txtLblAnexos = "";
						while(arquivos.hasNext()) {
							String nomeArquivo = arquivos.next();
							txtLblAnexos += nomeArquivo + ";";
						}
						lblAnexos.setText(txtLblAnexos);
						boxAnexos.setVisible(true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
				progressIndicator.setVisible(false);
			}
		});		
	}
	
	@FXML
	protected void btnURLAction(ActionEvent actionEvent) {
		txtLink.setVisible(true);
		txtLink.setText(lblURL.getText());
	}
	
	@FXML
	protected void txtLinkAction(ActionEvent actionEvent) {
		lblURL.setText(txtLink.getText());
		txtLink.setVisible(false);
	}
	
	public void setProjetoMensagem(ListItem projetoSelecionado) {
		this.projetoSelecionado = projetoSelecionado;
		this.lblProjeto.setText(projetoSelecionado.nome);
	}
	
	@FXML
	protected void btnProjetoAction(ActionEvent actionEvent) {
		final Stage dialog = new Stage(StageStyle.TRANSPARENT);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(JazzForms.PRIMARY_STAGE);
		
        Parent root = null;
        boolean excpt = false;
		try {
			
			DialogFactory dialogFactory = JazzForms.WELD_CONTAINER.instance().select(DialogFactory.class).get();
			DialogFactory.DialogRetVO vo = dialogFactory.loadDialog("top/dialog/projeto/JFCarregaProjetoDialog.fxml");
			
			root = (StackPane)vo.root;

			CarregaProjetoController carregaProjetoController = (CarregaProjetoController)vo.controller;
			carregaProjetoController.setMensagemController(this);
			
		} catch (IOException e) {
			excpt = true;
		}
        
		if (!excpt) {
			JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(new BoxBlur());
			dialog.setScene(SceneBuilder.create().width(500).height(400).root(root).build());
			dialog.show();
		}
	}
	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}	
	
}
