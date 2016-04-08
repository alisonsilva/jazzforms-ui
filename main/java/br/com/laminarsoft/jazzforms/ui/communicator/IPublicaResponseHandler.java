package br.com.laminarsoft.jazzforms.ui.communicator;

import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;

public interface IPublicaResponseHandler {
	public void onServerDeploymentError(Projeto projeto, Exception e);
	public void onProjetoDeployed(Projeto projeto, InfoRetornoProjeto infoProjeto);
}
