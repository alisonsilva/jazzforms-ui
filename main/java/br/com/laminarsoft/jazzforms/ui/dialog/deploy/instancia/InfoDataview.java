package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import br.com.laminarsoft.jazzforms.persistencia.model.Campo;
import br.com.laminarsoft.jazzforms.persistencia.model.Linha;
import br.com.laminarsoft.jazzforms.persistencia.model.ValorDataview;

public class InfoDataview {
	private Map<String, SimpleObjectProperty<Campo>> celulas;
	
	private Linha linhaDataview;
	private ValorDataview valor;
	
	public InfoDataview(ValorDataview valor, Linha linha) {
		celulas = new HashMap<String, SimpleObjectProperty<Campo>>();
		for(Campo campo : linha.getCampos()) {
			celulas.put(campo.getNomeCampo(), new SimpleObjectProperty<Campo>(campo));
		}
		this.linhaDataview = linha;
	}

	public Linha getLinhaDataview() {
		return linhaDataview;
	}

	public void setLinhaDataview(Linha instancia) {
		this.linhaDataview = instancia;
	}

	public Map<String, SimpleObjectProperty<Campo>> getCelulas() {
		return celulas;
	}

	public ValorDataview getValor() {
		return valor;
	}

	public void setValor(ValorDataview valor) {
		this.valor = valor;
	}
	
	
}
