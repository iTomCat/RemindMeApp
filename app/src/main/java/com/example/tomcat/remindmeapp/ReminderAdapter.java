package com.example.tomcat.remindmeapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.ArrayList;

/**
 * Adapter for RemindersFragment on Main View
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private ArrayList<Reminder> reminders;
    private final ReciepeAdapterOnClickHandler mClickHandler;

    ReminderAdapter(ArrayList<Reminder> reminders, ReciepeAdapterOnClickHandler clickHandler) {
        this.reminders = reminders;
        this.mClickHandler = clickHandler;
    }



    public interface ReciepeAdapterOnClickHandler {
        void onClick(int position);
    }


    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_row, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        /*@BindView(R.id.tv_name)
        TextView remNameTxt;*/
        //@BindView(R.id.cake_photo)
        //ImageView cakeView;

        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight remNameTxt;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            //ButterKnife.bind(this, view);
            remNameTxt = view.findViewById(R.id.tv_name);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }


        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
            Log.d("RemFrag", "Long" );
            return false;
        }
    }

    @Override
    public void onBindViewHolder(ReminderAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.remNameTxt.setText(reminders.get(position).getRemindName());

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
        return reminders.size();
    }
}
