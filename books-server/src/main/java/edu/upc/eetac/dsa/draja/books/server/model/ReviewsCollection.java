package edu.upc.eetac.dsa.draja.books.server.model;
import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.draja.books.server.MediaType;
import edu.upc.eetac.dsa.draja.books.server.BooksResource;

public class ReviewsCollection {

	private List<Reviews> reviews;
	private int beforereview;
	private int afterreview;
	

	public ReviewsCollection() {
		super();
		reviews = new ArrayList<Reviews>();
	}
	
	public void addReview(Reviews review) {
		reviews.add(review);
	}
	
	
	public List<Reviews> getReviews() {
		return reviews;
	}
	public void setReviews(List<Reviews> reviews) {
		this.reviews = reviews;
	}
	public int getBeforereview() {
		return beforereview;
	}
	public void setBeforereview(int beforereview) {
		this.beforereview = beforereview;
	}
	public int getAfterreview() {
		return afterreview;
	}
	public void setAfterreview(int afterreview) {
		this.afterreview = afterreview;
	}
	
}
