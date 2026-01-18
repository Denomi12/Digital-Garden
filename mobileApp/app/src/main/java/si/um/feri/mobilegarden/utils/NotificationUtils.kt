package si.um.feri.mobilegarden.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import si.um.feri.mobilegarden.MainActivity
import si.um.feri.mobilegarden.models.ExtremeEvent

private const val TAG = "NotificationDebug"

fun showWeatherNotification(
    context: Context,
    event: ExtremeEvent,
    precip: Double,
    wind: Double
) {
    Log.d(TAG, "showWeatherNotification called for event: ${event.locationName}")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission POST_NOTIFICATIONS is not granted. Aborting.")
            return
        }
        Log.d(TAG, "Permission POST_NOTIFICATIONS is granted.")
    }

    val channelId = "weather_alert_channel"
    val channelName = "Vremenska opozorila"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Kanal za pomembna vremenska opozorila."
        }
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel '$channelId' created or already exists.")
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("NAVIGATE_TO_MAP", true)
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        event.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val title = "Vremensko opozorilo za ${event.locationName}"
    val message = "Možnost nevihte: Padavine ${precip}mm, Veter ${wind}km/h"

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    Log.d(TAG, "Notification built. Notifying with ID: ${event.id.hashCode()}")
    notificationManager.notify(event.id.hashCode(), notification)
    Log.d(TAG, "Notification sent.")
}
