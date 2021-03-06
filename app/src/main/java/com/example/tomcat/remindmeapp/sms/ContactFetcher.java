package com.example.tomcat.remindmeapp.sms;


import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ContactFetcher {
    private final Context context;

    ContactFetcher(Context c) {
        this.context = c;
    }

    ArrayList<Contact> fetchAll() {
        String[] projectionFields = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
        };
        ArrayList<Contact> listContacts = new ArrayList<>();
        CursorLoader cursorLoader = new CursorLoader(context,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields, // the columns to retrieve
                null, // the selection criteria (none)
                null, // the selection args (none)
                null // the sort order (default)
        );

        Cursor c = cursorLoader.loadInBackground();

        final Map<String, Contact> contactsMap = new HashMap<>(c.getCount());

        if (c.moveToFirst()) {

            int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int photoIndex = c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

            do {
                String contactId = c.getString(idIndex);
                String contactDisplayName = c.getString(nameIndex);
                String photo = c.getString(photoIndex);
                Contact contact = new Contact(contactId, contactDisplayName, photo);
                contactsMap.put(contactId, contact);
                listContacts.add(contact);
            } while (c.moveToNext());
        }

        c.close();

        matchContactNumbers(contactsMap);
        return listContacts;
    }

    private void matchContactNumbers(Map<String, Contact> contactsMap) {
        // Get numbers
        final String[] numberProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };

        Cursor phone = new CursorLoader(context,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                numberProjection,
                null,
                null,
                null).loadInBackground();

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            final int contactIdColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            final int photoColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                final String contactId = phone.getString(contactIdColumnIndex);
                final String photo = phone.getString(photoColumnIndex);
                Contact contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                final int type = phone.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, customLabel);
                contact.addNumber(number, phoneType.toString());
                phone.moveToNext();
            }
        }

        phone.close();
    }

}
