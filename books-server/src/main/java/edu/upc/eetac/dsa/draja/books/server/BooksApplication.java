package edu.upc.eetac.dsa.draja.books.server;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class BooksApplication extends ResourceConfig{
	
	public BooksApplication() {
		super();
		register(DeclarativeLinkingFeature.class); //registra modulos, registra que quiere utilizar jersey
	}
}

