package br.com.laminarsoft.jazzforms.ui.dialog;

import br.com.laminarsoft.jazzforms.ui.IJFSystemControll;
import br.com.laminarsoft.jazzforms.ui.entidade.UIUsuario;


public interface ILDAPLoginResponseHandler {

	public void receiveUserByLogin(UIUsuario usuario);
	public void onServerError(Exception e);
	public void setApplicationDispatcher(IJFSystemControll controller);
}
