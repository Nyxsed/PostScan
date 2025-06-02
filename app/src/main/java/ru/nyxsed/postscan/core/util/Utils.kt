package ru.nyxsed.postscan.core.util

import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Date


object Constants {
    const val VK_API_VERSION = "5.199"
    const val VK_BASE_URL = "https://api.vk.com/method/"
    const val VK_WALL_URL = "https://vk.com/wall"
    const val VK_URL = "https://vk.com/"
    const val VK_PHOTO_URL = "https://vk.com/photo"

    const val MANGA_SEARCH_ACTION = "eu.kanade.tachiyomi.SEARCH"

    const val YANDEX_SEARCH_URL = "https://yandex.ru/images/search?rpt=imageview&url="
    const val BING_SEARCH_URL = "https://www.bing.com/images/searchbyimage?FORM=IRSBIQ&cbir=sbi&imgurl="
    const val SAUCENAO_SEARCH_URL = "https://saucenao.com/search.php?url="
    const val TINEYE_SEARCH_URL = "https://www.tineye.com/search?url="
    const val IQDB_SEARCH_URL = "https://iqdb.org/?url="
    const val TRACE_SEARCH_URL = "https://trace.moe/?url="

    const val DATE_MASK = "##.##.####"

    const val PROGRESS_CHANNEL_ID = "progress_channel"
    const val PROGRESS_CHANNEL_NAME = "Progress Notifications"
    const val PROGRESS_NOTIFICATION_ID = 1

    fun mihonIntent(query: String): Intent {
        return Intent().apply {
            action = MANGA_SEARCH_ACTION
            val cleanedText = query
                .replace(Regex("\\r?\\n"), " ")
                .replace(Regex("[\\p{So}\\p{Cn}]|[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]"), "")
            putExtra("query", cleanedText)
        }
    }

    fun <T> List<T>.findOrFirst(predicate: (T) -> Boolean): T =
        find(predicate) ?: first()

    fun <T> List<T>.findOrLast(predicate: (T) -> Boolean): T =
        find(predicate) ?: last()

    fun String.toDateLong(): Long {
        val format = SimpleDateFormat("ddMMyyyy")
        return format.parse(this)?.time ?: System.currentTimeMillis()
    }

    fun Long.toStringDate(): String {
        val date = Date(this)
        val format = SimpleDateFormat("dd.MM.yyyy")
        return format.format(date)
    }
}