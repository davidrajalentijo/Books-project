package edu.upc.eetac.dsa.draja.books.edu.upc.eetac.dsa.draja.books.api;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.draja.books.R;

/**
 * Created by david on 09/12/2014.
 */
public class BookAdapter extends BaseAdapter {
    private ArrayList<Books>data;
    private LayoutInflater inflater;
    public BookAdapter(Context context, ArrayList<Books> data) {
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvEditionDate;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Books) getItem(position)).getBookid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_books, null);
            //creas un viewholder donde vas asociando los elementos definidos en el layout
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView
                    .findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = (TextView) convertView
                    .findViewById(R.id.tvAuthor);
            viewHolder.tvEditionDate = (TextView) convertView
                    .findViewById(R.id.tvEditionDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //le doy el valor a cada uno
        String title = data.get(position).getTitle();
        String author = data.get(position).getAuthor();
        String date = SimpleDateFormat.getInstance().format(
                data.get(position).getLastModified());
        viewHolder.tvTitle.setText(title);
        viewHolder.tvAuthor.setText(author);
        viewHolder.tvEditionDate.setText(date);
        return convertView;
    }
}
