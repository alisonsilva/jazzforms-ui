package br.com.laminarsoft.jazzforms.ui.componentes;

import javafx.event.ActionEvent;

public interface IControllerDetalhes {
	public void setCustomComponent(IDetalhesComponentes customComponente);
	public void btnConfirmaAction(ActionEvent actionEvent);
	public void btnCancelaAction(ActionEvent actionEvent);
	
}
