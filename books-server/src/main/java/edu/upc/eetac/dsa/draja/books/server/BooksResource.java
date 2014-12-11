package edu.upc.eetac.dsa.draja.books.server;

import javax.ws.rs.Path;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

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
import edu.upc.eetac.dsa.draja.books.server.model.Authors;
import edu.upc.eetac.dsa.draja.books.server.model.Books;
import edu.upc.eetac.dsa.draja.books.server.model.BooksCollection;
import edu.upc.eetac.dsa.draja.books.server.model.Reviews;




@Path("/books")
public class BooksResource {
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource(); //obtenemos referencia al datasource, para hacer operaciones CRUD, obteniendola con el singelton que hemos generado
	@Context
	private SecurityContext security;
	private boolean admin, registered;
	//private String GET_BOOKS_QUERY_FROM_LAST = "select s.*, u.name from stings s, users u where u.username=s.username and s.stingid=?";
	//private String GET_BOOKS_QUERY = "select s.*, u.name from stings s, users u where u.username=s.username and s.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_BOOK_BY_ID_QUERY = "select * from books  where  bookid=?;";
	private String GET_REVIEW_BY_ID_QUERY ="select * from reviews  where bookid=?;";
	private String GET_BOOK_BY_TITLE = "SELECT  * FROM books  WHERE   title LIKE ? ;";
    private String GET_BOOK_BY_TITLE_AUTHOR = "SELECT  * FROM books  WHERE  author LIKE ? OR title LIKE ? ;";
	private String GET_BOOK_BY_AUTHOR ="SELECT  * FROM books  WHERE   author LIKE ? ;";
	private String GET_REVIEW_BY_ID_BOOK_QUERY ="select * from reviews  where bookid=?;";
	private String INSERT_REVIEW_QUERY ="insert into reviews (username,text,bookid, dateupdate) values (?,?,?,?);";
	private String GET_REVIEW = "select * from reviews where bookid=? and username=?;";
	private String UPDATE_REVIEW_QUERY ="update reviews set  text=ifnull(?, text) where bookid=? and reviewid=?;";
	private String GET_REVIEW_BY_REVIEWID_QUERY = "select * from reviews where bookid=? and reviewid=?;";
	private String DELETE_REVIEW_QUERY="delete from reviews where reviewid=? and bookid=?;";
	private String INSERT_BOOK ="insert into books (title,language,author,edition,editorial,printdate,editiondate) values(?,?,?,?,?,?,?);";
	private String UPDATE_BOOKS_QUERY = "update books set title=ifnull(?, title), language=ifnull(?, language), edition=ifnull(?, edition), editiondate=ifnull(?, editiondate), printdate=ifnull(?, printdate), editorial=ifnull(?, editorial) where bookid=?;";
    private String DELETE_BOOKS_QUERY ="delete from books where bookid=?;";
    private String GET_BOOKS_COLLECTION_QUERY = "select * from books";
    private String INSERT_AUTHOR ="insert into authors (name) values (?);";
    private String DELETE_AUTHOR_QUERY = "delete from authors where authorid=?;";
    private String UPDATE_AUTHOR_QUERY = "update authors set name=ifnull(?, name) where authorid=?;";
    private String SEARCH_AUTHOR_QUERY = "select * from authors where name=?;";
    private String INSERT_BOOK_AUTHOR = "insert into books_authors (bookid, authorid) values (?,?);";
    private String GET_AUTHOR_BY_NAME ="select * from authors where name = ?;";
    private String GET_REVIEW_BY_USER = "select * from reviews where username = ? and bookid = ?";
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
				throw new NotFoundException("There's no book with bookid ="
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
	
	
	
	//Metodo que nos permite buscar un libro por autor o por título
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
	
	
	
	
	//Metodo que nos permite crear una nueva reseña de un libro con rol registrado
	@POST 
	@Path("/{bookid}/reviews")
	@Consumes(MediaType.BOOKS_API_REVIEW) //no especifica el tipo que se come, jersey coje el json y crea un sting
	@Produces(MediaType.BOOKS_API_REVIEW)
	public Reviews createReseña(@PathParam("bookid") String bookid, Reviews review) {
		validateReseña(review);
		validatenocreate(review);
		if (!security.isUserInRole("registered"))
			throw new ForbiddenException(
					"You are not allowed to create reviews for a book");
		String tmp = security.getUserPrincipal().getName();
		System.out.println("Estas Registrado");

		setRegistered(security.isUserInRole("registered"));
		
		
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		System.out.println("Conectados a la base de datos");
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		try {
			
			//Para que un usuario solo haga una reseña de un libro
			
			
		
			stmt = conn.prepareStatement( INSERT_REVIEW_QUERY, Statement.RETURN_GENERATED_KEYS); 
			stmt.setString(1, tmp);
			stmt.setString(2, review.getText());
			stmt.setInt(3, Integer.parseInt(bookid));
			stmt.setDate(4, (Date) review.getDateupdate());
			System.out.println("Mirar reseña" + stmt);
			stmt.executeUpdate();
			System.out.println("REVIEW CREADA");
			
			
			stmt2 = conn.prepareStatement(GET_REVIEW); 
			stmt2.setInt(1, Integer.valueOf(bookid));
			stmt2.setString(2, tmp);
			ResultSet rs = stmt2.executeQuery();
			
			
			if (rs.next()) {
				review.setBookid(rs.getInt("bookid"));
				
				review.setDateupdate(rs.getDate("dateupdate"));
				review.setText(rs.getString("text"));
				review.setUsername(rs.getString("username"));
			} else {
				throw new BadRequestException("Can't view the Review");
			}
		} catch (SQLException e) {
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
	 
		return review;
	}
	
	
	
	// Método para comprobar que el usuario no ha escrito ya una review
		private void validatenocreate(Reviews review) {
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}

			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(GET_REVIEW_BY_USER);
				stmt.setString(1, review.getUsername());
				
				stmt.setInt(2, review.getBookid());
				System.out.println(stmt);
				ResultSet rs = stmt.executeQuery();
				if (rs.next())
					throw new BadRequestException("Ya has publicado una review para este libro");
			} catch (SQLException e) {
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
		}
	
	
	
	
	private void validateReseña(Reviews review) {
		
		
		if (review.getText().length() > 500)
			throw new BadRequestException("Text can't be greater than 500 characters.");
	}
	
	
	
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	
	
	
	//METODO PARA ACTUALIZAR UNA RESEÑA 
	@PUT 
	@Path("/{bookid}/reviews/{reviewid}")
	@Consumes(MediaType.BOOKS_API_REVIEW) 
	@Produces(MediaType.BOOKS_API_REVIEW)
	public Reviews updateReseña(@PathParam("bookid") String bookid, @PathParam("reviewid") String reviewid, Reviews review) {
		validateReseña(review);
		
		if (!security.isUserInRole("registered"))
			throw new ForbiddenException(
					"You are not allowed to create reviews for a book");
		String tmp = security.getUserPrincipal().getName();
		System.out.println("Estas Registrado");

		setRegistered(security.isUserInRole("registered"));
		
		
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		System.out.println("Conectados a la base de datos");
		
		PreparedStatement stmt = null;
		
try {
			
			
			stmt = conn.prepareStatement(UPDATE_REVIEW_QUERY);
			stmt.setString(1, review.getText());
			stmt.setString(2, bookid);
			stmt.setString(3, reviewid);
	 
			int rows = stmt.executeUpdate();
			if (rows == 1)
				review = getReviewFromDatabase(reviewid, bookid);
			else {
				throw new NotFoundException("There's no review with bookid="
						+ bookid);
			}
	 
		} catch (SQLException e) {
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
		
		
		
return review;
		
	}
	
	
	
	
	private Reviews getReviewFromDatabase(String reviewid, String bookid) {

		Reviews review = new Reviews();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(GET_REVIEW_BY_REVIEWID_QUERY);
			stmt.setInt(1, Integer.valueOf(bookid));
			stmt.setString(2, reviewid);

			ResultSet rs = stmt.executeQuery();
			System.out.println("Query: " + stmt);
			if (rs.next()) {
				review.setReviewid(rs.getInt("reviewid"));
				review.setDateupdate(rs.getDate("dateupdate"));
				review.setText(rs.getString("text"));
				review.setUsername(rs.getString("username"));
				review.setBookid(rs.getInt("bookid"));

			} else {
				throw new NotFoundException("There's no review with bookid ="
						+ bookid + "and reviewid = " + reviewid);
			}

		} catch (SQLException e) {
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

		return review;
	}

	
	//Metodo para borrar una Reseña hecha por él
	@DELETE //metodo para borrar un sting concreto
	@Path("/{bookid}/reviews/{reviewid}")
	public void deleteReview(@PathParam("bookid") String bookid, @PathParam("reviewid") String reviewid) {
		//tenemos un void de manera que no devuelve nada ni consume ni produce, devuelve 204 ya que no hay contenido
		if (!security.isUserInRole("registered"))
			throw new ForbiddenException(
					"You are not allowed to create reviews for a book");
		String tmp = security.getUserPrincipal().getName();

		setRegistered(security.isUserInRole("registered"));
		//setRegistered(security.isUserInRole("admin"));
		System.out.println("Entramos");
		System.out.println("Reviewid " +reviewid +"Bookid " + bookid);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		System.out.println("Conectados a la base de datos");
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_REVIEW_QUERY);
			stmt.setString(2, bookid);
			stmt.setString(1, reviewid);
			System.out.println("Query: " + stmt);
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no Review with reviewid="
						+ reviewid);
		} catch (SQLException e) {
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
	}

	
	//Metodo para Crear una Ficha de un libro
	//Metodo que nos permite crear una nueva reseña de un libro con rol registrado
		@POST 
		
