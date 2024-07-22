package com.example.myapplication.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;
        // 使用临时对象获取输入和输出流，因为成员流是最终的
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    public void run() {
        byte[] buffer = new byte[1024 * 3];  // 用于流的缓冲存储
        int bytes; // 从read()返回bytes

        // 持续监听InputStream，直到出现异常
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
//                StringBuilder sb = new StringBuilder();
//                while ((bytes = mmInStream.read(buffer)) != -1) {
//                    Log.i("QR_response", new String(buffer, 0, bytes));
//                    sb.append(new String(buffer, 0, bytes));
//                }
                if( bytes >0) {
                    Log.i("Connected", "MessageBytes:" + bytes);
                    Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA, new String(buffer, 0, bytes, "utf-8"));
                    Log.i("Connected", "封装Message完成");
                    mHandler.sendMessage(message);
                    Log.i("Connected", "发送Message完成");
                }
//                String receivedMessage = sb.toString();
//                Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA, receivedMessage);
//                mHandler.sendMessage(message);
                Log.d("GOTMSG", "message size" + bytes);

                // 从InputStream读取数据
//                bytes = mmInStream.read(buffer);
//                // 将获得的bytes发送到UI层activity
//
//
//
//                if( bytes >0) {
//                    Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA, new String(buffer, 0, bytes, "utf-8"));
//                    mHandler.sendMessage(message);
//                }

            } catch (IOException e) {
                Log.i("QR_response", e.toString());
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
        }
    }

    /**
     * 在main中调用此函数，将数据发送到远端设备中
     */
    public void write(byte[] bytes) {
        try {
            Log.i("bluetoothConnect", "蓝牙通信发送请求");
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /**
     * 在main中调用此函数，断开连接
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
