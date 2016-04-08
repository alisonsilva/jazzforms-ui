package br.com.laminarsoft.jazzforms.ui.dialog.deploy;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;

public interface IGerenciamentoDeployment {
	public void recebeInfoDeployments(InfoRetornoDeployments info);
	public void retornoRemocaoFisicaDeployment(InfoRetornoDeployments info);
	public void onRecuperaDeploymentError(Exception e);
}
