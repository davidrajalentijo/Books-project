package edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 05/12/2014.
 */
public class BooksRootAPI {
    private Map<String, Link> links;

    public BooksRootAPI() {
        links = new HashMap<String, Link>();
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}
