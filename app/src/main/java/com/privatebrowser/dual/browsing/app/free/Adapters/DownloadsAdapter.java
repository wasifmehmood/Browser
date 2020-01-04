package com.privatebrowser.dual.browsing.app.free.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.privatebrowser.dual.browsing.app.free.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadsAdapterViewHolder> {

    public static ArrayList<String> mFileName;
    private final adapterListener onClickListener;
    final private DownloadsAdapterOnClickHandler mClickHandler;

    public interface DownloadsAdapterOnClickHandler {
        void onClick(String fileName, View view, int position);
    }

    //region Interface adapter listener
    public interface adapterListener {

        void btnOnClick(View v, int position);
    }

    public DownloadsAdapter(DownloadsAdapterOnClickHandler handler, Context context, adapterListener listener) {
        mClickHandler = handler;
        this.onClickListener = listener;
    }


    @NonNull
    @Override
    public DownloadsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.downloads_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        DownloadsAdapterViewHolder viewHolder = new DownloadsAdapterViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull DownloadsAdapterViewHolder holder, final int position) {


        String fileNameClicked = mFileName.get(position);
        holder.mFileNameTextView.setText(fileNameClicked);

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickListener.btnOnClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (mFileName == null)
            return 0;
        return mFileName.size();
    }

    public void setFileNames(ArrayList<String> mFileName) {
        DownloadsAdapter.mFileName = mFileName;
        notifyDataSetChanged();
    }

    public class DownloadsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mFileNameTextView;
        final Button removeButton;

        DownloadsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mFileNameTextView = itemView.findViewById(R.id.tv_file_names);
            removeButton = itemView.findViewById(R.id.btn_remove);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            String clickedFileName = mFileName.get(adapterPosition);

            mClickHandler.onClick(clickedFileName, v, adapterPosition);
        }
    }
}
