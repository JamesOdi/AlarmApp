package james.odike.projects.alarmapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_ALARM
import james.odike.projects.alarmapp.TimeConstant.alarmContent
import james.odike.projects.alarmapp.TimeConstant.alarmRequestCode
import james.odike.projects.alarmapp.TimeConstant.alarmTitle
import james.odike.projects.alarmapp.TimeConstant.channelId
import james.odike.projects.alarmapp.TimeConstant.channelName
import james.odike.projects.alarmapp.TimeConstant.channelNumericId

class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmIntent = Intent(context,MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context,
                alarmRequestCode, alarmIntent, PendingIntent.FLAG_ONE_SHOT)
            val builder = NotificationCompat.Builder(context!!, channelId)

            builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            builder.setContentTitle(alarmTitle)
            builder.setContentText(alarmContent)
            builder.setContentIntent(pendingIntent)
            builder.setAutoCancel(true)
            builder.setVibrate(longArrayOf(1000))
            builder.setCategory(CATEGORY_ALARM)

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    IMPORTANCE_HIGH)
            )
            val notification = builder.build()
            notificationManager.notify(channelNumericId,notification)
        }
    }
}