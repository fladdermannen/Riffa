package com.example.absol.riffa;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<User> contactsList;
    private ArrayList<User> contactsListFull;
    private ArrayList<User> contactsToDelete = new ArrayList<>();
    private ContactsAdapter.ContactsAdapterListener listener;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public interface ContactsAdapterListener {
        void onContactSelected(User user);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;
        private ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.contact_thumbnail);
            name = view.findViewById(R.id.contact_name);
            email = view.findViewById(R.id.contact_email);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactsList.get(getAdapterPosition()));
                }
            });
        }
    }

    public ContactsAdapter(Context ctx,  ArrayList<User> contactsList, ContactsAdapter.ContactsAdapterListener listener) {
        this.context = ctx;
        this.listener = listener;
        this.contactsList = contactsList;
        this.contactsListFull = contactsList;
    }


    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);

        return new ContactsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.MyViewHolder holder, int position) {
        User user = contactsList.get(position);
        holder.name.setText(user.getFullName());
        holder.email.setText(user.getEmail());
        holder.thumbnail.setImageResource(R.drawable.ic_contacts);
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactsList = contactsListFull;
                } else {
                    ArrayList<User> filteredList = new ArrayList<>();
                    for (User row : contactsListFull) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getFullName().toLowerCase().contains(charString.toLowerCase()) || row.getEmail().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactsList = (ArrayList<User>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }



    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        final User user = contactsListFull.get(adapterPosition);
        Snackbar snackbar = Snackbar.make(recyclerView, "CONTACT REMOVED", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        contactsListFull.add(adapterPosition, user);
                        notifyItemInserted(adapterPosition);
                        recyclerView.scrollToPosition(adapterPosition);
                        contactsToDelete.remove(user);
                    }
                });

        snackbar.show();
        contactsListFull.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        contactsToDelete.add(user);
    }

    public void deleteItems() {

        if(contactsToDelete.size() != 0) {
            String uid = currentUser.getUid();
            for (User user : contactsToDelete) {
                String userID = user.getuID();
                Query deleteQuery = mRef.child("Users").child(uid).child("Contacts").orderByChild("uID").equalTo(userID);

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapshot : dataSnapshot.getChildren()) {
                            deleteSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Patrik", "onCancelled: delete failed", databaseError.toException());
                    }
                });
            }
        }
    }



}

