package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import javafx.event.ActionEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;

public interface IGerenciamentoDataview {
	public void recebeInfoRemoveLinhaInstanciaDataview(InfoRetornoInstancia info);
	public void recebeInfoAlteraInstanciaDataview(InfoRetornoInstancia info, ActionEvent action);
	public void recebeInfoNovaLinhaDataview(InfoRetornoInstancia info);
	public void onRecuperaInstanciasError(Exception e);
}
