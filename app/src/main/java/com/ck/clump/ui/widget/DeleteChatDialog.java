package com.ck.clump.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ck.clump.R;
import com.ck.clump.util.FontUtil;

/**
 * Created by Nhat on 9/21/17.
 */

public class DeleteChatDialog extends Dialog implements View.OnClickListener, Animation.AnimationListener {

    private TextView tvTitle;
    private Button btnDelete;
    private Button btnCancel;
    private RelativeLayout rlRoot;
    private LinearLayout llDialogContent;
    private Animation slideUpAnimation, slideDownAnimation;

    private DeleteClickListener listener;
    private String title;

    public DeleteChatDialog(@NonNull Context context, String title, DeleteClickListener listener) {
        super(context);
        this.title = title;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_message);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        rlRoot = (RelativeLayout) findViewById(R.id.root);
        llDialogContent = (LinearLayout) findViewById(R.id.llDialogContent);

        tvTitle.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_REGULAR));
        btnDelete.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));
        btnCancel.setTypeface(FontUtil.getTypeface(FontUtil.FontStyle.PT_SAN_BOLD));

        slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

        btnDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        rlRoot.setOnClickListener(this);

        slideDownAnimation.setAnimationListener(this);

        tvTitle.setText(title);

        llDialogContent.startAnimation(slideUpAnimation);
    }

    @Override
    public void onClick(View v) {
        if(v == btnDelete) {
            listener.onClick();
            dismiss();
        } else {
            llDialogContent.startAnimation(slideDownAnimation);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        dismiss();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public interface DeleteClickListener {
        void onClick();
    }
}
