package br.com.laminarsoft.jazzforms.ui.componentes.event.drag;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Container;
import br.com.laminarsoft.jazzforms.ui.center.handler.DesenhaComponenteHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFFormComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.JFBorderPane;
import br.com.laminarsoft.jazzforms.ui.dialog.DialogFactory;

public class FormTargetDragEventHandler {
	

	public static FormTargetDragEventHandler getInstance(final Node target, final JFBorderPane parent) {
		return new FormTargetDragEventHandler(target, parent);
	}
	
	public static FormTargetDragEventHandler getInstance(final Node target, final JFBorderPane parent, final Node component) {
		return new FormTargetDragEventHandler(target, parent, component);
	}
	
	
	private FormTargetDragEventHandler(final Node target, final JFBorderPane parent) {
		this(target, parent, null);
	}
	
	private FormTargetDragEventHandler(final Node target, final JFBorderPane parent, final Node component) {
		
		target.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				if(event.getGestureSource() != target && event.getDragboard().hasContent(TargetDragEventHandler.COMPONENT_DATA_FORMAT)) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}
				event.consume();
            }			
		});
		
		target.setOnDragEntered(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				if(event.getGestureSource() != target && event.getDragboard().hasContent(TargetDragEventHandler.COMPONENT_DATA_FORMAT)) {
					if (target.getStyleClass().contains(TargetDragEventHandler.STYLE_DRAG_EXITED)) {
	                    target.getStyleClass().remove(TargetDragEventHandler.STYLE_DRAG_EXITED);
                    }
					target.getStyleClass().add(TargetDragEventHandler.STYLE_DRAG_ENTERED);
				}
				event.consume();
            }
		});
		
		target.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				if(target.getStyleClass().contains(TargetDragEventHandler.STYLE_DRAG_ENTERED)) {
					target.getStyleClass().remove(TargetDragEventHandler.STYLE_DRAG_ENTERED);
				}
				target.getStyleClass().add(TargetDragEventHandler.STYLE_DRAG_EXITED);
				event.consume();
            }			
		});
		
		target.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
            public void handle(DragEvent event) {
				DialogFactory.DialogRetVO vo = null;
				if(event.getTransferMode() == TransferMode.MOVE && event.getDragboard().hasContent(TargetDragEventHandler.COMPONENT_DATA_FORMAT)) {
					DesenhaComponenteHandler desenhaHandler = DesenhaComponenteHandler.getInstance();
					if (component == null || !(component instanceof IJFFormComponent)) {
						vo = desenhaHandler.criaCustomComponent(
										(Pane) target,
										(ComponentWrapper) event.getDragboard().getContent(TargetDragEventHandler.COMPONENT_DATA_FORMAT),
										parent, ((Container) parent.getBackComponent()), true, -1, true);
					} else if(component != null && component instanceof IJFFormComponent) {
						Node transTarget = target.getParent();						
						int posicaoTarget = ((VBox)target.getParent()).getChildren().indexOf(target);
						vo = desenhaHandler.criaCustomComponent(
								(Pane) transTarget,
								(ComponentWrapper) event.getDragboard().getContent(TargetDragEventHandler.COMPONENT_DATA_FORMAT),
								parent, ((Container) parent.getBackComponent()), true, posicaoTarget, true);
					}
				}
				event.setDropCompleted(vo != null);
				
				event.consume();
            }			
		});
	}
}
