package com.example.usbtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class usbBroadcastReceiver extends BroadcastReceiver {


    private String USB_action = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (USB_action.equals(action))
        {
            Toast.makeText(context,"有USB设备接入",Toast.LENGTH_SHORT).show();
        }
        else{

        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
