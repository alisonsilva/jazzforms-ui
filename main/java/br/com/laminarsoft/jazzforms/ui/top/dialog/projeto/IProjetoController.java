package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.center.CenterController;

public interface IProjetoController {
	public static final String LDAP_SERVER = "ldap.server";
	public static final String LDAP_PORT = "ldap.port";
	public static final String LDAP_ROOT_DN = "ldap.root.dn";
	public static final String LDAP_PESSOAS = "ldap.pessoas.ou";
	public static final String LDAP_GRUPOS = "ldap.grupos.ou";
	
	public static final String GRUPO_JAZZFORMS_ADMIN = "ldap.grupo.jazzforms.admin";
	
	public void setProjetoLocal(CenterController.ProjetoContainer projeto);
	public CenterController.ProjetoContainer getProjetoLocal();
	public void setCenterDispathcer(ICenterDispatcher dispatcher);
}
