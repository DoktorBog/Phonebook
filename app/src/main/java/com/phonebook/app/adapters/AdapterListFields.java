package com.phonebook.app.adapters;

import android.provider.ContactsContract;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.phonebook.app.R;
import com.phonebook.app.models.PhoneNumber;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmList;


public class AdapterListFields extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RealmList<PhoneNumber> numbers;
    private boolean onBind;
    public AdapterListFields(RealmList<PhoneNumber> numbers) {
        this.numbers = numbers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemPhoneView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.phone_item_create, viewGroup, false);
        PhoneViewHolder contactViewHolder = new PhoneViewHolder(itemPhoneView ,new CustomEditTextListener(), new SpinnerListener());
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof PhoneViewHolder) {
            PhoneNumber item = numbers.get(holder.getAdapterPosition());
            PhoneViewHolder phoneViewHolder = (PhoneViewHolder) holder;
            phoneViewHolder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
            phoneViewHolder.spinnerListener.updatePosition(holder.getAdapterPosition());

            onBind = true;
            phoneViewHolder.mEditText.setText(item.getPhone());

            switch (item.getType()){
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    phoneViewHolder.appCompatSpinner.setSelection(0);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    phoneViewHolder.appCompatSpinner.setSelection(1);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    phoneViewHolder.appCompatSpinner.setSelection(2);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                    phoneViewHolder.appCompatSpinner.setSelection(3);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    phoneViewHolder.appCompatSpinner.setSelection(4);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                    phoneViewHolder.appCompatSpinner.setSelection(5);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                    phoneViewHolder.appCompatSpinner.setSelection(6);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    phoneViewHolder.appCompatSpinner.setSelection(7);
                    break;
            }
            onBind = false;
        }
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.editTextPhone) AppCompatEditText mEditText;
        @Bind(R.id.spinner) AppCompatSpinner appCompatSpinner;
        public CustomEditTextListener myCustomEditTextListener;
        public SpinnerListener spinnerListener;

        public PhoneViewHolder(View v, CustomEditTextListener myCustomEditTextListener, SpinnerListener spinnerListener) {
            super(v);
            ButterKnife.bind(this,v);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.mEditText.addTextChangedListener(myCustomEditTextListener);
            this.spinnerListener = spinnerListener;
            appCompatSpinner.setOnItemSelectedListener(spinnerListener);
        }
    }

    private class CustomEditTextListener implements TextWatcher {
        private int position;
        private boolean addMore = true;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void afterTextChanged(Editable editable) {}

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if(!onBind) {
                if (addMore) {
                    numbers.add(new PhoneNumber());
                    addMore = false;
                    notifyItemInserted(numbers.size() - 1);
                }
            }

            if(charSequence.toString().isEmpty()) {
                for (int j = 0; j < numbers.size(); j++) {
                    if(numbers.get(j).getPhone().isEmpty() && j!=position) {
                        numbers.get(position).setPhone(charSequence.toString());
                        addMore =true;
                        numbers.remove(j);
                        if(!onBind) {
                            notifyItemRemoved(j);
                        }
                        return;
                    }
                }
            }
            numbers.get(position).setPhone(charSequence.toString());
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {

        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            switch (i){
                case 0:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    break;
                case 1:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                    break;
                case 2:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
                    break;
                case 3:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN);
                    break;
                case 4:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);
                    break;
                case 5:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME);
                    break;
                case 6:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_PAGER);
                    break;
                case 7:
                    numbers.get(position).setType(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}
