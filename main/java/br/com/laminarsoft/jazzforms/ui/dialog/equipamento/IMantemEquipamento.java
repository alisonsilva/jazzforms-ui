package br.com.laminarsoft.jazzforms.ui.dialog.equipamento;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoEquipamento;

public interface IMantemEquipamento {
	public void recuperaEquipamnetosSuccess(InfoRetornoEquipamento info);
	public void recuperaGruposEquipamentosSucesso(InfoRetornoEquipamento info);
	public void removeGrupoEquipamentoSucesso(InfoRetornoEquipamento info);
	public void adicionaEquipamentoGrupoSucesso(InfoRetornoEquipamento info);
	
	public void recueraInfoError(Exception e);
	
	public void atualizaListagemEquipamentos();
}
