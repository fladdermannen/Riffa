package com.example.absol.riffa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Recording> recordingsList;
    private ArrayList<Recording> recordingsListFull;
    private RecordingsAdapterListener listener;

    public interface RecordingsAdapterListener {
        void onRecordingSelected(Recording rec);
        void accessChange(Recording rec, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, genre, length, date;
        private ImageButton access;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            length = (TextView) view.findViewById(R.id.length);
            genre = (TextView) view.findViewById(R.id.genre);
            date = (TextView) view.findViewById(R.id.date);
            access = view.findViewById(R.id.padlock);

            access.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.accessChange(recordingsList.get(getAdapterPosition()), view);
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected recording in callback
                    listener.onRecordingSelected(recordingsList.get(getAdapterPosition()));

                }
            });
        }
    }

     public RecordingsAdapter(Context ctx,  ArrayList<Recording> recordingsList, RecordingsAdapterListener listener) {
        this.context = ctx;
        this.listener = listener;
        this.recordingsList = recordingsList;
        this.recordingsListFull = recordingsList;
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
        holder.length.setText(String.valueOf(recording.getLength()));
        holder.genre.setText(recording.getGenre());
        holder.date.setText(recording.getDate());
        if(!recording.getAccess())
            holder.access.setBackgroundResource(R.drawable.ic_lock);
        else if(recording.getAccess())
            holder.access.setBackgroundResource(R.drawable.ic_lock_unlocked);
    }
    @Override
    public int getItemCount() {
         return recordingsList.size();
     }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    recordingsList = recordingsListFull;
                } else {
                    ArrayList<Recording> filteredList = new ArrayList<>();
                    for (Recording row : recordingsListFull) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getGenre().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    recordingsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = recordingsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                recordingsList = (ArrayList<Recording>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

}

