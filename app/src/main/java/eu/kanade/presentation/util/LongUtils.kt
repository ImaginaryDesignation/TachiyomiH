package eu.kanade.presentation.util

import android.content.Context
import eu.kanade.tachiyomi.R
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Long.getLastTimeString(
    now: Long,
    context: Context,
    relativeTime: Int,
    dateFormat: String,
    event: Event,
    status: Int,
): String {
    if (this == 0L) return ""
    val timeString = abs(now - this).toDuration(DurationUnit.MILLISECONDS)
        .toCompoundDurationString(context, relativeTime, dateFormat, true, this)
    when (event) {
        Event.LIBRARY_UPDATE -> {
            return context.getString(
                R.string.updates_last_update_info,
                timeString,
            )
        }
        Event.AUTOMATIC_BACKUP -> {
            return if (status == 1) {
                context.getString(
                    R.string.last_auto_backup_info,
                    "$timeString (${context.getString(R.string.success)})",
                )
            } else {
                context.getString(
                    R.string.last_auto_backup_info,
                    "$timeString (${context.getString(R.string.failed)})",
                )
            }
        }
    }
}

fun Long.getNextTimeString(
    now: Long,
    context: Context,
    relativeTime: Int,
    dateFormat: String,
    event: Event,
    interval: Int,
): String {
    if (interval == 0 || this == 0L) return ""
    val diff = (now - this) / 3600000.0
    var hours = 1
    // Check if number of hours that have passed since last update is greater than update interval
    if (diff > interval.toDouble()) {
        // Find number of times the interval has passed
        hours = ceil(diff / interval).toInt()
    }
    val time = this + (interval.toLong() * 3600000 * hours)
    val timeString = abs(now - time).toDuration(DurationUnit.MILLISECONDS)
        .toCompoundDurationString(context, relativeTime, dateFormat, false, time)
    return when (event) {
        Event.LIBRARY_UPDATE -> {
            context.getString(
                R.string.updates_next_update_info,
                timeString,
            )
        }
        Event.AUTOMATIC_BACKUP -> {
            context.getString(
                R.string.next_auto_backup_info,
                timeString,
            )
        }
    }
}

enum class Event {
    LIBRARY_UPDATE,
    AUTOMATIC_BACKUP,
}
