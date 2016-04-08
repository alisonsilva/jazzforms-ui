package br.com.laminarsoft.jazzforms.ui;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class JazzFormsWeldLounch {

	public static void main(String[] args) {
		Weld weld = new Weld();
		WeldContainer container = weld.initialize();
		JazzForms application = container.instance().select(JazzForms.class).get();
		application.run(args, container);
		weld.shutdown();
	}
}
