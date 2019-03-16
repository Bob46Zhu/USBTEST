package com.example.usbtest;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.ArrayList;

public class UARTDriver {

    private UsbManager usbManager;
    private PendingIntent pendingIntent;
    private UsbDevice usbDevice;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private UsbDeviceConnection usbDeviceConnection;
    private Context context;
    private String usbPermission;
    private int timeOutValue;
    private int listSize;
    private int packetsize;
    private boolean flag = false;
    private ArrayList ArrayDeivce = new ArrayList();


    public UARTDriver(UsbManager usbManager1,Context context1,String usbPermission1){
        usbManager = usbManager1;
        context = context1;
        usbPermission = usbPermission1;
        timeOutValue = 10000;
        usbManager("1a86:7523");//USB转串口的16进制PID和VID
        usbManager("1a86:5523");//USB转串口的16进制PID和VID
        usbManager("0483:5740");//STM32前面板16进制PID和VID
    }

    private void usbManager(String PID_VID)
    {
        ArrayDeivce.add(PID_VID);//添加设备到列表
        listSize = ArrayDeivce.size();//获取列表设备数量
    }

    public boolean SetTimeOut(int timeOutValue1,int timeOutValue2)
    {
        timeOutValue = timeOutValue1;
        return true;
    }

    private void usbManager(UsbDevice usbDev)
    {
        if(usbDev != null) //如果usb设备不为空
        {
            UsbDevice usbdevice = usbDev;
            if(usbDeviceConnection != null)
            {
                if (usbInterface != null){
                    usbDeviceConnection.releaseInterface(usbInterface);
                    usbInterface = null;
                }
                usbDeviceConnection.close();
                usbDevice = null;
                usbInterface = null;
            }
            UsbInterface usbInterface2;
            int var4;
            if(usbDev == null)
            {
                usbInterface2 = null;
            }
            else
            {
                var4 = 0;
                while(true)
                {
                    if(var4 >= usbdevice.getInterfaceCount()){
                        usbInterface2 = null;
                        break;
                    }
                    UsbInterface usbInterface3;
                    if((usbInterface3 = usbdevice.getInterface(var4)).getInterfaceClass() == 255 && usbInterface3.getInterfaceSubclass() == 1
                        && usbInterface3.getInterfaceProtocol() == 2)
                    {
                        usbInterface2 = usbInterface3;
                        break;
                    }
                    ++var4;
                }
            }
            UsbInterface usbInterface4 = usbInterface2;
            UsbDeviceConnection usbDeviceConnection12;
            if(usbDev != null  && usbInterface4 != null && (usbDeviceConnection12 = usbDevice.openDevice(usbDev)) != null
                && ((UsbDeviceConnection)usbDeviceConnection12).claimInterface(usbInterface4,true) ){
                usbDevice = usbDev;
                usbDeviceConnection = usbDeviceConnection12;
                usbInterface = usbInterface4;
                usbInterface4 = usbInterface4;

                UARTDriver uartDriver = this;
                boolean tf;
                if(usbInterface4 == null){
                    tf = false;
                }
                else{
                    for(var4 = 0; var4 < usbInterface4.getEndpointCount(); ++var4){
                        UsbEndpoint usbEndpoint;
                        if((usbEndpoint = usbInterface4.getEndpoint(var4)).getType() == 2 && usbEndpoint.getMaxPacketSize() == 32){
                            if(usbEndpoint.getDirection() == 128){
                                uartDriver.usbEndpointIn = usbEndpoint;
                            }
                            else {
                                uartDriver.usbEndpointOut = usbEndpoint;
                            }
                            uartDriver.packetsize = usbEndpoint.getMaxPacketSize();
                        }else {
                            usbEndpoint.getType();
                        }
                    }
                    tf = true;
                }
                if(tf){
                    Toast.makeText(context,"设备插入",Toast.LENGTH_SHORT).show();
                    if(!flag){
                        flag = true;
                        
                    }
                }

            }

        }
    }



}
