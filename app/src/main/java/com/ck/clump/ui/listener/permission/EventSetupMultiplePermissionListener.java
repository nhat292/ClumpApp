package com.ck.clump.ui.listener.permission;

import com.ck.clump.ui.activity.EventSetupActivity;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class EventSetupMultiplePermissionListener implements MultiplePermissionsListener {

    private final WeakReference<EventSetupActivity> activity;

    public EventSetupMultiplePermissionListener(EventSetupActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
            activity.get().showPermissionGranted(response.getPermissionName());
        }

        for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
            activity.get().showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        activity.get().showPermissionRationale(token);
    }
}
