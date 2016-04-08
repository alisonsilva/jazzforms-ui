package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.IValor;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.InstanciaVO;

public class InfoInstancia {
	private Map<String, SimpleObjectProperty<IValor>> celulas;
	
	private InstanciaVO instancia;
	
	public InfoInstancia(InstanciaVO instancia) {
		celulas = new HashMap<String, SimpleObjectProperty<IValor>>();
		for(IValor frmItemVo : instancia.getValoresFormulario()) {
			SimpleObjectProperty<IValor> sobjectp = new SimpleObjectProperty<IValor>(frmItemVo);
			celulas.put(frmItemVo.getFieldId(), sobjectp);
		}
		for(IValor dtvVo : instancia.getValoresDataview()) {
			SimpleObjectProperty<IValor> dtvobject = new SimpleObjectProperty<IValor>(dtvVo);
			celulas.put(dtvVo.getFieldId(), dtvobject);
		}
		this.instancia = instancia;
	}

	public InstanciaVO getProjetoImplantado() {
		return instancia;
	}

	public void setProjetoImplantado(InstanciaVO instancia) {
		this.instancia = instancia;
	}

	public Map<String, SimpleObjectProperty<IValor>> getCelulas() {
		return celulas;
	}
}
