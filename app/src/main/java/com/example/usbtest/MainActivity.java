package com.example.usbtest;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private TextView textView;
    private TextView resulttext;
    private TextView seekBarValue;
    private Button openBtn;

    private UsbManager mUsbManager = null;
    private UsbDevice mUsbDevice;
    private static final String ACTION_DEVICE_PERMISSION = "com.example.USB_PERMISSION";
    private PendingIntent mPermissionIntent;
    private UsbEndpoint mUsbEndpointIn;
    private UsbEndpoint mUsbEndpointOut;
    private UsbInterface mUsbInterface;
    private UsbDeviceConnection mUsbDeviceConnection;
    private BroadcastReceiver mUsbPermissionActionReceiver;
    private boolean isReading;
    private Thread mReadingthread = null;

    IntentFilter usbFilter = new IntentFilter();



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
        final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbBroadcastReceiver myusbBroadcastReceiver = new usbBroadcastReceiver();
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        registerReceiver(myusbBroadcastReceiver, itFilter);

        resulttext = findViewById(R.id.result);
        textView = findViewById(R.id.message);
        openBtn = findViewById(R.id.Btn1);

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryGetUsbPermission();
                searchUsb();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void searchUsb() {          // 搜索USB设置
        final String ACTION_USB_PERMISSION = "com.example.usbtest.USB_PERMISSION";
        HashMap<String, UsbDevice> devices = mUsbManager.getDeviceList();
        Iterator<UsbDevice> iterator = devices.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = iterator.next();
            if (mUsbManager.hasPermission(device)) {
//                mUsbDevice = device;//将搜索到的USB设备给mUsbDevice
                textView.setText("PID:"+device.getProductId()+" | "+"VID:"+device.getVendorId());
                initUsbDevice(device);
            } else {
                mUsbManager.requestPermission(device, mPermissionIntent);
            }
        }
    }

    private void initUsbDevice(UsbDevice dev1){
        UsbInterface usbInterface = dev1.getInterface(0);
        UsbEndpoint ep = usbInterface.getEndpoint(0);
        if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT){
            if (ep.getDirection() == UsbConstants.USB_DIR_IN){
                mUsbEndpointIn = ep;
            }else{
                mUsbEndpointOut = ep;
            }
            if ((null) == mUsbEndpointIn){
                mUsbEndpointIn = null;
                mUsbInterface = null;
            } else {
                mUsbInterface = usbInterface;
                mUsbDeviceConnection = mUsbManager.openDevice(dev1);
                startReading();
            }
        }
      //  textView.setText("USB初始化完成");
    }

    //获取USB权限
    private void tryGetUsbPermission(){

        mUsbPermissionActionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_DEVICE_PERMISSION.equals(action)) {
                    context.unregisterReceiver(this);//解注册
                    synchronized (this) {
                        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if(null != usbDevice){
                                Toast.makeText(context,usbDevice.getDeviceName()+"已获取USB权限",Toast.LENGTH_SHORT).show();
                               // Log.e(TAG,usbDevice.getDeviceName()+"已获取USB权限");
                            }
                        }
                        else {
                            //user choose NO for your previously popup window asking for grant perssion for this usb device
                            Toast.makeText(context,"USB权限被拒绝",Toast.LENGTH_SHORT).show();
                            //Log.e(TAG,String.valueOf("USB权限已被拒绝，Permission denied for device" + usbDevice));
                        }
                    }

                }
            }
        };

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_DEVICE_PERMISSION);

        if(mUsbPermissionActionReceiver != null) {
            registerReceiver(mUsbPermissionActionReceiver, filter);
        }

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_DEVICE_PERMISSION), 0);

        boolean has_idcard_usb = false;
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {

            if(usbDevice.getVendorId() == 1155 && usbDevice.getProductId() == 22336)//身份证设备USB
            {
                has_idcard_usb = true;
               // Log.e(TAG,usbDevice.getDeviceName()+"已找到身份证USB");
                Toast.makeText(this,usbDevice.getDeviceName()+"已找到身份证USB",Toast.LENGTH_SHORT).show();
                if(mUsbManager.hasPermission(usbDevice)){
                    Toast.makeText(this,usbDevice.getDeviceName()+"已获取过USB权限",Toast.LENGTH_SHORT).show();
                  //  Log.e(TAG,usbDevice.getDeviceName()+"已获取过USB权限");
                }else{
                    Toast.makeText(this,usbDevice.getDeviceName()+"请求获取USB权限",Toast.LENGTH_SHORT).show();
                   // Log.e(TAG,usbDevice.getDeviceName()+"请求获取USB权限");
                    mUsbManager.requestPermission(usbDevice, mPermissionIntent);
                }
            }

        }

        if(!has_idcard_usb)
        {
            Toast.makeText(this,"未找到身份证USB",Toast.LENGTH_SHORT).show();
           // Log.e(TAG,"未找到身份证USB");
        }

    }



    //开线程读取数据
    private void startReading() {
        mUsbDeviceConnection.claimInterface(mUsbInterface, true);

        isReading = true;

        final StringBuffer qr = new StringBuffer();

        mReadingthread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReading) {
                    synchronized (this) {
                        byte[] bytes = new byte[mUsbEndpointIn.getMaxPacketSize()];
                        int ret = mUsbDeviceConnection.bulkTransfer(mUsbEndpointIn, bytes, bytes.length, 100);


                        if (ret > 0) {
                            StringBuilder stringbuilder = new StringBuilder(bytes.length);
                            for (byte b : bytes) {
                                if (b != 0) {
                                    if (b == 2) {
                                        stringbuilder.append("da");
                                    }
                                    stringbuilder.append(Integer.toHexString(b));

                                }
                            }

                            resulttext.setText(stringbuilder.toString());
                            //最终处理数据
                           // log.d(TAG, stringbuilder.toString());
                        }
                    }

                }
                mUsbDeviceConnection.close();
            }


        });


        mReadingthread.start();
    }



}
