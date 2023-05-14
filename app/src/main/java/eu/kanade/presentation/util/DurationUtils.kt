package eu.kanade.presentation.util

import android.content.Context
import eu.kanade.tachiyomi.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration

fun Duration.toDurationString(context: Context, fallback: String): String {
    return toComponents { days, hours, minutes, seconds, _ ->
        buildList(4) {
            if (days != 0L) add(context.getString(R.string.day_short, days))
            if (hours != 0) add(context.getString(R.string.hour_short, hours))
            if (minutes != 0 && (days == 0L || hours == 0)) add(context.getString(R.string.minute_short, minutes))
            if (seconds != 0 && days == 0L && hours == 0) add(context.getString(R.string.seconds_short, seconds))
        }.joinToString(" ").ifBlank { fallback }
    }
}

fun Duration.toCompoundDurationString(context: Context, relativeTime: Int, dateFormat: String, last: Boolean, ogTime: Long): String {
    val dateFormatter = SimpleDateFormat("$dateFormat hh:mm aa", Locale.getDefault())
    if (relativeTime == 0) {
        return dateFormatter.format(Date(ogTime))
    }
    return toComponents { days, hours, minutes, seconds, _ ->
        if (days > relativeTime) {
            return dateFormatter.format(Date(ogTime))
        } else if (minutes == 0 && hours == 0 && days == 0L && seconds > 0) {
            if (last) {
                context.getString(
                    R.string.backup_last_less_than_a_minute,
                )
            } else {
                context.getString(
                    R.string.backup_next_in_less_than_a_minute,
                )
            }
        } else {
            val compoundString = buildList(3) {
                if (days != 0L) add(context.getString(R.string.day_short, days))
                if (hours != 0) add(context.getString(R.string.hour_short, hours))
                if (minutes != 0) add(context.getString(R.string.minute_short, minutes))
            }.joinToString(" ")
            if (last) {
                context.getString(
                    R.string.backup_last_ago,
                    compoundString,
                )
            } else {
                context.getString(
                    R.string.backup_next_in,
                    compoundString,
                )
            }
        }
    }
}
