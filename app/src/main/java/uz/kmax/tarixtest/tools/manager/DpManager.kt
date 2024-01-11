package uz.kmax.tarixtest.tools.manager

import android.content.res.Resources

class DpManager {

    val Int.toDp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.toPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()//    val Int.toDp: Int
}