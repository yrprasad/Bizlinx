package com.nglinx.pulse.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nglinx.pulse.R;
import com.nglinx.pulse.models.NotificationModel;

import java.util.ArrayList;

public class NotificationsAdapter extends ArrayAdapter<NotificationModel> {

    public NotificationsAdapter(Context context, ArrayList<NotificationModel> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        NotificationModel notificationModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_notifications_row, parent, false);
        }

        // Lookup view for data population
        TextView notif_date = (TextView) convertView.findViewById(R.id.notif_date);
        TextView notif_message = (TextView) convertView.findViewById(R.id.notif_message);
        // Populate the data into the template view using the data object

        notif_date.setText(notificationModel.getType().toString() + " : " + notificationModel.getCreatedDate());

        if (notificationModel.getMessage().length() > 50) {
            notif_message.setText(notificationModel.getMessage().substring(0, 50) + "...");
        } else {
            notif_message.setText(notificationModel.getMessage());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}