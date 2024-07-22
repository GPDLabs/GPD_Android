package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//import com.example.myapplication.bluetooth.BlueToothController;
import com.example.myapplication.BluetoothActivity;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toast mToast;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_SCAN =111;
    private static final int MY_DISCOVERABLE_TIME = 1024;
    Button button1, button2;

    BluetoothAdapter bluetoothAdapter;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.btn_connect);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.btn_qrActivity);
        button2.setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
        }, REQUEST_BLUETOOTH_SCAN);
//        Log.i("bluetooth", "View Create!");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect: {
                //跳转至蓝牙连接界面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);

            }

            break;
            case R.id.btn_qrActivity:
                //初始化QR，跳转至QR配置界面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity2.class);
                startActivity(intent);
                break;
            default:

                break;
        }
    }


    /**
     * 打开手机蓝牙
     *
     * @return true 表示打开成功
     */
//    public boolean enable() {
//        if (!getBluetoothAdapter().isEnabled()) {
//            //若未打开手机蓝牙，则会弹出一个系统的是否打开/关闭蓝牙的对话框，禁止或者未处理返回false，允许返回true
//            //若已打开手机蓝牙，直接返回true
//            boolean enableState = getBluetoothAdapter().enable();
//            Log.d(TAG, "（用户操作）手机蓝牙是否打开成功：" + enableState);
//            return enableState;
//        } else return true;
//    }

}