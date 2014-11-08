package edu.upc.eetac.dsa.draja.books.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import edu.upc.eetac.dsa.draja.books.server.MediaType;
import edu.upc.eetac.dsa.draja.books.server.BooksResource;

public class BooksCollection {
private List<Link> links;
	
	private List<Books> books;
	private int beforebook;
	private int afterbook;
	
	
	
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
