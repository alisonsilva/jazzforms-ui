package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.reenvio;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.IRefreshAprsesentacao;

public interface IReenvioInstanciaUsuario {
	public void recebeUsuariosPossiveis(InfoRetornoInstancia info);
	public void recebeResultadoReenvioInstancia(InfoRetornoInstancia info);
	public void setInstanciaId(IRefreshAprsesentacao refresher, Long instanciaId);
	public void onServerError(Exception e);
}
