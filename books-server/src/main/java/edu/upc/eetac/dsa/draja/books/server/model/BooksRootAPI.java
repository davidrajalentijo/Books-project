package edu.upc.eetac.dsa.draja.books.server.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.draja.books.server.BooksResource;
import edu.upc.eetac.dsa.draja.books.server.BooksRootAPIResource;
import edu.upc.eetac.dsa.draja.books.server.MediaType;


public class BooksRootAPI {
	
	@InjectLinks({ //nos devuelve los primeros enlaces a partir de los cuales evoluciona la aplicacion
		@InjectLink(resource = BooksRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Books Root API", method = "getRootAPI"),
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "books", title = "Latest stings", type = MediaType.BOOKS_API_BOOKS_COLLECTION),
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "create-books", title = "Latest stings", type = MediaType.BOOKS_API_BOOKS) })
	//injectlinks los enlaces son un array, resource indicas el enlace, sytle-< abslolute veremos la uri absoluta, metodo es indiferente que lo pongas o no
	//resource = sitingresource indica que el path esta con /stings, y indicamos el tipo de media que trabaja el recurso
	private List<Link> links; //atributo con getters y setters
 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
