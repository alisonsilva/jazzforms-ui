package br.com.laminarsoft.jazzforms.ui.top.dialog.projeto;

import java.util.List;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoPreenchimentoCampos;

@SuppressWarnings("all")
public class PreenchimentoException extends Exception {
	private List<InfoPreenchimentoCampos> lstInfo;
	private String pgNumber;
	
	public PreenchimentoException(List<InfoPreenchimentoCampos> info, String pgNumber) {
		super(info.toString());
		this.lstInfo = info;
		this.pgNumber = pgNumber;
	}
	
	public PreenchimentoException(List<InfoPreenchimentoCampos> info) {
		super(info.toString());
		this.lstInfo = info;
	}	
	
	public List<InfoPreenchimentoCampos> getLstInfo() {
		return this.lstInfo;
	}
	
	public String getPgNumber() {
		return this.pgNumber;
	}
	
}
