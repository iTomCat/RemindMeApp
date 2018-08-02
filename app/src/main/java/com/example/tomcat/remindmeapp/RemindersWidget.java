package com.example.tomcat.remindmeapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class RemindersWidget extends AppWidgetProvider {
    public static List<Reminder> mReminderList = null;

    static void addClickListener(Context context, AppWidgetManager appWidgetManager,
                                 int[] appWidgetIds){

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminders_widget);

        // ------------------------------------------------------- Intent to launch App when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    public static void updateIngriedentsWidgets(Context context, AppWidgetManager appWidgetManager,
                                                int[] appWidgetIds,List<Reminder> reminderList) {

        mReminderList = reminderList;

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reminders_widget);

            Intent intent = new Intent(context, ListWidgetService.class);
            views.setRemoteAdapter(R.id.widget_list_view, intent);
            ComponentName component = new ComponentName(context, RemindersWidget.class);

            //Trigger data update to handle the ListView widgets and force a data refresh
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

            appWidgetManager.updateAppWidget(component, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
            addClickListener(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

