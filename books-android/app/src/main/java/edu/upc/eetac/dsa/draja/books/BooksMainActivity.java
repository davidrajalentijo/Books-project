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
import android.widget.EditText;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import com.google.gson.Gson;

import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.AppException;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BookAdapter;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.Books;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksAPI;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksCollection;


public class BooksMainActivity extends ListActivity {
   // private final static String TAG = BooksMainActivity.class.toString();
    //private ArrayList<Books> booksList;

    //private BookAdapter adapter;
    //private ArrayAdapter<String> adapter;
    //private class FetchStingsTask extends
      //      AsyncTask<Void, Void, BooksCollection> {
       // private ProgressDialog pd;

        //@Override
       // protected BooksCollection doInBackground(Void... params) {
         //   BooksCollection stings = null;
           // try {
             //   stings = BooksAPI.getInstance(BooksMainActivity.this)
               //         .getBooks();
            //} catch (AppException e) {
              //  e.printStackTrace();
           // }
            //return stings;
        //}



        //@Override
        //protected void onPreExecute() {
          //  pd = new ProgressDialog(BooksMainActivity.this);
           // pd.setTitle("Searching...");
            //pd.setCancelable(false);
           // pd.setIndeterminate(true);
            //pd.show();
     //   }





       // @Override
        //protected void onPostExecute(BooksCollection result) {
          //  addBooks(result);
           // if (pd != null) {
             //   pd.dismiss();
            //}
        //}


    //}
   // @Override
    //public void onCreate(Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_books_search);

        //booksList = new ArrayList<Books>();
        //adapter = new BookAdapter(this, booksList);
        //setListAdapter(adapter);

        //Authenticator.setDefault(new Authenticator() {
          //  protected PasswordAuthentication getPasswordAuthentication() {
            //    return new PasswordAuthentication("alicia", "alicia"
              //          .toCharArray());
            //}
        //});
        //(new FetchStingsTask()).execute();
    //}
    //private void addBooks(BooksCollection books) {
      //  booksList.addAll(books.getBooks());
        //adapter.notifyDataSetChanged();
    //}

    //@Override
    //protected void onListItemClick(ListView l, View v, int position, long id) {
      //  Books sting = booksList.get(position);
        //Log.d(TAG, sting.getLinks().get("self").getTarget());

        //Intent intent = new Intent(this, BookDetailActivity.class);
        //intent.putExtra("url", sting.getLinks().get("self").getTarget());
        //startActivity(intent);
    //}


    private class FetchBooksTask extends
            AsyncTask<Void, Void, BooksCollection> {
        private ProgressDialog pd;

        @Override
        protected BooksCollection doInBackground(Void... params) {
            BooksCollection books = null;
            try {
                EditText et = (EditText) findViewById(R.id.inputBook);
                books = BooksAPI.getInstance(BooksMainActivity.this)
                        .getBooksByTitle(et.getText().toString());
            } catch (AppException e) {
                e.printStackTrace();
            }
            return books;
        }

        @Override
        protected void onPostExecute(BooksCollection result) {
            addBooks(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BooksMainActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    private final static String TAG = BooksMainActivity.class.toString();
    //    private static final String[] items = { "lorem", "ipsum", "dolor", "sit",
//            "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
//            "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel",
//            "erat", "placerat", "ante", "porttitor", "sodales", "pellentesque",
//            "augue", "purus" };
//    private ArrayAdapter<String> adapter;
    private ArrayList<Books> booksList;
    private BookAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_search);
    }

    public void clickMe(View v) {
        booksList = new ArrayList<Books>();
        adapter = new BookAdapter(this, booksList);
        setListAdapter(adapter);

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("test", "test"
                        .toCharArray());
            }
        });
        (new FetchBooksTask()).execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Books book = booksList.get(position);

        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("url", book.getLinks().get("self").getTarget());
        //Intent intent = new Intent(this, BookReviewsActivity.class);
        //intent.putExtra("url", book.getLinks().get("reviews").getTarget());
       // intent.putExtra("url_reviews", book.getLinks().get("reviews").getTarget());
        startActivity(intent);
    }

    private void addBooks(BooksCollection books){
        booksList.addAll(books.getBooks());
        adapter.notifyDataSetChanged();
    }


}
