package com.tencent.testshengao

import android.app.Application
import com.hd.serialport.method.DeviceMeasureController

class MyApp : Application() {


    companion object {
        lateinit var ApplicationINSTANCE: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        ApplicationINSTANCE = this;
        DeviceMeasureController.init(this,true)
    }
}
