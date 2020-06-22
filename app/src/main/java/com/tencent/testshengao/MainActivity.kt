package com.tencent.testshengao

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hd.serialport.config.UsbPortDeviceType
import com.hd.serialport.listener.UsbMeasureListener
import com.hd.serialport.method.DeviceMeasureController
import com.hd.serialport.param.UsbMeasureParameter
import com.hd.serialport.usb_driver.UsbSerialPort
import com.hd.serialport.utils.HexDump
import com.tencent.testshengao.dialog.AlertDialog


class MainActivity : AppCompatActivity() {

    private var buffer = StringBuffer()
    private var Resultstring: String = ""
    private var measure = false;
    var outLineDialog: AlertDialog? = null
    var lineDeviceErrorDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onResume() {
        super.onResume()
        //注册USB插入拔出广播
        listenerUSBInOut()
        tryConnect()
    }


    override fun onPause() {
        super.onPause()
        stopDeviceListenerService()
       //取消USB插入拔出广播
        DeviceUtils.unregisterUSBReceiver(this);
    }


    /**
     *尝试去连接硬件
     */
    fun tryConnect() {
        //判断设备是否在线
        if (DeviceUtils.deviceisLine()) {
            //判断释放具有usb权限
            if (DeviceUtils.hasUSBPermission(this)) {
                //有权限，开始服务
                startDeviceListenerService()
            } else {
                //没有权限，请求权限
                requestUSBPermission()
            }
        } else {
            //  设备未连接
            showDeviceOutLineDialog()
        }
    }


    /**
     * 设置USB插入和拔出监听
     */
    fun listenerUSBInOut() {
        DeviceUtils.monitorUSBInOut(this, object : DeviceUtils.OutInCallBack {
            override fun Out() {
                dismassErrorDiviceDialog()
                showDeviceOutLineDialog()
                stopDeviceListenerService()

            }

            override fun In() {
                dismassErrorDiviceDialog()
                dismassDeviceOutLineDialog()
                tryConnect()
            }

            override fun InErrorDevice() {
                showErrorDeviceDialog()

            }
        })


    }


    /**
     * 显示设备不在线dialog
     */
    fun showDeviceOutLineDialog() {
        outLineDialog = AlertDialog.Builder(this)
            .setContentView(R.layout.outlinedialog)
            .setWidthAndHeight(dp2px(300F), dp2px(100F))
            .setCancelable(false)
            .setBackgroundTransparence(0.4F)
            .setback(false)
            .show()
    }

    /**
     * 隐藏设备未连接的dialog
     */

    fun dismassDeviceOutLineDialog() {
        outLineDialog?.dismiss()
    }

    /**
     * 提示用户插入设备不对的dialog
     */
    fun showErrorDeviceDialog() {
        lineDeviceErrorDialog = AlertDialog.Builder(this)
            .setContentView(R.layout.lineerrordialog)
            .setWidthAndHeight(dp2px(300F), dp2px(100F))
            .setCancelable(false)
            .setBackgroundTransparence(0.4F)
            .setback(false)
            .show()
    }

    /**
     * 取消插入不对的提示dialog
     */
    fun dismassErrorDiviceDialog() {
        lineDeviceErrorDialog?.dismiss()
    }


    /**
     * 开启数据回调监听
     */

    fun startDeviceListenerService() {
        measure = true
        DeviceMeasureController.measure(DeviceUtils.getUsbSerialPort(), UsbMeasureParameter(
            UsbPortDeviceType.USB_OTHERS,
            4800, 8,
            1, 0
        ), object : UsbMeasureListener {
            override fun measureError(tag: Any?, message: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity,message,Toast.LENGTH_LONG).show()
                }
            }
            override fun write(tag: Any?, usbSerialPort: UsbSerialPort) {
            }

            override fun measuring(tag: Any?, usbSerialPort: UsbSerialPort, data: ByteArray) {
                val hexString = HexDump.toHexString(data)
                //这里是子线程
                buffer.append(hexString)
                if (buffer.length == 34) {//如果数据长度够就解析数据
                    Resultstring = String(HexDump.hexStringToByteArray(buffer.toString()))
                    buffer = StringBuffer()
                    //拿到最后的解析数据
                    val parseData = DeviceUtils.parseData(Resultstring)
                    runOnUiThread {
                        //拿到测量数据
                        Toast.makeText(
                            this@MainActivity,
                            parseData.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            }


        })

    }

    /**
     * 关闭数据监听服务
     */

    fun stopDeviceListenerService() {
        if (measure) {
            DeviceMeasureController.stop()
            measure = false
        }


    }

    /**
     * 请求USB权限
     */
    fun requestUSBPermission() {
        DeviceUtils.tryGetUsbPermission(this, {
            //有权限
            startDeviceListenerService()
        })

    }


}
