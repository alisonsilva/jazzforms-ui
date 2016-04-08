package br.com.laminarsoft.jazzforms.ui.dialog.deploy.instancia;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import br.com.laminarsoft.jazzforms.persistencia.model.Campo;
import br.com.laminarsoft.jazzforms.persistencia.model.Linha;


public class DataviewValueFactory<T, R> 
		implements Callback<TableColumn.CellDataFeatures<Linha, Campo>, ObservableValue<Campo>> {

	private String columnName;
	
	public DataviewValueFactory(String columnName) {
		this.columnName = columnName;
	}
	
	
	@Override
    public ObservableValue<Campo> call(CellDataFeatures<Linha, Campo> celula) {
		Linha info = ((Linha)celula.getValue());
		Campo sel = null;
		for (Campo campo : info.getCampos()) {
			if (campo.getNomeCampo().equalsIgnoreCase(columnName)) {
				sel = campo;
			}
		}
		return new SimpleObjectProperty<Campo>(sel);
    }
}
