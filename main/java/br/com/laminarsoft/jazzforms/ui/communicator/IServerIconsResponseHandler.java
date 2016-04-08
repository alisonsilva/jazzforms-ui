package br.com.laminarsoft.jazzforms.ui.communicator;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoIcon;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFIconComponent;

public interface IServerIconsResponseHandler {
	public void onIconRetrieve(InfoRetornoIcon infoIcon);
	public void setShowingElement(IJFIconComponent showingElement);
	
	public void onServerError(Exception e);
}
