package com.tencent.testshengao



/**
 *@desc
 *@Author yinfeilong
 *@Date 2019/10/11
 */
/**
 * Value of dp to value of px.
 *
 * @param dpValue The value of dp.
 * @return value of px
 */
fun dp2px(dpValue: Float): Int {
    val scale =MyApp.ApplicationINSTANCE.getResources().getDisplayMetrics().density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * Value of px to value of dp.
 *
 * @param pxValue The value of px.
 * @return value of dp
 */
fun px2dp(pxValue: Float): Int {
    val scale = MyApp.ApplicationINSTANCE.getResources().getDisplayMetrics().density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * Value of sp to value of px.
 *
 * @param spValue The value of sp.
 * @return value of px
 */
fun sp2px(spValue: Float): Int {
    val fontScale =MyApp.ApplicationINSTANCE.getResources().getDisplayMetrics().scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

/**
 * Value of px to value of sp.
 *
 * @param pxValue The value of px.
 * @return value of sp
 */
fun px2sp(pxValue: Float): Int {
    val fontScale = MyApp.ApplicationINSTANCE.getResources().getDisplayMetrics().scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}
