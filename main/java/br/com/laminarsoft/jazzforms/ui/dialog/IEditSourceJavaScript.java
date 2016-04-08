package br.com.laminarsoft.jazzforms.ui.dialog;

import java.util.List;

import br.com.laminarsoft.jazzforms.persistencia.model.ImplementacaoEvento;

public interface IEditSourceJavaScript {
	public void setupJavaScriptCode(byte[] codigo);
	public void addImplementacao(ImplementacaoEvento implementacao);
	public void removeImplementacao(ImplementacaoEvento implementacao);
	public List<ImplementacaoEvento> getImplementacoes();
}
