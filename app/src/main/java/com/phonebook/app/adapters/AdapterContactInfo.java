package com.phonebook.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonebook.app.R;
import com.phonebook.app.models.PhoneNumber;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmList;

import static android.provider.ContactsContract.CommonDataKinds.Phone.*;


public class AdapterContactInfo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RealmList<PhoneNumber> phoneNumbers;
    private Context ctx;

    public void setInfo(Context ctx, RealmList<PhoneNumber> phoneNumbers){
        this.phoneNumbers = phoneNumbers;
        this.ctx = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemPhone = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.phone_item, viewGroup, false);
        PhoneViewHolder phoneViewHolder = new PhoneViewHolder(itemPhone);
        return phoneViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PhoneViewHolder){
            PhoneNumber phoneNumber = phoneNumbers.get(position);
            PhoneViewHolder phoneViewHolder = (PhoneViewHolder) holder;

            phoneViewHolder.phoneNumber.setText(phoneNumber.getPhone());

            if(position>0)
                phoneViewHolder.callIcon.setVisibility(View.INVISIBLE);

            String subtitle = "";
            switch (phoneNumber.getType()) {
                case TYPE_ASSISTANT: subtitle = "Assistant"; break;
                case TYPE_CALLBACK: subtitle = "Callback"; break;
                case TYPE_CAR: subtitle = "Car"; break;
                case TYPE_COMPANY_MAIN: subtitle = "Company main"; break;
                case TYPE_FAX_HOME: subtitle = "Fax home"; break;
                case TYPE_FAX_WORK: subtitle = "Fax work"; break;
                case TYPE_HOME: subtitle = "Home"; break;
                case TYPE_ISDN: subtitle = "ISDN"; break;
                case TYPE_MAIN: subtitle = "Main"; break;
                case TYPE_MMS: subtitle = "MMS"; break;
                case TYPE_MOBILE: subtitle = "Mobile"; break;
                case TYPE_OTHER: subtitle = "Other"; break;
                case TYPE_OTHER_FAX: subtitle = "Other fax"; break;
                case TYPE_PAGER: subtitle = "Pager"; break;
                case TYPE_RADIO: subtitle = "Radio"; break;
                case TYPE_TELEX: subtitle = "Telex"; break;
                case TYPE_TTY_TDD: subtitle = "TTY TDD"; break;
                case TYPE_WORK: subtitle = "Work"; break;
                case TYPE_WORK_MOBILE: subtitle = "Work mobile"; break;
                case TYPE_WORK_PAGER: subtitle = "Work pager"; break;
            }
            phoneViewHolder.description.setText(subtitle+"");
        }
    }

    @Override
    public int getItemCount() {
        return phoneNumbers.size();
    }

    public class PhoneViewHolder extends RecyclerView.ViewHolder{

        public @Bind(R.id.callIcon) ImageView callIcon;
        public @Bind(R.id.phoneNumber) TextView phoneNumber;
        public @Bind(R.id.description) TextView description;
        public @Bind(R.id.message) ImageView message;

        public PhoneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
