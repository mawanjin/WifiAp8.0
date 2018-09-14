package com.vn.py;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by root on 17-11-22.
 */

@RuntimePermissions
public class WelcomeActivity extends Activity {
    private final String TAG = "WifiAp8.0";
    protected static final int MSG_OK = 1;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_OK) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        WelcomeActivityPermissionsDispatcher.writeSettingsWithCheck(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @NeedsPermission(value = Manifest.permission.WRITE_SETTINGS)
    void writeSettings() {
        Log.d(TAG, "writeSettings");
        myHandler.sendEmptyMessage(MSG_OK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        WelcomeActivityPermissionsDispatcher.onActivityResult(this, requestCode);
    }

    @OnShowRationale(Manifest.permission.WRITE_SETTINGS)
    void whyNeedPermission(final PermissionRequest request) {
        Log.d(TAG, "whyNeedPermission");
    }

    @OnPermissionDenied(Manifest.permission.WRITE_SETTINGS)
    void remindMessage() {
        Log.d(TAG, "remindMessage");

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
