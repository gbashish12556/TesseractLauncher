package com.example.sdk

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo


class FetchAppListUtil{

    var context:Context? = null
    constructor(context: Context){
        this.context = context
    }

    fun getListOfApps():List<ResolveInfo>{
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList = context!!.packageManager.queryIntentActivities(mainIntent, 0)
        return pkgAppsList
    }

}