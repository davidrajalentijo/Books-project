package edu.upc.eetac.dsa.draja.books;

/**
 * Created by david on 09/12/2014.
 */
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.AppException;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BookAdapter;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.Books;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksAPI;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksCollection;


public class WriteBookActivity extends ListActivity {
    private final static String TAG = WriteBookActivity.class.toString();
    private ArrayList<Books> booksList;

    private BookAdapter adapter;
    private class FetchBooksTask extends
            AsyncTask<Void, Void, BooksCollection> {
        private ProgressDialog pd;

        @Override
        protected BooksCollection doInBackground(Void... params) {

            BooksCollection libros = null;
            try {
                EditText et = (EditText) findViewById(R.id.etContent);
                libros  = BooksAPI.getInstance(WriteBookActivity.this)
                        .getBooksByTitle(et.getText().toString());
            } catch (AppException e) {
                e.printStackTrace();
            }
            return libros;
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
            pd = new ProgressDialog(WriteBookActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_book_layout);
    }

    public void clickMe(View v) {
        booksList = new ArrayList<Books>();
        adapter = new BookAdapter(this, booksList);
        setListAdapter(adapter);

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("alicia", "alicia"
                        .toCharArray());
            }
        });
        (new FetchBooksTask()).execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Books book = booksList.get(position);
        Log.d(TAG, book.getLinks().get("self").getTarget());

        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("url", book.getLinks().get("self").getTarget());
        startActivity(intent);
    }

    private void addBooks(BooksCollection books){
        booksList.addAll(books.getBooks());
        adapter.notifyDataSetChanged();
    }

}
