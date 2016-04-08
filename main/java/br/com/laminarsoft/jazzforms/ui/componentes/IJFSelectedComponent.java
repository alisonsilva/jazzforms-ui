package br.com.laminarsoft.jazzforms.ui.componentes;


public interface IJFSelectedComponent {
	public void setUnfocusedStyle();
	public void setFocusedStyle();
	public boolean isDeleted();
	public void setDeleted(boolean deleted);
}
