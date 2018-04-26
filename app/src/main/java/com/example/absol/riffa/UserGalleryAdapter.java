package com.example.absol.riffa;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserGalleryAdapter extends RecyclerView.Adapter<UserGalleryAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Recording> recordingsList;
    private ArrayList<Recording> recordingsListFull;
    private UserGalleryAdapterListener listener;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public interface UserGalleryAdapterListener {
        void onRecordingSelected(Recording rec);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, genre, date;
        private Chronometer length;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            length = view.findViewById(R.id.length);
            genre = (TextView) view.findViewById(R.id.genre);
            date = (TextView) view.findViewById(R.id.date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected recording in callback
                    listener.onRecordingSelected(recordingsList.get(getAdapterPosition()));

                }
            });
        }
    }

    public UserGalleryAdapter(Context ctx, ArrayList<Recording> recordingsList, UserGalleryAdapterListener listener) {
        this.context = ctx;
        this.listener = listener;
        this.recordingsList = recordingsList;
        this.recordingsListFull = recordingsList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_gallery_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Recording recording = recordingsList.get(position);

        holder.title.setText(recording.getTitle());
        holder.length.setBase(SystemClock.elapsedRealtime() - (recording.getLength()));
        holder.genre.setText(recording.getGenre());
        holder.date.setText(recording.getDate());

    }

    @Override
    public int getItemCount() {
        return recordingsList.size();
    }

}