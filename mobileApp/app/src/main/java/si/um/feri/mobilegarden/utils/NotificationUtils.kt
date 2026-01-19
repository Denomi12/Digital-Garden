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
import si.um.feri.mobilegarden.models.Garden

private const val TAG = "NotificationDebug"
fun showWeatherNotification(
    context: Context,
    garden: Garden,
    precip: Double,
    wind: Double
) {

    Log.d(TAG, "Weather notification for garden: ${garden.name}")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "POST_NOTIFICATIONS not granted")
            return
        }
    }

    val channelId = "weather_alert_channel"
    val channelName = "Vremenska opozorila"

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Opozorila za nevarne vremenske razmere"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("NAVIGATE_TO_MAP", true)
    }

    val notificationId = (garden.name + garden.latitude).hashCode()

    val pendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val title = "⚠️ Vremensko opozorilo"
    val message = buildString {
        append("Vrt: ${garden.name}\n")
        append("Padavine: %.1f mm, ".format(precip))
        append("Veter: %.1f km/h".format(wind))
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle(title)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    notificationManager.notify(notificationId, notification)

    Log.d(TAG, "Notification sent (fixed ID)")
}
