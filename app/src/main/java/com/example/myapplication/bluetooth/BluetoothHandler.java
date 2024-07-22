package com.example.myapplication.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.BluetoothActivity;

public class BluetoothHandler extends Handler {

    public void handleMessage(Message message) {
        super.handleMessage(message);
        Log.i("bluetooth", "data:" + String.valueOf(message.obj) + " ,what值：" + message.what);
        switch (message.what) {
            case Constant.MSG_GOT_DATA:
                Log.i("bluetooth", "data:" + String.valueOf(message.obj));
                break;
            case Constant.MSG_ERROR:
                Log.i("bluetooth", "error:" + String.valueOf(message.obj));
                break;
            case Constant.MSG_CONNECTED_TO_SERVER:
                Log.i("bluetooth", "连接到服务端");
                break;
            case Constant.MSG_GOT_A_CLINET:
                Log.i("bluetooth", "找到服务端");
                break;
        }
    }
}
