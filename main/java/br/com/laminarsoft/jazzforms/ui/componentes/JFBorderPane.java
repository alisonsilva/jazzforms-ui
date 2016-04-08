package br.com.laminarsoft.jazzforms.ui.componentes;

import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import javafx.scene.layout.BorderPane;

public abstract class JFBorderPane extends BorderPane implements IJFUIBackComponent {

	@Override
	public abstract Component getBackComponent();
	
	public void inicializaCustomComponent(Component componente) {
		throw new RuntimeException("É necessário implementar esse método");
	}

	@Override
	public void removeComponent(JFBorderPane component) {
		throw new RuntimeException("É necessário implementar esse método para esse objeto");
	}
	
	@Override
	public void adicionaComponent(JFBorderPane component, String posicao) {
		throw new RuntimeException("É necessário implementar esse método para esse objeto");
	}
}
