package edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 05/12/2014.
 */
public class Books {
    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    private  String  eTag;
    //modela un sting
    private int bookid;
    private String username;
    private String author;
    private String language;
    private String edition;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    private String title;

    public String getEditioral() {
        return editioral;
    }

    public void setEditioral(String editioral) {
        this.editioral = editioral;
    }

    private String editioral;
    private long lastModified;
    private long creationTimestamp;
    private Map<String, Link> links = new HashMap<String, Link>();
    //nos permite coger los enlaces que venian en la respuesta
    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Map<String, Link> getLinks() {
        return links;
    }
}
