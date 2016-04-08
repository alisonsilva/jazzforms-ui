package br.com.laminarsoft.jazzforms.ui;

import br.com.laminarsoft.jazzforms.ui.entidade.UIUsuario;

public interface IJFSystemControll {

	public UIUsuario getUsuarioLogado();
	public void setUsuarioLogado(UIUsuario usuario);
	public void finalizarAplicacao();
}
