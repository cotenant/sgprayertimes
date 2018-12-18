package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote

import android.content.Context
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.util.isConnectedToInternet
import com.nibmz7gmail.sgprayertimemusollah.core.util.toListItems
import timber.log.Timber
import javax.inject.Inject

interface RemoteDataSource {
    fun getRemoteCalendarData(): List<CalendarData>?
}

class RemoteCalendarDataSource @Inject constructor(
    private val context: Context
) : RemoteDataSource {

    override fun getRemoteCalendarData(): List<CalendarData>? {
        if (!context.isConnectedToInternet()) {
            Timber.e("Network not connected")
            return null
        }

        Timber.i("Trying to download data from network")
        val responseSource = try {
            CalendarDataDownloader().fetch()
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

        val jsonData = responseSource?.body()?.string() ?: return null

        val parsedData = try {
            jsonData.toListItems<CalendarData>()
        } catch (e: Exception) {
            Timber.e(e, "Error parsing downloaded data")
            null
        }
        responseSource.close()
        return parsedData
    }

}
