package com.example.letterbox.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.letterbox.MessageChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging :FirebaseMessagingService()
{
    override fun onMessageReceived(mRemoteMessage: RemoteMessage)
    {
        super.onMessageReceived(mRemoteMessage)

        val sented=mRemoteMessage.data["sented"]
        val user=mRemoteMessage.data["user"]

        val sharedPref= getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser=sharedPref.getString("currentUser", "none")

        val firebaseUser= FirebaseAuth.getInstance().currentUser

        if (firebaseUser!=null && sented==firebaseUser.uid)
        {
            if(currentOnlineUser!=user)
            {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)        //"O" stands for Oreo   //From Oreo and higher versions, there have been change in the way notifications are sent
                {
                    sendOreoNotifications(mRemoteMessage)
                }
                else                                                    //For versions below "Oreo"
                {
                    sendNotifications(mRemoteMessage)
                }
            }
        }

    }





    private fun sendOreoNotifications(mRemoteMessage: RemoteMessage)
    {
        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification=mRemoteMessage.notification
        val j=user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent= Intent(this, MessageChatActivity::class.java)       //Whenever a user clicks the notification, it will go straight to that person's MessageChatActivity instead of going to MainActivity as it used to do versions before Oreo

        val bundle=Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent=PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification= OreoNotification(this)
        val builder:Notification.Builder=oreoNotification.getOreoNotification(title, body!!, pendingIntent, defaultSound, icon!!)

        var i=0
        if(j>0)
        {
            i=j
        }

        oreoNotification.getManager!!.notify(i, builder.build())

    }



    private fun sendNotifications(mRemoteMessage:RemoteMessage)
    {
        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notificatioon=mRemoteMessage.notification
        val j=user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent= Intent(this, MessageChatActivity::class.java)       //Whenever a user clicks the notification, it will go straight to that person's MessageChatActivity instead of going to MainActivity as it used to do versions before Oreo

        val bundle=Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent=PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder=NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        var i=0
        if(j>0)
        {
            i=j
        }

        noti.notify(i, builder.build())



    }




}