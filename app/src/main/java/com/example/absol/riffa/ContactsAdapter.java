package com.example.absol.riffa;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Contact> contactsList;
    private ArrayList<Contact> contactsListFull;
    private ContactsAdapter.ContactsAdapterListener listener;

    public interface ContactsAdapterListener {
        void onContactSelected(Contact contact);
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

    public ContactsAdapter(Context ctx,  ArrayList<Contact> contactsList, ContactsAdapter.ContactsAdapterListener listener) {
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
        Contact contact = contactsList.get(position);
        holder.name.setText(contact.getName());
        holder.email.setText(contact.getEmail());
        holder.thumbnail.setImageResource(contact.getImage());
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
                    ArrayList<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contactsListFull) {

                        // name match condition. this might differ depending on your requirement
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getEmail().contains(charSequence)) {
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
                contactsList = (ArrayList<Contact>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

}

