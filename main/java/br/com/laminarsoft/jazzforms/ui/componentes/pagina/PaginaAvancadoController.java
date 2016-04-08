package br.com.laminarsoft.jazzforms.ui.componentes.pagina;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import br.com.laminarsoft.jazzforms.persistencia.model.ComponentType;
import br.com.laminarsoft.jazzforms.persistencia.model.ImplementacaoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.TipoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.communicator.IServerResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;
import br.com.laminarsoft.jazzforms.ui.componentes.Evento;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerDetalhes;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.numbertextfield.NumberSpinner;
import br.com.laminarsoft.jazzforms.ui.dialog.IEditSourceJavaScript;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;
import br.com.laminarsoft.jazzforms.ui.dialog.JFEditSourceDialog;

@SuppressWarnings("all")
public class PaginaAvancadoController implements Initializable, IServerResponseHandler, IControllerDetalhes {

	@FXML private GridPane comboBoxAvancado;	
	@FXML private TextField biblioteca;
	@FXML private TextField estilo;
	@FXML private TextField classe;
	@FXML private Button uploadBiblioteca;	
	@FXML private Button btnApaga;	 
	@FXML private Button btnAdiciona;	
	@FXML private Button btnEdita;
	@FXML private Button editJSBiblioteca;
	@FXML private Button editJSEstilo;
	@FXML private Button uploadEstilo;
	@FXML private TableView<Evento> tblEventos;
	@FXML private CheckBox chkFullScreen;
	
