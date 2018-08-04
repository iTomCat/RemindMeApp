package com.example.tomcat.remindmeapp.sms;


import android.content.ContentResolver;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.example.tomcat.remindmeapp.R;
import java.io.IOException;
import java.lang.ref.WeakReference;


class ContactPhotoAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private ContentResolver contentResolver;
    private Resources resources;
    private WeakReference<ImageView> imageView;

    ContactPhotoAsyncTask(ImageView imageView, ContentResolver contentResolver, Resources resources) {
        this.imageView = new WeakReference<>(imageView);
        this.contentResolver = contentResolver;
        this.resources = resources;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Bitmap doInBackground(String... imageUri) {
        Bitmap photoPerson = null;

        if (imageUri[0] != null) {
            try {
                photoPerson = MediaStore.Images.Media
                        .getBitmap(contentResolver, Uri.parse(imageUri[0]));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photoPerson;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Drawable drawableIcon;
            if (result != null) {
                drawableIcon = new BitmapDrawable(resources, result);
            }else{
                drawableIcon = resources.getDrawable(R.drawable.ic_face);
            }
        imageView.get().setBackground(drawableIcon);
        }
    }


