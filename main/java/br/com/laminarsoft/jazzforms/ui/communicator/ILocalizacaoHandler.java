package br.com.laminarsoft.jazzforms.ui.communicator;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoUsuario;

public interface ILocalizacaoHandler {
	public void onRecuperaLocalizacoesUsuario(InfoRetornoUsuario info);
	
	public void onServerError(Exception e);
}
