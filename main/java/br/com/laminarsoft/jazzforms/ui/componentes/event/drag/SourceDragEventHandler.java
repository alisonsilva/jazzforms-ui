package br.com.laminarsoft.jazzforms.ui.componentes.event.drag;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.ui.componentes.IDetalhesComponentes;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFFormComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFUIBackComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;


public class SourceDragEventHandler  {

	private Node source;
//	private ImageView dragImageView = new ImageView();
//	private Node dragItem;
	
	public static SourceDragEventHandler getInstance(final Node source, final String identificador) {
		return new SourceDragEventHandler(source, identificador);
	}
	
	private SourceDragEventHandler(final Node source, final String identificador) {
		this.source = source;
		this.source.setOnDragDetected(new EventHandler<MouseEvent>(){

			@Override
            public void handle(MouseEvent event) {
				Dragboard db = source.startDragAndDrop(TransferMode.ANY);
				db.setContent(null);
				if (source instanceof JFBorderPane) {
	                ClipboardContent content = new ClipboardContent();
	                ComponentWrapper wrapper = new ComponentWrapper();
	                wrapper.componente = ((JFBorderPane)source).getBackComponent();
	                String tipoComponente = "";
	                String tipoFormComponente = "";
	                String strTipo = source.getClass().getSimpleName();
	                if (source instanceof IJFFormComponent) {
	                	tipoComponente = strTipo.replace("Form", "");
	                	tipoFormComponente = strTipo;
	                } else {
	                	tipoComponente = strTipo;
	                	tipoFormComponente = strTipo + "Form";
	                }
	                wrapper.tipoComponente = tipoComponente;
	                wrapper.tipocomponenteForm = tipoFormComponente;
	                
	                content.put(TargetDragEventHandler.COMPONENT_DATA_FORMAT, wrapper);
	                db.setContent(content);
                }
				event.consume();
            }
			
		});
		
	
		this.source.setOnDragDone(new EventHandler<DragEvent>(){

			@Override
            public void handle(DragEvent event) {
				if(event.getTransferMode() == TransferMode.MOVE) {

					IJFUIBackComponent pai = ((IDetalhesComponentes)source).getContainerPai();
					pai.removeComponent((JFBorderPane)source);
					
					if (source instanceof IDetalhesComponentes && source instanceof JFBorderPane) {
						JFBorderPane pane = (JFBorderPane)source;
	                    IDetalhesComponentes det = (IDetalhesComponentes) source;
	                    JFBorderPane parentPane = (JFBorderPane) det.getContainerPai();
	                    if (parentPane.getBackComponent() instanceof Container) {
		                    Container cont = (Container) parentPane.getBackComponent();
		                    cont.getItems().remove(pane.getBackComponent());
	                    }
                    }					
				}
				event.consume();
            }
			
		});
	}
	
}
