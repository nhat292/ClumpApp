package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.CreateEventResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.presenter.EventSetupPresenter;
import com.ck.clump.ui.view.EventSetupView;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.EmojiExcludeFilter;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.KeyboardUtil;
import com.ck.clump.util.SharedPreference;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.PermissionToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EventSetupActivity extends BaseActivity implements EventSetupView,
        OnMapReadyCallback, LocationListener {

    public static final int REQUEST_SELECT_DATE = 1;
    public static final int REQUEST_SELECT_TIME = 2;
    public static final int REQUEST_ADD_INFO = 3;
    public static final int REQUEST_GET_LOCATION = 4;

    @Inject
    public Service service;

    @Bind(R.id.edtEventName)
    EditText edtEventName;
    @Bind(R.id.tvSetDate)
    TextView tvSetDate;
    @Bind(R.id.tvSetTime)
    TextView tvSetTime;
    @Bind(R.id.btnAdditionalInfo)
    Button btnAdditionalInfo;
    @Bind(R.id.tvHeader)
    TextView tvHeader;
    @Bind(R.id.imvRight)
    TextView imvRight;
    @Bind(R.id.tvOptionDetails)
    TextView tvOptionDetails;
    @Bind(R.id.btnPinLocation)
    Button btnPinLocation;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;

    /*Others*/
    private Context mContext;
    private LatLng latLng;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mFragment;
    private Marker currLocationMarker;
    private EventSetupPresenter presenter;

    private String groupID = "";
    List<String> memberList = new ArrayList<>();
    private Date eventDate = new Date();
    private String locationName;

    private boolean isUpdate = false;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.FINISH_ACTIVITIES_AFTER_CREATE_EVENT_BROADCAST)) {
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectEventSetupActivity(this);
        renderView();
        /*Permissions*/
        presenter = new EventSetupPresenter(service, this);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constant.FINISH_ACTIVITIES_AFTER_CREATE_EVENT_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_event_setup);
        ButterKnife.bind(this);
        setupMap();
        locationName = getString(R.string.no_location);
        Intent intent = getIntent();
        if (intent.hasExtra("friendsID")) {
            memberList = intent.getStringArrayListExtra("friendsID");
        }
        if (intent.hasExtra("groupID")) {
            groupID = intent.getStringExtra("groupID");
        }

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvHeader.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        imvRight.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        edtEventName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvSetDate.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvSetTime.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvOptionDetails.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnAdditionalInfo.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnPinLocation.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));

        edtEventName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edtEventName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        setupViewIfUpdate();
    }

    private void setupViewIfUpdate() {
        Intent intent = getIntent();
        if (intent.hasExtra("AdditionInfo")) {
            isUpdate = true;
            ((TextView) findViewById(R.id.imvRight)).setText("Update");
            edtEventName.setText(intent.getStringExtra("Name"));
            eventDate = new Date(intent.getLongExtra("Time", 0));
            tvSetDate.setText(Common.showDate(eventDate));
            tvSetTime.setText(Common.showTime(eventDate));

            String additionInfo = intent.getStringExtra("AdditionInfo");
            if (additionInfo != null && !additionInfo.isEmpty()) {
                btnAdditionalInfo.setText(additionInfo);
            }
            locationName = intent.getStringExtra("LocationAddress");
            latLng = new LatLng(intent.getDoubleExtra("Latitude", 0), intent.getDoubleExtra("Longitude", 0));
        }
    }

    @Override
    public void showWait() {
        showLoadingDialog();
    }

    @Override
    public void removeWait() {
        dismissLoadingDialog();
    }

    @Override
    public void onFailure(String appErrorMessage, boolean finishActivity) {
        showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {
        CreateEventResponse response = (CreateEventResponse) o;
        Intent intent = new Intent(EventSetupActivity.this, EventDetailActivity.class);
        intent.putExtra("eventID", response.getdATA().getId());
        intent.putExtra("fromSetup", true);
        startActivity(intent);
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    @Override
    public void onUpdateSuccess(CreateEventResponse response) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_SELECT_DATE) {
            int day = data.getIntExtra("day", 1);
            int month = data.getIntExtra("month", 1) - 1;
            int year = data.getIntExtra("year", 2017);
            eventDate = getDate(year, month, day);
            tvSetDate.setText(Common.showDate(eventDate));
        }
        if (requestCode == REQUEST_SELECT_TIME) {
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            eventDate = getTime(hour, minute);
            tvSetTime.setText(Common.showTime(eventDate));
        }
        if (requestCode == REQUEST_ADD_INFO) {
            String info = data.getStringExtra("info");
            if (!info.equalsIgnoreCase("")) {
                btnAdditionalInfo.setText(info);
            }
        }
        if (requestCode == REQUEST_GET_LOCATION) {
            latLng = new LatLng(data.getDoubleExtra("lat", 0.0f), data.getDoubleExtra("lng", 0.0f));
            locationName = data.getStringExtra("locationName");
            zoomToLocation();
        }
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventDate);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    private Date getTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        KeyboardUtil.hideKeyboard(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        onClickLeft();
    }

    @OnClick(R.id.imvRight)
    public void onClickCreate() {
        if (edtEventName.getText().toString().equalsIgnoreCase("")) {
            showErrorMessage(getString(R.string.please_enter_event_title), false);
            return;
        }
        if (new Date().after(eventDate)) {
            showErrorMessage(getString(R.string.message_event_past_day), false);
            return;
        }
        Map<String, RequestBody> params = new HashMap<>();
        String startTime = String.valueOf(eventDate.getTime());
        String startTimeString = Common.getDateFormat(eventDate);
        String name = edtEventName.getText().toString();
        String latitude = "14.937062";
        String longitude = "103.013325";
        if (latLng != null) {
            latitude = String.valueOf(latLng.latitude);
            longitude = String.valueOf(latLng.longitude);
        }
        String locationAddress = locationName;
        String additionInfo = btnAdditionalInfo.getText().toString();
        if (additionInfo.equals(getString(R.string.provide_additional_info))) {
            additionInfo = "";
        }
        String memberListStr = "";
        if (memberList.size() > 0) {
            memberListStr += "[";
            for (int i = 0; i < memberList.size(); i++) {
                memberListStr += "\"" + memberList.get(i) + "\"";
                if (i < memberList.size() - 1) {
                    memberListStr += ",";
                }
            }
            memberListStr += "]";
        }
        params.put("startTime", RequestBody.create(MediaType.parse("text/plain"), startTime));
        params.put("name", RequestBody.create(MediaType.parse("text/plain"), name));
        params.put("latitude", RequestBody.create(MediaType.parse("text/plain"), latitude));
        params.put("longitude", RequestBody.create(MediaType.parse("text/plain"), longitude));
        params.put("locationAddress", RequestBody.create(MediaType.parse("text/plain"), locationAddress));
        params.put("additionInfo", RequestBody.create(MediaType.parse("text/plain"), additionInfo));
        if (isUpdate) {
            params.put("id", RequestBody.create(MediaType.parse("text/plain"), getIntent().getStringExtra("eventId")));
            presenter.updateEvent(params);
        } else {
            params.put("startTimeString", RequestBody.create(MediaType.parse("text/plain"), startTimeString));
            params.put("memberList", RequestBody.create(MediaType.parse("text/plain"), memberListStr));
            params.put("groupId", RequestBody.create(MediaType.parse("text/plain"), groupID));
            presenter.createEvent(params);
        }
    }

    @OnClick(R.id.tvSetDate)
    public void onClickSetDate() {
        Intent intent = new Intent(EventSetupActivity.this, SetDateActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_DATE);
    }

    @OnClick(R.id.tvSetTime)
    public void onClickSetTime() {
        Intent intent = new Intent(EventSetupActivity.this, SetTimeActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_TIME);
    }

    @OnClick(R.id.btnAdditionalInfo)
    public void onClickAdditionalInfo() {
        Intent intent = new Intent(this, EditInfoActivity.class);
        intent.putExtra("info", btnAdditionalInfo.getText().toString());
        startActivityForResult(intent, REQUEST_ADD_INFO);
    }

    @OnClick(R.id.btnPinLocation)
    public void onClickPinLocation() {
        Intent intent = new Intent(this, PinLocationActivity.class);
        startActivityForResult(intent, REQUEST_GET_LOCATION);
    }


    private void setupMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);

        if (isUpdate) {
            zoomToLocation();
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        /*if (!isUpdate) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            zoomToLocation();
        }*/
    }

    private void zoomToLocation() {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Common.createBitmapWithSize(mContext, R.drawable.pin, 50, 55)));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        //zoom to current position:
        float zoomLevel = locationName.equals(getString(R.string.no_location)) ? 17.0f : 15.0f;
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        if (isUpdate) return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.info_title)
                .setMessage(R.string.permission_rationale_message_multi_permission)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    public void showPermissionGranted(String permission) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, Constant.OTHER_ACTIVITIES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        SharedPreference.saveString(SharedPreference.KEY_CURRENT_VIEW, null);
        handler.removeCallbacks(dismissNotificationRunnable);
        llNotification.setVisibility(View.GONE);
    }

    private Handler handler = new Handler();
    private Runnable dismissNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            llNotification.setVisibility(View.GONE);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChatMessageEvent event) {
        if (llNotification.getVisibility() == View.VISIBLE) {
            handler.removeCallbacks(dismissNotificationRunnable);
        } else {
            llNotification.setVisibility(View.VISIBLE);
        }
        txtNotificationName.setText(event.getName());
        txtNotificationMessage.setText(event.getContent());
        handler.postDelayed(dismissNotificationRunnable, 2000);
    }
}
