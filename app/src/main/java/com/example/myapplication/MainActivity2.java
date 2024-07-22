package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.bluetooth.ConnectThread;
import com.example.myapplication.entity.HeaderEntity;
import com.example.myapplication.entity.MessageBodyEntity;
import com.example.myapplication.entity.MessageEntity;
import com.example.myapplication.entity.MessageTypeEnum;
import com.example.myapplication.entity.WirelessConfEntity;

import com.example.myapplication.utils.DataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private EditText wifiNameInput;
    private EditText wifiPasswordInput;
    private Button getWifiButton;
    private Button configureWifiButton;

    private ConnectThread mConnectThread = new ConnectThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // 假设您的布局文件名为 activity_wifi_config.xml

        wifiNameInput = findViewById(R.id.wifi_name_input);
        wifiPasswordInput = findViewById(R.id.wifi_password_input);
        getWifiButton = findViewById(R.id.get_wifi_button);
        configureWifiButton = findViewById(R.id.configure_wifi_button);

        getWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取WiFi管理器
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                // 检查WiFi是否已打开
                if (!wifiManager.isWifiEnabled()) {
                    Toast.makeText(MainActivity2.this, "WiFi未打开", Toast.LENGTH_SHORT).show();
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

                        //获取WiFi密码
//                    if (ActivityCompat.checkSelfPermission(MainActivity2.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
//                    for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
//                        if (wifiConfiguration.SSID.equals(wifiInfo.getSSID())) {
//                            String wifiPassword = wifiConfiguration.preSharedKey;
//                            // 使用wifiPassword
//                            break;
//                        }
//                    }
                    } else {
                        Toast.makeText(MainActivity2.this, "未连接到WiFi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        configureWifiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                    mConnectThread.sendData(jsonMessage.getBytes());
                } catch (JsonProcessingException e) {
                    Log.i("QR_Config", "报错：" + e.toString());
                    throw new RuntimeException(e);
                }
            }
        });

    }
}