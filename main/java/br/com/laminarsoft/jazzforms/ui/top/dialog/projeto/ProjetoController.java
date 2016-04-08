package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;

import br.com.laminarsoft.jazzforms.persistencia.model.Grupo;
import br.com.laminarsoft.jazzforms.persistencia.model.Historico;
import br.com.laminarsoft.jazzforms.persistencia.model.Pagina;
import br.com.laminarsoft.jazzforms.persistencia.model.Projeto;
import br.com.laminarsoft.jazzforms.persistencia.model.Usuario;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoPreenchimentoCampos;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoLdap;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LdapGrupoVO;
import br.com.laminarsoft.jazzforms.ui.ICenterDispatcher;
import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController;
import br.com.laminarsoft.jazzforms.ui.center.CenterController.ProjetoContainer;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;

public abstract class ProjetoController implements IProjetoController{

	protected CenterController.ProjetoContainer projetoLocal;
	
	
	
	@Override
	public void setProjetoLocal(ProjetoContainer projeto) {
		this.projetoLocal = projeto;
	}



	@Override
	public ProjetoContainer getProjetoLocal() {
		return this.projetoLocal;
	}



	@Override
	public abstract void setCenterDispathcer(ICenterDispatcher dispatcher);

	protected void limpaAtualizacaoPaginas() {
		for(CenterController.PaginaContainer paginaLocal : projetoLocal.paginas) {
			br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina pag = paginaLocal.paginaAtual;
			pag.setAtualizada(false);
		}
	}
	

	protected Projeto parseCriacaoProjeto(String msgHistorico) throws PreenchimentoException {
		Projeto projeto = new Projeto();
		projeto.setId((projetoLocal.projetoId != null && projetoLocal.projetoId > 0 ? projetoLocal.projetoId : 0l));
		projeto.setNome(projetoLocal.nome);
		projeto.setAplicacao(projetoLocal.aplicacao);
		projeto.setDescricao(projetoLocal.descricao);
		projeto.setVersionControllDesc(projetoLocal.versionControlDescription);
		projeto.getPaginas().clear();
		int pgNum = 1;
		for (CenterController.PaginaContainer paginaLocal : projetoLocal.paginas) {
			Pagina pag = paginaLocal.paginaAtual.getPaginaModel();
			pag.setProjeto(projeto);
			projeto.getPaginas().add(pag);
			if(StringUtils.isEmpty(pag.getNome()) || pag.getNome().contains(" ")) {
				List<InfoPreenchimentoCampos> lstPr = new ArrayList<InfoPreenchimentoCampos>();
				InfoPreenchimentoCampos info = new InfoPreenchimentoCampos();
				info.nomeCampo = "Nome";
				info.mensagem = "O campo \"Nome\" da página deve ser preenchido";
				lstPr.add(info);
				throw new PreenchimentoException(lstPr, String.valueOf(pgNum));
			}
			try {
				validaPreenchimentoContainer(pag.getContainer());
			} catch (PreenchimentoException e) {
				throw new PreenchimentoException(e.getLstInfo(), String.valueOf(pgNum));
			}
			pgNum++;
		}
		
		Historico historico = new Historico();
		historico.setDhAlteracao(new Date());
		historico.setDescricao(msgHistorico);
		Usuario usuario = new Usuario();
		usuario.setLogin(JazzForms.USUARIO_LOGADO.getUid());
		usuario.setNome(JazzForms.USUARIO_LOGADO.getCn());
		InfoRetornoLdap info = WebServiceController.getInstance().getGruposUsuario(usuario.getLogin());
		if(info.codigo == 0) {
			List<LdapGrupoVO> grupos = info.grupos;
			for(LdapGrupoVO grupo : grupos) {
				Grupo pGrupo = new Grupo();
				pGrupo.setNome(grupo.getCn());
				usuario.getGrupos().add(pGrupo);
			}
		}
		
		historico.setUsuario(usuario);
		
		projeto.getHistoricos().add(historico);
		return projeto;
	}	
	
	private void validaPreenchimentoContainer(Component component) throws PreenchimentoException {
		List<InfoPreenchimentoCampos> lstInfo = component.isPreenchimentoValido();
		if (lstInfo.size() > 0) {
			throw new PreenchimentoException(lstInfo);
		} else if(component instanceof Container) {
			Container cnt = (Container)component;
			for(Component comp : cnt.getItems()) {
				validaPreenchimentoContainer(comp);
			}
		}
	}
}
