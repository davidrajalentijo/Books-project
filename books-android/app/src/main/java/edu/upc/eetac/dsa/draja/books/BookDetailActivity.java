package edu.upc.eetac.dsa.draja.books;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.AppException;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.Books;
import edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api.BooksAPI;

/**
 * Created by david on 09/12/2014.
 */
public class BookDetailActivity extends Activity{
    private final static String TAG = BookDetailActivity.class.getName();
    private class FetchStingTask extends AsyncTask<String, Void, Books> {
        private ProgressDialog pd;

        @Override
        protected Books doInBackground(String... params) {
            Books sting = null;
            try {
                sting = BooksAPI.getInstance(BookDetailActivity.this)
                        .getBook(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return sting;
        }

        @Override
        protected void onPostExecute(Books result) {
            loadBook(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BookDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail_layout);
        String urlSting = (String) getIntent().getExtras().get("url");
        (new FetchStingTask()).execute(urlSting);
    }


    private void loadBook(Books book) {
        TextView tvDetailTitle = (TextView) findViewById(R.id.tvDetailTitle);
        TextView tvDetailAuthor = (TextView) findViewById(R.id.tvDetailAuthor);
        TextView tvDetailLanguage = (TextView) findViewById(R.id.tvDetailLanguage);
        TextView tvDetailEdition = (TextView) findViewById(R.id.tvDetailEdition);
        TextView tvDetailEditorial = (TextView) findViewById(R.id.tvDetailEditorial);

        TextView tvDetailLastModified = (TextView) findViewById(R.id.tvDetailLastModified);

        tvDetailTitle.setText(book.getTitle());
        tvDetailAuthor.setText(book.getAuthor());
        tvDetailLanguage.setText(book.getLanguage());
        tvDetailEdition.setText(book.getEdition());
        tvDetailEditorial.setText(book.getEditioral());

        tvDetailLastModified.setText(SimpleDateFormat.getInstance().format(
                book.getLastModified()));
    }
}
