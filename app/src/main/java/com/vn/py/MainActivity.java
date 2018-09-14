package com.vn.py;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WifiAp8.0_MainActivity";
    private RelativeLayout mLayout;
    private Button mBtnStart,mBtnStop;

    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;

    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    private WifiApReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.relative_layout);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnStop.setOnClickListener(this);

        mReceiver = new WifiApReceiver();
        IntentFilter filter = new IntentFilter(WIFI_AP_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                if(getWifiAPState() != WIFI_AP_STATE_ENABLED){
                    if (Build.VERSION.SDK_INT >= 25) {
                        setWifiConfig("abc","123456789");
                        mConnectivityManager.startTethering(ConnectivityManager.TETHERING_WIFI,
                                true, new ONStartTetheringCallback());
                    }
                }
                break;
            case R.id.btn_stop:
                if(getWifiAPState() != WIFI_AP_STATE_DISABLED){
                    if (Build.VERSION.SDK_INT >= 25) {
                        mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
                    }
                }
                break;
        }
    }

    class ONStartTetheringCallback extends
            ConnectivityManager.OnStartTetheringCallback {
    }

    private class WifiApReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: " + intent.getAction());
            if (WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int cstate = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
                if (cstate == WIFI_AP_STATE_ENABLED) {
                    Snackbar.make(mLayout, "热点已开启", Snackbar.LENGTH_LONG).show();
                }else if (cstate == WIFI_AP_STATE_ENABLING){
                    Snackbar.make(mLayout, "热点正在开启", Snackbar.LENGTH_SHORT).show();
                }else if (cstate == WIFI_AP_STATE_DISABLED){
                    Snackbar.make(mLayout, "热点已关闭", Snackbar.LENGTH_LONG).show();
                }else if (cstate == WIFI_AP_STATE_DISABLING){
                    Snackbar.make(mLayout, "热点正在关闭", Snackbar.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void setWifiConfig(String SSID,String passWord){
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = SSID ;
        netConfig.preSharedKey = passWord;
        netConfig.hiddenSSID = false;
        netConfig.status = WifiConfiguration.Status.ENABLED;
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        Method mMethod;
        try {
            mMethod = mWifiManager.getClass().getMethod("setWifiApConfiguration");
            mMethod.invoke(mWifiManager, netConfig,false);
            mWifiManager.saveConfiguration();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                mMethod = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                mMethod.invoke(mWifiManager, netConfig, true);
                mWifiManager.saveConfiguration();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    public int getWifiAPState() {
        int state = -1;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getWifiAPState.state " + state);
        return state;
    }
}
