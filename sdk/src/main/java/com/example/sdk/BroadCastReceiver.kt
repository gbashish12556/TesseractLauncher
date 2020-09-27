package com.example.sdk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast


class BroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        var appName = intent.data?.encodedSchemeSpecificPart
        var action = intent.action
        when(action){
            Intent.ACTION_PACKAGE_REMOVED->{
                Toast.makeText(context,"Teserract Uninstalled "+appName,Toast.LENGTH_LONG).show()
            }
            Intent.ACTION_PACKAGE_ADDED->{
                Toast.makeText(context,"Teserract Installed "+appName,Toast.LENGTH_LONG).show()
            }
        }
    }
}