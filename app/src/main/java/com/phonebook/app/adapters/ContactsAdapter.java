package com.phonebook.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.phonebook.app.R;
import com.phonebook.app.adapters.viewHolders.ContactViewHolder;
import com.phonebook.app.models.Contact;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private Context _context;
    private List<Contact> items;
    private boolean favorite;

    public void setItemsToAdapter(Context _context, List<Contact> items, boolean favorite){
        this.items = items;
        this._context = _context;
        this.favorite = favorite;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(!favorite) {
            View itemContact = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.contact_item, viewGroup, false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(itemContact);
            return contactViewHolder;
        }else {
            View itemContactBig = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.contact_item_big, viewGroup, false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(itemContactBig);
            return contactViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ContactViewHolder){

            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            Contact contact = items.get(position);

            contactViewHolder.name.setText(contact.getName()+"");

            contactViewHolder.firstLetter.setText(String.valueOf(contact.getName().charAt(0)).toUpperCase());
            if(contact.getPhotoURI()!=null) {
                contactViewHolder.firstLetter.setVisibility(View.INVISIBLE);
                Glide.with(_context).load(contact.getPhotoURI())
                        .into(contactViewHolder.profileImage);
            } else {
                contactViewHolder.firstLetter.setVisibility(View.VISIBLE);
                contactViewHolder.profileImage.setImageResource(R.drawable.circle_icon);

                GradientDrawable drawable = (GradientDrawable) contactViewHolder.profileImage.getDrawable();
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                drawable.setColor(color);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(items.get(position).getName().charAt(0)).toUpperCase();
    }
}
