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
import com.google.gson.Gson;

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Books sting = booksList.get(position);
        Log.d(TAG, sting.getLinks().get("self").getTarget());

        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("url", sting.getLinks().get("self").getTarget());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miWrite:
                Intent intent = new Intent(this, WriteBookActivity.class);
                startActivityForResult(intent, WRITE_ACTIVITY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final static int WRITE_ACTIVITY = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WRITE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String jsonSting = res.getString("json-sting");
                    Books sting = new Gson().fromJson(jsonSting, Books.class);
                    booksList.add(0, sting);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
