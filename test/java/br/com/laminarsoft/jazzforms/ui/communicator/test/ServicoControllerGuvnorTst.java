package br.com.laminarsoft.jazzforms.ui.communicator.test;

import java.util.Iterator;

import junit.framework.Assert;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Ignore;
import org.junit.Test;

import br.com.laminarsoft.jazzforms.persistencia.model.util.*;
import br.com.laminarsoft.jazzforms.ui.communicator.IGuvnorResponseHandler;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;

public class ServicoControllerGuvnorTst implements IGuvnorResponseHandler {

	@Override
	public void receivePackages(InfoRetornoProcessModel packages) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onServerError(Exception e) {
		// TODO Auto-generated method stub

	}
	
	
	
	@Override
	public void receivePackageAssets(Document<Feed> assets) {
		// TODO Auto-generated method stub
		
	}

	@Ignore
	public void recebePackages() {
		WebServiceController controller = WebServiceController.getInstance();
//		try {
//			Document<Feed> doc = controller.getPackages();
//			Assert.assertNotNull(doc);
//			Feed feed = doc.getRoot();
//			
//			Assert.assertNotNull("Feed está nulo", feed);
//			Assert.assertNotNull("Feed.getBaseUri() está nulo", feed.getBaseUri());
//			
//			System.out.println("Title: " + feed.getTitle());
//			System.out.println("BaseUriPath: " + feed.getBaseUri().getPath());
//			
//			Iterator<Entry> it = feed.getEntries().iterator();
//			while(it.hasNext()) {
//				Entry entry = it.next();
//				System.out.println("Title: " + entry.getTitle());
//				List<Link> links = entry.getLinks();
//				System.out.println("Href: " + links.get(0).getHref().getPath());
//			}
//		} catch (Exception e) {
//			Assert.assertTrue("Msg: erro: " + e.getMessage(), false);
//		}
	}
	
	@Ignore
	public void recebePackageAssets() {
		WebServiceController controller = WebServiceController.getInstance();
//		try{
//			Document<Feed> doc = controller.getPackageAssets("http://localhost/guvnor/rest/packages/br.com.laminarsoft.jazzforms.procs");
//			Assert.assertNotNull(doc);
//			Feed feed = doc.getRoot();
//			
//			Assert.assertNotNull("Feed está nulo", feed);
//			Assert.assertNotNull("Não há entries", feed.getEntries());
//			
//			Iterator<Entry> it = feed.getEntries().iterator();
//			while(it.hasNext()) {
//				Entry entry = it.next();
//				if (entry.toString().contains("bpmn2")) {
//					System.out.println(entry.getTitle());
//					System.out.println(entry.getId().toString());
//
//					System.out.println("");
//				}
//			}
//
//		}catch(Exception e) {
//			
//		}
	}

}
