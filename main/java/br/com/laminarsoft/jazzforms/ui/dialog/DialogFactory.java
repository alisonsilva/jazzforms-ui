package br.com.laminarsoft.jazzforms.ui.dialog;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

public class DialogFactory {
	
	
	public DialogRetVO loadDialog(String dialogo) throws IOException {
		String vlrUrl = "../" + dialogo;
		URL location = this.getClass().getResource(vlrUrl);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(location);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		
		Object root = loader.load();

		Object controller = loader.getController();
		DialogRetVO vo = new DialogRetVO();
		vo.controller = controller;
		vo.root = root;
		return vo;
	}
	
	public class DialogRetVO {
		public Object root;
		public Object controller;
	}	
}



