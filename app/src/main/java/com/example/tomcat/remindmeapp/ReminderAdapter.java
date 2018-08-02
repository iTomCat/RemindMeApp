package com.example.tomcat.remindmeapp;

import android.app.Activity;
import android.database.Cursor;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.models.Reminder;

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

        @BindView(R.id.place_descr)
        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight placeNameTxt;

        @BindView(R.id.action_descr)
        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight actionTxt;

        @BindView(R.id.week_days_tv)
        com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight weekDays;

        @BindView(R.id.in_out_icon) ImageView inOutIcon;

        @BindView(R.id.sms_icon) ImageView smsIcon;

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
        int txtColor = activity.getResources().getColor(R.color.colorPrimary);;
        String placeDescr = null;
        int icon = R.drawable.ic_icon_out;

        holder.inOutIcon.setImageResource(R.drawable.ic_icon_in);


        if (mRemindersData.get(position).getInOut() == AddReminderActivity.WHEN_ENTER){
            placeDescr = activity.getString(R.string.in_at) + " ";
            txtColor = activity.getResources().getColor(R.color.green);
            icon = R.drawable.ic_icon_in;
        }else if (mRemindersData.get(position).getInOut() == AddReminderActivity.WHEN_EXIT){
            placeDescr = activity.getString(R.string.out) + ": ";
            txtColor = activity.getResources().getColor(R.color.colorPrimary);
            icon = R.drawable.ic_icon_out;
        }


        String place = placeDescr + placeName;
        if (placeName != null) holder.placeNameTxt.setText(place);
        holder.placeNameTxt.setTextColor(txtColor);

        holder.inOutIcon.setImageResource(icon);

        // ----------------------------------------------------------------------------------------- SETTINGS
        String currSettings;
        int settings = mRemindersData.get(position).getSettings();

        if((settings & AddReminderActivity.REMIND_ONCE) > 0){ // ----------------------- Remind Once
            currSettings = activity.getString(R.string.remind_once);

        }else if ((settings & AddReminderActivity.REMIND_ALWAYS) > 0){ // ------------ Remind Always
            currSettings = activity.getString(R.string.remind_always);

        }else { // ------------------------------------------------------ Remind on days of the week
            currSettings = activity.getString(R.string.remind_me_on);
            holder.weekDays.setVisibility(View.VISIBLE);
            showPickedWeekDays(settings);
            holder.weekDays.setText(TextUtils.concat(showPickedWeekDays(settings)));
        }


        // ----------------------------------------------------------------------------------------- ACTIONS
        if((mRemindersData.get(position).getAction() > 0)) { // ---------------------------- Send SMS
            holder.smsIcon.setVisibility(View.VISIBLE);
        }

        /*}else if ((settings & AddReminderActivity.REMIND_ALWAYS) > 0) { // ---------------- no Action
                   }*/


        holder.actionTxt.setText(currSettings);



    }


    // ********************************************************************************************* Picked Week Days
    private SpannableString[] showPickedWeekDays(int settings){
        SpannableString[] finalString = new SpannableString[7];
        String originalText;

        for(int i=0; i<=6; i++) {

            String dayName = "day_" + Integer.toString(i);
            int dayNameRef = activity.getResources().getIdentifier
                    (dayName, "string", activity.getPackageName());

           originalText = activity.getString(dayNameRef);

            if (i >0 ) originalText = "  " + originalText;
            SpannableString highlighted = new SpannableString(originalText);

            int currDay = AddReminderActivity.WEEK_DAYS[i];

            if ((settings & currDay) > 0){
                highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor
                                (activity, R.color.colorPrimary)),
                        0, originalText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            finalString[i]= highlighted;
        }
        return finalString;
        //weekDaysTv.setText(TextUtils.concat(finalString));
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
