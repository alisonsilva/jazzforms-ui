package br.com.laminarsoft.jazzforms.ui.communicator;

public interface INomeServicos {
	public static final String PROJETO_SERVICES = "url.projeto.services";
	public static final String PROJETO_SERVICES_FINDALL_RELATIVE = "url.projeto.services.find.all.relative";
	public static final String PROJETO_SERVICES_FINDALL = "url.projeto.services.find.all";
	public static final String PROJETO_SERVICES_FINDALL_PUBLICADOS = "url.projeto.services.find.all.publicados";
	public static final String PROJETO_SERVICES_PERSIST = "url.projeto.services.persist";
	public static final String PROJETO_SERVICES_DEPLOY = "url.projeto.services.deploy";
	public static final String PROJETO_DEPLOYMENT_SERVICES_ALTERAR_DESTINATARIOS = "url.projeto.services.alterdestinatarios";
	public static final String PROJETO_DEPLOYMENT_FINDALL_DEPLOYMENTS_INFO = "url.deployment.services.findalldeploymentsinfo";
	public static final String PROJETO_DEPLOYMENT_FIND_INSTANCIAS_PROJETO_INFO = "url.deployment.services.findinstanciasporprojeto";
	public static final String PROJETO_DEPLOYMENT_FIND_VALORES_DATAVIEW_BYID = "url.deployment.services.findvaloesdataviewbyid";
	public static final String PROJETO_DEPLOYMENT_NOVA_INSTANCIA_PROJETO = "url.deployment.services.novainstanciaprojeto";
	public static final String PROJETO_DEPLOYMENT_NOVA_LINHA_DATAVIEW = "url.deployment.services.novainstancialinhadataview";
	public static final String PROJETO_DEPLOYMENT_REMOVE_LINHA_INSTANCIA_DATAVIEW = "url.deployment.services.removelinhainstanciadataview";
	public static final String PROJETO_DEPLOYMENT_REMOVE_DEPLOYMENT_FISICAMENTE = "url.deployment.services.removedeploymentfisicamente";
	public static final String PROJETO_DEPLOYMENT_REMOVE_INSTANCIA = "url.deployment.services.removeinstancia";
	public static final String PROJETO_DEPLOYMENT_ALTERA_LINHAS_INSTANCIA_DATAVIEW = "url.deployment.services.alteralinhasinstanciadataview";
	public static final String PROJETO_DEPLOYMENT_ALTERA_LINHAS_INSTANCIAS = "url.deployment.services.alteralinhasinstancias";
	public static final String PROJETO_INSTANCIA_USUARIOS_REENVIO_INSTANCIA = "url.deployment.services.usuariosreenvioinstancia";
	public static final String PROJETO_INSTANCIA_REENVIA_INSTANCIA_USUARIO = "url.deployment.services.reenviainstanciausuario";
	public static final String PROJETO_INSTANCIA_CANCELA_REENVIO_INSTANCIA = "url.deployment.services.cancelareenvioinstancia";
	public static final String PROJETO_SERVICES_FIND_BYID = "url.projeto.services.find.byid";
	public static final String PROJETO_SERVICES_FIND_EVENTOS_BYCOMPONENTNAME = "url.projeto.services.findeventos.bynomecomponent";
	public static final String COMPONENT_SERVICES_FIND_TIPOEVENTO_BYCTYPEID = "url.componente.services.findtipoeventos.byctypeid";
	public static final String ICON_SERVICES_FINDALL_WITH_NAME = "url.icons.services.find.all.with.name";
	public static final String PROJETO_ACTIVATEDEACTIVATE_DEPLOYMENT = "url.icons.services.deactivate.deploied";
	public static final String PROJETO_REFRESH_DEPLOYMENT = "url.projeto.refreshdeployment";
	public static final String PROJETO_REMOVE_DEPLOYMENT = "url.projeto.removedeployment";
	public static final String PROJETO_SERVICES_DEPLOY_GETBYID = "url.projeto.recuperadeploymentbyid";
	
	public static final String PROJETO_SERVICES_INSTANCIA_FOTOSPORINSTANCIA = "url.instancia.fotosporinstancia";
	
	public static final String PROJETO_SERVICES_USUARIO_LOCALIZACOES = "url.usuario.localizacoes";
	
