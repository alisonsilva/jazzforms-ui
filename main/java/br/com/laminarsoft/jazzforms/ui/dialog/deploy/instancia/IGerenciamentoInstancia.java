package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import javafx.event.ActionEvent;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;

public interface IGerenciamentoInstancia {
	public void recebeInfoInstancias(InfoRetornoDeployments info);
	public void recebeInfoValorDataview(InfoRetornoInstancia info);
	public void recebeInfoValorNovaInstancia(InfoRetornoInstancia info);
	public void recebeInfoFotosInstancia(InfoRetornoInstancia info);
	public void salvarInstanciaSucesso(InfoRetornoInstancia info, ActionEvent actionEvent);
	public void cancelaReenvioInstanciaSucesso(InfoRetornoInstancia info);
	public void removeInstanciaSucesso(InfoRetornoInstancia info);
	public void onRecuperaInstanciasError(Exception e);
}
