package es.juanavila.liverss.framework.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import es.juanavila.liverss.presentation.main.MainActivity
import es.juanavila.liverss.R
import es.juanavila.liverss.application.services.NotificationService
import es.juanavila.liverss.application.services.SimpleNotification
import javax.inject.Inject

class AndroidNotificationService @Inject constructor(val appContext: Application) : NotificationService{

    override fun notify(msg : SimpleNotification) {
        val CHANNEL_ID = "my_channel_01"// The providerId of the channel.
        val notifyID = 1

        val name: CharSequence = "SpiderNews"

        val notificationIntent = Intent(
            appContext,
            MainActivity::class.java
        )

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val contentIntent: PendingIntent? = PendingIntent.getActivity(
            appContext,
            notifyID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification: Notification? =
            NotificationCompat.Builder(appContext,CHANNEL_ID)
                .setSmallIcon(R.drawable.world_simple)
                .setContentIntent(contentIntent)
                .setContentTitle(msg.title)
            //    .setContentText("Hay $newsNb noticias nuevas")
                .setContentText(msg.message)
                .setChannelId(CHANNEL_ID).build()


        val mNotificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(notifyID, notification)
    }

}