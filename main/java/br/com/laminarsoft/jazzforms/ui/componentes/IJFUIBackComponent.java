package br.com.laminarsoft.jazzforms.ui.componentes;

import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;

public interface IJFUIBackComponent {
	public Component getBackComponent();
	public void removeComponent(JFBorderPane component);
	public void adicionaComponent(JFBorderPane component, String posicao);
	
	/**
	 * Verifica se a inser��o do componente sendo constru�do � v�lida no tipo de container passado
	 * @param container O container no qual o componente implementando esse m�todo ser� inserido
	 * @return True caso o componente que implementa esse m�todo possa ser inserido no container passado, false caso contr�rio
	 */
	public boolean isInsersaoValida(Container container);
}
