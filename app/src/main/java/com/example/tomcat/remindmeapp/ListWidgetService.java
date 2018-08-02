package com.example.tomcat.remindmeapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.tomcat.remindmeapp.models.Reminder;
import java.util.List;



public class ListWidgetService extends RemoteViewsService {

    List<Reminder> mReminderList;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    // ********************************************************************************************* RemoteViewFactory
    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        Context mContext;

        ListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
            mReminderList = RemindersWidget.mReminderList;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mReminderList = RemindersWidget.mReminderList;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (mReminderList == null) return 0;
            return mReminderList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.reminders_widget_row);

            // ------------------------------------------------------------------------------------- Reminder Icon
            int txtColor = getResources().getColor(R.color.colorPrimary);
            int icon = R.drawable.ic_icon_out;

            if (mReminderList.get(i).getInOut() == AddReminderActivity.WHEN_ENTER){
                txtColor = getResources().getColor(R.color.green);
                icon = R.drawable.ic_icon_in;
            }else if (mReminderList.get(i).getInOut() == AddReminderActivity.WHEN_EXIT){
                txtColor = getResources().getColor(R.color.colorPrimary);
                icon = R.drawable.ic_icon_out;
            }

            views.setImageViewResource(R.id.in_out_widget, icon);

            // ------------------------------------------------------------------------------------- Reminder Name
            views.setTextViewText(R.id.text_view_recipe_widget, mReminderList.get(i).getName());
            views.setTextColor(R.id.text_view_recipe_widget, txtColor);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
