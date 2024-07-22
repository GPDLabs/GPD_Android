package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.bluetooth.ConnectThread;
import com.example.myapplication.bluetooth.Constant;
import com.example.myapplication.entity.GetRandomEntity;
import com.example.myapplication.entity.HeaderEntity;
import com.example.myapplication.entity.MessageBodyEntity;
import com.example.myapplication.entity.MessageEntity;
import com.example.myapplication.entity.MessageTypeEnum;
import com.example.myapplication.entity.ResponseBodyEntity;
import com.example.myapplication.entity.ResponseEntity;
import com.example.myapplication.entity.ResponseGetRandomEntity;
import com.example.myapplication.entity.ResponseWalletAddrEntity;
import com.example.myapplication.entity.ResponseWirelessConfEntity;
import com.example.myapplication.entity.WalletAddrEntity;
import com.example.myapplication.entity.WirelessConfEntity;
import com.example.myapplication.utils.DataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothActivity";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 根据你的需求修改UUID

    private Toast mToast;
    //    private static final int REQUEST_ENABLE_BT = 111;
    private static final int REQUEST_BLUETOOTH_SCAN = 112;
    private static final int REQUEST_BLUETOOTH_CONNECT = 113;
    private static final int REQUEST_WRITE_STORAGE = 117;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> bluetoothDevicesAdapter;
    private BluetoothDevice selectedDevice;
    private BluetoothSocket bluetoothSocket;
    private ConnectThread mConnectThread;

