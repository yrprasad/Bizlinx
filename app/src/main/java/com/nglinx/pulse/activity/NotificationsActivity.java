package com.nglinx.pulse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nglinx.pulse.R;
import com.nglinx.pulse.adaptor.NotificationsAdapter;
import com.nglinx.pulse.models.NotificationModel;
import com.nglinx.pulse.models.ResponseDto;
import com.nglinx.pulse.models.UserModel;
import com.nglinx.pulse.session.DataSession;
import com.nglinx.pulse.session.SharedPrefUtility;
import com.nglinx.pulse.utils.ProgressbarUtil;
import com.nglinx.pulse.utils.dialog.DialogUtils;
import com.nglinx.pulse.utils.retrofit.ApiEndpointInterface;
import com.nglinx.pulse.utils.retrofit.RetroResponse;
import com.nglinx.pulse.utils.retrofit.RetroUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationsActivity extends Activity implements AdapterView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    // Create the adapter to convert the array to views
    NotificationsAdapter adapter;

    ListView notifications_lv;

    List<NotificationModel> notificationList;

    private ImageView refresh_notifications;

    public DrawerLayout drawerLayout;

    private TextView user_name;

    private DataSession ds = DataSession.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        refresh_notifications = (ImageView) findViewById(R.id.refresh_notifications);
        RefreshClickListener refresh_click_listener = new RefreshClickListener();
        refresh_notifications.setOnClickListener(refresh_click_listener);

        notificationList = new ArrayList<NotificationModel>();

        notifications_lv = (ListView) findViewById(R.id.lv_notifications);
        adapter = new NotificationsAdapter(getApplicationContext(), (ArrayList<NotificationModel>) notificationList);
        notifications_lv.setAdapter(adapter);

        getAndPopulateNotifications();

        notifications_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NotificationsActivity.this, NotificationsDetailActivity.class);
                NotificationModel selectedNotifModel = (NotificationModel) notifications_lv.getItemAtPosition(i);
                intent.putExtra("NotifDateBundle", selectedNotifModel.getType().toString() + " : " + selectedNotifModel.getCreatedDate());
                intent.putExtra("NotifTextBundle", selectedNotifModel.getMessage());
                startActivity(intent);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_notif);
        navigationView.setNavigationItemSelectedListener(this);
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View headerLayout = navigationView.getHeaderView(0);
        user_name = (TextView) headerLayout.findViewById(R.id.username);
        user_name.setText(ds.getUserModel().getName());
    }

    private List<NotificationModel> getAndPopulateNotifications() {

        final ProgressDialog mProgressDialog1 = ProgressbarUtil.startProgressBar(this);
        ApiEndpointInterface apiEndpointInterface = RetroUtils.getHostAdapterForAuthenticate(getApplicationContext(), RetroUtils.URL_HIT).create(ApiEndpointInterface.class);
        apiEndpointInterface.getAllNotifications(DataSession.getInstance().getUserModel().getId(), new RetroResponse<NotificationModel>() {
            @Override
            public void onSuccess() {
                if ((models != null) && (models.size() > 0)) {
                    notificationList.clear();
                    notificationList.addAll(models);
//                    Collections.sort((List<NotificationModel>) notificationList);
                    adapter.notifyDataSetChanged();
                    ProgressbarUtil.stopProgressBar(mProgressDialog1);
                }
            }

            @Override
            public void onFailure() {
                ProgressbarUtil.stopProgressBar(mProgressDialog1);
                DialogUtils.diaplayFailureDialog(NotificationsActivity.this, errorMsg);
            }
        });

        return notificationList;
    }

    void reloadIntent() {
        Intent intent2 = new Intent(NotificationsActivity.this, NotificationsActivity.class);
        startActivity(intent2);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class RefreshClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            reloadIntent();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        //Checking if the item is in checked state or not, if not make it in checked state
        if (menuItem.isChecked()) menuItem.setChecked(false);
        else menuItem.setChecked(true);

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(NotificationsActivity.this, SensorsActivity.class);
                startActivity(intent);
                return true;
            case R.id.notifications:
                Intent intent2 = new Intent(NotificationsActivity.this, NotificationsActivity.class);
                startActivity(intent2);
                return true;
            case R.id.signout:
                SignOutApi();
                return true;
            default:
                return true;
        }
    }

    private void SignOutApi() {
        final ProgressDialog mProgressDialog = ProgressbarUtil.startProgressBar(this);

        ApiEndpointInterface apiEndpointInterface = RetroUtils.getHostAdapterForAuthenticate(getApplicationContext(), RetroUtils.URL_HIT).create(ApiEndpointInterface.class);
        apiEndpointInterface.logout(new Callback<ResponseDto<UserModel>>() {
            @Override
            public void success(ResponseDto<UserModel> userModelResponseDto, Response response) {
                ProgressbarUtil.stopProgressBar(mProgressDialog);
                SharedPrefUtility.clearprofile(getApplicationContext());
                ds.clearDataSession();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                ProgressbarUtil.stopProgressBar(mProgressDialog);
                SharedPrefUtility.clearprofile(getApplicationContext());
                ds.clearDataSession();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }
}