package edu.upc.eetac.dsa.draja.books.server;

import javax.ws.rs.Path;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.draja.books.server.DataSourceSPA;
import edu.upc.eetac.dsa.draja.books.server.model.Books;
import edu.upc.eetac.dsa.draja.books.server.model.BooksCollection;
import edu.upc.eetac.dsa.draja.books.server.model.Reviews;



@Path("/books")
public class BooksResource {
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource(); //obtenemos referencia al datasource, para hacer operaciones CRUD, obteniendola con el singelton que hemos generado
	@Context
	private SecurityContext security;
	
	private String GET_BOOKS_QUERY_FROM_LAST = "select s.*, u.name from stings s, users u where u.username=s.username and s.stingid=?";
	private String GET_BOOKS_QUERY = "select s.*, u.name from stings s, users u where u.username=s.username and s.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_BOOK_BY_ID_QUERY = "select * from books  where  bookid=?;";
	private String GET_REVIEW_BY_ID_QUERY ="select * from reviews  where bookid=?;";
	private String GET_BOOK_BY_TITLE = "SELECT  * FROM books  WHERE   title LIKE ? ;";
    private String GET_BOOK_BY_TITLE_AUTHOR = "SELECT  * FROM books  WHERE  author LIKE ? OR title LIKE ? ;";
	private String GET_BOOK_BY_AUTHOR ="SELECT  * FROM books  WHERE   author LIKE ? ;";
	private String GET_REVIEW_BY_ID_BOOK_QUERY ="select * from reviews  where bookid=?;";
    //Metodo que devuelve la ficha de un libro en concreto, además es cacheable
	@GET
	@Path("/{bookid}")
	@Produces(MediaType.BOOKS_API_BOOKS)
	public Response getBook(@PathParam("bookid") String bookid,@Context Request request) {

		//Creamos CacheControl
		CacheControl cc= new CacheControl();

		//Sacamos un book de la base de datos
		Books book = getBookFromDatabase(bookid);

		//Calculamos ETag de la ultima modificación de la reseña

		String s= book.getReviews()+book.getAuthor()+book.getEdition()+"21";


		EntityTag eTag = new EntityTag(Long.toString(s.hashCode()));


		//Comparamos el eTag creado con el que viene de la peticiOn HTTP
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);// comparamos

		if (rb != null) {// Si el resultado no es nulo, significa que no ha sido modificado el contenido ( o es la 1º vez )
				return rb.cacheControl(cc).tag(eTag).build();
		}


		// Si es nulo construimos la respuesta de cero.
		rb = Response.ok(book).cacheControl(cc).tag(eTag);

		return rb.build();

	}
	private Books getBookFromDatabase(String bookid){

		Books book=new Books();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt=null;
		PreparedStatement stmtr=null;

		try{
			stmt=conn.prepareStatement(GET_BOOK_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(bookid));
			ResultSet rs = stmt.executeQuery();

			if(rs.next()) {
				book.setId(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getString("edition"));
				book.setEditiondate(rs.getDate("editiondate"));
				book.setPrintdate(rs.getDate("printdate"));
				book.setEditorial(rs.getString("editorial"));

			}else{
				throw new NotFoundException("There's no sting with stingid ="
						+ bookid);
			}

			//Cogemos las reviews de un libro
			stmtr=conn.prepareStatement( GET_REVIEW_BY_ID_QUERY);
			stmtr.setInt(1, Integer.valueOf(bookid));
			ResultSet rsr = stmtr.executeQuery();

			while(rsr.next()) {							
				Reviews review = new Reviews();
				review.setDateupdate(rsr.getDate("dateupdate"));
				review.setText(rsr.getString("text"));
				review.setUsername(rsr.getString("username"));
				review.setBookid(rsr.getInt("bookid"));

				book.addReviews(review);

			}



		}catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}



		return book;
	}
	
	
	
	
	@GET
	@Path("/search")
	@Produces(MediaType.BOOKS_API_BOOKS)
	public BooksCollection getBookbytitleAuthor(@QueryParam("title") String title, @QueryParam("author") String author) {
		
		BooksCollection books = new BooksCollection();
		validateSearch(author, title);
		Connection conn = null;
		try{ conn =ds.getConnection();
		}catch (SQLException e)
		{
			throw new ServerErrorException("Could not connect to the databes", 
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		System.out.println("datos: " + author);
		try{
			
			if (title != null && author !=null){
				stmt = conn.prepareStatement(GET_BOOK_BY_TITLE_AUTHOR);
				stmt.setString (2, title);
				stmt.setString (1, author);
				
			}
			else if (title != null && author == null){
				stmt = conn.prepareStatement(GET_BOOK_BY_TITLE);
				stmt.setString (1, title);
			}
			else if (title == null && author != null){
				stmt = conn.prepareStatement(GET_BOOK_BY_AUTHOR);
				stmt.setString (1, author);
			}
	
			System.out.println("Query salida: " + stmt);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			Books book = new Books();
			book.setId(rs.getInt("bookid"));
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setLanguage(rs.getString("language"));
			book.setEdition(rs.getString("edition"));
			book.setEditiondate(rs.getDate("editiondate"));
			book.setPrintdate(rs.getDate("printdate"));
			book.setEditorial(rs.getString("editorial"));
			
			PreparedStatement stmtr = null;
			stmtr = conn.prepareStatement(GET_REVIEW_BY_ID_BOOK_QUERY);
			stmtr.setInt(1, book.getId());
			
			ResultSet rsr = stmtr.executeQuery();

			while (rsr.next()) {
				Reviews review = new Reviews();
				review.setReviewid(rsr.getInt("reviewid"));
				review.setDateupdate(rsr.getDate("dateupdate"));
				review.setText(rsr.getString("text"));
				review.setUsername(rsr.getString("username"));
				review.setBookid(rsr.getInt("bookid"));
			
				book.addReviews(review);
			}
			books.addBook(book);
		}
			}
			
		
		catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return books;
		
	
	}
	
	private void validateSearch(String author, String title){
		
		if (author == null && title ==null)
			throw new BadRequestException(
					"No se han introducido datos en los campos de búsqueda");
		
	}
	
	
	
	
	
	

}
