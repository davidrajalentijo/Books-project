package edu.upc.eetac.dsa.draja.books.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.draja.books.server.model.BooksRootAPI;



@Path("/")
public class BooksRootAPIResource {
	@GET
	public BooksRootAPI getRootAPI() {
		BooksRootAPI api = new BooksRootAPI();
		return api;
	}
}