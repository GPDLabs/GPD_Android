package com.example.myapplication.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BlueToothController {

    private BluetoothAdapter mAdapter;


    public BlueToothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * is support bluetooth
     * @return
     */
    public boolean isSupportBlueTooth(){
        if(mAdapter != null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Determine the current status of Bluetooth
     * @return true open，false close
     */
    public boolean getBlueToothStatus(){
        if(isSupportBlueTooth()){
            return mAdapter.isEnabled();
        }else{
            return false;
        }
    }

    public void turnOnBlueTooth(Activity activity, int requestCode){
//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 检查是否拥有蓝牙权限
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, requestCode);
        } else {
            // 如果有权限，启动蓝牙启用请求
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, requestCode);
        }
//        activity.startActivityForResult(intent, requestCode);
    }


}
