package br.com.laminarsoft.jazzforms.ui.componentes.util;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;
import br.com.laminarsoft.jazzforms.ui.dialog.JFDialog;

public class KeyListenerCanvas implements EventHandler<KeyEvent> {

	private Pagina pagina;

	public KeyListenerCanvas(Pagina pagina) {
		this.pagina = pagina;
	}

	@Override
	public void handle(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			List<IJFSelectedComponent> selectedNodes = pagina.getSelectedNodes();
			if (selectedNodes.size() > 0) {
				JFDialog dialog = new JFDialog();

				EventHandler<ActionEvent> act1 = new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						pagina.deleteSelectedNodes();
					}
				};
				dialog.show("Tem certeza que deseja excluir o(s) componente(s) selecionado(s)?", "Sim", "Não", act1);
				event.consume();
			}
		}
	}
}
