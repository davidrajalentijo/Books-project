package edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 05/12/2014.
 */
public class Link {

    private String target; //apunta a la url del servicio que queremos acceder
    private Map<String, String> parameters;

    public Link() {
        parameters = new HashMap<String, String>();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
