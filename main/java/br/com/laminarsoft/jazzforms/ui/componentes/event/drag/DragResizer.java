package br.com.laminarsoft.jazzforms.ui.componentes.event.drag;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import br.com.laminarsoft.jazzforms.persistencia.model.senchawrapper.Component;

/**
 * {@link DragResizer} can be used to add mouse listeners to a {@link Region}
 * and make it resizable by the user by clicking and dragging the border in the
 * same way as a window.
 * <p>
 * Only height resizing is currently implemented. Usage:
 * 
 * <pre>
 * DragResizer.makeResizable(myAnchorPane);
 * </pre>
 */
public class DragResizer {

	/**
	 * The margin around the control that a user can click in to start resizing
	 * the region.
	 */
	private static final int RESIZE_MARGIN = 3;

	private final Region region;
	private final Component component;

	private double y;

	private boolean initMinHeight;

	private boolean dragging;

	private DragResizer(Region aRegion, Component component) {
		region = aRegion;
		this.component = component;
	}

	public static void makeResizable(Region region, Component component) {
		final DragResizer resizer = new DragResizer(region, component);

		region.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resizer.mousePressed(event);
//				event.consume();
			}
		});
		region.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resizer.mouseDragged(event);
//				event.consume();
			}
		});
		region.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resizer.mouseOver(event);
//				event.consume();
			}
		});
		region.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resizer.mouseReleased(event);
//				event.consume();
			}
		});
	}

	protected void mouseReleased(MouseEvent event) {
		dragging = false;
		region.setCursor(Cursor.DEFAULT);
	}

	protected void mouseOver(MouseEvent event) {
		if (isInDraggableZone(event) || dragging) {
			region.setCursor(Cursor.S_RESIZE);
		} else {
			region.setCursor(Cursor.DEFAULT);
		}
	}

	protected boolean isInDraggableZone(MouseEvent event) {
		return event.getY() > (region.getHeight() - RESIZE_MARGIN);
	}

	protected void mouseDragged(MouseEvent event) {
		if (!dragging) {
			return;
		}

		double mousey = event.getY();

		double newHeight = region.getMinHeight() + (mousey - y);

		region.setMinHeight(newHeight);
		component.setJfxPreferedHeight(new Double(newHeight));

		y = mousey;
	}

	protected void mousePressed(MouseEvent event) {

		// ignore clicks outside of the draggable margin
		if (!isInDraggableZone(event)) {
			return;
		}

		dragging = true;

		// make sure that the minimum height is set to the current height once,
		// setting a min height that is smaller than the current height will
		// have no effect
		if (!initMinHeight) {
			region.setMinHeight(region.getHeight());
			initMinHeight = true;
		}

		y = event.getY();
	}
}