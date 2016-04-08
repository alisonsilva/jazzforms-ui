package br.com.laminarsoft.jazzforms.ui.communicator;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocketFactory;

import br.com.laminarsoft.jazzforms.ui.entidade.UIGrupo;
import br.com.laminarsoft.jazzforms.ui.entidade.UIUsuario;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.IProjetoController;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public class LDAPServiceController {

	private static LDAPServiceController LDAP_CONTROLLER;
	
	private LDAPConnection ldapConnection;
	private String basePessoalDn;
	private String baseGruposDn;

	public LDAPServiceController() {
		SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
		try {
			basePessoalDn = PropertiesServiceController.getInstance().getProperty(IProjetoController.LDAP_PESSOAS) + "," +
					PropertiesServiceController.getInstance().getProperty(IProjetoController.LDAP_ROOT_DN);
			baseGruposDn = 	PropertiesServiceController.getInstance().getProperty(IProjetoController.LDAP_GRUPOS) + "," + 
					PropertiesServiceController.getInstance().getProperty(IProjetoController.LDAP_ROOT_DN);
			
			SSLSocketFactory socketFactory = sslUtil.createSSLSocketFactory();
			ldapConnection = new LDAPConnection(socketFactory, 
					PropertiesServiceController.getInstance().getProperty(IProjetoController.LDAP_SERVER), 
					Integer.parseInt(PropertiesServiceController.getInstance().getProperty(IProjetoController.LDAP_PORT)));
		} catch (NumberFormatException e) {
		} catch (LDAPException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {			
		}
	}
	
	public static LDAPServiceController getInstance() {
		if (LDAP_CONTROLLER == null) {
			LDAP_CONTROLLER = new LDAPServiceController();
		}
		return LDAP_CONTROLLER;
	}
	
	public BindResult bind(String login, String senha) throws LDAPException {
		String dn = "uid=" + login + "," + basePessoalDn;
		return ldapConnection.bind(dn, senha);
	}
	
	/**
	 * Recupera o usuário cujo login é passado
	 * @param uid Login do usuário
	 * @return Usuário com suas informações
	 * @throws LDAPSearchException
	 */
	public UIUsuario getUsuario(String uid) throws LDAPSearchException {
		UIUsuario ret = null;
		SearchResult sresult = ldapConnection.search(basePessoalDn, SearchScope.SUB, "(uid=" + uid + ")");
		if (sresult.getEntryCount() > 0) {
			ret = new UIUsuario();
			SearchResultEntry entry = sresult.getSearchEntries().get(0);
			ret.setCn(entry.getAttributeValue("cn"));
			ret.setDn(entry.getDN());
			ret.setUid(uid);
		}
		return ret;
	}
	
	public UIGrupo getGrupo(String grpCn) throws LDAPSearchException {
		UIGrupo ret = null;
		SearchResult sresult = ldapConnection.search(baseGruposDn, SearchScope.SUB, "(cn=" + grpCn + ")");
		if (sresult.getEntryCount() > 0) {
			ret = new UIGrupo();
			SearchResultEntry entry = sresult.getSearchEntries().get(0);
			ret.setCn(grpCn);
			ret.setNome(grpCn);
			ret.setDescription(entry.getAttributeValue("description"));
			String[] usuarios = entry.getAttributeValues("member");
			for (String usrFullName : usuarios) {
				UIUsuario ldapusr = getUsuario(usrFullName.replace("uid=", "").replace("," + basePessoalDn, ""));
				ldapusr.setNome(ldapusr.getCn());
				ret.getUsuarios().add(ldapusr);
			}
		}
		return ret;
	}	
	
	public boolean usuarioPodeInserirGrupo(String uid) throws LDAPSearchException {
		boolean ret = false;
		List<UIGrupo> grupos = getGruposUsuario(uid);
		for(UIGrupo grupo : grupos) {
			if (grupo.getNome().equalsIgnoreCase(PropertiesServiceController.getInstance().getProperty("ldap.grupo.jazzforms.admin"))) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Retorna uma lista com os grupos referentes ao uid do usuário 
	 * @param uid Identificação do usuário (login)
	 * @return Lista com os grupos aos quais esse usuário pertence
	 * @throws LDAPSearchException
	 */
	public List<UIGrupo> getGruposUsuario(String uid) throws LDAPSearchException {
		List<UIGrupo> grupos = new ArrayList<UIGrupo>();
		String memberURL = "(member=uid=" + uid +"," + basePessoalDn  + ")";
		SearchResult searchResult = LDAPServiceController.getInstance().search(baseGruposDn, SearchScope.SUB, memberURL);
		for(SearchResultEntry entry : searchResult.getSearchEntries()) {
			UIGrupo grupo = new UIGrupo();
			grupo.setCn(entry.getAttribute("cn").getValue());
			grupo.setDn(entry.getDN());
			grupo.setNome(grupo.getCn());
			grupo.setDescription((entry.getAttribute("description") != null ? entry.getAttribute("description").getValue() : ""));
			grupos.add(grupo);
		}
		return grupos;
	}
	
	public List<UIUsuario> getTodosUsuarios() throws LDAPException {
		List<UIUsuario> usuarios = new ArrayList<UIUsuario>();
		SearchResult searchResult = ldapConnection.search(basePessoalDn, SearchScope.SUB, "(objectClass=person)");
		for(SearchResultEntry entry : searchResult.getSearchEntries()) {
			UIUsuario usuario = new UIUsuario();
			usuario.setCn(entry.getAttributeValue("cn"));
			usuario.setNome(entry.getAttributeValue("cn"));
			usuario.setUid(entry.getAttributeValue("uid"));
			usuario.setDn(entry.getAttributeValue("uid") + "," + basePessoalDn);
			usuarios.add(usuario);
		}
		return usuarios;
	}
	
	/**
	 * Recupera todos os grupos criados na OU Grupos juntamente com os usuários cadastrados em cada um
	 * @return Lista com os grupos recuperados na OU Grupos
	 * @throws LDAPSearchException
	 */
	public List<UIGrupo> getTodosGrupos() throws LDAPSearchException {
		List<UIGrupo> grupos = new ArrayList<UIGrupo>();
		SearchResult searchResult = ldapConnection.search(baseGruposDn, SearchScope.SUB, "(objectClass=groupOfNames)");
		for(SearchResultEntry entry : searchResult.getSearchEntries()) {
			UIGrupo grupo = new UIGrupo();
			grupo.setNome(entry.getAttributeValue("cn"));
			grupo.setDn(entry.getDN());
			grupo.setDescription((entry.getAttribute("description") != null ? entry.getAttribute("description").getValue() : ""));
			if (grupo.getNome() == null || grupo.getNome().length() == 0) continue;
			String[] dnUsuarios = entry.getAttributeValues("member");
			for(int i = 0; dnUsuarios != null && i < dnUsuarios.length; i++) {
				String dnUsuario = dnUsuarios[i].trim();
				if (dnUsuario.isEmpty()) {
					continue;
				}
				StringTokenizer tok = new StringTokenizer(dnUsuario, ",");
				String cnUsuario = tok.nextToken();
				SearchResult searchResultPessoa = search(basePessoalDn, SearchScope.SUB, "(" + cnUsuario + ")");
				if(searchResultPessoa.getEntryCount() > 0) {
					SearchResultEntry pessoa = searchResultPessoa.getSearchEntries().get(0);
					UIUsuario user = new UIUsuario();
					user.setDn(pessoa.getDN());
					user.setNome(pessoa.getAttributeValue("cn"));
					user.setUid(pessoa.getAttributeValue("uid"));
					grupo.getUsuarios().add(user);
				}
			}
			grupos.add(grupo);
		}		
		return grupos;
	}
	
	
	public void addGroup(String groupName, String groupDescription) throws LDAPException, LDIFException{
		String[] ldifLines = {"dn: cn="+ groupName +"," + baseGruposDn, "changetype: add", "cn:" + groupName, 
				"description:" + groupDescription, "objectClass: groupOfNames", "objectClass: top", "member:", "ou: Grupos"};
		ldapConnection.add(new AddRequest(ldifLines));
	}
	
	public void addUser(String userLogin, String password, String userName, String nomeGrupo) throws LDAPException, LDIFException {
		StringTokenizer tok = new StringTokenizer(userName, " ");
		String sobrenome = "NA";
		while (tok.hasMoreTokens()) {
			sobrenome = tok.nextToken();
		}
		String[] ldifLines = {"dn: uid="+ userLogin +"," + basePessoalDn, "changetype: add", "cn:" + userName, "sn: " + sobrenome,  
				"objectClass: inetOrgPerson", "objectClass: organizationalPerson", "objectClass: person", "objectClass: simpleSecurityObject", "ou: " + nomeGrupo, "uid: " + userLogin, "userPassword: " + password};
		ldapConnection.add(new AddRequest(ldifLines));
		addUserToGroup(userLogin, nomeGrupo);
	}	
	
	public void addUserToGroup(String userLogin, String groupName) throws LDAPException {
		Modification mod = new Modification(ModificationType.ADD, "member", "uid="+ userLogin +"," + basePessoalDn);
		ModifyRequest req = new ModifyRequest("cn="+ groupName +"," + baseGruposDn, mod);
		ldapConnection.modify(req);
	}
	
	public void removeUserFromGroup(String userLogin, String groupName) throws LDAPException {
		Modification mod = new Modification(ModificationType.DELETE, "member", "uid=" + userLogin + "," + basePessoalDn);
		ModifyRequest req = new ModifyRequest("cn=" + groupName + "," + baseGruposDn, mod);
		ldapConnection.modify(req);
		
//		List<UIGrupo> grpUsuario = getGruposUsuario(userLogin);
//		if (grpUsuario.size() == 0) {// se o usuário não estiver mais em nenhum grupo, remove o usuário
//			DeleteRequest delRequest = new DeleteRequest("uid="+ userLogin +"," + basePessoalDn);
//			ldapConnection.delete(delRequest);			
//		}
	}
	
	public void removeGrupo(String groupName) throws LDAPException {
		DeleteRequest delRequest = new DeleteRequest("cn="+ groupName +"," + baseGruposDn);
		ldapConnection.delete(delRequest);
	}
	
	
	public void removeUsuario(String userLogin) throws LDAPException {
		List<UIGrupo> grupos = this.getGruposUsuario(userLogin);
		for(UIGrupo grupo : grupos) {
			removeUserFromGroup(userLogin, grupo.getNome());
		}
		DeleteRequest delRequest = new DeleteRequest("uid="+ userLogin +"," + basePessoalDn);
		ldapConnection.delete(delRequest);
	}
	
	public SearchResult search(String baseDn, SearchScope scope, String criteria) throws LDAPSearchException {
		return ldapConnection.search(baseDn, scope, criteria);
	}
}
