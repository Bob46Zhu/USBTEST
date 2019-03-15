package com.example.usbtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取系统服务得到UsbManager实例
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //查找所有插入的设备
        //List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        //Map<String,usbDrivce> usbList = usbManager.getDeviceList();



        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    public class USBReceiver extends BroadcastReceiver{

        public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this){
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if(device != null)
                    {
                        if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                            Log.e("USBReceiver","获取权限成功:"+device.getDeviceName());
                        }
                        else
                        {
                            Log.e("USBReceiver","获取权限失败:"+ device.getDeviceName());
                        }
                    }
                }
            }
            else if(UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)){

            }
            else if(UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)){

            }
        }
    }


}
