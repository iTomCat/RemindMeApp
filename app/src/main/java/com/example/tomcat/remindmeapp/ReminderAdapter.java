package com.example.tomcat.remindmeapp;

import android.app.Activity;
import android.database.Cursor;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Places;
import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Reminders List
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private final ReminderAdapterOnClickHandler mClickHandler;
    private Activity activity;
    private List<Reminder> mRemindersData;
    private Cursor mCursor;

    ReminderAdapter(Activity activity, ReminderAdapterOnClickHandler clickHandler) {
        this.activity = activity;
        this.mClickHandler = clickHandler;
    }

    public interface ReminderAdapterOnClickHandler {
        void onClick(int position, boolean longClick);
    }

    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_row, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        @BindView(R.id.tv_name)
        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight remNameTxt;

        @BindView(R.id.tv_descr)
        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight placeNameTxt;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition, false);
        }

        @Override
        public boolean onLongClick(View view) {
            //int adapterPosition = getAdapterPosition();
            int tag = (int) view.getTag();
            mClickHandler.onClick(tag, true);
            return false;
        }
    }

    @Override
    public void onBindViewHolder(ReminderAdapter.ViewHolder holder, int position) {

        String descr = mRemindersData.get(position).getName();
        holder.remNameTxt.setText(descr);

        int id = mRemindersData.get(position).getRemIDinDB();
        holder.itemView.setTag(id);

        String currPlaceID = mRemindersData.get(position).getPlaceID();
        String placeName = AppContentProvider.getPlaceNameBasedGoogleID(activity, currPlaceID);
        if (placeName != null) holder.placeNameTxt.setText(placeName);


        /*String imageUrl = reminders.get(position).getImage();
        if (imageUrl.isEmpty()){
            switch (holder.id) {

                case 1: // ------------------------------------------------------------------------- Nutella Pie
                    Picasso.with(context).load(R.drawable.nutella_pie)
                            .fit().centerCrop().into(holder.cakeView);
                    break;
                case 2: // ------------------------------------------------------------------------- Brownies
                    Picasso.with(context).load(R.drawable.brownies)
                            .fit().centerCrop().into(holder.cakeView);
                    break;
                case 3: // ------------------------------------------------------------------------- Yellow Cake
                    Picasso.with(context).load(R.drawable.yellow_cake)
                            .fit().centerCrop().into(holder.cakeView);
                    break;
                case 4: //-------------------------------------------------------------------------- Cheesecake
                    Picasso.with(context).load(R.drawable.cheescake_1)
                            .fit().centerCrop().into(holder.cakeView);
                    break;
            }
        } else {
            Picasso.with(context).load(imageUrl).fit().into(holder.cakeView);
        }*/

    }


    @Override
    public int getItemCount() {
        if (null == mRemindersData) return 0;
        return mRemindersData.size();
    }

    void refresh(){
        this.notifyDataSetChanged();
    }

    void setRemindersData(List<Reminder> remidersData) {
        mRemindersData = remidersData;
        notifyDataSetChanged();
    }

}
