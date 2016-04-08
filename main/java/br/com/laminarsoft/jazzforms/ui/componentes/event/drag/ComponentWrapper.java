package br.com.laminarsoft.jazzforms.ui.componentes.event.drag;

import java.io.Serializable;

import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;

public class ComponentWrapper implements Serializable{
	public static final long serialVersionUID = 1111l;
	
	public Component componente;
	public String tipoComponente;
	public String tipocomponenteForm;

}
