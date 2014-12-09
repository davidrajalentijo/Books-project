package edu.upc.eetac.dsa.draja.books.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;

import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.draja.books.server.MediaType;
import edu.upc.eetac.dsa.draja.books.server.BooksResource;

public class Books {
	@InjectLinks({
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Book", type = MediaType.BOOKS_API_BOOKS, method = "getBook", bindings = @Binding(name = "bookid", value = "${instance.id}")), 
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "create-book", title = "Create Book", type = MediaType.BOOKS_API_BOOKS, method = "createBook"), 
		@InjectLink(resource = BooksResource.class, style = Style.ABSOLUTE, rel = "search author", title = "Search", type = MediaType.BOOKS_API_BOOKS, method = "getBookbytitleAuthor", bindings ={ @Binding(name = "author", value = "${instance.author}"),@Binding(name = "title", value = "${instance.title}")}) 
	})
	
	
	
	private List<Link> links;
	int id=0;
	String title=null;
	String author= null;
	String language=null;
	String edition=null;
	//Date editiondate=null;
	//Date printdate=null;
	String editorial=null;
	private Date editiondate;
	private long lastModified;
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	private List<Authors> authors = new ArrayList<Authors>();
	
	
	public List<Authors> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Authors> authors) {
		this.authors = authors;
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
	private Date printdate;
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
	//public Date getEditiondate() {
		//return editiondate;
	//}
	//public void setEditiondate(Date editiondate) {
		//this.editiondate = editiondate;
	//}
	//public Date getPrintdate() {
		//return printdate;
	//}
	//public void setPrintdate(Date printdate) {
		//this.printdate = printdate;
	//}
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
