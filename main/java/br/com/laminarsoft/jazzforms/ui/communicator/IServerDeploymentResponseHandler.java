package br.com.laminarsoft.jazzforms.ui.communicator;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;

public interface IServerDeploymentResponseHandler {
	public void onDeploymentProjetoRefreshed(InfoRetornoDeployments infoDeployment);
	public void onDeploymentRemoved(InfoRetornoDeployments infoDeployment);
	public void onServerError(Exception e);
}
