package com.example.privatebrowser.Adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.privatebrowser.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchBookmarksAdapter extends RecyclerView.Adapter<SearchBookmarksAdapter.SearchBookmarksAdapterViewHolder> {

    private final SearchBookmarksAdapter.SearchBookmarkButtonAdapterListener btnOnClickListener;
    private static ArrayList<String> searchBookmarksUrlList;
    private static ArrayList<String> searchBookmarksNameList;
    Context context;

    public interface SearchBookmarkButtonAdapterListener {

        void btnOnClick(View v, int position);
    }

    public SearchBookmarksAdapter(SearchBookmarkButtonAdapterListener listener, Context context) {
        this.btnOnClickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchBookmarksAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.search_bookmarks_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        SearchBookmarksAdapterViewHolder viewHolder = new SearchBookmarksAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBookmarksAdapterViewHolder holder, int position) {

        String searchBookmarkUrlStr = searchBookmarksUrlList.get(position);
        String searchBookmarkNameStr = searchBookmarksNameList.get(position);
        try {
            URL url = new URL(searchBookmarkUrlStr);
            String host = url.getHost();

            int nextIndex = host.indexOf('.');

            holder.searchBookmarkTextView.setText(String.format("%s", host.charAt(nextIndex + 1)));

            if(searchBookmarkNameStr.equals(""))
            {
                holder.searchBookmarkTextView1.setText(host);
            }
            else
            {
                holder.searchBookmarkTextView1.setText(searchBookmarkNameStr);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //TODO pick the first letter of url domain and set to button




        holder.searchBookmarkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOnClickListener.btnOnClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (searchBookmarksUrlList == null)
            return 0;
        return searchBookmarksUrlList.size();
    }

    public void setSearchBookmarksUrlList(ArrayList<String> searchBookmarksUrlList) {
        SearchBookmarksAdapter.searchBookmarksUrlList = searchBookmarksUrlList;
        notifyDataSetChanged();
    }

    public void setSearchBookmarksNameList(ArrayList<String> searchBookmarksNameList) {
        SearchBookmarksAdapter.searchBookmarksNameList = searchBookmarksNameList;
        notifyDataSetChanged();
    }

    public class SearchBookmarksAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView searchBookmarkTextView;
        final TextView searchBookmarkTextView1;

        SearchBookmarksAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            searchBookmarkTextView = itemView.findViewById(R.id.tv_search_bookmark);
            searchBookmarkTextView1 = itemView.findViewById(R.id.tv_one_search_bookmark);
            searchBookmarkTextView1.setSelected(true);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
