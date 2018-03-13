package com.ck.clump.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ck.clump.BuildConfig;
import com.ck.clump.R;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.request.VerifyCodeRequest;
import com.ck.clump.model.response.VerifyCodeResponse;
import com.ck.clump.service.Service;
import com.ck.clump.model.event.VerificationCodeReceivedEvent;
import com.ck.clump.ui.presenter.SignUpTwoPresenter;
import com.ck.clump.ui.view.SignUpTwoView;
import com.ck.clump.util.FontUtil;
import com.ck.clump.util.KeyboardUtil;
import com.ck.clump.util.SharedPreference;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class SignUpTwoActivity extends BaseActivity implements SignUpTwoView, TextWatcher {

    @Inject
    public Service service;

    @Bind(R.id.asutCode1)
    EditText asutCode1;
    @Bind(R.id.asutCode2)
    EditText asutCode2;
    @Bind(R.id.asutCode3)
    EditText asutCode3;
    @Bind(R.id.asutCode4)
    EditText asutCode4;
    @Bind(R.id.asutCode)
    EditText asutCode;
    @Bind(R.id.asutTv1)
    TextView asutTv1;
    @Bind(R.id.asutTv2)
    TextView asutTv2;
    @Bind(R.id.asuoBtnDone)
    Button asuoBtnDone;

    /*Others*/
    private String activeCode, phone;
    private SignUpTwoPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeps().injectSignUpTwoActivity(this);
        renderView();

        presenter = new SignUpTwoPresenter(service, this);
        activeCode = getIntent().getStringExtra("active_code");
        phone = getIntent().getStringExtra("phone");
        if (BuildConfig.DEBUG) {
            showInfoMessage("Your active code: " + activeCode);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onStop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VerificationCodeReceivedEvent event) {
        KeyboardUtil.hideKeyboard(this);
        asutCode.setText(event.getVerificationCode());
        String inputActiveCode = asutCode.getText().toString();
        presenter.postVerifyCode(new VerifyCodeRequest(phone, inputActiveCode));
    }

    private void renderView() {
        setContentView(R.layout.activity_sign_up_two);
        ButterKnife.bind(this);
        asutCode.addTextChangedListener(this);
        asutCode.requestFocus();
        KeyboardUtil.showKeyBoard(this);
        asutCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asutCode.setSelection(asutCode.getText().toString().length());
            }
        });

        asutTv1.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asutTv2.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asuoBtnDone.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        asutCode1.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asutCode2.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asutCode3.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        asutCode4.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
    }

    @OnClick(R.id.asuoBtnDone)
    public void onClickDone() {
        String inputActiveCode = asutCode.getText().toString();
        KeyboardUtil.hideKeyboard(this);
        if (inputActiveCode.length() != 4) {
            showErrorMessage(getString(R.string.message_please_enter_4_digits), false);
        } else if (!inputActiveCode.equalsIgnoreCase(activeCode)) {
            showErrorMessage(getString(R.string.message_code_is_not_correct), false);
        } else {
            presenter.postVerifyCode(new VerifyCodeRequest(phone, inputActiveCode));
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
        final VerifyCodeResponse response = (VerifyCodeResponse) o;
        SharedPreference.saveString(SharedPreference.KEY_API_TOKEN, response.getDATA().getSessionToken());
        this.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateObjectFromJson(UserModel.class, new Gson().toJson(response.getDATA()));
            }
        });
        setResult(SignUpOneActivity.REQUEST_ACTIVE_CODE);
        finish();
    }

    @Override
    public void logout() {

    }

    private String getCode() {
        return asutCode.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String code = getCode();
        if (code.length() != 4) {
            asuoBtnDone.setEnabled(false);
            asuoBtnDone.setBackgroundResource(R.drawable.bg_btn_secondary);
        } else {
            asuoBtnDone.setEnabled(true);
            asuoBtnDone.setBackgroundResource(R.drawable.bg_btn_primary);
        }
        TextView[] codeViews = {asutCode1, asutCode2, asutCode3, asutCode4};
        for (int i = 0; i < codeViews.length; i++) {
            codeViews[i].setText("");
            codeViews[i].setBackgroundResource(R.drawable.bg_text_verification_digit_blank);
        }
        for (int i = 0; i < code.length(); i++) {
            codeViews[i].setText(String.valueOf(code.charAt(i)));
            codeViews[i].setBackgroundResource(R.drawable.bg_text_verification_digit_present);
        }
    }
}
