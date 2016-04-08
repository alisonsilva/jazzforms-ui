package br.com.laminarsoft.jazzforms.ui.dialog.equipamento;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEquipamento;

public interface IGrupoEquipamento {	
	public void manipulacaoGrupoEquipamentoSucesso(InfoRetornoEquipamento info);
	public void manipulacaoGrupoEquipamentoErro(Exception excp);
	
	public void setCallbackAtualizacao(IMantemEquipamento callback);
}
