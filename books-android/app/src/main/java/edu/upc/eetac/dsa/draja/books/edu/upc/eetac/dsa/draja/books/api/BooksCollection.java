package edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 05/12/2014.
 */
public class BooksCollection {
    private Map<String, Link> links = new HashMap<String, Link>();

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

    private long newestTimestamp;
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

    public Map<String, Link> getLinks() {
        return links;
    }
    private List<Books> books;
    private int beforebook;
    private int afterbook;
}
