package com.example.tomcat.remindmeapp.sms;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomcat.remindmeapp.R;
import java.util.ArrayList;

public class ContactsAdapter extends ArrayAdapter<Contact> {
    private Contact contact;
    private Dialog dialog;
    private Context context;


    ContactsAdapter(Context context, ArrayList<Contact> contacts, Dialog dialog) {
        super(context, 0, contacts);
        this.dialog = dialog;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        contact = getItem(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.adapter_contact_row, parent, false);
        }
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(contact.name);

        String imageUri = contact.photo;
        ImageView presonIv = view.findViewById(R.id.person);
        ContentResolver contentResolver = context.getContentResolver();
        new ContactPhotoAsyncTask(presonIv, contentResolver, context.getResources()).execute(imageUri);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Contact currContact = getItem(position);

                if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
                    assert currContact != null;
                    DialogPrepareSms.smsNumber = currContact.numbers.get(0).number;
                }

                assert currContact != null;
                DialogPrepareSms.smsContact = currContact.name;

                dialog.dismiss();
            }
        });
        return view;
    }
}
