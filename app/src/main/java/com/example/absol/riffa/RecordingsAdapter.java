package com.example.absol.riffa;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by absol on 2018-03-15.
 */

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.MyViewHolder> {

    private List<Recording> recordingsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, genre, length;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            length = (TextView) view.findViewById(R.id.length);
            genre = (TextView) view.findViewById(R.id.genre);
        }
    }

     public RecordingsAdapter(List<Recording> recordingsList) {
         this.recordingsList = recordingsList;
     }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Recording recording = recordingsList.get(position);
        holder.title.setText(recording.getTitle());
        holder.length.setText(recording.getLength());
        holder.genre.setText(recording.getGenre());
    }
     @Override
     public int getItemCount() {
         return recordingsList.size();
     }
}

