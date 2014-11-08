package edu.upc.eetac.dsa.draja.books.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Link;

import edu.upc.eetac.dsa.draja.books.server.MediaType;
import edu.upc.eetac.dsa.draja.books.server.BooksResource;

public class Books {
	private List<Link> links;
	int id=0;
	String title=null;
	String author= null;
	String language=null;
	String edition=null;
	Date editiondate=null;
	Date printdate=null;
	String editorial=null;
	List<Reviews> reviews= new ArrayList<Reviews>();
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public Date getEditiondate() {
		return editiondate;
	}
	public void setEditiondate(Date editiondate) {
		this.editiondate = editiondate;
	}
	public Date getPrintdate() {
		return printdate;
	}
	public void setPrintdate(Date printdate) {
		this.printdate = printdate;
	}
	public String getEditorial() {
		return editorial;
	}
	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}
	public List<Reviews> getReviews() {
		return reviews;
	}
	public void addReviews(Reviews review) {
		reviews.add(review);
	}
	public void setReviews(List<Reviews> review) {
		this.reviews= review;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	

}
