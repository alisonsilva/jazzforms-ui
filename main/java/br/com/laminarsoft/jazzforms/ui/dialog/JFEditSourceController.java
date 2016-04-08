package br.com.laminarsoft.jazzforms.ui.dialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import br.com.laminarsoft.jazzforms.persistencia.model.ImplementacaoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.TipoEvento;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.componentes.Evento;
import br.com.laminarsoft.jazzforms.ui.componentes.util.ComponentesUtil;

public class JFEditSourceController implements Initializable {

	@FXML private Label lblEditText;
	@FXML private Pane codigo;
	
	private IEditSourceJavaScript componente;
	private JavaScriptCodeEditor codeEditor;
	private CSSCodeEditor cssCodeEditor;
	private JavaScriptCodeEditor functionCodeEditor;
	private HTMLCodeEditor htmlCodeEditor;
	private int tipoEventoId;
	private Evento evento;
	private TableView<Evento> table;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
	}
	
	public Pane getCodigoPane() {
		return codigo;
	}

	public void setLabel(String label) {
		lblEditText.setText(label);
	}

	
	public void setJSEditor(JavaScriptCodeEditor editor) {
		codigo.getChildren().add(editor);
		codeEditor = editor;
	}
	
	public void setJSFunctionEditor(JavaScriptCodeEditor editor, int tipoEventoId, Evento evento, TableView<Evento> table) {
		codigo.getChildren().add(editor);
		functionCodeEditor = editor;
		this.tipoEventoId = tipoEventoId;
		this.evento = evento;
		this.table = table;
	}
	
	public void setCSSEditor(CSSCodeEditor editor) {
		codigo.getChildren().add(editor);
		cssCodeEditor = editor;
	}
	
	public void setHTMLEditor(HTMLCodeEditor editor) {
		codigo.getChildren().add(editor);
		htmlCodeEditor = editor;
	}
	
	public void setComponente(IEditSourceJavaScript componente) {
		this.componente = componente;
	}
	
	@FXML
	protected void btnOkAction(ActionEvent actionEvent) {
		if (codeEditor != null) {
			byte[] codigo = codeEditor.getCodeAndSnapshot().getBytes();
			componente.setupJavaScriptCode(codigo);
		} else if(cssCodeEditor != null) {
			byte[] codigo = cssCodeEditor.getCodeAndSnapshot().getBytes();
			componente.setupJavaScriptCode(codigo);
		} else if(htmlCodeEditor != null) {
			byte[] codigo = htmlCodeEditor.getCodeAndSnapshot().getBytes();
			componente.setupJavaScriptCode(codigo);
		} else if(functionCodeEditor != null) {
			String strcodigo = functionCodeEditor.getCodeAndSnapshot().trim();
			if (strcodigo.length() > 0) {
				byte[] codigo = strcodigo.getBytes();
				String fname = ComponentesUtil.getMainFunctionName(new String(strcodigo));

				if(fname.length() == 0) {
					JFDialog diag = new JFDialog();
					diag.show("Não foi definida nenhuma função", "Ok");
				}
								
				ImplementacaoEvento impEvento = new ImplementacaoEvento();
				TipoEvento tpevento = new TipoEvento();
				tpevento.setId(tipoEventoId);
				tpevento.setNome(evento.getNomeEvento());
				impEvento.setTipoEvento(tpevento);
				
				impEvento.setNome(fname);
				evento.setNomeImplementacaoEvento(fname);
				impEvento.setImplementacao(codigo);
				
				ImplementacaoEvento existente = null;
				if ((existente = getImplementacao()) != null) {
					impEvento.setId(existente.getId());
					componente.removeImplementacao(existente);					
				}
				
				componente.addImplementacao(impEvento);
				
				refreshTable(table.getSelectionModel().getSelectedIndex());
			}			
		}
		JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
		Node source = (Node) actionEvent.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}	
	
	
	private ImplementacaoEvento getImplementacao() {
		List<ImplementacaoEvento> metodos = componente.getImplementacoes();
		ImplementacaoEvento ret = null;
		for (ImplementacaoEvento imp : metodos) {
			if (imp.getTipoEvento().getId() == tipoEventoId) {
				ret = imp;
				break;
			}
		}
		return ret;
	}
	
	private void refreshTable(final int pos) {
	    final List<Evento> items = table.getItems();
	    if( items == null || items.size() == 0) return;

	    items.remove(pos);
	    Platform.runLater(new Runnable(){
	        @Override
	        public void run() {
	            items.add(pos, evento);
	            List<Evento> eventos = new ArrayList<Evento>();
	            for(Evento evt : items) {
	            	eventos.add(evt.clone());
	            }
	            table.getItems().clear();
	            table.getItems().addAll(eventos);
	        }
	    });
	 }	

	
	@FXML
	protected void btnCancelaAction(ActionEvent actionEvent) {
		
		JFDialog dialog = new JFDialog();
		final Node source = (Node) actionEvent.getSource();
		
		EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				JazzForms.PRIMARY_STAGE.getScene().getRoot().setEffect(null);
				Stage stage = (Stage) source.getScene().getWindow();
				stage.close();
			}
		};
		
		dialog.show("Tem certeza que deseja cancelar edição?", "Sim", "Não", act1);		
	}	
	
}

