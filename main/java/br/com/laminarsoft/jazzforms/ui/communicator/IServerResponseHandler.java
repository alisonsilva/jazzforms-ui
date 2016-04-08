package br.com.laminarsoft.jazzforms.ui.communicator;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;

public interface IServerResponseHandler {
	public void onProjetosRetrieveAll(InfoRetornoProjeto infoProjeto);
	public void onProjetoRetrieve(InfoRetornoProjeto infoProjeto);
	public void onProjetoSVNVersions(InfoRetornoSVN infoSVN);
	public void onEventosPorNomeComponente(InfoRetornoEvento infoEvento);

	public void onProjetoCreation(InfoRetornoProjeto infoProjeto);
	
	public void onServerError(Exception e);
}