		@Consumes(MediaType.BOOKS_API_BOOKS) //no especifica el tipo que se come, jersey coje el json y crea un sting
		@Produces(MediaType.BOOKS_API_BOOKS)
		public Books createBook(Books book) {
			//validateReseña(review);
			validateBook(book);
			if (!security.isUserInRole("admin"))
				throw new ForbiddenException("You are not allowed to create a book");
			
			
			
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
			System.out.println("Conectados a la base de datos");
			PreparedStatement stmt = null;
			//PreparedStatement stmt2 = null;
			
			try {
			
				//Para mirar si el autor ya existe
				stmt = conn.prepareStatement( INSERT_BOOK,Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, book.getTitle());
				stmt.setString(3, book.getAuthor());
				stmt.setString(2, book.getLanguage());
				stmt.setString(4, book.getEdition());
				stmt.setString(5, book.getEditorial());
				stmt.setDate(6, new Date(Calendar.getInstance().getTime().getTime()));
				stmt.setDate(7, new Date(Calendar.getInstance().getTime().getTime()));
				System.out.println(stmt);
				stmt.executeUpdate();
				System.out.println("BOOK CREADO");
				ResultSet rs = stmt.getGeneratedKeys();


				
				if (rs.next()) {
					int bookid = rs.getInt(1);
					
					book = getBookFromDatabase(Integer.toString(bookid));
				

					
					
				} 
			} catch (SQLException e) {
				
			
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
		 
			return book;
		}
	
		
		
		
		private void validateBook(Books book) {
		

			int aut = getAuthorFromDatabase(book.getAuthor());
			if (aut == 0)
				throw new BadRequestException("No se puede crear el libro ya que el autor no existe en la base de datos");
		}
		

		// Método para ver si el autor ya tiene ficha
		private int getAuthorFromDatabase(String name) {
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}

			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(GET_AUTHOR_BY_NAME);
				stmt.setString(1, name);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return 1;
				} else {
					return 0;
				}
			} catch (SQLException e) {
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
		}
		
		
		private void validateAuthor(String author){
			
			
			
			
			if (author == null ){
				throw new BadRequestException(
						"No se puede crear la ficha del libro porque no existe ningún autor con este nombre en nuestra base de datos");}
			
			
			
		}
		
		
		
		
	//Metodo par Actualizar la ficha de un libro
		@PUT
		@Path("/{bookid}")
		@Consumes(MediaType.BOOKS_API_BOOKS)
		@Produces(MediaType.BOOKS_API_BOOKS)
		public Books updateBook(@PathParam("bookid") int bookid, Books book) {

			if (!security.isUserInRole("admin"))
				throw new ForbiddenException("You are not allowed to delete a book");
			System.out.println("Eres admin");

			//setAdministrator(security.isUserInRole("admin"));

			//ValidateBookforUpdate(book);
			System.out.println("Book validado");
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
			System.out.println("BD establecida");
			PreparedStatement stmt = null;

			try {
				
				
				stmt = conn.prepareStatement(UPDATE_BOOKS_QUERY);
				
				stmt.setString(1, book.getTitle());
				//stmt.setString(2, book.getAuthor());
				stmt.setString(2, book.getLanguage());
				stmt.setString(3, book.getEdition());
				stmt.setDate(4, new Date(Calendar.getInstance().getTime().getTime()));
				stmt.setDate(5, new Date(Calendar.getInstance().getTime().getTime()));
				stmt.setString(6, book.getEditorial());
				stmt.setInt(7, bookid);
				
				int rows = stmt.executeUpdate();
				
				String sbookid = Integer.toString(bookid);
				System.out.println("Miramos si hay contestación row=" + sbookid);
				if (rows == 1) {
					
					book = getBookFromDatabase(sbookid);
					
				} else {
					throw new NotFoundException("There's no book with bookid="
							+ bookid);// Updating inexistent sting
				}

			} catch (SQLException e) {
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

		//Metodo para eliminar una ficha de un libro
		@DELETE
		@Path("/{bookid}")
		public void deleteBook(@PathParam("bookid") String bookid) {

			if (!security.isUserInRole("admin"))
				throw new ForbiddenException("You are not allowed to delete a book");

			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}

			PreparedStatement stmt = null;

			try {
				
				stmt = conn.prepareStatement(DELETE_BOOKS_QUERY);
				stmt.setInt(1, Integer.valueOf(bookid));

				int rows = stmt.executeUpdate();

				if (rows == 0)
					throw new NotFoundException("There's no sting with book="
							+ bookid);

			} catch (SQLException e) {
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

		}

		//METODO QUE DEVUELVE TODA LA COLECCIÓN DE LIBROS
		@GET
		@Produces(MediaType.BOOKS_API_BOOKS_COLLECTION)
		public BooksCollection getBooks(@QueryParam("length") int length,
				@QueryParam("before") long before, @QueryParam("after") long after) {

			
			
			
			BooksCollection books = new BooksCollection();

			Connection conn = null;
			try {
				conn = ds.getConnection();// Conectamos con la base de datos
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
			PreparedStatement stmt = null;
			try {
				boolean updateFromLast = after > 0;
				stmt = conn.prepareStatement(buildGetLibrosQuery(updateFromLast));
				if (updateFromLast) {
					stmt.setTimestamp(1, new Timestamp(after));
				} else {
					if (before > 0)
						stmt.setTimestamp(1, new Timestamp(before));
					else
						stmt.setTimestamp(1, null);
					length = (length <= 0) ? 20 : length;
					stmt.setInt(2, length);
				}
				ResultSet rs = stmt.executeQuery();
				boolean first = true;
				long oldestTimestamp = 0;
				while (rs.next()) {
					Books libro = new Books();
					libro.setId(rs.getInt("bookid"));
					libro.setTitle(rs.getString("title"));
					libro.setAuthor(rs.getString("author"));
					libro.setLanguage(rs.getString("language"));
					libro.setEdition(rs.getString("edition"));
					libro.setEditiondate(rs.getDate(6));
					libro.setPrintdate(rs.getDate(7));
					libro.setEditorial(rs.getString("editorial"));
					oldestTimestamp = rs.getTimestamp("last_modified").getTime();
					libro.setLastModified(oldestTimestamp);
					if (first) {
						first = false;
						books.setNewestTimestamp(libro.getLastModified());
					}
					books.addBook(libro);
				}
				books.setOldestTimestamp(oldestTimestamp);

			} catch (SQLException e) {
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

			return books;
		}


		private String buildGetLibrosQuery(boolean updateFromLast) {
			if (updateFromLast)
				return "SELECT * FROM books WHERE last_modified > ? ORDER BY last_modified DESC";
			else
				return "SELECT * FROM books WHERE last_modified < ifnull(?, now()) ORDER BY last_modified DESC LIMIT ?";
		}
		
		//Metodo para crear una ficha de autor
@POST 
		@Path("/author")
		@Consumes(MediaType.BOOKS_API_BOOKS) //no especifica el tipo que se come, jersey coje el json y crea un sting
		@Produces(MediaType.BOOKS_API_BOOKS)
		public Authors createAuthor(Authors author) {
			//validateReseña(review);
			
			if (!security.isUserInRole("admin"))
				throw new ForbiddenException("You are not allowed to create a book");
			
			
			
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
			System.out.println("Conectados a la base de datos");
			PreparedStatement stmt = null;
			
			try {
				
				
				
				
				stmt = conn.prepareStatement( INSERT_AUTHOR,  Statement.RETURN_GENERATED_KEYS); 
				
				
				
				stmt.setString(1, author.getName());
				System.out.println(author.getName());
				System.out.println(stmt);
				
				System.out.println("AUTOR CREADO");
				
				
				
				stmt.executeUpdate();// Ejecuto la actualización
				ResultSet rs = stmt.getGeneratedKeys();// query para saber si ha ido
				
				
				if (rs.next()) {
					int bookid = rs.getInt(1);
				} else {
					throw new BadRequestException("Can't view the Review");
				}
			} catch (SQLException e) {
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
		 
			return author;
		}
		
//Metodo para actualizar una ficha Autor
@PUT
@Path("author/{authorid}")
@Consumes(MediaType.BOOKS_API_BOOKS)
@Produces(MediaType.BOOKS_API_BOOKS)
public Authors updateAuthor(@PathParam("authorid") int authorid, Authors author) {

	if (!security.isUserInRole("admin"))
		throw new ForbiddenException("You are not allowed to delete a book");
	System.out.println("Eres admin");

	//setAdministrator(security.isUserInRole("admin"));

	//ValidateBookforUpdate(book);
	System.out.println("Book validado");
	Connection conn = null;
	try {
		conn = ds.getConnection();
	} catch (SQLException e) {
		throw new ServerErrorException("Could not connect to the database",
				Response.Status.SERVICE_UNAVAILABLE);
	}
	System.out.println("BD establecida");
	PreparedStatement stmt = null;

	try {
		
		
		stmt = conn.prepareStatement(UPDATE_AUTHOR_QUERY,  Statement.RETURN_GENERATED_KEYS);
		
		stmt.setString(1, author.getName());
		stmt.setInt(2, authorid);
		
		
		int rows = stmt.executeUpdate();
		
		String sauthorid = Integer.toString(authorid);
		System.out.println("Miramos si hay contestación row=" + sauthorid);
		if (rows == 1) {
			
			//author = getBookFromDatabase(sauthorid);
			System.out.println("Ha ido bieeen");
			
		} else {
			throw new NotFoundException("There's no author with authorid="
					+ authorid);// Updating inexistent sting
		}

	} catch (SQLException e) {
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

	return author;
}







//Metodo para borrar una ficha de autor
@DELETE
@Path("author/{authorid}")
public void deleteAuthor(@PathParam("authorid") String authorid) {

	if (!security.isUserInRole("admin"))
		throw new ForbiddenException("You are not allowed to delete a book");

	Connection conn = null;
	try {
		conn = ds.getConnection();
	} catch (SQLException e) {
		throw new ServerErrorException("Could not connect to the database",
				Response.Status.SERVICE_UNAVAILABLE);
	}

	PreparedStatement stmt = null;

	try {
		
		stmt = conn.prepareStatement(DELETE_AUTHOR_QUERY);
		stmt.setInt(1, Integer.valueOf(authorid));

		int rows = stmt.executeUpdate();

		if (rows == 0)
			throw new NotFoundException("There's no author with authorid="
					+authorid);

	} catch (SQLException e) {
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

}

	
	

}
