package edu.upc.eetac.dsa.draja.books.server.model;
import edu.upc.eetac.dsa.draja.books.server.MediaType;
import edu.upc.eetac.dsa.draja.books.server.BooksResource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Link;

public class Reviews {
	private List<Link> links;
	
	int reviewid= 0;
	String username=null;
	Date dateupdate=null;
	String text=null;
	int bookid=0;
	

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getDateupdate() {
		return dateupdate;
	}
	public void setDateupdate(Date dateupdate) {
		this.dateupdate = dateupdate;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getBookid() {
		return bookid;
	}
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}
	public int getReviewid() {
		return reviewid;
	}
	public void setReviewid(int reviewid) {
		this.reviewid = reviewid;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	
}
