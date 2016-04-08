package br.com.laminarsoft.jazzforms.ui.communicator;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javafx.event.ActionEvent;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.cxf.common.util.Base64Utility;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.ValorDataview;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoBase;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoDeployments;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEquipamento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEvento;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoIcon;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoMensagem;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProcessModel;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProjeto;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoSVN;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoUsuario;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.DataViewWrapperVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.GrupoEquipamentoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.InstanciasVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapUsuarioVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.MensagemVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.ProjetoWrapperVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.RequestVO;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.UsuarioVO;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.IGerenciamentoDeployment;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.IGerenciamentoDataview;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.IGerenciamentoInstancia;
import br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia.reenvio.IReenvioInstanciaUsuario;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.IGrupoEquipamento;
import br.com.laminarsoft.jazzforms.ui.dialog.equipamento.IMantemEquipamento;
import br.com.laminarsoft.jazzforms.ui.dialog.message.IMensagem;
import br.com.laminarsoft.jazzforms.ui.top.dialog.projeto.CarregaProjetoSVNController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;


public class WebServiceController {
	private static WebServiceController CONTROLLER;
	private Client client;
	
	private static final int QTD_MINUTOS = 5;
	
	private String usuario;
	private String senha;
	
	private Long timeStamp;
	private String token;
	
