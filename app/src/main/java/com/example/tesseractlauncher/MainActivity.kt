package com.example.tesseractlauncher

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdk.BroadCastReceiver
import com.example.sdk.FetchAppListUtil
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var searchApp: EditText

    var listResolver:List<ResolveInfo>? = null
    var adapter:ListAdapter? = null
    var br: BroadCastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchApp = findViewById(R.id.searchApp)

        intialiseRecyclerView()
        intialiseSearch()

    }

    override fun onResume() {
        super.onResume()
        intialiseBroadCast()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(br != null) {
            unregisterReceiver(br);
            br = null
        }
    }

    fun intialiseRecyclerView(){
        var packageManager = getPackageManager()
        listResolver = FetchAppListUtil(this).getListOfApps()
        Collections.sort(
                listResolver,
                ResolveInfo.DisplayNameComparator(packageManager)
        )
        var copyList:List<ResolveInfo> = ArrayList(listResolver!!)

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        adapter = ListAdapter(this@MainActivity, copyList as ArrayList<ResolveInfo>,packageManager)
        adapter?.getLaunchAppPublishSubject()?.subscribe{parent->
            launActivity(parent)
        }
        recyclerView.adapter = adapter
    }

    fun intialiseSearch(){
        searchApp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
            ) {


                var newList = listResolver?.filter { it.loadLabel(packageManager).toString().toLowerCase().startsWith(s.toString().toLowerCase()) }
                adapter?.setData(newList!!)
            }
        })
    }

    fun launActivity(parent: ResolveInfo){
        val activity: ActivityInfo = parent.activityInfo
        val name = ComponentName(
                activity.applicationInfo.packageName,
                activity.name
        )
        val i = Intent(Intent.ACTION_MAIN)

        i.addCategory(Intent.CATEGORY_LAUNCHER)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        i.component = name

        startActivity(i)
    }


    fun intialiseBroadCast(){
        br  = BroadCastReceiver()
        val intentFilter: IntentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter)

    }

}