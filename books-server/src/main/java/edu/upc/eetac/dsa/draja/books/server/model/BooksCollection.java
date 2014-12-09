package edu.upc.eetac.dsa.draja.books.server.model;

import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;

import org.glassfish.jersey.linking.InjectLink.Style;

import javax.ws.rs.core.Link;
import edu.upc.eetac.dsa.draja.books.server.MediaType;
import edu.upc.eetac.dsa.draja.books.server.BooksResource;

public class BooksCollection {
	@InjectLinks({
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "self", title = "books", type = MediaType.BOOKS_API_BOOKS_COLLECTION),
		//@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, condition="${resource.administrador}", rel = "create-book", title = "Create book", type = MediaType.BOOKS_API_BOOKS, method="createBook"),
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "book", title = "book", type = MediaType.BOOKS_API_BOOKS)
		})
	
private List<Link> links;
	
	private List<Books> books;
	private int beforebook;
	private int afterbook;
	private long newestTimestamp;
	public long getNewestTimestamp() {
		return newestTimestamp;
	}

	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}

	public long getOldestTimestamp() {
		return oldestTimestamp;
	}

	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}

	private long oldestTimestamp;
	
	
	public BooksCollection() {
		super();
		books = new ArrayList<Books>();
	}
	
	public void addBook(Books book) {
		books.add(book);
	}
	
	public List<Books> getBooks() {
		return books;
	}
	public void setBooks(List<Books> books) {
		this.books = books;
	}
	public int getBeforebook() {
		return beforebook;
	}
	public void setBeforebook(int beforebook) {
		this.beforebook = beforebook;
	}
	public int getAfterbook() {
		return afterbook;
	}
	public void setAfterbook(int afterbook) {
		this.afterbook = afterbook;
	}
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	

}
