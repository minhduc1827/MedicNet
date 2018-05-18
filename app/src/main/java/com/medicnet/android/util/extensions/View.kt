package com.medicnet.android.util.extensions

import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.medicnet.android.R

fun View.openTabbedUrl(url: Uri) {
    with(this) {
        val tabsbuilder = CustomTabsIntent.Builder()
        tabsbuilder.setToolbarColor(ResourcesCompat.getColor(context.resources, R.color.colorPrimary, context.theme))
        val customTabsIntent = tabsbuilder.build()
        customTabsIntent.launchUrl(context, url)
    }
}