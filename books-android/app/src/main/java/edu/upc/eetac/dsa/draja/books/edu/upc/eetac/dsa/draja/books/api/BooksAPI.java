package edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * Created by david on 05/12/2014.
 */
public class BooksAPI {
    private final static String TAG = BooksAPI.class.getName();
    private static BooksAPI instance = null;
    private URL url;

    private BooksRootAPI rootAPI = null;

    private BooksAPI(Context context) throws IOException, AppException {
        super();
        System.out.println("Vamos a configurar");
        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties"));
        String urlHome = config.getProperty("books.home");
        url = new URL(urlHome);
        System.out.println("Conectados a la misma IP");
        Log.d("LINKS", url.toString());
        getRootAPI();
    }

    public final static BooksAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new BooksAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }

    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new BooksRootAPI();
        HttpURLConnection urlConnection = null;
        try {
            System.out.println("Intentamos obtener todo los libros");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            System.out.println("Punto 1");
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Books API Web Service");
        }
        System.out.println("Punto 2");
        BufferedReader reader;
        try {
            System.out.println("Punto 3");
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            System.out.println("Punto 4");
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("Punto 5");
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
            System.out.println("Punto 6");
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Books API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Books Root API");
        }

    }

    public BooksCollection getBooks() throws AppException {
        Log.d(TAG, "getBooks()");
        BooksCollection books = new BooksCollection();
        System.out.println("Obtener Coleccion de Libros");
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("books").getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Books API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, books.getLinks());

           books.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
           books.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonStings = jsonObject.getJSONArray("books");
           for (int i = 0; i < jsonStings.length(); i++) {
                Books book = new Books();
               System.out.println("LLegamos aqui");
                JSONObject jsonSting = jsonStings.getJSONObject(i);
               book.setTitle(jsonSting.getString("title"));
                book.setAuthor(jsonSting.getString("author"));
               book.setEditioral(jsonSting.getString("editorial"));
               book.setBookid(jsonSting.getInt("id"));
               book.setLanguage(jsonSting.getString("language"));

               System.out.println(jsonSting);
                jsonLinks = jsonSting.getJSONArray("links");
                parseLinks(jsonLinks, book.getLinks());
                books.getBooks().add(book);
               System.out.println("Estamoooooos");
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Books API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Books Root API");
        }

        return books;
    }

    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s");
            for (String s : rels)
                map.put(s, link);
        }
    }




}