	private Map<String, String> valoresPreenchimento = new HashMap<String, String>();
	private TableColumn clEvento;
	private TableColumn clMetodo;
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		inicializa();
	}

	private void inicializa() {
	    Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		List<IJFSelectedComponent> selNode = pagina.getSelectedNodes();
		
		double tblWidth = tblEventos.getPrefWidth();
		
		clEvento = new TableColumn("Evento");
		clEvento.setPrefWidth(tblWidth/2);
		clMetodo = new TableColumn("M�todo");
		clMetodo.setPrefWidth(tblWidth/2);
		
		clEvento.setCellValueFactory(new PropertyValueFactory<Evento, String>("nomeEvento"));
		clMetodo.setCellValueFactory(new PropertyValueFactory<Evento, String>("nomeImplementacaoEvento"));
		tblEventos.getColumns().addAll(clEvento, clMetodo);
		
		AwesomeDude.setIcon(btnApaga, AwesomeIcon.TRASH_ALT, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(editJSBiblioteca, AwesomeIcon.PENCIL, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(editJSEstilo, AwesomeIcon.PENCIL, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(btnEdita, AwesomeIcon.EDIT, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(uploadEstilo, AwesomeIcon.UPLOAD, "16px", ContentDisplay.CENTER);
		AwesomeDude.setIcon(uploadBiblioteca, AwesomeIcon.UPLOAD, "16px", ContentDisplay.CENTER);
		
		classe.setText(pagina.getPaginaModel().getContainer().getCls());
		double bottom = pagina.getPaginaModel().getContainer().getBottom() == null ? 0 : pagina.getPaginaModel().getContainer().getBottom();
		double left = pagina.getPaginaModel().getContainer().getLeft() == null ? 0 : pagina.getPaginaModel().getContainer().getLeft();
		double right = pagina.getPaginaModel().getContainer().getRight() == null ? 0 : pagina.getPaginaModel().getContainer().getRight();
		double top = pagina.getPaginaModel().getContainer().getTop() == null ? 0 : pagina.getPaginaModel().getContainer().getTop();
		chkFullScreen.setSelected(pagina.getPaginaModel().getContainer().getFullscreen());
		
		WebServiceController wcontroller = WebServiceController.getInstance();
		wcontroller.findEventosByComponentName(this, pagina.getPaginaModel().getFieldType());
		
		if(pagina.getPaginaModel().getPacoteCodigoCustomizacao() != null && 
				pagina.getPaginaModel().getPacoteCodigoCustomizacao().length > 0) {
			biblioteca.setText("Conte�do encontrado");
		}
		if (pagina.getPaginaModel().getStyle() != null && 
				pagina.getPaginaModel().getContainer().getStyle().length > 0) {
			estilo.setText("Conte�do encontrado");
		}
    }
	
	
	
	@Override
	public void onProjetosRetrieveAll(InfoRetornoProjeto infoProjeto) {
	}

	@Override
	public void onProjetoSVNVersions(InfoRetornoSVN infoSVN) {
	}



	@Override
	public void onProjetoRetrieve(InfoRetornoProjeto infoProjeto) {
	}


	@Override
	public void setCustomComponent(IDetalhesComponentes customComponente) {
		inicializa();		
	}


	@Override
	public void onEventosPorNomeComponente(InfoRetornoEvento infoEvento) {
		List<Evento> data = new ArrayList<Evento>();
		tblEventos.getItems().clear();

		if (infoEvento.codigo == 0) {
			List<ComponentType> ctypes = infoEvento.ctypes;
			if (ctypes != null) {
				for (ComponentType ct : ctypes) {
					InfoRetornoEvento infoTiposEvento = WebServiceController.getInstance().findTiposEventoByCTId(ct.getId());
					for (TipoEvento tpevt : infoTiposEvento.tiposEvento) {
						String nomeImplFunction = getNomeImplementacaoFuncao(tpevt.getId());
						data.add(new Evento(tpevt.getNome(), nomeImplFunction,
								ct.getNomeComponent(), String.valueOf(tpevt.getId()), tpevt.getDescricao(), 
								tpevt.getAssinatura()));
					}
				}
			}
		}
		tblEventos.setItems(FXCollections.observableArrayList(data));
	}

	private String getNomeImplementacaoFuncao(long idTipoEvento) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		String nomeImpl = "";
		List<ImplementacaoEvento> metodos = pagina.getPaginaModel().getMetodos();
		for(ImplementacaoEvento impl : metodos) {
			if (impl.getTipoEvento().getId() == idTipoEvento) {
				nomeImpl = impl.getNome();
			}
		}
		return nomeImpl;
	}



	@Override
	public void onProjetoCreation(InfoRetornoProjeto infoProjeto) {
	}




	@Override
	public void onServerError(final Exception e) {
		Platform.runLater(new Runnable() {
			public void run() {
				showErrorMessage(e.getMessage());
				e.printStackTrace();
			}
		});		
	}
	
	private void showErrorMessage(String errorMessage) {
		JFDialog dialog = new JFDialog();
		dialog.show("N�o foi poss�vel carregar tipos de evento.", "Ok");
	}	




	@FXML
	protected void buscaBibliotecaAction(ActionEvent actionEvent) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos JavaScript (*.js)", "*.js");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(JazzForms.PRIMARY_STAGE);
		if (file != null) {
			try {
				BufferedInputStream buffin = new BufferedInputStream(new FileInputStream(file));
				byte[] buffout = new byte[1024];
				int qtdRead = 0;
				String str = "";
				while((qtdRead = buffin.read(buffout)) > 0) {
					str += new String(buffout);
				}
				pagina.getPaginaModel().getContainer().setPacoteCodigoCustomizado(str.getBytes());
				biblioteca.setText(file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	@FXML
	protected void buscaEstiloAction(ActionEvent actionEvent) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos CSS (*.css)", "*.css");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(JazzForms.PRIMARY_STAGE);
		if (file != null) {
			try {
				BufferedInputStream buffin = new BufferedInputStream(new FileInputStream(file));
				byte[] buffout = new byte[1024];
				int qtdRead = 0;
				String str = "";
				while((qtdRead = buffin.read(buffout)) > 0) {
					str += new String(buffout);
				}
				pagina.getPaginaModel().getContainer().setStyle(str.getBytes());
				estilo.setText(file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		
	}
	
	@FXML
	protected void editJSBibliotecaAction(ActionEvent actionEvent) {
		final Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		
		JFEditSourceDialog sourceEditDiag = new JFEditSourceDialog();
		byte[] codigo = pagina.getPaginaModel().getPacoteCodigoCustomizacao();
		String cdStr = (codigo != null && codigo.length > 0 ? new String(codigo) : "");
		sourceEditDiag.showJavaScript(cdStr, "Editando javascript", new IEditSourceJavaScript() {
			
			@Override
			public void setupJavaScriptCode(byte[] codigo) {
				pagina.getPaginaModel().setPacoteCodigoCustomizacao(codigo);
				biblioteca.setText("Conte�do definido");
			}
			
			@Override
			public void removeImplementacao(ImplementacaoEvento implementacao) {
				pagina.getPaginaModel().getContainer().getImplementacoes().remove(implementacao);
				
			}
			
			@Override
			public List<ImplementacaoEvento> getImplementacoes() {
				return pagina.getPaginaModel().getContainer().getImplementacoes();
			}
			
			@Override
			public void addImplementacao(ImplementacaoEvento implementacao) {
				pagina.getPaginaModel().getContainer().getImplementacoes().add(implementacao);
			}
		});
		
	}
	
	@FXML
	protected void editJSEstiloAction(ActionEvent actionEvent) {
		final Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		
		JFEditSourceDialog sourceEditDiag = new JFEditSourceDialog();
		byte[] codigo = pagina.getPaginaModel().getStyle();
		String cdStr = (codigo != null && codigo.length > 0 ? new String(codigo) : "");
		sourceEditDiag.showCSS(cdStr, "Editando stilo",  new IEditSourceJavaScript() {
			
			@Override
			public void setupJavaScriptCode(byte[] codigo) {
				pagina.getPaginaModel().setStyle(codigo);
				estilo.setText("Conte�do definido");
			}
			
			@Override
			public void removeImplementacao(ImplementacaoEvento implementacao) {
				pagina.getPaginaModel().getContainer().getImplementacoes().remove(implementacao);
				
			}
			
			@Override
			public List<ImplementacaoEvento> getImplementacoes() {
				return pagina.getPaginaModel().getContainer().getImplementacoes();
			}
			
			@Override
			public void addImplementacao(ImplementacaoEvento implementacao) {
				pagina.getPaginaModel().getContainer().getImplementacoes().add(implementacao);
			}
		});		
	}
	
	@Override
	public void btnConfirmaAction(ActionEvent actionEvent) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		List<IJFSelectedComponent> selNode = pagina.getSelectedNodes();
		
		pagina.getPaginaModel().getContainer().setCls(classe.getText());
		pagina.getPaginaModel().getContainer().setFullscreen(chkFullScreen.isSelected());
		
	}


	@Override
	public void btnCancelaAction(ActionEvent actionEvent) {
		Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		List<IJFSelectedComponent> selNode = pagina.getSelectedNodes();
	}
	
	@FXML
	public void btnApagaEventoAction(ActionEvent actionEvent) {
		final Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");
		final Evento evento = tblEventos.getSelectionModel().getSelectedItem();
		if(evento == null) {
			JFDialog diag = new JFDialog();
			diag.show("� necess�rio selecionar um evento", "Ok");
		} else {
			JFDialog dialog = new JFDialog();
			
			EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					List<ImplementacaoEvento> metodos = pagina.getPaginaModel().getMetodos();
					ImplementacaoEvento metodo = null;
					int idx = -1;
					for(int i = 0; i < metodos.size(); i++) {
						metodo = metodos.get(i);
						if (metodo.getTipoEvento().getId() == Long.valueOf(evento.getIdTipoEvento())) {
							metodos.remove(metodo);
							idx = i;
							break;
						}
					}
					if(idx >= 0 && metodo != null) {
						metodos.remove(metodo);
					} 
					evento.setNomeImplementacaoEvento("");
				    final List<Evento> items = tblEventos.getItems();

		            List<Evento> eventos = new ArrayList<Evento>();
		            for(Evento evt : items) {
		            	eventos.add(evt.clone());
		            }
		            tblEventos.getItems().clear();
		            tblEventos.getItems().addAll(eventos);
				}
			};
			
			
			dialog.show("Tem certeza que deseja excluir a implementa��o?", "Sim", "N�o", act1);			
		}
	}

	@FXML
	public void btnEditaAction(ActionEvent actionEvent) {
		final Pagina pagina = (Pagina)JazzForms.PRIMARY_STAGE.getScene().lookup("#paginaDesenhoAtual");

		Evento evento = tblEventos.getSelectionModel().getSelectedItem(); 
		
		
		if (evento == null) {
			JFDialog diag = new JFDialog();
			diag.show("� necess�rio selecionar um evento", "Ok");
		} else {
			JFEditSourceDialog sourceEditDiag = new JFEditSourceDialog();
			
			
			byte[] codigo = null;
			ImplementacaoEvento implSel = null;
			for(ImplementacaoEvento imp : pagina.getPaginaModel().getMetodos()) {
				if(imp.getTipoEvento().getId() == Long.valueOf(evento.getIdTipoEvento())) {
					codigo = imp.getImplementacao();
					break;
				}
			}
			String nomeFuncao = evento.getEvtAssinatura() + "{}";//"function " + evento.getEvtAssinatura().replace("function", " on" + evento.getNomeEvento()) + "{}"; 
			String cdStr = (codigo != null && codigo.length > 0 ? new String(codigo) : nomeFuncao);
			sourceEditDiag.showJavaScriptFunction(cdStr, "Editando implementa��o para: " + evento.getNomeEvento(), 
					 new IEditSourceJavaScript() {
				
				@Override
				public void setupJavaScriptCode(byte[] codigo) {
					pagina.getPaginaModel().setPacoteCodigoCustomizacao(codigo);
					
				}
				
				@Override
				public void removeImplementacao(ImplementacaoEvento implementacao) {
					pagina.getPaginaModel().getMetodos().remove(implementacao);
					
				}
				
				@Override
				public List<ImplementacaoEvento> getImplementacoes() {
					return pagina.getPaginaModel().getMetodos();
				}
				
				@Override
				public void addImplementacao(ImplementacaoEvento implementacao) {
					pagina.getPaginaModel().getMetodos().add(implementacao);
				}
			}, Integer.valueOf(evento.getIdTipoEvento()), evento, tblEventos);
		}
	}
	

	class Delta { double x, y; }
}

