package com.example.privatebrowser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarksAdapterViewHolder> {

    public static ArrayList<String> bookmarksList;
    public static ArrayList<String> bookmarksUrlList;
    final private BookmarksAdapter.BookmarksAdapterOnClickHandler mClickHandler;
    private final BookmarksAdapter.AdapterListener onClickListener;
    private final DatabaseClass databaseClass;

    public interface BookmarksAdapterOnClickHandler {
        void onClick(String bookmarksStr);
    }

    //region Interface adapter listener
    public interface AdapterListener {

        void btnOnClick(View v, int position);
    }

    public BookmarksAdapter(BookmarksAdapterOnClickHandler handler, AdapterListener listener, Context context) {
        mClickHandler = handler;
        this.onClickListener = listener;
        databaseClass = new DatabaseClass(context);

    }

    @NonNull
    @Override
    public BookmarksAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.bookmarks_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

        return new BookmarksAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarksAdapterViewHolder holder, final int position) {

        String bookmarksUrlStr = bookmarksList.get(position);
        String bookmarkName = databaseClass.searchBookmarkRecord(bookmarksUrlStr);

        try {
            URL url = new URL(bookmarksUrlStr);
            if (!bookmarkName.equals("")) {
                holder.mBookmarksNameTextView.setText(bookmarkName);
                holder.mBookmarksUrlTextView.setText(url.getHost());
            } else {
                holder.mBookmarksNameTextView.setText(url.getHost());
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickListener.btnOnClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (bookmarksList == null)
            return 0;
        return bookmarksList.size();
    }

    public void setBookmarkUrl(ArrayList<String> mFileName) {
        bookmarksList = mFileName;
        notifyDataSetChanged();
    }

    public void setBookmarkNames(ArrayList<String> mFileName) {
        bookmarksUrlList = mFileName;
        notifyDataSetChanged();
    }

    public class BookmarksAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mBookmarksNameTextView;
        final TextView mBookmarksUrlTextView;
        final Button removeButton;

        BookmarksAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mBookmarksNameTextView = itemView.findViewById(R.id.tv_bookmark_name);
            mBookmarksUrlTextView = itemView.findViewById(R.id.tv_bookmarks);
            removeButton = itemView.findViewById(R.id.btn_remove);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            String clickedFileName = bookmarksList.get(adapterPosition);

            mClickHandler.onClick(clickedFileName);

        }
    }
}
