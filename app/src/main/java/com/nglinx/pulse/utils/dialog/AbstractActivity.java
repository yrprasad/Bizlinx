package com.nglinx.pulse.utils.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nglinx.pulse.R;
import com.nglinx.pulse.activity.NotificationsActivity;
import com.nglinx.pulse.activity.SensorsActivity;


/**
 * Created by yelisetr on 1/26/2017.
 */

public class AbstractActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public String classTag;

    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void createErrorDialog(final Intent intent, final String errorMsg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());

        // set title
        alertDialogBuilder.setTitle("Error");

        // set dialog errorMsg
        alertDialogBuilder
                .setMessage(errorMsg)
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it alertDialog.show();
        //dialog.setCancelable(false

        //dialog.setCancelable(false);
        final Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.show();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), "Home is clicked", Toast.LENGTH_SHORT).show();
            //Open the Notifications Activity
            Intent myIntent = new Intent(this,
                    SensorsActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.notifications) {
            Toast.makeText(getApplicationContext(), "Notifications is clicked", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(this,
                    NotificationsActivity.class);
        } else if (id == R.id.signout) {
            Toast.makeText(getApplicationContext(), "Signout is clicked", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}