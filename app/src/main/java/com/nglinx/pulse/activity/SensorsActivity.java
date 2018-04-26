package com.nglinx.pulse.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nglinx.pulse.R;
import com.nglinx.pulse.adaptor.DevicesAdapter;
import com.nglinx.pulse.constants.ApplicationConstants;
import com.nglinx.pulse.models.DeviceModel;
import com.nglinx.pulse.models.GroupMemberModel;
import com.nglinx.pulse.models.GroupModel;
import com.nglinx.pulse.models.ResponseDto;
import com.nglinx.pulse.models.UserModel;
import com.nglinx.pulse.models.UserTrackingModel;
import com.nglinx.pulse.session.DataSession;
import com.nglinx.pulse.session.SharedPrefUtility;
import com.nglinx.pulse.utils.ApplicationUtils;
import com.nglinx.pulse.utils.ProgressbarUtil;
import com.nglinx.pulse.utils.dialog.AbstractActivity;
import com.nglinx.pulse.utils.dialog.DialogUtils;
import com.nglinx.pulse.utils.retrofit.ApiEndpointInterface;
import com.nglinx.pulse.utils.retrofit.RetroResponse;
import com.nglinx.pulse.utils.retrofit.RetroUtils;
import com.nglinx.pulse.utils.view.HorizontalView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SensorsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<GroupMemberModel> mySensors = new ArrayList<GroupMemberModel>();

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.customer_image1};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();

    private DevicesAdapter devicesAdapter;
    private HorizontalView devicesListView;

    private DataSession ds = DataSession.getInstance();

    private WebView webView;
    private RelativeLayout r11;
    private RelativeLayout r3;
    private RelativeLayout r20;
    private RelativeLayout rl31;
    private RelativeLayout rl40;

    DeviceClickListener deviceClickListener = new DeviceClickListener();
    String url;

    public DrawerLayout drawerLayout;

    private TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_sensors);
        devicesAdapter = new DevicesAdapter(getApplicationContext(), mySensors);

        mySensors = new ArrayList<GroupMemberModel>();
        getSensorList();

        devicesListView = (HorizontalView) findViewById(R.id.gallery);

        devicesListView.setAdapter(devicesAdapter);
        devicesListView.setOnItemClickListener(deviceClickListener);

        r11 = findViewById(R.id.frame1);
        r3 = findViewById(R.id.frame3);
        r20 = findViewById(R.id.frame20);
        rl31 = findViewById(R.id.frame31);
        rl40 = findViewById(R.id.frame40);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View headerLayout = navigationView.getHeaderView(0);
        user_name = (TextView) headerLayout.findViewById(R.id.username);
        user_name.setText(ds.getUserModel().getName());

        initPager();
    }

    private void renderPost(String url) {

        if (null != webView) {
            webView.stopLoading();
            webView.clearView();
            webView.clearHistory();
            webView.resumeTimers();
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearCache(true);
            webView = null;
        }

        webView = (WebView) findViewById(R.id.webView);
        System.out.println("Web view original url after findViewById: " + webView.getOriginalUrl());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebChromeClient(null);
        final ProgressDialog mProgressDialog1 = ProgressbarUtil.startProgressBar(SensorsActivity.this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                ProgressbarUtil.stopProgressBar(mProgressDialog1);
            }
        });

        this.url = url;
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (url.contains("1a")) {
            this.url = "http://device.nglinx.com:8500/";
        }
        System.out.println("URL being Passed : " + this.url);

        System.out.println("URL retrieved from Web before loadurl : " + webView.getUrl());
        webView.clearCache(true);
        webView.loadUrl(this.url);
        System.out.println("URL retrieved from Web before reload: " + webView.getUrl());

        System.out.println("Web view original url: " + webView.getOriginalUrl());

        r11.setVisibility(View.GONE);
        r3.setVisibility(View.GONE);
        r20.setVisibility(View.GONE);
        rl40.setVisibility(View.VISIBLE);
        rl31.setVisibility(View.VISIBLE);

        if (url.contains("1a")) {
            webView.setVisibility(View.INVISIBLE);
        } else {
            webView.setVisibility(View.VISIBLE);
        }
    }

    private GroupMemberModel getClearDataSensor() {
        GroupMemberModel dm = new GroupMemberModel();
        dm.setTrackingModel(new UserTrackingModel());
        dm.getTrackingModel().setUuid(ApplicationConstants.DUMMY_USER_CLEAR_ID);
        dm.setName("Clear");
        return dm;
    }

    public void getSensorList() {

        final ProgressDialog mProgressDialog1 = ProgressbarUtil.startProgressBar(SensorsActivity.this);

        ApiEndpointInterface apiEndpointInterface = RetroUtils.getHostAdapterForAuthenticate(getApplicationContext(), RetroUtils.URL_HIT).create(ApiEndpointInterface.class);
        apiEndpointInterface.getGroups(ds.getUserModel().getId(), "web", new RetroResponse<GroupModel>() {

            @Override
            public void onSuccess() {
                ProgressbarUtil.stopProgressBar(mProgressDialog1);
                mySensors.clear();
                mySensors.add(getClearDataSensor());
                List<GroupModel> groupsList = new ArrayList<GroupModel>(models);
                mySensors.addAll(groupsList.get(0).getMembers());
                devicesAdapter.setArr2(mySensors);
                devicesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {
                ProgressbarUtil.stopProgressBar(mProgressDialog1);
                if ((errorMsg == null) || (errorMsg.trim().isEmpty())) {
                    errorMsg = "Failed to get the fence list. Server Error";
                }
                if (errorMsg.contains("Device Not Found with userId")) {
                    ProgressbarUtil.stopProgressBar(mProgressDialog1);
                    mySensors.clear();
                    mySensors.add(getClearDataSensor());
                    devicesAdapter.setArr2(mySensors);
                    devicesAdapter.notifyDataSetChanged();
                    DialogUtils.diaplayErrorDialog(SensorsActivity.this, "No Sensors configured for this User");
                } else {
                    DialogUtils.diaplayErrorDialog(SensorsActivity.this, errorMsg);
                }
            }
        });
    }

    class DeviceClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            GroupMemberModel dm = mySensors.get(position);
            ds.setSelectedDeviceId(dm.getId());
            Toast.makeText(getApplicationContext(), "Sensor Selected : " + dm.getName(), Toast.LENGTH_LONG).show();
            String webUrl = ApplicationConstants.DEVICE_GRAPH_URL + dm.getTrackingModel().getUuid();
            renderPost(webUrl);
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
            case R.id.home:
                Intent intent = new Intent(SensorsActivity.this, SensorsActivity.class);
                startActivity(intent);
                return true;
            case R.id.notifications:
                Intent intent2 = new Intent(SensorsActivity.this, NotificationsActivity.class);
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


    private void initPager() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) findViewById(R.id.pager1);
        mPager.setAdapter(new MyAdapter(SensorsActivity.this,XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);
    }

}