package com.horizon.spatialshell

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import org.json.JSONArray
import org.json.JSONObject

object AppIndex {

    fun listLaunchableAppsJson(context: Context): String {
        val pm = context.packageManager
        val query = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)

        val items = pm.queryIntentActivities(query, PackageManager.MATCH_DEFAULT_ONLY)
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        val arr = JSONArray()
        for (ri in items) {
            val label = ri.loadLabel(pm).toString()
            val pkg = ri.activityInfo.packageName
            val cls = ri.activityInfo.name

            val o = JSONObject()
            o.put("label", label)
            o.put("package", pkg)
            o.put("activity", cls)
            arr.put(o)
        }

        return JSONObject().put("apps", arr).toString()
    }

    fun launchPackage(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        val launch = pm.getLaunchIntentForPackage(packageName) ?: return false
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launch)
        return true
    }
}
