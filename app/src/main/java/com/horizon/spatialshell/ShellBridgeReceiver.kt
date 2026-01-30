package com.horizon.spatialshell

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ShellBridgeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_LIST_APPS -> {
                val json = AppIndex.listLaunchableAppsJson(context)
                sendToUnity(context, ACTION_LIST_APPS_RESULT, json)
            }

            ACTION_LAUNCH_APP -> {
                val pkg = intent.getStringExtra(EXTRA_PACKAGE) ?: return
                val ok = AppIndex.launchPackage(context, pkg)
                val json = """{"package":"$pkg","launched":$ok}"""
                sendToUnity(context, ACTION_LAUNCH_APP_RESULT, json)
            }
        }
    }

    private fun sendToUnity(context: Context, action: String, json: String) {
        val out = Intent(action).apply {
            // Send result specifically to Unity app package
            setPackage(UNITY_PACKAGE)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            putExtra(EXTRA_JSON, json)
        }
        context.sendBroadcast(out)
    }

    companion object {
        // Unity app package
        const val UNITY_PACKAGE = "com.Zinara.ARGlassesProject"

        // Commands (Unity -> Shell)
        const val ACTION_LIST_APPS = "com.horizon.spatialshell.LIST_APPS"
        const val ACTION_LAUNCH_APP = "com.horizon.spatialshell.LAUNCH_APP"

        // Results (Shell -> Unity)
        const val ACTION_LIST_APPS_RESULT = "com.horizon.spatialshell.LIST_APPS_RESULT"
        const val ACTION_LAUNCH_APP_RESULT = "com.horizon.spatialshell.LAUNCH_APP_RESULT"

        // Extras
        const val EXTRA_PACKAGE = "package"
        const val EXTRA_JSON = "json"
    }
}
