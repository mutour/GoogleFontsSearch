package com.doufu.google.fonts.search.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri


fun Activity.gotoBrowser(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(android.content.Intent.ACTION_VIEW, uri)
    this.startActivity(intent)
}