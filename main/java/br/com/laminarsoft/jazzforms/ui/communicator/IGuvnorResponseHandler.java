package br.com.laminarsoft.jazzforms.ui.communicator;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoProcessModel;



public interface IGuvnorResponseHandler {
	public void receivePackages(InfoRetornoProcessModel packages);
	public void receivePackageAssets(Document<Feed> assets);
	
	public void onServerError(Exception e);
	
}
