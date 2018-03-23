package com.example.absol.riffa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Recording> favoritesList;
    private ArrayList<Recording> favoritesListFull;
    private FavoritesAdapter.FavoritesAdapterListener listener;

    public interface FavoritesAdapterListener {
        void onFavoriteSelected(Recording rec);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, genre, length, date;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            length = (TextView) view.findViewById(R.id.length);
            genre = (TextView) view.findViewById(R.id.genre);
            date = (TextView) view.findViewById(R.id.date);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected recording in callback
                    listener.onFavoriteSelected(favoritesList.get(getAdapterPosition()));

                }
            });
        }
    }

    public FavoritesAdapter(Context ctx,  ArrayList<Recording> favoritesList, FavoritesAdapter.FavoritesAdapterListener listener) {
        this.context = ctx;
        this.listener = listener;
        this.favoritesList = favoritesList;
        this.favoritesListFull = favoritesList;
    }


    @Override
    public FavoritesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_item_layout, parent, false);

        return new FavoritesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoritesAdapter.MyViewHolder holder, int position) {
        Recording recording = favoritesList.get(position);
        holder.title.setText(recording.getTitle());
        holder.length.setText(recording.getLength());
        holder.genre.setText(recording.getGenre());
        holder.date.setText(recording.getDate());
    }
    @Override
    public int getItemCount() {
        return favoritesList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    favoritesList = favoritesListFull;
                } else {
                    ArrayList<Recording> filteredList = new ArrayList<>();
                    for (Recording row : favoritesListFull) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getGenre().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    favoritesList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = favoritesList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                favoritesList = (ArrayList<Recording>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}

