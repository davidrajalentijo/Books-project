package edu.upc.eetac.dsa.draja.books.server;

public interface MediaType {
	public final static String BOOKS_API_BOOKS = "application/vnd.books.server.books+json"; //usuario
	public final static String BOOKS_API_BOOKS_COLLECTION = "application/vnd.books.server.books.collection+json"; //coleción usuarios
	public final static String BOOKS_API_STING = "application/vnd.books.server.sting+json"; //sting
	public final static String BOOKS_API_STING_COLLECTION = "application/vnd.books.server.sting.collection+json"; //coleción stings
	public final static String BOOKS_API_ERROR = "application/vnd.dsa.books.error+json";

}
