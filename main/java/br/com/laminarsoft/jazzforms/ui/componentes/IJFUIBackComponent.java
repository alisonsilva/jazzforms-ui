package br.com.laminarsoft.jazzforms.ui.componentes;

import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;

public interface IJFUIBackComponent {
	public Component getBackComponent();
	public void removeComponent(JFBorderPane component);
	public void adicionaComponent(JFBorderPane component, String posicao);
	
	/**
	 * Verifica se a inserção do componente sendo construído é válida no tipo de container passado
	 * @param container O container no qual o componente implementando esse método será inserido
	 * @return True caso o componente que implementa esse método possa ser inserido no container passado, false caso contrário
	 */
	public boolean isInsersaoValida(Container container);
}
