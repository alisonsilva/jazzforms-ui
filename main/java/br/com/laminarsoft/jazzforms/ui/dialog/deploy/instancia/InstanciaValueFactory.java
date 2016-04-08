package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.IValor;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.InstanciaVO;


public class InstanciaValueFactory<T, R> 
		implements Callback<TableColumn.CellDataFeatures<InstanciaVO, IValor>, ObservableValue<IValor>> {

	private String nomeColuna;
	
	public InstanciaValueFactory(String coluna) {
		this.nomeColuna = coluna;
	}
	
	
	@Override
    public ObservableValue<IValor> call(CellDataFeatures<InstanciaVO, IValor> celula) {
	    InstanciaVO instancia = celula.getValue();
	    IValor sel = null;
	    for(IValor valor : instancia.getValoresFormulario()) {
	    	if(valor.getFieldId().equalsIgnoreCase(nomeColuna)) {
	    		sel = valor;
	    		break;
	    	}
	    }
	    if (sel == null) {
	    	for(IValor valor : instancia.getValoresDataview()) {
	    		if(valor.getFieldId() == null || valor.getFieldId().equalsIgnoreCase(nomeColuna)) {
	    			sel = valor;
	    			break;
	    		}
	    	}
	    }

	    return new SimpleObjectProperty<IValor>(sel);
    }
}
