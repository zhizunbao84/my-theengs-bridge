package com.example.theengsbg;

import android.app.Service;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.*;
import android.os.*;
import android.util.Log;

import java.util.*;

public class BgService extends Service {

    private static final String TAG = "TheengsBg";
    private BluetoothAdapter adapter;
    private ScanCallback callback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service onCreate");
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        adapter = bm.getAdapter();
        startScan();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "service onStartCommand");
        // 被杀后自动重启
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void startScan() {
        if (adapter == null || !adapter.isEnabled()) {
            Log.e(TAG, "BT unavailable");
            stopSelf();
            return;
        }
        // 6.0+ 权限已在安装时授予（adb install -g）
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        callback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                byte[] raw = result.getScanRecord().getBytes();
                String hex = bytesToHex(raw);
                String json = TheengsJni.decode(hex);
                Log.i(TAG, "mac=" + result.getDevice().getAddress() + " json=" + json);
                // TODO: 上传/写文件/广播
            }
        };
        adapter.getBluetoothLeScanner().startScan(Collections.emptyList(), settings, callback);
        Log.i(TAG, "BLE scan started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null && callback != null)
            adapter.getBluetoothLeScanner().stopScan(callback);
        Log.i(TAG, "service onDestroy");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }
}
