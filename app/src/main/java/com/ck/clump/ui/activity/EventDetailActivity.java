package com.ck.clump.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.enums.EventStatus;
import com.ck.clump.enums.EventUserStatus;
import com.ck.clump.model.event.ChatMessageEvent;
import com.ck.clump.model.response.EventDetailResponse;
import com.ck.clump.model.response.SimpleResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.adapter.MemberAdapter;
import com.ck.clump.ui.activity.adapter.MenuAdapter;
import com.ck.clump.ui.activity.model.EventModel;
import com.ck.clump.ui.activity.model.Friend;
import com.ck.clump.ui.activity.model.Member;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.ui.listener.permission.WriteCalendarPermissionListener;
import com.ck.clump.ui.presenter.EventDetailPresenter;
import com.ck.clump.ui.view.EventDetailView;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.CalendarUtil;
import com.ck.clump.util.Common;
import com.ck.clump.util.Constant;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.SharedPreference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

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

public class EventDetailActivity extends BaseActivity implements EventDetailView, OnMapReadyCallback {

    public static int REQUEST_UPDATE = 1;
    public static final int PICK_FRIENDS_REQUEST = 2;

    @Inject
    public Service service;

    @Bind(R.id.tvDateTime)
    TextView tvDateTime;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.rvMembers)
    RecyclerView rvMembers;
    @Bind(R.id.tvDescription)
    TextView tvDescription;
    @Bind(R.id.btnLocationName)
    Button btnLocationName;
    @Bind(R.id.imvBackground)
    ImageView imvBackground;
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.tvIn)
    TextView tvIn;
    @Bind(R.id.tvOut)
    TextView tvOut;
    @Bind(R.id.imvIn)
    ImageView imvIn;
    @Bind(R.id.imvOut)
    ImageView imvOut;
    @Bind(R.id.imvRight)
    ImageView imvRight;
    @Bind(R.id.tvDetails)
    TextView tvDetails;

    @Bind(R.id.llNotification)
    LinearLayout llNotification;
    @Bind(R.id.txtNotificationName)
    TextView txtNotificationName;
    @Bind(R.id.txtNotificationMessage)
    TextView txtNotificationMessage;


    /*Others*/
    private Context mContext;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mFragment;
    private MemberAdapter mAdapter;
    private List<Member> mMembers = new ArrayList<>();
    private EventDetailPresenter presenter;
    private SweetAlertDialog mDialog;
    private Handler countInOutHandle = new Handler();

    private String eventID = "";
    private String eventStatus = "";
    private EventModel eventDetail;
    private String actionCountInOut;

    private boolean needRefreshEventList = false;
    private boolean fromSetupEvent = false;

    private PermissionListener calendarPermissionListener;
    private String calendarAction = "";
    private boolean askPermissionFirst = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getDeps().injectEventDetailActivity(this);
        renderView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        createPermissionListeners();
        checkCalenderPermission();
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    private void renderView() {
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);
        setupMap();
        setupRecycleView();

        presenter = new EventDetailPresenter(service, this);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        if (intent.hasExtra("fromSetup")) {
            fromSetupEvent = true;
        }
        if (intent.hasExtra("eventStatus")) {
            eventStatus = intent.getStringExtra("eventStatus");
            if (eventStatus.equals(EventStatus.PAST.getValue())) {
                imvRight.setVisibility(View.INVISIBLE);
            }
        }

        txtNotificationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        txtNotificationMessage.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvStatus.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvIn.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvOut.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvDetails.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDescription.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        tvDateTime.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnLocationName.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));

        if (fromSetupEvent) {
            loadDelay();
        } else {
            presenter.getEventDetail(eventID, true);
        }
    }

    private void loadDelay() {
        showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.getEventDetail(eventID, false);
            }
        }, 2000);
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
        enableOrDisableCountInCountOut(true);
        showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {
        EventDetailResponse response = (EventDetailResponse) o;
        eventDetail = response.getdATA();
        Date date = new Date(eventDetail.getStartTime());
        tvDateTime.setText(Common.showDateTime(date));
        tvName.setText(eventDetail.getName());
        if (eventDetail.getAdditionInfo() != null && !eventDetail.getAdditionInfo().isEmpty()) {
            tvDescription.setText(eventDetail.getAdditionInfo());
        }
        mMembers.clear();
        mMembers.add(new Member("", "", "", "", false, ""));
        mMembers.addAll(response.getdATA().getMemberList());
        mAdapter.notifyDataSetChanged();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            imvBackground.setImageResource(R.drawable.event_detail_morning);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            imvBackground.setImageResource(R.drawable.event_detail_afternoon);
        } else {
            imvBackground.setImageResource(R.drawable.event_detail_evening);
        }

        if (eventStatus.equals(EventStatus.PAST.getValue())) {
            tvIn.setEnabled(false);
            tvOut.setEnabled(false);
            if (eventDetail.getLoginUserStatus().equals(EventUserStatus.COUNT_IN.getValue())) {
                imvIn.setImageResource(R.drawable.rsvp_in_active);
                imvOut.setImageResource(R.drawable.rsvp_out_inactive);
                tvIn.setTextColor(getResources().getColor(R.color.colorWhite));
                tvOut.setTextColor(getResources().getColor(R.color.textColorNormal));
            } else if (eventDetail.getLoginUserStatus().equals(EventUserStatus.COUNT_OUT.getValue())) {
                imvIn.setImageResource(R.drawable.rsvp_in_inactive);
                imvOut.setImageResource(R.drawable.rsvp_out_active);
                tvIn.setTextColor(getResources().getColor(R.color.textColorNormal));
                tvOut.setTextColor(getResources().getColor(R.color.colorWhite));
            } else {
                imvIn.setImageResource(R.drawable.rsvp_in_inactive);
                imvOut.setImageResource(R.drawable.rsvp_out_inactive);
                tvIn.setTextColor(getResources().getColor(R.color.textColorNormal));
                tvOut.setTextColor(getResources().getColor(R.color.textColorNormal));
            }
        } else {
            if (eventDetail.getLoginUserStatus().equals(EventUserStatus.COUNT_IN.getValue())
                    || eventDetail.getLoginUserStatus().equals(EventUserStatus.OWNER.getValue())) {
                updateUI(EventUserStatus.COUNT_IN.getValue(), true);
            } else if (eventDetail.getLoginUserStatus().equals(EventUserStatus.COUNT_OUT.getValue())) {
                updateUI(EventUserStatus.COUNT_OUT.getValue(), true);
            } else {
                updateUI(EventUserStatus.NOT_YET.getValue(), true);
            }
        }

        if (mGoogleMap == null)
            return;
        btnLocationName.setText(eventDetail.getLocationAddress());
        if (eventDetail.getLocationAddress().equals(getString(R.string.no_location))) {
            zoomToLocation(new LatLng(response.getdATA().getLatitude(), response.getdATA().getLongitude()), 17.0f);
        } else {
            zoomToLocation(new LatLng(response.getdATA().getLatitude(), response.getdATA().getLongitude()), 15.0f);
        }
    }

    @Override
    public void onCountInSuccess() {
        needRefreshEventList = true;
        showInfoMessage(getString(R.string.message_attending_event));
        updateUI(EventUserStatus.COUNT_IN.getValue(), true);
        calendarAction = "Add";
        addOrRemoveFromCalendar();
        enableOrDisableCountInCountOut(true);
    }

    @Override
    public void onCountOutSuccess() {
        needRefreshEventList = true;
        updateUI(EventUserStatus.COUNT_OUT.getValue(), true);
        calendarAction = "Remove";
        addOrRemoveFromCalendar();
        enableOrDisableCountInCountOut(true);
    }

    @Override
    public void onDeleteEventSuccess(SimpleResponse response) {
        if (response.getcODE() == 200) {
            onClickLeft();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onAddMemberSuccess(SimpleResponse response) {
        if (response.getcODE() == 200) {
            presenter.getEventDetail(eventID, true);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void logout() {
        clearDataAndLogout();
    }

    private void checkCalenderPermission() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(calendarPermissionListener, Manifest.permission.WRITE_CALENDAR);
    }

    private void addOrRemoveFromCalendar() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        askPermissionFirst = false;
        Dexter.checkPermission(calendarPermissionListener, Manifest.permission.WRITE_CALENDAR);
    }

    private void updateUI(String status, boolean updateMember) {
        if (status.equals(EventUserStatus.COUNT_IN.getValue())
                || status.equals(EventUserStatus.OWNER.getValue())) {
            imvIn.setImageResource(R.drawable.rsvp_in_active);
            imvOut.setImageResource(R.drawable.rsvp_out_inactive);
            tvIn.setTextColor(getResources().getColor(R.color.colorWhite));
            tvOut.setTextColor(getResources().getColor(R.color.textColorNormal));
            tvStatus.setText(EventUserStatus.COUNT_IN.getDisplay());
            tvStatus.setTextColor(getResources().getColor(R.color.textColorNormal));
        } else if (status.equals(EventUserStatus.COUNT_OUT.getValue())) {
            imvIn.setImageResource(R.drawable.rsvp_in_inactive);
            imvOut.setImageResource(R.drawable.rsvp_out_active);
            tvIn.setTextColor(getResources().getColor(R.color.textColorNormal));
            tvOut.setTextColor(getResources().getColor(R.color.colorWhite));
            tvStatus.setText(EventUserStatus.COUNT_OUT.getDisplay());
            tvStatus.setTextColor(getResources().getColor(R.color.textColorNormal));
        } else {
            imvIn.setImageResource(R.drawable.rsvp_in_inactive);
            imvOut.setImageResource(R.drawable.rsvp_out_inactive);
            tvIn.setTextColor(getResources().getColor(R.color.textColorNormal));
            tvOut.setTextColor(getResources().getColor(R.color.textColorNormal));
            tvStatus.setText(EventUserStatus.NOT_YET.getDisplay());
            tvStatus.setTextColor(getResources().getColor(R.color.red_btn_bg_pressed_color));
        }
        if (updateMember) {
            updateMemberList(status);
        }
    }

    private void updateMemberList(String status) {
        UserModel userModel = UserModel.currentUser();
        for (int i = 0; i < mMembers.size(); i++) {
            if (mMembers.get(i).getId().equals(userModel.getId())) {
                mMembers.get(i).setEventConfirm(status);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.imvLeft)
    public void onClickLeft() {
        if (needRefreshEventList) {
            setResult(RESULT_OK);
        }
        if (fromSetupEvent) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constant.REFRESH_EVENT_LIST_BROADCAST));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constant.FINISH_ACTIVITIES_AFTER_CREATE_EVENT_BROADCAST));
        }
        finish();
    }

    @OnClick(R.id.imvRight)
    public void onRightClick() {
        List<String> items = new ArrayList<>();
        items.add("Edit Event");
        items.add("Delete Event");
        showMenu(items);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_UPDATE) {
            needRefreshEventList = true;
            presenter.getEventDetail(eventID, true);
        }
        if (requestCode == PICK_FRIENDS_REQUEST) {
            List<Friend> friends = data.getParcelableArrayListExtra("DATA");
            for (Member member : mMembers) {
                for (Friend friend : friends) {
                    if (friend.getId().equals(member.getId())) {
                        friends.remove(friend);
                    }
                }
            }
            String memberListStr = "";
            if (friends.size() > 0) {
                memberListStr += "[";
                for (int i = 0; i < friends.size(); i++) {
                    memberListStr += "\"" + friends.get(i).getId() + "\"";
                    if (i < friends.size() - 1) {
                        memberListStr += ",";
                    }
                }
                memberListStr += "]";
                Map<String, String> params = new HashMap<>();
                params.put("eventId", eventID);
                params.put("memberList", memberListStr);
                presenter.addMember(params);
            }
        }
    }

    @Override
    public void onBackPressed() {
        onClickLeft();
    }

    @OnClick(R.id.tvIn)
    public void onCountInClick() {
        if (eventDetail == null || eventStatus.equals(EventStatus.PAST.getValue()))
            return;
        updateUI(EventUserStatus.COUNT_IN.getValue(), false);
        actionCountInOut = EventUserStatus.COUNT_IN.getValue();
        countInOutHandle.removeCallbacks(countInOutRunnable);
        countInOutHandle.postDelayed(countInOutRunnable, 1000);
    }

    @OnClick(R.id.tvOut)
    public void onCountOutClick() {
        if (eventDetail == null || eventStatus.equals(EventStatus.PAST.getValue()))
            return;
        updateUI(EventUserStatus.COUNT_OUT.getValue(), false);
        actionCountInOut = EventUserStatus.COUNT_OUT.getValue();
        countInOutHandle.removeCallbacks(countInOutRunnable);
        countInOutHandle.postDelayed(countInOutRunnable, 1000);
    }

    private Runnable countInOutRunnable = new Runnable() {
        @Override
        public void run() {
            enableOrDisableCountInCountOut(false);
            actionCountInOut(actionCountInOut);
        }
    };

    private void actionCountInOut(String eventConfirm) {
        Map<String, String> params = new HashMap<>();
        params.put("eventId", eventDetail.getId());
        params.put("eventConfirm", eventConfirm);
        presenter.eventCount(params);
    }

    private void setupRecycleView() {
        /*Members*/
        mAdapter = new MemberAdapter(mContext, mMembers, new MemberAdapter.OnItemClickListener() {
            @Override
            public void onClick(Member Item) {
                if (Item.getId().isEmpty()) {
                    if (eventStatus.equals(EventStatus.PAST.getValue())) return;
                    //Pick Friends
                    Intent intent = new Intent(EventDetailActivity.this, PickFriendActivity.class);
                    intent.putExtra("ACTION", "PICK");
                    startActivityForResult(intent, PICK_FRIENDS_REQUEST);
                } else {
                    Intent intent = new Intent(EventDetailActivity.this, FriendInfoActivity.class);
                    intent.putExtra("userId", Item.getId());
                    startActivity(intent);
                }
            }
        });
        rvMembers.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvMembers.setAdapter(mAdapter);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);
    }

    private void zoomToLocation(LatLng latLng, float zoom) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Common.createBitmapWithSize(mContext, R.drawable.pin, 50, 55)));
        mGoogleMap.addMarker(markerOptions);
        //zoom to current position:
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
                .setMessage(R.string.permission_write_calendar)
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
        if (askPermissionFirst) return;
        if (calendarAction.equals("Add")) {
            long time = eventDetail.getStartTime();
            if (time < 0) {
                time = eventDetail.getCreatedAt();
            }
            CalendarUtil.saveEventToCalendar(eventDetail.getName(), time, eventDetail.getId());
        } else {
            CalendarUtil.removeEventFromCalendar(eventDetail.getId());
        }
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
    }

    private void createPermissionListeners() {
        WriteCalendarPermissionListener feedbackViewPermissionListener =
                new WriteCalendarPermissionListener(this);
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();

        calendarPermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                        R.string.permission_write_calendar)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
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
        countInOutHandle.removeCallbacks(countInOutRunnable);
    }

    private void enableOrDisableCountInCountOut(boolean enable) {
        tvIn.setEnabled(enable);
        tvOut.setEnabled(enable);
        tvIn.setClickable(enable);
        tvOut.setClickable(enable);
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

    private void showMenu(List<String> items) {
        mDialog = new SweetAlertDialog(mContext, SweetAlertDialog.RECYCLER_VIEW_TYPE);
        mDialog.setTitleText(mContext.getString(R.string.menu));
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        mDialog.hideSearchBar();
        mDialog.hideButtonControl();
        mDialog.hideTitleMessage();

        // Setup RecyclerView
        RecyclerView recyclerView = mDialog.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerView.setAdapter(new MenuAdapter(getApplicationContext(), items, new MenuAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(String Item, int position) {
                mDialog.dismissWithAnimation();
                switch (position) {
                    case 0:
                        Intent intent = new Intent(EventDetailActivity.this, EventSetupActivity.class);
                        intent.putExtra("eventId", eventDetail.getId());
                        intent.putExtra("AdditionInfo", eventDetail.getAdditionInfo());
                        intent.putExtra("Name", eventDetail.getName());
                        long time = eventDetail.getStartTime();
                        if (time < 0) {
                            time = eventDetail.getCreatedAt();
                        }
                        intent.putExtra("Time", time);
                        intent.putExtra("Latitude", eventDetail.getLatitude());
                        intent.putExtra("Longitude", eventDetail.getLongitude());
                        intent.putExtra("LocationAddress", eventDetail.getLocationAddress());
                        startActivityForResult(intent, REQUEST_UPDATE);
                        break;
                    case 1:
                        SweetAlertDialog dialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
                        dialog.setTitleText(getString(R.string.delete));
                        dialog.setContentText(getString(R.string.message_delete_event_confirm));
                        dialog.show();
                        dialog.setCancelText(getString(R.string.no));
                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                needRefreshEventList = true;
                                presenter.deleteEvent(eventID);
                            }
                        });
                        break;
                }
            }
        }));
    }

}
