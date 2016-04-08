package br.com.laminarsoft.jazzforms.ui.communicator;

import java.io.IOException;
import java.util.Properties;

public class PropertiesServiceController {
	private static PropertiesServiceController PROPS;
	
	public Properties prop;
		
	public static PropertiesServiceController getInstance() {
		if (PROPS == null) {
			PROPS = new PropertiesServiceController();
		}
		return PROPS;
	}
	
	public PropertiesServiceController() {
		prop = new Properties();
		try {
			prop.load(ClassLoader.class.getResourceAsStream("/jazzforms-ui.properties"));
		} catch (IOException e) {
		}				
	}
	
	public String getProperty(String propriedade) {
		return prop.getProperty(propriedade);
	}
}
