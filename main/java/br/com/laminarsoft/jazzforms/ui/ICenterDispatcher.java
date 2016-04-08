package br.com.laminarsoft.jazzforms.ui;

import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.ui.center.CenterController;

public interface ICenterDispatcher {

	public CenterController.ProjetoContainer getProjetoComponentesContainer();
	public void setProjetoComponentesContainer(CenterController.ProjetoContainer projeto);
	public void carregaProjeto(Projeto projeto);
	public void novoProjeto();
	public boolean isProjetoNaoSalvo();
}
