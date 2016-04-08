package br.com.laminarsoft.jazzforms.ui.center;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

import br.com.laminarsoft.jazzforms.ui.JazzForms;
import br.com.laminarsoft.jazzforms.ui.center.CenterController.PaginaContainer;
import br.com.laminarsoft.jazzforms.ui.center.handler.DesenhaComponenteHandler;
import br.com.laminarsoft.jazzforms.ui.center.handler.IDesenhaComponente;
import br.com.laminarsoft.jazzforms.ui.componentes.IJFSelectedComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.IJazzFormsUIComponent;
import br.com.laminarsoft.jazzforms.ui.componentes.RootController;
import br.com.laminarsoft.jazzforms.ui.componentes.pagina.Pagina;

@SuppressWarnings("all")
public class MouseClickEventsCanvas implements EventHandler<MouseEvent>{

	private List<IJFSelectedComponent> focuseds;
	private Pagina paginaRect;
	private Tab detalhesTab;
	private Tab avancadoTab;
	private List<ISelectedChangeListener> selChangeListeners;
	private RootController raizDetalhes;
	private RootController raizAvancado;
	private PaginaContainer paginaAtualContainer;
	private CenterController controller;
	
	@Inject @IDesenhaComponente private DesenhaComponenteHandler desenhaComponente;
	
	public MouseClickEventsCanvas(Pagina rect, PaginaContainer paginaAtualContainer, Tab detailsObjectsTab, Tab avancadoObjectsTab, CenterController controller){
		this.paginaRect = rect;
		this.detalhesTab = detailsObjectsTab;
		this.avancadoTab = avancadoObjectsTab;
		this.paginaAtualContainer = paginaAtualContainer;
		this.controller = controller;
		selChangeListeners = new ArrayList<ISelectedChangeListener>();
		focuseds = new ArrayList<IJFSelectedComponent>();		
	}
	
	public void addSelectChangeListener(ISelectedChangeListener listener) {
		selChangeListeners.add(listener);
	}
	
	public void removeSelectChangeListener(ISelectedChangeListener listener) {
		selChangeListeners.remove(listener);
	}
	
	
	
	public Tab getDetalhesTab() {
		return detalhesTab;
	}

	public void setDetalhesTab(Tab detalhesTab) {
		this.detalhesTab = detalhesTab;
	}

	public Tab getAvancadoTab() {
		return avancadoTab;
	}

	public void setAvancadoTab(Tab avancadoTab) {
		this.avancadoTab = avancadoTab;
	}

	@Override
	public void handle(MouseEvent evt) {
		
		if(evt.getButton() == MouseButton.SECONDARY) {
			evt.consume();
			return;
		}
		
		boolean bldesenhaComponente = false;
		Node origem =  (Node)evt.getSource();
		
		if (origem instanceof Pagina){
			Pagina pg = (Pagina)origem;
			bldesenhaComponente = pg.getComponenteDesenho() > 0;
			if(bldesenhaComponente) {
				if (desenhaComponente == null) {
					desenhaComponente = DesenhaComponenteHandler.getInstance();
				}
				desenhaComponente.setDetalhesTab(detalhesTab);
				desenhaComponente.setAvancadoTab(avancadoTab);
				desenhaComponente.desenhaComponente(pg, pg.getPaginaModel().getContainer(), pg);
				pg.setAtualizada(true);
			}
		}
		
		
		if (!bldesenhaComponente) {
			
			if (evt.isControlDown() && focuseds.contains(origem) && origem instanceof Group) {
				Group org = (Group)origem;
				Node ndOrg = org.getChildren().get(0);
//				((IComponenteJazzForms)ndOrg).setUnfocusedStyle();

				raizDetalhes = null;
				raizAvancado = null;
				detalhesTab.setContent(null);
				avancadoTab.setContent(null);
				paginaRect.clearSelectedNodes();
				
				focuseds.remove(origem);
			} else if(evt.isControlDown() && !focuseds.contains(origem) && origem instanceof Group) {
				Group org = (Group)origem;
				Node ndOrg = org.getChildren().get(0);
//				((IComponenteJazzForms)ndOrg).setFocusedStyle();
				focuseds.add((IJFSelectedComponent)origem);
				
				raizDetalhes = null;
				raizAvancado = null;
				detalhesTab.setContent(null);
				avancadoTab.setContent(null);
				paginaRect.addAllSelectedNodes(focuseds);
			} else if (!evt.isControlDown() && !focuseds.contains(origem) && origem instanceof Group) {
				Group org = (Group)origem;
				Node ndOrg = org.getChildren().get(0);
//				((IComponenteJazzForms)ndOrg).setFocusedStyle();
				
//				for (IJFSelectedComponent nd : focuseds) {
//					if (nd instanceof Group) {
//						Node ndcnt = ((Group)nd).getChildren().get(0);
//						((IComponenteJazzForms)ndcnt).setUnfocusedStyle();
//					} else if (nd instanceof IComponenteJazzForms) {
//						((IComponenteJazzForms)nd).setUnfocusedStyle();
//					}
//				}
				
				focuseds.clear();
				focuseds.add((IJFSelectedComponent)origem);
				
				paginaRect.addAllSelectedNodes(focuseds);
//				RootController raiz = ((IComponenteJazzForms)ndOrg).getContentDetalhes();
//				raizDetalhes = raiz;
				detalhesTab.setContent((raizDetalhes != null ? raizDetalhes.root : null));
				
//				raiz = ((IComponenteJazzForms)ndOrg).getContentAvancado();
//				raizAvancado = raiz;
				avancadoTab.setContent((raizAvancado != null ? raizAvancado.root : null));
			} else if (!evt.isControlDown() && origem instanceof Pagina) {
				for (IJFSelectedComponent nd : focuseds) {
					if (nd instanceof Group && ((Group)nd).getChildren().size() > 0) {
						Node ndcnt = ((Group)nd).getChildren().get(0);
						((IJazzFormsUIComponent)ndcnt).setUnfocused();
					} else if (nd instanceof IJazzFormsUIComponent) {
						((IJazzFormsUIComponent)nd).setUnfocused();
					}
				}				
				
				focuseds.clear();
				focuseds.add((IJFSelectedComponent)origem);
				
	
				paginaRect.clearSelectedNodes();
				paginaRect.addAllSelectedNodes(focuseds);
				RootController raiz = ((IJazzFormsUIComponent)origem).getContentDetalhes();
				raizDetalhes = raiz;
				detalhesTab.setContent((raizDetalhes != null ? raizDetalhes.root : null));
				
				raiz = ((IJazzFormsUIComponent)origem).getContentAvancado();
				raizAvancado = raiz;
				avancadoTab.setContent((raizAvancado != null ? raizAvancado.root : null));				
			}
			paginaRect.requestFocus();
			
			
			// avisa aos liteners que o elemento focado mudou
			for(ISelectedChangeListener listener : selChangeListeners) {
				listener.fireSelectedChange(focuseds);
			}
			evt.consume();
		}
	}

	
	public RootController getRaizDetalhes() {
		return raizDetalhes;
	}
	
	public void setRaizDetalhes(RootController raiz) {
		this.raizDetalhes = raiz;
	}	

	public RootController getRaizAvancado() {
		return raizAvancado;
	}

	public void setRaizAvancado(RootController raiz) {
		this.raizAvancado = raiz;
	}
	
	
	public interface ISelectedChangeListener {
		public void fireSelectedChange(List<IJFSelectedComponent> selected);
	}
}


