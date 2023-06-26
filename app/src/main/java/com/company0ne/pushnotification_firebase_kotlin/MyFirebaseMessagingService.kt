package com.company0ne.pushnotification_firebase_kotlin


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val chanelName = "com.company0ne.pushnotification_firebase_kotlin"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    //generate the notifications
    //attach the notification with the custom layout
    //show the notification
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //receive the notification
        if (remoteMessage.getNotification() != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    //create an method remoteView
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView =
            RemoteViews("com.company0ne.pushnotification_firebase_kotlin", R.layout.notification)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.notification_icon)
        return remoteView
    }

    //generate the notification
    fun generateNotification(title: String, message: String) {

        //create intent because when the user click on notification, the app will open
        val intent = Intent(this, MainActivity::class.java)

        //clear all the activities and put this(MainActivity) at the top priority
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //we use this pending activity only once( it will destroy after used once)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        //We use channel id , channel name (after Oreo update)
        //we create notification using NotificationBuilder
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                //set icons,autoCancel , OnlyAlertOnce
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        //attach the builder with the notification layout(create getRemoteView method)
        builder = builder.setContent(getRemoteView(title, message))

        //notificationManager( Android allows to put notification into the titleBar of your application)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //check user version must be greater than OreoVersion which is Code O(oh not zero)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create an notificationChannel( all notifications must be assigned to a channel)
            val notificationChannel =
                NotificationChannel(channelId, chanelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //get notify
        notificationManager.notify(0, builder.build())
    }
}