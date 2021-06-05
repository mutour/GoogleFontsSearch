package com.doufu.google.fonts.search.utils

import android.util.Log


class DFLog(var tag: String = "Log") {

    companion object {
        val Default: DFLog by lazy { DFLog("GoogleFontsSearch") }
        fun get(name: String): DFLog {
            return DFLog(name)
        }

        fun get(clazz: Class<*>): DFLog {
            return DFLog(clazz.simpleName)
        }
    }

    inline fun d(info: String) {
        Log.d(tag, info)
    }

    inline fun d(format: String, vararg args: Any?) {
        Log.d(tag, format.format(args))
    }

    inline fun i(info: String) {
        Log.i(tag, info)
    }

    inline fun i(format: String, vararg args: Any?) {
        Log.i(tag, format.format(args))
    }

    inline fun w(info: String) {
        Log.w(tag, info)
    }

    inline fun w(format: String, vararg args: Any?) {
        Log.w(tag, format.format(args))
    }

    inline fun e(info: String) {
        Log.e(tag, info)
    }

    inline fun e(format: String, vararg args: Any?) {
        Log.e(tag, format.format(args))
    }

    inline fun e(info: String, e: Throwable) {
        Log.e(tag, info, e)
    }

}