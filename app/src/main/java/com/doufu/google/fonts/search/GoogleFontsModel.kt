package com.doufu.google.fonts.search

import android.app.Activity
import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.doufu.google.fonts.search.utils.DFLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class FontInfo(val name: String) {
    var searchMatchGroups: List<MatchGroup>? = null
}

class GoogleFontsModel(application: Application) : AndroidViewModel(application) {

    private var _familyNames = arrayOf<String>()
    val familyNames = mutableStateListOf<FontInfo>()

    init {
        GlobalScope.launch {
            val names = application.resources.getStringArray(R.array.family_names)
            _familyNames = names
            reset()
        }
    }

    private fun reset() {
        familyNames.clear()
        familyNames.addAll(_familyNames.map { FontInfo(it) })
    }

    fun search(name: String) {
        if (name.isNullOrEmpty()) {
            DFLog.Default.d("search result: ${_familyNames.size}")
            reset()
            return
        }
        val regex = Regex(name.split(" ").joinToString(separator = ").*(", prefix = "(", postfix = ")"), RegexOption.IGNORE_CASE)
        val names = _familyNames.mapNotNull {
//            it.toLowerCase().contains(name)
            val matchResult = regex.find(it)
            if (matchResult != null) {
                FontInfo(it).apply {
                    searchMatchGroups = matchResult.groups.mapIndexedNotNull { index, matchGroup ->
                        if (index != 0) matchGroup else null
                    }
                }

            } else null
        }
        DFLog.Default.d("search result: ${names.size}")
        familyNames.clear()
        familyNames.addAll(names)
    }

    fun goto(activity: Activity, fontInfo: FontInfo) {
        DFLog.Default.d("goto: ${fontInfo.name}")
        // https://fonts.google.com/specimen/Noto+Sans+TC
        val url = "https://fonts.google.com/specimen/${fontInfo.name.replace(" ", "+")}"
        WebActivity.show(activity, url)
    }
}