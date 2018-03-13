package com.ck.clump.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ck.clump.ui.activity.BaseActivity;

/**
 * Created by anthony on 4/6/17.
 */

public abstract class BaseFragment extends Fragment {

    protected BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
    }

}
