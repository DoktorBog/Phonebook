package com.phonebook.app.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.phonebook.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ContactViewHolder extends RecyclerView.ViewHolder {

    public @Bind(R.id.profile_image) CircleImageView profileImage;
    public @Bind(R.id.name) TextView name;
    public @Bind(R.id.contact_first_letter) TextView firstLetter;

    public ContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
