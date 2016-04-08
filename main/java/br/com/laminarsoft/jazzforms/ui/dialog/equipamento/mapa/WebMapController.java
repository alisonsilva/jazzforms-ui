package br.com.laminarsoft.jazzforms.ui.dialog.equipamento.mapa;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoUsuario;
import br.com.laminarsoft.jazzforms.persistencia.model.wrapper.LocalizacaoVO;
import br.com.laminarsoft.jazzforms.ui.communicator.ILocalizacaoHandler;
import br.com.laminarsoft.jazzforms.ui.componentes.IControllerComponent;

public class WebMapController implements Initializable, IControllerComponent, ILocalizacaoHandler{

	@FXML private BorderPane mapPane;
	private WebMap mapReference;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resBundle) {
		mapReference = new WebMap();
		mapPane.setCenter(mapReference.getMapPane());
	}


	@Override
	public Node getBackComponent() {
		return mapReference.getMapPane();
	}

	public void addMapListener(IMapListener listener) {
		mapReference.addListener(listener);
	}
	
	public void setMapCenter(String latitude, String longitude) {
		mapReference.setCenterPosition(latitude, longitude);
	}
    public void putPin(String latitude, String longitude, String content) {
    	mapReference.putPin(latitude, longitude, content);
    }


	@Override
	public void onRecuperaLocalizacoesUsuario(final InfoRetornoUsuario info) {
		final WebMapController me = this;
		if(info.codigo == 0) {
			
			Task<Void> task = new Task<Void>() {

			     @Override 
			     protected Void call() throws Exception {
					String content = "Nome: <b>"+info.infoUsuario.showName+"</b><br/>";
					content += "Login: <b>"+info.infoUsuario.lgn+"</b><br/>";
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					final List<LocalizacaoVO> localizacoes = info.localizacoes;
					try {
						for(int idx = 0; idx < localizacoes.size(); idx++) {	
							LocalizacaoVO loc = localizacoes.get(idx);
						    final String baloon = content + "Data: <b>" + sdf.format(loc.getData()) + "</b>";
						    final String latitude = loc.latitude;
						    final String longitude = loc.longitude;
						    final Integer qtd = idx;
						    Platform.runLater(new Runnable() {
								@Override
								public void run() {
								    me.putPin(latitude, longitude, baloon);
								    if(qtd == localizacoes.size() - 1) {
								    	 me.setMapCenter(latitude, longitude);								    	
								    }
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
			     }
			 };	
			 task.run();
		} else {
			System.out.println("Erro na chamada: " + info.mensagem);
		}
	}


	@Override
	public void onServerError(Exception e) {
		e.printStackTrace();
	}	
    
    
}
