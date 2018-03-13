package com.ck.clump.ui.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.BlockingActivity;
import com.ck.clump.ui.activity.SettingChatActivity;
import com.ck.clump.ui.activity.SettingNotificationActivity;
import com.ck.clump.ui.activity.SettingProfileActivity;
import com.ck.clump.ui.activity.TermAndConditionActivity;
import com.ck.clump.ui.widget.sweet_alert.SweetAlertDialog;
import com.ck.clump.util.FontUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.tvAccount)
    TextView tvAccount;
    @Bind(R.id.tvGeneral)
    TextView tvGeneral;
    @Bind(R.id.tvMore)
    TextView tvMore;
    @Bind(R.id.btnEditProfile)
    Button btnEditProfile;
    @Bind(R.id.btnNotifications)
    Button btnNotifications;
    @Bind(R.id.btnChatSettings)
    Button btnChatSettings;
    @Bind(R.id.btnBlocking)
    Button btnBlocking;
    @Bind(R.id.btnAbout)
    Button btnAbout;
    @Bind(R.id.btnTermConditions)
    Button btnTermConditions;
    @Bind(R.id.btnLogout)
    Button btnLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        applyFonts();
        return view;
    }


    private void applyFonts() {
        tvAccount.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvGeneral.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        tvMore.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnEditProfile.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnNotifications.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnChatSettings.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnBlocking.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnAbout.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnTermConditions.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnLogout.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick(R.id.btnEditProfile)
    public void onClickEditProfile() {
        Intent intent = new Intent(getActivity(), SettingProfileActivity.class);
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.btnNotifications)
    public void onClickNotifications() {
        Intent intent = new Intent(getActivity(), SettingNotificationActivity.class);
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.btnChatSettings)
    public void onClickChatSettings() {
        Intent intent = new Intent(getActivity(), SettingChatActivity.class);
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.btnBlocking)
    public void onClicBlocking() {
        Intent intent = new Intent(getActivity(), BlockingActivity.class);
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.btnAbout)
    public void onClickAbout() {

    }

    @OnClick(R.id.btnTermConditions)
    public void onClickTermCondition() {
        Intent intent = new Intent(getActivity(), TermAndConditionActivity.class);
        intent.putExtra("FromSetting", true);
        startActivity(intent);
    }

    @OnClick(R.id.btnLogout)
    public void onClickLogout() {
        SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(getString(R.string.logout));
        dialog.setContentText(getString(R.string.logout_confirm));
        dialog.show();
        dialog.setCancelText(getString(R.string.no));
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                baseActivity.clearDataAndLogout();
            }
        });
    }
}
