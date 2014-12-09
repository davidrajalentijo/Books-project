package edu.upc.eetac.dsa.draja.books;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.AppException;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BookAdapter;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.Books;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksAPI;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksCollection;


public class BooksMainActivity extends ListActivity {
    private final static String TAG = BooksMainActivity.class.toString();
    private ArrayList<Books> booksList;

    private BookAdapter adapter;
    //private ArrayAdapter<String> adapter;
    private class FetchStingsTask extends
            AsyncTask<Void, Void, BooksCollection> {
        private ProgressDialog pd;

        @Override
        protected BooksCollection doInBackground(Void... params) {
            BooksCollection stings = null;
            try {
                stings = BooksAPI.getInstance(BooksMainActivity.this)
                        .getBooks();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return stings;
        }



        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BooksMainActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }





        @Override
        protected void onPostExecute(BooksCollection result) {
            addBooks(result);
            if (pd != null) {
                pd.dismiss();
            }
        }


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_main);

        booksList = new ArrayList<Books>();
        adapter = new BookAdapter(this, booksList);
        setListAdapter(adapter);

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("alicia", "alicia"
                        .toCharArray());
            }
        });
        (new FetchStingsTask()).execute();
    }
    private void addBooks(BooksCollection books) {
        booksList.addAll(books.getBooks());
        adapter.notifyDataSetChanged();
    }
}
