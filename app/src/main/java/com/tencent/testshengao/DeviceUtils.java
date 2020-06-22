package com.tencent.testshengao;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hd.serialport.method.DeviceMeasureController;
import com.hd.serialport.usb_driver.UsbSerialDriver;
import com.hd.serialport.usb_driver.UsbSerialPort;

import java.util.HashMap;
import java.util.List;


public class DeviceUtils {


    private static int vendorId = 1659;
    private static int productId = 8963;
    private static final String ACTION_USB_PERMISSION = "com.tencent.testshengao.USB_PERMISSION";

    private static BroadcastReceiver mUSBInOutBroadcastReceiver;
    private static BroadcastReceiver mPermissionBroadcastReceiver;

    /**
     * 判断硬件是否连接在手机上
     *
     * @return
     */
    public static boolean deviceisLine() {
        List<UsbSerialDriver> usbSerialDrivers = DeviceMeasureController.INSTANCE.scanUsbPort();
        for (int i = 0; i < usbSerialDrivers.size(); i++) {
            if (usbSerialDrivers.get(i).getDevice().getVendorId() == vendorId && usbSerialDrivers.get(i).getDevice().getProductId() == productId) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断手机是否有USB权限
     */
    public static boolean hasUSBPermission(Context context) {
        List<UsbSerialDriver> usbSerialDrivers = DeviceMeasureController.INSTANCE.scanUsbPort();
        for (int i = 0; i < usbSerialDrivers.size(); i++) {
            if (usbSerialDrivers.get(i).getDevice().getVendorId() == vendorId && usbSerialDrivers.get(i).getDevice().getProductId() == productId) {
                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                return usbManager.hasPermission(usbSerialDrivers.get(i).getDevice());
            }
        }
        return false;
    }

    /**
     * 尝试usb权限
     */
    public static void tryGetUsbPermission(Context context, final PermissionCallBack callBack) {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(getPermissionBroadcastReceiver(callBack), filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        List<UsbSerialDriver> usbSerialDrivers = DeviceMeasureController.INSTANCE.scanUsbPort();
        for (int i = 0; i < usbSerialDrivers.size(); i++) {
            if (usbSerialDrivers.get(i).getDevice().getVendorId() == vendorId && usbSerialDrivers.get(i).getDevice().getProductId() == productId) {
                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                usbManager.requestPermission(usbSerialDrivers.get(i).getDevice(), mPermissionIntent);
            }
        }

    }

    /**
     * 请求USB权限的广播
     *
     * @param callBack
     * @return
     */
    private static synchronized BroadcastReceiver getPermissionBroadcastReceiver(final PermissionCallBack callBack) {
        if (mPermissionBroadcastReceiver == null) {
            mPermissionBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    String action = intent.getAction();
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        synchronized (this) {
                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                callBack.requestPermissionSucceed();
                            } else {
                                tryGetUsbPermission(context, callBack);
                            }
                        }
                    }
                }
            };

        }
        return mPermissionBroadcastReceiver;


    }

    //拿到权限的回调
    private interface PermissionCallBack {
        void requestPermissionSucceed();
    }


    /**
     * 监听USB插入和拔出
     */
    public static void monitorUSBInOut(Context context, final OutInCallBack callBack) {
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(getUSBInOutBroadcastReceiver(callBack), usbDeviceStateFilter);
    }


    /**
     * 监听USB插入和拔出的监听
     *
     * @param callBack
     * @return
     */
    private static synchronized BroadcastReceiver getUSBInOutBroadcastReceiver(final OutInCallBack callBack) {
        if (mUSBInOutBroadcastReceiver == null) {
            mUSBInOutBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                        if (device != null && device.getVendorId() == vendorId && device.getProductId() == productId) {
                            callBack.Out();
                        }
                    } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                        if (device != null && device.getVendorId() == vendorId && device.getProductId() == productId) {
                            callBack.In();
                        } else {
                            callBack.InErrorDevice();
                        }

                    }

                }
            };
        }
        return mUSBInOutBroadcastReceiver;


    }


    //USB插入和拔出回调
    public interface OutInCallBack {
        void Out();

        void In();

        void InErrorDevice();

    }


    /**
     * 获取连接点对象
     *
     * @return
     */
    public static UsbSerialPort getUsbSerialPort() {
        List<UsbSerialDriver> usbSerialDrivers = DeviceMeasureController.INSTANCE.scanUsbPort();
        for (int i = 0; i < usbSerialDrivers.size(); i++) {
            if (usbSerialDrivers.get(i).getDevice().getVendorId() == vendorId && usbSerialDrivers.get(i).getDevice().getProductId() == productId) {
                return usbSerialDrivers.get(i).getPorts().get(0);
            }
        }
        return null;
    }


    /**
     * 字符串解析成数据
     */
    public static HashMap<String, String> parseData(String dataString) {
        HashMap<String, String> data = new HashMap<>();
        data.put("weight", "获取数据失败");
        data.put("height", "获取数据失败");
        String[] s = dataString.split(" ");
        if (s.length == 2) {
            String[] weights = s[0].split(":");
            String[] heights = s[1].split(":");
            if (weights.length == 2) {
                data.put("weight", weights[1]);
            }
            if (heights.length == 2) {
                data.put("height", heights[1]);
            }
        }
        return data;
    }



    public static void unregisterUSBReceiver(Context context) {
        if (mUSBInOutBroadcastReceiver != null) {
            context.unregisterReceiver(mUSBInOutBroadcastReceiver);
        }

    }

}