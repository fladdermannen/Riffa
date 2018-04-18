package com.example.absol.riffa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements Filterable {

    private static final String TAG = "Patrik";

    private Context context;
    private ArrayList<User> usersList;
    private ContactListAdapter.ContactListAdapterListener listener;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");

    public interface ContactListAdapterListener {
        void onUserSelected(User user);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;
        private ImageView thumbnail;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.user_name);
            email = (TextView) view.findViewById(R.id.user_email);
            thumbnail = view.findViewById(R.id.user_thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected recording in callback
                    listener.onUserSelected(usersList.get(getAdapterPosition()));

                }
            });
        }
    }

    public ContactListAdapter(Context ctx,  ArrayList<User> usersList, ContactListAdapter.ContactListAdapterListener listener) {
        this.context = ctx;
        this.listener = listener;
        this.usersList = usersList;
    }


    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersearch_item_layout, parent, false);

        return new ContactListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.MyViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.name.setText(user.getfName() + " " + user.getlName());
        holder.email.setText(user.getEmail());
        holder.thumbnail.setImageResource(R.drawable.ic_contacts);
    }
    @Override
    public int getItemCount() {
        return usersList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (!charString.isEmpty()) {

                    Query mQuery = mRef.orderByChild("fullName").startAt(charString.toLowerCase()).endAt((charString + "z").toLowerCase());
                    mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usersList.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                User user = ds.getValue(User.class);
                                usersList.add(user);
                            }

                            Log.d(TAG, "onDataChange: found user" + usersList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = usersList;
                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                usersList = (ArrayList<User>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public void clearUserList() {
        usersList.clear();
    }
}

