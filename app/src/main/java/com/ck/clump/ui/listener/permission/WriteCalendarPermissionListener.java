package com.ck.clump.ui.listener.permission;

import com.ck.clump.ui.activity.EventDetailActivity;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.ref.WeakReference;

/**
 * Created by Nhat on 5/14/17.
 */

public class WriteCalendarPermissionListener implements PermissionListener {

    private final WeakReference<EventDetailActivity> activity;

    public WriteCalendarPermissionListener(EventDetailActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        activity.get().showPermissionGranted(response.getPermissionName());
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        activity.get().showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        activity.get().showPermissionRationale(token);
    }
}
