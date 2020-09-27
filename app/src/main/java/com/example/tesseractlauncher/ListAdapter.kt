package com.example.tesseractlauncher

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.row.view.*

class ListAdapter(context: Activity, optionList: ArrayList<ResolveInfo>?, packageManager: PackageManager) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var optionList: ArrayList<ResolveInfo>? = optionList
    var context: Context = context
    var packageManager: PackageManager = packageManager
    var launchActivityPublishSubject: PublishSubject<ResolveInfo> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val constraintLayout = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(constraintLayout)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.imageView
        val appName: TextView = itemView.appName
        val packagName: TextView = itemView.packagName
        val versionCode: TextView = itemView.versionCode
        val versionName: TextView = itemView.versionName
        val mainActivityClass: TextView = itemView.mainActivityClass
        val rootLayout: CardView = itemView.rootLayout
    }

    override fun getItemCount(): Int {
        return optionList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var parent = optionList!![position]
        holder.appName.text = String.format(context.resources.getString(R.string.app1_name)!!, parent.loadLabel(packageManager))
        holder.packagName.text = String.format(context.resources.getString(R.string.package_name)!!, parent.activityInfo.packageName)
        val packageInfo: PackageInfo = packageManager.getPackageInfo(parent.activityInfo.packageName, 0)
        holder.mainActivityClass.text = String.format(context.resources.getString(R.string.activity_name)!!, parent.activityInfo.name)
        holder.versionCode.text = String.format(context.resources.getString(R.string.version_code)!!, packageInfo.versionCode.toString())
        holder.versionName.text = String.format(context.resources.getString(R.string.version_name)!!, packageInfo.versionName)
        try {

            Glide.with(context!!)
                .load( parent.loadIcon(packageManager))
                .thumbnail(0.1f)
                .into(holder.imageView);

        } catch (e: RuntimeException) {

        }
        holder.rootLayout.setOnClickListener{
            launchActivityPublishSubject.onNext(parent)
        }

    }

    fun getLaunchAppPublishSubject(): PublishSubject<ResolveInfo> {
        return launchActivityPublishSubject
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setData(data: List<ResolveInfo>) {
        this.optionList?.clear()
        this.optionList?.addAll(data)
        notifyDataSetChanged()
    }
}