	private WebServiceController() {
		client = Client.create();
		client.setConnectTimeout(120*1000);
		client.setReadTimeout(120*1000);
		
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new  HostnameVerifier() {
			
			@Override
			public boolean verify(String hostName, SSLSession sslSession) {
				if(hostName.equalsIgnoreCase("localhost")) {
					return true;
				}
				return false;
			}
		});
	}

	public static WebServiceController getInstance() {
		if(CONTROLLER  == null) {
			CONTROLLER = new WebServiceController();
		}
		return CONTROLLER;
	}
	
	/**
	 * Chama o serviço de criação de projeto. Esse serviço vai criar um projeto caso esse não exista; caso exista, 
	 * o mesmo será alterado. 
	 * @param projeto O projeto a ser criado/alterado
	 * @return InfoRetornoProjeto Informações sobre o retorno da criação do projeto
	 */
	public InfoRetornoProjeto createProjeto(Projeto projeto) {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			ProjetoWrapperVO prjWrapper = new ProjetoWrapperVO();
			prjWrapper.projeto = projeto;
			prjWrapper.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_PERSIST));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, prjWrapper);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}

	public void createProjeto(final Projeto projeto, final IServerResponseHandler serverResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetoCreation(createProjeto(projeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}
	

	/**
	 * Chama o serviço de publicação de projeto. Esse serviço vai publicar um projeto. 
	 * @param deployment O projeto a ser publicado
	 * @return InfoRetornoProjeto Informações sobre o retorno da publicação do projeto
	 */
	public InfoRetornoProjeto deployProjeto(Projeto deployment) {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			ProjetoWrapperVO prjWrapper = new ProjetoWrapperVO();
			prjWrapper.projeto = deployment;
			prjWrapper.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_DEPLOY));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, prjWrapper);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}

	public void deployProjeto(final Projeto deployment, final IPublicaResponseHandler publicaResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					publicaResponseHandler.onProjetoDeployed(deployment, deployProjeto(deployment));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					publicaResponseHandler.onServerDeploymentError(deployment, e);
				}
			}
		});
		t.start();
	}
	
	
	public InfoRetornoProjeto alteraDestinatarios(Projeto projeto) {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			ProjetoWrapperVO wrapper = new ProjetoWrapperVO();
			wrapper.projeto = projeto;
			wrapper.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_SERVICES_ALTERAR_DESTINATARIOS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, wrapper);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}	
	
	public void alteraDestinatarios(final Projeto projeto, final IPublicaResponseHandler publicaResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					publicaResponseHandler.onProjetoDeployed(projeto, alteraDestinatarios(projeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					publicaResponseHandler.onServerDeploymentError(projeto, e);
				}
			}
		});
		t.start();
	}
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoProjeto findProjetoAll() {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO request = new RequestVO();
			request.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_FINDALL));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, request);
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	private InfoRetornoBase validaAutenticacao() {
		InfoRetornoBase info = null;
		if(!isAbleToSendRequest()) {
			InfoRetornoLdap infoLdap = autenticaUsuario(this.usuario, this.senha);
			if(infoLdap.codigo > 0) {
				info = new InfoRetornoBase();
				info.codigo = infoLdap.codigo;
				info.mensagem = infoLdap.mensagem;
			}
		}
		return info;
	}
	
	public void findProjetoAll(final IServerResponseHandler serverResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetosRetrieveAll(findProjetoAll());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
		
	public InfoRetornoDeployments findInformacoesImplantacoes() {
		InfoRetornoDeployments info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoDeployments();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO req = new RequestVO(null, this.token);
			
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_FINDALL_DEPLOYMENTS_INFO));			
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoDeployments.class, req);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findInformacoesImplantacoes(final IGerenciamentoDeployment serverResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoDeployments(findInformacoesImplantacoes());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaDeploymentError(e);
				}
			}
		});
		t.start();
	}	
	
	
	public InfoRetornoSVN findInformacoesSVNProjeto(String nomeProjeto) {
		InfoRetornoSVN info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoSVN();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			if(nomeProjeto.contains(" ")) {
				nomeProjeto = nomeProjeto.replace(" ", "_i_");
			}
			
			RequestVO request = new RequestVO(new String[]{nomeProjeto}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_SVN_INFOVERSIONS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoSVN.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findInformacoesSVNProjeto(final IServerResponseHandler serverResponseHandler, final String nomeProjeto) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetoSVNVersions(findInformacoesSVNProjeto(nomeProjeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	
	public InfoRetornoUsuario findLocalizacoesUsuario(String loginUsuario) {
		InfoRetornoUsuario info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoUsuario();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			
			RequestVO request = new RequestVO(new String[]{loginUsuario}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_USUARIO_LOCALIZACOES));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoUsuario.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findLocalizacoesUsuario(final ILocalizacaoHandler localizacaoHandler, final String loginUsuario) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					localizacaoHandler.onRecuperaLocalizacoesUsuario(findLocalizacoesUsuario(loginUsuario));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					localizacaoHandler.onServerError(e);
				}
			}
		});
		t.start();
	}		
	
	public InfoRetornoSVN checkoutSVNProjeto(String nomeProjeto, String versao) {
		InfoRetornoSVN info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoSVN();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			if(nomeProjeto.contains(" ")) {
				nomeProjeto = nomeProjeto.replace(" ", "_i_");
			}
			RequestVO request = new RequestVO(new String[]{nomeProjeto, versao}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_SVN_CHECKOUT_PROJECT));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoSVN.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void checkoutSVNProjeto(final CarregaProjetoSVNController serverResponseHandler, final String nomeProjeto, final String versaoProjeto) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetoRecuperado(checkoutSVNProjeto(nomeProjeto, versaoProjeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}		
	
	public InfoRetornoDeployments findInformacoesInstanciasProjeto(Long idProjeto) {
		InfoRetornoDeployments info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoDeployments();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{idProjeto + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_FIND_INSTANCIAS_PROJETO_INFO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoDeployments.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findInformacoesInstanciasProjeto(final IGerenciamentoInstancia serverResponseHandler, final Long idProjeto) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoInstancias(findInformacoesInstanciasProjeto(idProjeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}		

	
	public InfoRetornoInstancia getFotosPorInstancia(Long idInstancia) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{idInstancia + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_INSTANCIA_FOTOSPORINSTANCIA));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void getFotosPorInstancia(final IGerenciamentoInstancia serverResponseHandler, final Long idInstancia) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoFotosInstancia(getFotosPorInstancia(idInstancia));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}		
	
	
	public InfoRetornoInstancia findValoresDataviewPorId(Long dataviewId) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO request = new RequestVO(new String[]{dataviewId + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_FIND_VALORES_DATAVIEW_BYID));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findValoresDataviewPorId(final IGerenciamentoInstancia serverResponseHandler, final Long idProjeto) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoValorDataview(findValoresDataviewPorId(idProjeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}	
	
	
	public InfoRetornoInstancia novaInstanciaProjeto(Long projetoId, String loginUsuario) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO request = new RequestVO(new String[]{projetoId + "", loginUsuario}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_NOVA_INSTANCIA_PROJETO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void novaInstanciaProjeto(final IGerenciamentoInstancia serverResponseHandler, final Long idProjeto, final String loginUsuario) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoValorNovaInstancia(novaInstanciaProjeto(idProjeto, loginUsuario));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}	
	
	
	public InfoRetornoInstancia novaLinhaDataview(Long dataviewId) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO request = new RequestVO(new String[]{dataviewId + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_NOVA_LINHA_DATAVIEW));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void novaLinhaDataview (final IGerenciamentoDataview serverResponseHandler, final Long dataviewId) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoNovaLinhaDataview(novaLinhaDataview(dataviewId));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoInstancia recuperaUsuariosReenvioInstancia(Long instanciaId) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{instanciaId  + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_INSTANCIA_USUARIOS_REENVIO_INSTANCIA));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void recuperaUsuariosReenvioInstancia (final IReenvioInstanciaUsuario serverResponseHandler, final Long instanciaId) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeUsuariosPossiveis(recuperaUsuariosReenvioInstancia(instanciaId));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoInstancia reenviaInstanciaUsuario(Long instanciaId, String loginUsuario, String loginUsuarioAlteracao) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO request = new RequestVO(new String[]{instanciaId + "", loginUsuario, loginUsuarioAlteracao}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_INSTANCIA_REENVIA_INSTANCIA_USUARIO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void reenviaInstanciaUsuario (final IReenvioInstanciaUsuario serverResponseHandler, final Long instanciaId, final String loginUsuario, final String loginUsuarioAlteracao) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeResultadoReenvioInstancia(reenviaInstanciaUsuario(instanciaId, loginUsuario, loginUsuarioAlteracao));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}		
	
	
	public InfoRetornoInstancia cancelaReenvioInstanciaUsuario(Long instanciaId, String loginUsuarioAlteracao) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{instanciaId + "", loginUsuarioAlteracao}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_INSTANCIA_CANCELA_REENVIO_INSTANCIA));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void cancelaReenvioInstanciaUsuario (final IGerenciamentoInstancia serverResponseHandler, final Long instanciaId, final String loginUsuarioAlteracao) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.cancelaReenvioInstanciaSucesso(cancelaReenvioInstanciaUsuario(instanciaId, loginUsuarioAlteracao));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoDeployments removeDeploymentFisico(Long projetoId) {
		InfoRetornoDeployments info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoDeployments();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO request = new RequestVO(new String[]{projetoId + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_REMOVE_DEPLOYMENT_FISICAMENTE));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoDeployments.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	
	
	public void removeDeploymentFisico(final IGerenciamentoDeployment serverResponseHandler, final Long projetoId) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.retornoRemocaoFisicaDeployment(removeDeploymentFisico(projetoId));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaDeploymentError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoInstancia removeLinhaInstanciaDataview(Long linhaId) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO request = new RequestVO(new String[]{linhaId + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_REMOVE_LINHA_INSTANCIA_DATAVIEW));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.delete(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	
	
	public void removeLinhaInstanciaDataview(final IGerenciamentoDataview serverResponseHandler, final Long linhaId) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoRemoveLinhaInstanciaDataview(removeLinhaInstanciaDataview(linhaId));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}

	
	public InfoRetornoInstancia removeInstancia(Long instanciaId) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{instanciaId + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_REMOVE_INSTANCIA));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.delete(InfoRetornoInstancia.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void removeInstancia(final IGerenciamentoInstancia serverResponseHandler, final Long instanciaId) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.removeInstanciaSucesso(removeInstancia(instanciaId));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}
	
	
	@SuppressWarnings("all")
	public InfoRetornoInstancia alteraValoresDataview(ValorDataview valorDataview) {
		InfoRetornoInstancia info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			
			DataViewWrapperVO wrapper = new DataViewWrapperVO();
			wrapper.token = this.token;
			wrapper.valorDataview = valorDataview;
			
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_ALTERA_LINHAS_INSTANCIA_DATAVIEW));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, wrapper);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void alteraValoresDataview(final IGerenciamentoDataview serverResponseHandler, final ValorDataview valorDataview, final ActionEvent action) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.recebeInfoAlteraInstanciaDataview(alteraValoresDataview(valorDataview), action);
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}	
	
	@SuppressWarnings("all")
	public InfoRetornoInstancia alterarValoresInstancias(InstanciasVO insts) {
		InfoRetornoInstancia info = null;
		try{
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoInstancia();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			insts.token = this.token;
			
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_DEPLOYMENT_ALTERA_LINHAS_INSTANCIAS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoInstancia.class, insts);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void alterarValoresInstancias(final IGerenciamentoInstancia serverResponseHandler, final InstanciasVO instancias, final ActionEvent action) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.salvarInstanciaSucesso(alterarValoresInstancias(instancias), action);
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onRecuperaInstanciasError(e);
				}
			}
		});
		t.start();
	}		
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoProjeto deactivateDeployedProject(final Long projectId, final Boolean status, final String usuario) {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{projectId + "", status + "", usuario}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_ACTIVATEDEACTIVATE_DEPLOYMENT));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void deactivateDeployedProject(final IServerResponseHandler serverResponseHandler, final Long projectId, final Boolean status, final String usuario) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetoRetrieve(deactivateDeployedProject(projectId, status, usuario));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoDeployments getDeploymentById(Long deploymentId) {
		InfoRetornoDeployments info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoDeployments();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{deploymentId +""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_DEPLOY_GETBYID));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoDeployments.class, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
		return info;
	}
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoDeployments changeDeploymentProject(final Long deploymentId, final String login) {
		InfoRetornoDeployments info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoDeployments();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			
			RequestVO req = new RequestVO(new String[]{deploymentId + "", login}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_REFRESH_DEPLOYMENT));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoDeployments.class, req);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void changeDeploymentProject(final IServerDeploymentResponseHandler serverResponseHandler, final Long deploymentId, final String login) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onDeploymentProjetoRefreshed(changeDeploymentProject(deploymentId, login));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}		

	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoDeployments removeDeploymentProject(final Long deploymentId, final String login) {
		InfoRetornoDeployments info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoDeployments();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO req = new RequestVO(new String[]{deploymentId + "", login}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_REMOVE_DEPLOYMENT));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoDeployments.class, req);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void removeDeploymentProject(final IServerDeploymentResponseHandler serverResponseHandler, final Long deploymentId, final String login) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onDeploymentRemoved(removeDeploymentProject(deploymentId, login));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}		
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoProjeto findProjetosPublicados() {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO req = new RequestVO(null, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_FINDALL_PUBLICADOS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, req);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findProjetosPublicados(final IServerResponseHandler serverResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetosRetrieveAll(findProjetosPublicados());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoProjeto findProjetoById(long idProjeto) {
		InfoRetornoProjeto info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProjeto();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{idProjeto + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_FIND_BYID ));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProjeto.class, request);
			if (info.codigo > 1) {
				throw new Exception(info.mensagem);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	public void findProjetoById(final IServerResponseHandler serverResponseHandler, final long idProjeto) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onProjetoRetrieve(findProjetoById(idProjeto));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	
	
	public InfoRetornoEvento findTiposEventoByCTId(Long ctypeId) {
		InfoRetornoEvento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEvento();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{ctypeId + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.COMPONENT_SERVICES_FIND_TIPOEVENTO_BYCTYPEID));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEvento.class, request);
		} catch (Throwable e) {
		}		
		return info;
		
	}
	
	public InfoRetornoEvento findEventosByComponentName(String name) {
		InfoRetornoEvento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEvento();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{name}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_FIND_EVENTOS_BYCOMPONENTNAME));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEvento.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void findEventosByComponentName(final IServerResponseHandler serverResponseHandler, final String nomeComponent) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverResponseHandler.onEventosPorNomeComponente(findEventosByComponentName(nomeComponent.toUpperCase()));
				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage() != null && e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					serverResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoProcessModel getPackages() {
		InfoRetornoProcessModel info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoProcessModel();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_PROCESS_MODEL_FINDALL));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoProcessModel.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void getPackages(final IGuvnorResponseHandler handler) {
		Thread t = new Thread(new Runnable(){
			public void run() {
				try {
					handler.receivePackages(getPackages());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					handler.onServerError(e);
				}
			}
		});
		t.start();
	}
	
	public Document<Feed> getGuvnorPackages() {
		Document<Feed> doc = null;
		
		try {
			URL url = new URL(PropertiesServiceController.getInstance().getProperty(INomeServicos.GUVNOR_ROOT) + "/packages");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + Base64Utility.encode(("admin:admin".getBytes())));
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
			connection.connect();
			
			InputStream in = connection.getInputStream();
			doc = Abdera.getInstance().getParser().parse(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return doc;
	}
	
	public void getGuvnorPackages(final IGuvnorResponseHandler handler) {
		Thread t = new Thread(new Runnable(){
			public void run() {
				try {
					handler.receivePackages(getPackages());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					handler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	
	public Document<Feed> getPackageAssets(String packageHref) {
		Document<Feed> doc = null;
		
		try {
			URL url = new URL(packageHref + "/assets");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + Base64Utility.encode(("admin:admin".getBytes())));
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
			connection.connect();
			
			InputStream in = connection.getInputStream();
			doc = Abdera.getInstance().getParser().parse(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return doc;
	}
	
	public void getPackageAssets(final String packageHref, final IGuvnorResponseHandler handler) {
		Thread t = new Thread(new Runnable(){
			public void run() {
				try {
					handler.receivePackageAssets(getPackageAssets(packageHref));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					handler.onServerError(e);
				}
			}
		});
		t.start();
	}
	
	
	public InfoRetornoIcon findIconsWithName() {
		InfoRetornoIcon info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoIcon();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO request = new RequestVO(new String[]{}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.ICON_SERVICES_FINDALL_WITH_NAME));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoIcon.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void findIconsWithName(final IServerIconsResponseHandler iconsResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					iconsResponseHandler.onIconRetrieve(findIconsWithName());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					iconsResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	
	/*****************************************************************************************
	 * 
	 * Manipulação de equipamentos
	 * 
	 *****************************************************************************************/
	
	public InfoRetornoEquipamento findEquipamentos() {
		InfoRetornoEquipamento info = null;
		
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_EQUIPAMENTOS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEquipamento.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void findEquipamentos(final IMantemEquipamento equipamentoResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.recuperaEquipamnetosSuccess(findEquipamentos());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.recueraInfoError(e);
				}
			}
		});
		t.start();
	}
	
	public InfoRetornoEquipamento findGruposEquipamentos() {
		InfoRetornoEquipamento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo;
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			RequestVO request = new RequestVO(new String[]{}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_GRUPOS_EQUIPAMENTOS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEquipamento.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void findGruposEquipamentos(final IMantemEquipamento equipamentoResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.recuperaGruposEquipamentosSucesso(findGruposEquipamentos());
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.recueraInfoError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoEquipamento novoGrupoEquipamento(GrupoEquipamentoVO grupoEquipamento) {
		InfoRetornoEquipamento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			grupoEquipamento.token = this.token;			
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_NOVO_GRUPO_EQUIPAMENTO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEquipamento.class, grupoEquipamento);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void novoGrupoEquipamento(final IGrupoEquipamento equipamentoResponseHandler, final GrupoEquipamentoVO grupoEquipamento) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.manipulacaoGrupoEquipamentoSucesso(novoGrupoEquipamento(grupoEquipamento));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.manipulacaoGrupoEquipamentoErro(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoEquipamento alteraGrupoEquipamento(GrupoEquipamentoVO grupoEquipamento) {
		InfoRetornoEquipamento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			grupoEquipamento.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_ALTERA_GRUPO_EQUIPAMENTO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEquipamento.class, grupoEquipamento);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void alteraGrupoEquipamento(final IGrupoEquipamento equipamentoResponseHandler, final GrupoEquipamentoVO grupoEquipamento) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.manipulacaoGrupoEquipamentoSucesso(alteraGrupoEquipamento(grupoEquipamento));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.manipulacaoGrupoEquipamentoErro(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoEquipamento adicionarEquipamentoAoGrupo(GrupoEquipamentoVO grupoEquipamento) {
		InfoRetornoEquipamento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}
			grupoEquipamento.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_ADICIONA_EQUIPAMENTO_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEquipamento.class, grupoEquipamento);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void adicionarEquipamentoAoGrupo(final IMantemEquipamento equipamentoResponseHandler, final GrupoEquipamentoVO grupoEquipamento) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.removeGrupoEquipamentoSucesso(adicionarEquipamentoAoGrupo(grupoEquipamento));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.recueraInfoError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoEquipamento removerEquipamentoDoGrupo(GrupoEquipamentoVO grupoEquipamento) {
		InfoRetornoEquipamento info = null;
		
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			grupoEquipamento.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_REMOVE_EQUIPAMENTO_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoEquipamento.class, grupoEquipamento);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void removerEquipamentoDoGrupo(final IMantemEquipamento equipamentoResponseHandler, final GrupoEquipamentoVO grupoEquipamento) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.removeGrupoEquipamentoSucesso(removerEquipamentoDoGrupo(grupoEquipamento));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.recueraInfoError(e);
				}
			}
		});
		t.start();
	}	
	public InfoRetornoEquipamento removeGrupoEquipamento(Long idGrupoEquipamento) {
		InfoRetornoEquipamento info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoEquipamento();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			RequestVO request = new RequestVO(new String[]{idGrupoEquipamento + ""}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_EQUIPAMENTO_REMOVE_GRUPO_EQUIPAMENTO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.delete(InfoRetornoEquipamento.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void removeGrupoEquipamento(final IMantemEquipamento equipamentoResponseHandler, final Long idGrupoEquipamento) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					equipamentoResponseHandler.removeGrupoEquipamentoSucesso(removeGrupoEquipamento(idGrupoEquipamento));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					equipamentoResponseHandler.recueraInfoError(e);
				}
			}
		});
		t.start();
	}	
	
	/*****************************************************************************************
	 *  
	 *  Manipulação mensagens
	 * 
	 *****************************************************************************************/

	public InfoRetornoMensagem novaMensagemUsuario(MensagemVO mensagem) {
		InfoRetornoMensagem info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoMensagem();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			mensagem.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_MESSAGE_NOVA_USUARIO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoMensagem.class, mensagem);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void novaMensagemUsuario(final IMensagem messageHandler, final MensagemVO mensagem) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					messageHandler.mensagemEnviadaSucesso(novaMensagemUsuario(mensagem));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					messageHandler.mensagemEnviadaError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoMensagem novaMensagemGrupo(MensagemVO mensagem) {
		InfoRetornoMensagem info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoMensagem();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			mensagem.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_MESSAGE_NOVA_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoMensagem.class, mensagem);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void novaMensagemGrupo(final IMensagem messageHandler, final MensagemVO mensagem) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					messageHandler.mensagemEnviadaSucesso(novaMensagemGrupo(mensagem));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					messageHandler.mensagemEnviadaError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoMensagem novaMensagemEquipamento(MensagemVO mensagem) {
		InfoRetornoMensagem info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoMensagem();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}	
			mensagem.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_MESSAGE_NOVA_EQUIPAMENTO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoMensagem.class, mensagem);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void novaMensagemEquipamento(final IMensagem messageHandler, final MensagemVO mensagem) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					messageHandler.mensagemEnviadaSucesso(novaMensagemEquipamento(mensagem));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					messageHandler.mensagemEnviadaError(e);
				}
			}
		});
		t.start();
	}	
	
	public InfoRetornoMensagem novaMensagemGrupoEquipamento(MensagemVO mensagem) {
		InfoRetornoMensagem info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoMensagem();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}			
			mensagem.token = this.token;
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_MESSAGE_NOVA_GRUPO_EQUIPAMENTO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoMensagem.class, mensagem);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void novaMensagemGrupoEquipamento(final IMensagem messageHandler, final MensagemVO mensagem) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					messageHandler.mensagemEnviadaSucesso(novaMensagemGrupoEquipamento(mensagem));
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					messageHandler.mensagemEnviadaError(e);
				}
			}
		});
		t.start();
	}		
	
    /*****************************************************************************************
     * 
     * Manipulação de LDAP
     * 
     *****************************************************************************************/	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap getGrupo(String nomeGrupo) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{nomeGrupo}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_FIND_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap adicionaUsuarioAoGrupo(String uid, String nomeGrupo) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{uid, nomeGrupo}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_ADD_USUARIO_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap removeUsuarioDoGrupo(String uid, String nomeGrupo) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{uid, nomeGrupo}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_REMOVE_USUARIO_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.delete(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}	

	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap removeUsuario(String uid) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{uid}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_REMOVE_USUARIO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.delete(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}		
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap getUsuario(String uid) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{uid}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_FIND_USUARIO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap getGruposUsuario(String uid) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{uid}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_FIND_GRUPOS_USUARIO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}		
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap usuarioPodeInserirGrupo(String uid) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{uid}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_USUARIO_PODE_INSERIR_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}
	
	
	public InfoRetornoLdap getUsuarios() {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_FIND_USUARIOS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void getUsuarios(final ILDAPResponseHandler iconsResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					iconsResponseHandler.receiveUsers(getUsuarios().usuarios);
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					iconsResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}
	
	public InfoRetornoLdap getGrupos() {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}				
			RequestVO request = new RequestVO(new String[]{}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_FIND_GRUPOS));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Throwable e) {
		}		
		return info;
	}
	
	public void getGrupos(final ILDAPResponseHandler iconsResponseHandler) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					iconsResponseHandler.receiveGroups(getGrupos().grupos);
				} catch (Exception e) {
					if (e.getMessage().contains("401")) {
						e = new Exception("Não autorizado a realizar essa operação");
					}
					iconsResponseHandler.onServerError(e);
				}
			}
		});
		t.start();
	}	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap adicionaGrupo(LdapGrupoVO grupo) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}
			grupo.setToken(this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_ADICIONA_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, grupo);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap adicionaUsuario(LdapUsuarioVO usuario) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}
			usuario.setToken(this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_ADICIONA_USUARIO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, usuario);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}		
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap grupoTemDeployment(String nomeGrupo) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO request = new RequestVO(new String[]{nomeGrupo}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_GRUPO_DEPLOYMENT));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap removeGrupo(String nomeGrupo) {
		InfoRetornoLdap info = null;
		try {
			InfoRetornoBase infoBase = validaAutenticacao();
			if (infoBase != null) {
				info = new InfoRetornoLdap();
				info.codigo = infoBase.codigo; 
				info.mensagem = infoBase.mensagem;
				return info;
			}
			RequestVO request = new RequestVO(new String[]{nomeGrupo}, this.token);
			WebResource webResource = client.resource(PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_REMOVE_GRUPO));
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.delete(InfoRetornoLdap.class, request);
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			throw new RuntimeException(e);
		}		
		return info;
	}	
	
	@SuppressWarnings({"unchecked", "all"})
	public InfoRetornoLdap autenticaUsuario(String nomeUsuario, String senha) {
		InfoRetornoLdap info = null;
		try {
			UsuarioVO usrvo = new UsuarioVO();
			usrvo.lgn = nomeUsuario;
			usrvo.sn = senha;
			String caminho = PropertiesServiceController.getInstance().getProperty(INomeServicos.PROJETO_SERVICES_LDAP_AUTENTICA_USUARIO);
			WebResource webResource = client.resource(caminho);
			webResource.accept(MediaType.APPLICATION_XML);
			info = webResource.post(InfoRetornoLdap.class, usrvo);
			if (info != null && info.codigo == 0) {
				this.timeStamp = System.currentTimeMillis();
				this.token = info.token;
				this.usuario = nomeUsuario;
				this.senha = senha;
			}
		} catch (Exception e) {
			if (e.getMessage().contains("401")) {
				e = new Exception("Não autorizado a realizar essa operação");
			}
			e.printStackTrace();
			throw new RuntimeException(e);
		}		
		return info;
	}		
	
	/*****************************************************************************************/
	
	private boolean isAbleToSendRequest() {
		Date dt = new Date(this.timeStamp);
		Date dtNow = new Date();
		long difDuration = dtNow.getTime() - dt.getTime();
		long diffInMinuts = TimeUnit.MILLISECONDS.toMinutes(difDuration);
		return diffInMinuts < QTD_MINUTOS;
	}
	
	public KnowledgeBase  getSource(String sourceURL) {
		String content = null;
		KnowledgeBase kbase = null;
		try {
			WebResource webResource = client.resource(sourceURL);
			client.addFilter(new HTTPBasicAuthFilter("admin", "admin"));
			webResource.accept(MediaType.TEXT_PLAIN);
			content = webResource.get(String.class);
			if (content.getBytes() != null && content.getBytes().length > 0) {
	            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	            kbuilder.add(ResourceFactory.newByteArrayResource(content.getBytes()), ResourceType.BPMN2);
	            kbase = kbuilder.newKnowledgeBase();
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return kbase;
	}

}
