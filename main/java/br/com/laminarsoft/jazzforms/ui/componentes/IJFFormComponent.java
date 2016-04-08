package br.com.laminarsoft.jazzforms.ui.componentes;

import java.io.IOException;

public interface IJFFormComponent {
	public static final Integer LEFT_PREF_WIDTH = 150;
	public static final Integer CENTER_PREF_WIDTH = 230;
	
	public void setSetupTabs() throws IOException;
	public void setLabelText(String label);
}
