package com.carriez.flutter_hbb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ffi.FFI
import io.flutter.embedding.android.FlutterActivity

/**
 * AnyKey: lets the AnyKey app (signed with the same key; enforced by the manifest permission)
 * turn this device into a host: sets the permanent password and starts on boot.
 * Result code: 1 = done, 2 = AnyKey Remote was never opened, 3 = password refused.
 */
class AnyKeySetupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences(KEY_SHARED_PREFERENCES, FlutterActivity.MODE_PRIVATE)
        val dir = prefs.getString(KEY_APP_DIR_CONFIG_PATH, "") ?: ""
        if (dir.isEmpty()) {
            resultCode = 2
            return
        }
        val pw = intent.getStringExtra("password") ?: ""
        val ok = try {
            FFI.anykeySetPassword(dir, pw)
        } catch (e: Throwable) {
            false
        }
        if (!ok) {
            resultCode = 3
            return
        }
        prefs.edit().putBoolean(KEY_START_ON_BOOT_OPT, true).apply()
        resultCode = 1
    }
}