//    private Handler handler = new BluetoothHandler();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("bluetooth_handler", "收到消息：" + (String)msg.obj);
            switch (msg.what) {
                case Constant.MSG_GOT_DATA: {
                    String receivedMessage = (String) msg.obj;
                    ObjectMapper objectMapper = new ObjectMapper();
//                    boolean wifiConfStatus = false;
                    try {
                        JSONObject jsonObject = new JSONObject(receivedMessage);
                        String rspHeader = jsonObject.getString("header");
                        String rspBody = jsonObject.getString("body");
                        JSONObject jsonRspBody = new JSONObject(rspBody);
                        String rspType = jsonRspBody.getString("messageType");
                        String rspData = jsonRspBody.getString("messageData");
                        Log.i("bluetooth-WiFi","校验码：" + rspHeader);
                        ClassifyResponseData(rspType, rspData);
                    } catch (Exception e) {
                        Log.i("bluetooth_handler", "Errer:" + e.toString());
                        e.printStackTrace();
                    }
                }
                    break;
                case Constant.MSG_ERROR:
                    Log.i("bluetooth", "error:" + String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    Log.i("bluetooth", "连接到服务端");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    Log.i("bluetooth", "找到服务端");
                    break;
            }

            return true;
        }
    });

    private void ClassifyResponseData(String rspType, String rspData){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            switch (rspType) {
                case "wirelessConfResult":{
                    ResponseWirelessConfEntity responseWirelessConf
                            = objectMapper.readValue(rspData, ResponseWirelessConfEntity.class);
                    HandleWirelessConfRsp(responseWirelessConf);
                }
                break;
                case "walletAddrResult":{
                    ResponseWalletAddrEntity responseWalletAddr
                            = objectMapper.readValue(rspData, ResponseWalletAddrEntity.class);
                    HandleWalletAddrRsp(responseWalletAddr);
                }
                break;
                case "getRandomResult":{
                    ResponseGetRandomEntity responseGetRandom
                            = objectMapper.readValue(rspData, ResponseGetRandomEntity.class);
                    HandleGetRandomRsp(responseGetRandom);
                }
                break;
                default:
                    Log.i("bluetooth-response", "未知Response Type: " + rspType);
            }

        } catch (Exception e) {
            Log.i("bluetooth-response", "Errer:" + e.toString());
            e.printStackTrace();
        }
    }

    private void HandleWirelessConfRsp(ResponseWirelessConfEntity responseWirelessConf){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 更新UI，例如显示接收到的消息
                Button btnWifiConf = findViewById(R.id.configure_wifi_button2);
                if(responseWirelessConf.getStatus() != "success"){
                    showToast("QR已成功连接WiFi");
                    btnWifiConf.setEnabled(false);
                }else{
                    showToast("QR连接WiFi失败，请检查账号密码是否正确");
                }

            }
        });
    }

    private void HandleWalletAddrRsp(ResponseWalletAddrEntity responseWalletAddr){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 更新UI，例如显示接收到的消息
                TextView tvWalletAddr = findViewById(R.id.wallet_address_label2);
                if(responseWalletAddr.getStatus() != "success"){
                    tvWalletAddr.setText(responseWalletAddr.getWalletAddr());
                }else{
                    showToast("QR生成地址失败！");
                }

            }
        });
    }

    private void HandleGetRandomRsp(ResponseGetRandomEntity responseGetRandom){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 更新UI，例如显示接收到的消息
                TextView tvRandomFilePath = findViewById(R.id.random_file_path);
                if(responseGetRandom.getStatus() != "success"){
                    writeDataFile("random-0001.txt", responseGetRandom.getRandom());

                }else{
                    showToast("QR生成地址失败！");
                }

            }
        });
    }


    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("bluetooth", "discoveryReceiver发现设备");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                    }, REQUEST_BLUETOOTH_CONNECT);
                    return;
                }
                if (device.getName() != null) {
                    bluetoothDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(BluetoothActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ActivityResultLauncher<Intent> openBluetoothLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                showToast("蓝牙已打开");
            } else {
                showToast("请授权蓝牙权限以确保APP正常工作");
            }
        }
    );

    private ActivityResultLauncher<Intent> requestBluetoothEnableLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // 蓝牙已启用，可以继续扫描
                    scanForDevices();
                } else {
                    // 用户未启用蓝牙，处理这种情况
                    Toast.makeText(BluetoothActivity.this, "请启用蓝牙以继续", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private ActivityResultLauncher<Intent> requestWriteFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    showToast("权限已授予");
                    Log.i("writeRandomFile", "权限已授予");
                    writeDataFile("/random-0001.txt", "");
                } else {
                    // 用户未启用蓝牙，处理这种情况
                    Toast.makeText(BluetoothActivity.this, "请授权确保APP能保存随机数文件", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void scanForDevices() {
        if (!bluetoothAdapter.isEnabled()) {
            // 蓝牙未打开，提示用户打开蓝牙
            Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
        } else {
            // 蓝牙已打开，开始扫描
            bluetoothDevicesAdapter.clear();
            if (ActivityCompat.checkSelfPermission(BluetoothActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(BluetoothActivity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showToast("无位置权限");
            }
            if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                Log.i("bluetooth", "无权限,继续申请获取权限");
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN }, REQUEST_BLUETOOTH_SCAN);
                return;
            }
            showToast("开始搜索");
            boolean startScan = bluetoothAdapter.startDiscovery();
            Log.i("bluetooth", "开启：" + startScan);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        EditText wifiNameInput = findViewById(R.id.wifi_name_input2);
        EditText wifiPasswordInput = findViewById(R.id.wifi_password_input2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ListView listView = findViewById(R.id.bluetooth_devices_list);
        bluetoothDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(bluetoothDevicesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceAddress = ((TextView) view).getText().toString();
                String macAdress = DataUtil.extractMacAddress(deviceAddress);
                if(macAdress != null){
                    Log.i("bluetooth", "蓝牙地址：" + macAdress);
                    selectedDevice = bluetoothAdapter.getRemoteDevice(macAdress);
                    if (mConnectThread != null) {
                        mConnectThread.cancel();
                    }
                    mConnectThread = new ConnectThread(selectedDevice, bluetoothAdapter, mHandler);
                    mConnectThread.start();
//                    connectToDevice(selectedDevice);
                }else{
                    showToast("蓝牙地址不规范，请检查后再连接");
                }
            }
        });

        //打开蓝牙
        Button btnOpen = findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                // 蓝牙未打开，请求用户打开蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                openBluetoothLauncher.launch(enableBtIntent);
            } else {
                // 蓝牙已打开，可以进行下一步操作
                showToast("蓝牙已打开");
            }
        });

        //关闭蓝牙
        Button btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            //关闭蓝牙连接
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("bluetooth", "蓝牙关闭无权限");
                    return;
                }
                bluetoothAdapter.disable();
            }
        });

        //搜索按钮点击事件
        Button btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                requestBluetoothEnableLauncher.launch(enableBtIntent);
            } else {
                bluetoothDevicesAdapter.clear();
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                    Log.i("bluetooth", "申请扫描权限");
                    ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN }, REQUEST_BLUETOOTH_SCAN);
                    return;
                }
                Log.i("bluetooth", "开始搜索");
                boolean startScan = bluetoothAdapter.startDiscovery();
                Log.i("bluetooth", "开启：" + startScan);
            }
        });

        //获取已连接的WiFi名称点击事件
        Button btnGetWifi = findViewById(R.id.get_wifi_button2);
        btnGetWifi.setOnClickListener(v -> {
            // 获取WiFi管理器
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            // 检查WiFi是否已打开
            if (!wifiManager.isWifiEnabled()) {
                Toast.makeText(BluetoothActivity.this, "WiFi未打开", Toast.LENGTH_SHORT).show();
                return;
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    // 获取SSID（WiFi名称）
                    String ssid = wifiInfo.getSSID();
                    wifiNameInput.setText(ssid.replace("\"", ""));
                } else {
                    Toast.makeText(BluetoothActivity.this, "未连接到WiFi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //配置WiFi点击事件
        Button btnConfigureWifi = findViewById(R.id.configure_wifi_button2);
        btnConfigureWifi.setOnClickListener(v -> {

            WirelessConfEntity wirelessConf = new WirelessConfEntity(
                    wifiNameInput.getText().toString(),
                    wifiPasswordInput.getText().toString()
            );
            MessageBodyEntity messageBody = new MessageBodyEntity("wirelessConf", wirelessConf);
            HeaderEntity header = new HeaderEntity(1, MessageTypeEnum.request, "1.0", DataUtil.generateRandomCode());
            MessageEntity message = new MessageEntity(header, messageBody);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonMessageBody = objectMapper.writeValueAsString(messageBody);
                Log.i("QR_Config", "messageBody:" + jsonMessageBody + "    messageBody长度：" + jsonMessageBody.length());
                message.getHeader().setMessageLength(jsonMessageBody.length());
                Log.i("QR_Config", "jsonMessageBody长度 ： " + jsonMessageBody.length() + "  @MessageBodyEntity:" + "MessageBodyEntity".length());
                String jsonMessage = objectMapper.writeValueAsString(message);
                Log.i("QR_Config", "Message:" + jsonMessage);
                if(mConnectThread != null){
                    mConnectThread.sendData(jsonMessage.getBytes());
                }else{
                    showToast("未连接到QR设备，请连接后再试。");
                }
            } catch (JsonProcessingException e) {
                Log.i("QR_Config", "报错：" + e.toString());
                throw new RuntimeException(e);
            }
        });

        //生成钱包地址按钮点击事件
        Button btnGetWalletAddr = findViewById(R.id.get_wallet_address_button2);
        btnGetWalletAddr.setOnClickListener(v -> {
            WalletAddrEntity walletAddr = new WalletAddrEntity(1);
            MessageBodyEntity messageBody = new MessageBodyEntity("walletAddr", walletAddr);
            HeaderEntity header = new HeaderEntity(1, MessageTypeEnum.request, "1.0", DataUtil.generateRandomCode());
            MessageEntity message = new MessageEntity(header, messageBody);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonMessageBody = objectMapper.writeValueAsString(messageBody);
                message.getHeader().setMessageLength(jsonMessageBody.length());
                String jsonMessage = objectMapper.writeValueAsString(message);
                Log.i("QR_Config", "Message:" + jsonMessage);
                if(mConnectThread != null){
                    mConnectThread.sendData(jsonMessage.getBytes());
                }else{
                    showToast("未连接到QR设备，请连接后再试。");
                }
            } catch (JsonProcessingException e) {
                Log.i("QR_Config", "报错：" + e.toString());
                throw new RuntimeException(e);
            }

        });

        //获取随机数点击事件
        Button btnGetRandom = findViewById(R.id.get_random);
        btnGetRandom.setOnClickListener(v -> {
            requestPermission();


        });
    }

    private void sendGetRandomReq(int randomCount){
        writeDataFile("random-0001.txt", "");
        GetRandomEntity getRandom = new GetRandomEntity(randomCount);
        MessageBodyEntity messageBody = new MessageBodyEntity("getRandom", getRandom);
        sendRequest(messageBody);
        Log.i("writeRandomFile", "已发送随机数请求");
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                sendGetRandomReq(1);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + BluetoothActivity.this.getPackageName()));
                requestWriteFileLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("writeFile-request", "Android 6及以上");
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(
                    BluetoothActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(BluetoothActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                writeFile();
                sendGetRandomReq(1);
            } else {
                ActivityCompat.requestPermissions(
                        BluetoothActivity.this,
                        new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, REQUEST_WRITE_STORAGE);
            }
        } else {
//            writeFile();
            sendGetRandomReq(1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.i("bluetooth", "返回码:" + requestCode);
        switch(requestCode){
            case REQUEST_BLUETOOTH_SCAN: {
                Log.i("bluetooth", "扫描权限：" + grantResults.length + "  " + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanForDevices();
                } else {
                    showToast("蓝牙扫描权限未授予");
                }
            }
            break;
            case REQUEST_BLUETOOTH_CONNECT: {
                Log.i("bluetooth", "连接权限判断");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 用户已经授予了权限，现在可以继续搜索蓝牙设备
                    scanForDevices();
                } else {
                    showToast("蓝牙连接权限未授予");
                }
            }
            break;
            case REQUEST_WRITE_STORAGE:{
                Log.i("writeRandomFile", "读写文件判断");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetRandomEntity getRandom = new GetRandomEntity(1);
                    MessageBodyEntity messageBody = new MessageBodyEntity("getRandom", getRandom);
                    sendRequest(messageBody);
                    Log.i("writeRandomFile", "已发送随机数请求");
                } else {
                    showToast("文件权限未授予，无法在本地保存随机数文件");
                }
            }
            break;
            default:
                Log.i("bluetooth", "跳过判断");
                break;
        }


    }

    private boolean sendRequest(MessageBodyEntity messageBody){
        HeaderEntity header;
        MessageEntity message;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonMessageBody = objectMapper.writeValueAsString(messageBody);
            header = new HeaderEntity(jsonMessageBody.length(), MessageTypeEnum.request, "1.0", DataUtil.generateRandomCode());
            message = new MessageEntity(header, messageBody);
            String jsonMessage = objectMapper.writeValueAsString(message);
            Log.i("BluetoothRequest", "Message:" + jsonMessage);
            if(mConnectThread != null){
                mConnectThread.sendData(jsonMessage.getBytes());
            }else{
                showToast("未连接到QR设备，请连接后再试。");
            }
        } catch (JsonProcessingException e) {
            Log.i("QR_Config", "报错：" + e.toString());
            throw new RuntimeException(e);
        }

        return false;
    }


    private void connectToDevice(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            Toast.makeText(this, "连接到设备：" + device.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Could not connect to device", e);
            Toast.makeText(this, "无法连接到设备", Toast.LENGTH_SHORT).show();
        }
    }


    //读取内置data目录下文件
    public String readDataFile(String fileName) {
        String res = "";
        try {
            FileInputStream fin = openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer);
            fin.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "readDataFile Error!" + e.getMessage());
        }
        return res;
    }

    public File createAppNameDirectory() {
        File appNameDir = new File(Environment.getExternalStorageDirectory() + "/Quakey");
        if (!appNameDir.exists()) {
            boolean mkdirsDir = appNameDir.mkdirs();
            Log.i("writeRandomFile", "路径创建结果： " + (mkdirsDir?"成功":"失败"));
        }
        return appNameDir;
    }

    //写入内置data目录下文件
    private void writeDataFile(String fileName, String content) {
        try {
            File randomDir = createAppNameDirectory();
            File randomFile = new File(randomDir, fileName);
            String filePath = randomFile.getAbsolutePath();
            Log.i("writeRandomFile","文件的绝对路径: " + filePath);
            if (!randomFile.exists()) {
                randomFile.createNewFile();
            }


            Writer writer = new OutputStreamWriter(new FileOutputStream(randomFile));
            writer.write(content);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "writeDataFile Error!" + e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close Bluetooth socket", e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播接收器
        Log.i("bluetooth", "注册广播接收器");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销广播接收器
        Log.i("bluetooth", "注销广播接收器");
        unregisterReceiver(discoveryReceiver);
    }

    private void showToast(String text){
        if( mToast == null){
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(text);
        }
        mToast.show();
    }
}
