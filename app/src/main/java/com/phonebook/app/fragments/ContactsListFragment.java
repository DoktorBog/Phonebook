package com.phonebook.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phonebook.app.R;
import com.phonebook.app.adapters.ContactsAdapter;
import com.phonebook.app.adapters.ItemClickSupport;
import com.phonebook.app.models.Contact;
import com.phonebook.app.models.ContactChangeEvent;
import com.phonebook.app.ui.ProfileContactActivity;
import com.phonebook.app.untils.BusProvider;
import com.phonebook.app.untils.Helper;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ContactsListFragment extends Fragment{

    View rootView;
    @Bind(R.id.recyclerView) FastScrollRecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private Realm realm;
    private RealmResults<Contact> contacts;
    private boolean favorite;

    public static ContactsListFragment newInstance(boolean favorite) {
        Bundle args = new Bundle();
        ContactsListFragment fragment = new ContactsListFragment();
        args.putBoolean("favorite", favorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact_list_fragment, null);
        ButterKnife.bind(this,rootView);
        BusProvider.getInstance().register(this);

        favorite = getArguments().getBoolean("favorite");

        realm = Realm.getDefaultInstance();
        if(!favorite) {
            contacts = realm.where(Contact.class).findAllSorted("name");
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(llm);
        }
        else {
            contacts = realm.where(Contact.class).equalTo("favorite", true).findAllSorted("name");
            GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);
            recyclerView.setLayoutManager(glm);
        }

        contactsAdapter = new ContactsAdapter();
        contactsAdapter.setItemsToAdapter(getActivity(), contacts, favorite);
        recyclerView.setAdapter(contactsAdapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView1, position, v) -> {

            Intent contactIntent = new Intent(getActivity(), ProfileContactActivity.class);
            contactIntent.putExtra("contact_id", contacts.get(position).getUuid());
            startActivity(contactIntent);
            getActivity().overridePendingTransition(0, 0);
        });

        return rootView;
    }

    @Subscribe
    public void onContactChange(ContactChangeEvent event) {
        switch (event.getTypeChange()){
            case Helper.TYPE_CHANGE_FAVORITE:
            case Helper.TYPE_CHANGE_DELETE:
            case Helper.TYPE_CHANGE_EDIT:
                if(!favorite) {
                    contacts = realm.where(Contact.class).findAllSorted("name");
                    contactsAdapter.setItemsToAdapter(getActivity(), contacts, favorite);
                    contactsAdapter.notifyDataSetChanged();
                }else {
                    contacts = realm.where(Contact.class).equalTo("favorite", true).findAllSorted("name");
                    contactsAdapter.setItemsToAdapter(getActivity(), contacts, favorite);
                    contactsAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}
