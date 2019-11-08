package com.example.privatebrowser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.privatebrowser.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder> {

    public static ArrayList<String> historyItemList;
    public static ArrayList<String> historyUrlList;
    private final HistoryAdapter.AdapterListener onClickListener;
    final private HistoryAdapter.HistoryAdapterOnClickHandler mClickHandler;



    public interface HistoryAdapterOnClickHandler {
        void onClick(String historyStr);
    }

    //region Interface adapter listener
    public interface AdapterListener {

        void btnOnClick(View v, int position);
    }


    public HistoryAdapter(AdapterListener onClickListener, HistoryAdapterOnClickHandler mClickHandler, Context context) {

        this.onClickListener = onClickListener;
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public HistoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.history_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        HistoryAdapterViewHolder viewHolder = new HistoryAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapterViewHolder holder, int position) {

        String historyItemClicked = historyItemList.get(position);
        holder.historyTextView.setText(historyItemClicked);

        holder.historyRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickListener.btnOnClick(v, position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return historyUrlList.size();
    }

    public void setHistoryItem(ArrayList<String> historyItem) {
        HistoryAdapter.historyItemList = historyItem;
        notifyDataSetChanged();
    }


    public void setHistoryUrl(ArrayList<String> historyUrl) {

        HistoryAdapter.historyUrlList = historyUrl;
        notifyDataSetChanged();
    }
    public class HistoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView historyTextView;
        Button historyRemoveBtn;
        ImageView historyImageView;

        public HistoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            historyTextView = itemView.findViewById(R.id.tv_history);
            historyRemoveBtn = itemView.findViewById(R.id.btn_history_remove);
            historyImageView = itemView.findViewById(R.id.history_image_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            String clickedHistoryItem = historyUrlList.get(adapterPosition);

            mClickHandler.onClick(clickedHistoryItem);

        }
    }
}