	public static final String PROJETO_SERVICES_LDAP_FIND_GRUPO = "url.projeto.ldap.findgrupo";
	public static final String PROJETO_SERVICES_LDAP_FIND_GRUPOS = "url.projeto.ldap.findgrupos";
	public static final String PROJETO_SERVICES_LDAP_ADICIONA_GRUPO = "url.projeto.ldap.adicionagrupo";
	public static final String PROJETO_SERVICES_LDAP_USUARIO_PODE_INSERIR_GRUPO = "url.projeto.ldap.usuariopodeinserirgrupo";
	public static final String PROJETO_SERVICES_LDAP_REMOVE_GRUPO = "url.projeto.ldap.removegrupo";
	public static final String PROJETO_SERVICES_LDAP_GRUPO_DEPLOYMENT = "url.projeto.ldap.grupotemdeployments";
	public static final String PROJETO_SERVICES_LDAP_FIND_USUARIOS = "url.projeto.ldap.recuperausuarios";
	public static final String PROJETO_SERVICES_LDAP_FIND_USUARIO = "url.projeto.ldap.recuperausuario";
	public static final String PROJETO_SERVICES_LDAP_FIND_GRUPOS_USUARIO = "url.projeto.ldap.recuperagruposusuario";
	public static final String PROJETO_SERVICES_LDAP_ADD_USUARIO_GRUPO = "url.projeto.ldap.addusuariotogroup";
	public static final String PROJETO_SERVICES_LDAP_REMOVE_USUARIO_GRUPO = "url.projeto.ldap.removeusuariofromgroup";
	public static final String PROJETO_SERVICES_LDAP_REMOVE_USUARIO = "url.projeto.ldap.removeusuario";
	public static final String PROJETO_SERVICES_LDAP_ADICIONA_USUARIO = "url.projeto.ldap.adicionausuario";
	public static final String PROJETO_SERVICES_LDAP_AUTENTICA_USUARIO = "url.projeto.ldap.autenticausuario";
	
	public static final String PROJETO_SERVICES_MESSAGE_NOVA_USUARIO = "url.projeto.mensagem.novamensagemusuario";
	public static final String PROJETO_SERVICES_MESSAGE_NOVA_GRUPO = "url.projeto.mensagem.novamensagemgrupo";
	public static final String PROJETO_SERVICES_MESSAGE_NOVA_EQUIPAMENTO = "url.projeto.mensagem.novamensagemequipamento";
	public static final String PROJETO_SERVICES_MESSAGE_NOVA_GRUPO_EQUIPAMENTO = "url.projeto.mensagem.novamensagemgrupoequipamento";
	
	public static final String PROJETO_SERVICES_EQUIPAMENTO_EQUIPAMENTOS = "url.projeto.equipamento.findequipamentos";
	public static final String PROJETO_SERVICES_EQUIPAMENTO_GRUPOS_EQUIPAMENTOS = "url.projeto.equipamento.findgruposequipamentos";
	public static final String PROJETO_SERVICES_EQUIPAMENTO_NOVO_GRUPO_EQUIPAMENTO = "url.projeto.equipamento.novogrupoequipamento";
	public static final String PROJETO_SERVICES_EQUIPAMENTO_ALTERA_GRUPO_EQUIPAMENTO = "url.projeto.equipamento.alteragrupoequipamento";
	public static final String PROJETO_SERVICES_EQUIPAMENTO_REMOVE_GRUPO_EQUIPAMENTO = "url.projeto.equipamento.removegrupoequipamento";
	public static final String PROJETO_SERVICES_EQUIPAMENTO_ADICIONA_EQUIPAMENTO_GRUPO = "url.projeto.equipamento.adicionaequipamentogrupo";
	public static final String PROJETO_SERVICES_EQUIPAMENTO_REMOVE_EQUIPAMENTO_GRUPO = "url.projeto.equipamento.removeequipamentogrupo";
	
	public static final String PROJETO_SERVICES_PROCESS_MODEL_FINDALL = "url.processmodel.findall";
	
	public static final String PROJETO_SERVICES_SVN_INFOVERSIONS = "url.svn.infoversions";
	public static final String PROJETO_SERVICES_SVN_CHECKOUT_PROJECT = "url.svn.checkout_project";
	
	public static final String GUVNOR_ROOT = "url.guvnor";
	public static final String GUVNOR_SERVER_BASE_URL = "url.guvnor.base";

}
