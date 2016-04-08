package br.com.laminarsoft.jazzforms.ui.communicator;

import java.util.List;

import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapUsuarioVO;

public interface ILDAPResponseHandler {
	public void receiveUsers(List<LdapUsuarioVO> usuarios);
	public void receiveGroups(List<LdapGrupoVO> grupos);
	
	public void onServerError(Exception e);
}
