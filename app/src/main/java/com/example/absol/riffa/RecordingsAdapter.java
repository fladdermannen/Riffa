package com.example.absol.riffa;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.MyViewHolder> implements Filterable{

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


    private List<Recording> contactListFiltered;

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = recordingsList;
                } else {
                    List<Recording> filteredList = new ArrayList<>();
                    for (Recording row : recordingsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getGenre().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Recording>) filterResults.values;

                Log.d("TAG", "publishresults" + contactListFiltered);
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }


}

