package br.com.laminarsoft.jazzforms.ui.componentes;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;

public interface IDetalhesComponentes extends IControllerCustomComponent {
	public void setDetalhesTab(Tab tabDetalhes);
	public void setAvancadoTab(Tab tabAvancado);
	public void setPagina(Pagina pagina);
	public void setContainerPai(IJFUIBackComponent containerPai);
	public IJFUIBackComponent getContainerPai();
	public Component getBackComponent();
	public Node getCustomComponent();
}
