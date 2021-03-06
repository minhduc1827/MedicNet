package com.medicnet.android.push

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.RemoteInput
import android.text.Html
import android.text.Spanned
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.roomTypeOf
import com.medicnet.android.R
import com.medicnet.android.main.ui.MainActivity
import com.medicnet.android.server.domain.GetAccountInteractor
import com.medicnet.android.server.domain.GetSettingsInteractor
import com.medicnet.android.server.domain.siteName
import com.medicnet.android.server.ui.changeServerIntent
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.runBlocking
import se.ansman.kotshi.JsonSerializable
import se.ansman.kotshi.KotshiConstructor
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

/**
 * Refer to: https://github.com/RocketChat/Rocket.Chat.Android/blob/9e846b7fde8fe0c74b9e0117c37ce49293308db5/app/src/main/java/com.medicnet.android/push/PushManager.kt
 * for old source code.
 */
class PushManager @Inject constructor(
        private val groupedPushes: GroupedPush,
        private val manager: NotificationManager,
        private val moshi: Moshi,
        private val getAccountInteractor: GetAccountInteractor,
        private val getSettingsInteractor: GetSettingsInteractor,
        private val context: Context
) {
    private val randomizer = Random()

    /**
     * Handles a receiving push by creating and displaying an appropriate notification based
     * on the *data* param bundle received.
     */
    @Synchronized
    fun handle(data: Bundle) = runBlocking {
        val message = data["message"] as String?
        val ejson = data["ejson"] as String?
        val title = data["title"] as String?
        val notId = data["notId"] as String? ?: randomizer.nextInt().toString()
        val image = data["image"] as String?
        val style = data["style"] as String?
        val summaryText = data["summaryText"] as String?
        val count = data["count"] as String?

        try {
            val adapter = moshi.adapter<PushInfo>(PushInfo::class.java)
            val info = adapter.fromJson(ejson)

            val pushMessage = PushMessage(title!!, message!!, info!!, image, count, notId, summaryText, style)

            Timber.d("Received push message: $pushMessage")

            showNotification(pushMessage)
        } catch (ex: Exception) {
            Timber.d(ex, "Error parsing PUSH message: $data")
            ex.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    suspend fun showNotification(pushMessage: PushMessage) {
        if (!hasAccount(pushMessage.info.host)) {
            Timber.d("ignoring push message: $pushMessage")
            return
        }

        val notId = pushMessage.notificationId.toInt()
        val host = pushMessage.info.host
        val groupTuple = getGroupForHost(host)

        groupTuple.second.incrementAndGet()
        val notIdListForHostname: MutableList<PushMessage>? = groupedPushes.hostToPushMessageList.get(host)
        if (notIdListForHostname == null) {
            groupedPushes.hostToPushMessageList[host] = arrayListOf(pushMessage)
        } else {
            notIdListForHostname.add(0, pushMessage)
        }

        val notification = createSingleNotification(pushMessage)
        val pushMessageList = groupedPushes.hostToPushMessageList[host]

        notification?.let {
            manager.notify(notId, notification)
        }

        pushMessageList?.let {
            if (pushMessageList.size > 1) {
                val groupNotification = createGroupNotification(pushMessage)
                groupNotification?.let {
                    NotificationManagerCompat.from(context).notify(groupTuple.first, groupNotification)
                }
            }
        }
    }

    private fun getGroupForHost(host: String): TupleGroupIdMessageCount {
        val size = groupedPushes.groupMap.size
        var group = groupedPushes.groupMap[host]
        if (group == null) {
            group = TupleGroupIdMessageCount(size + 1, AtomicInteger(0))
            groupedPushes.groupMap[host] = group
        }
        return group
    }

    private suspend fun hasAccount(host: String): Boolean {
        return getAccountInteractor.get(host) != null
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createGroupNotification(pushMessage: PushMessage): Notification? {
        with(pushMessage) {
            val host = info.host

            val builder = createBaseNotificationBuilder(pushMessage, grouped = true)
                    .setGroupSummary(true)

            if (style == null || style == "inbox") {
                val pushMessageList = groupedPushes.hostToPushMessageList[host]

                pushMessageList?.let {
                    val count = pushMessageList.filter {
                        it.title == title
                    }.size

                    builder.setContentTitle(getTitle(count, title))

                    val inbox = NotificationCompat.InboxStyle()
                            .setBigContentTitle(getTitle(count, title))

                    for (push in pushMessageList) {
                        inbox.addLine(push.message)
                    }

                    builder.setStyle(inbox)
                }
            } else {
                val bigText = NotificationCompat.BigTextStyle()
                        .bigText(message.fromHtml())
                        .setBigContentTitle(title.fromHtml())

                builder.setStyle(bigText)
            }

            return builder.build()
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createSingleNotification(pushMessage: PushMessage): Notification? {
        with(pushMessage) {
            val host = info.host

            val builder = createBaseNotificationBuilder(pushMessage)
                    .setGroupSummary(false)

            if (style == null || "inbox" == style) {
                val pushMessageList = groupedPushes.hostToPushMessageList.get(host)

                pushMessageList?.let {
                    val userMessages = pushMessageList.filter {
                        it.notificationId == pushMessage.notificationId
                    }

                    val count = pushMessageList.filter {
                        it.title == title
                    }.size

                    builder.setContentTitle(getTitle(count, title))

                    if (count > 1) {
                        val inbox = NotificationCompat.InboxStyle()
                        inbox.setBigContentTitle(getTitle(count, title))
                        for (push in userMessages) {
                            inbox.addLine(push.message)
                        }

                        builder.setStyle(inbox)
                    } else {
                        val bigTextStyle = NotificationCompat.BigTextStyle()
                                .bigText(message.fromHtml())
                        builder.setStyle(bigTextStyle)
                    }
                }
            } else {
                val bigTextStyle = NotificationCompat.BigTextStyle()
                        .bigText(message.fromHtml())
                builder.setStyle(bigTextStyle)
            }

            return builder.addReplyAction(pushMessage)
                    .build()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBaseNotificationBuilder(pushMessage: PushMessage, grouped: Boolean = false): NotificationCompat.Builder {
        return with(pushMessage) {
            val id = notificationId.toInt()
            val host = info.host
            val contentIntent = getContentIntent(context, id, pushMessage, grouped)
            val deleteIntent = getDismissIntent(context, pushMessage)

            val builder = NotificationCompat.Builder(context, host)
                    .setWhen(info.createdAt)
                    .setContentTitle(title.fromHtml())
                    .setContentText(message.fromHtml())
                    .setGroup(host)
                    .setDeleteIntent(deleteIntent)
                    .setContentIntent(contentIntent)
                    .setMessageNotification()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(host, host, NotificationManager.IMPORTANCE_HIGH)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                channel.enableLights(false)
                channel.enableVibration(true)
                channel.setShowBadge(true)
                manager.createNotificationChannel(channel)
            }

            //TODO: Get Site_Name PublicSetting from cache
            val subText = getSiteName(host)
            if (subText.isNotEmpty()) {
                builder.setSubText(subText)
            }

            return@with builder
        }
    }

    private fun getSiteName(host: String): String {
        val settings = getSettingsInteractor.get(host)
        return settings.siteName() ?: "Rocket.Chat"
    }

    private fun getTitle(messageCount: Int, title: String): CharSequence {
        return if (messageCount > 1) "($messageCount) ${title.fromHtml()}" else title.fromHtml()
    }

    private fun getDismissIntent(context: Context, pushMessage: PushMessage): PendingIntent {
        val deleteIntent = Intent(context, DeleteReceiver::class.java)
                .putExtra(EXTRA_NOT_ID, pushMessage.notificationId.toInt())
                .putExtra(EXTRA_HOSTNAME, pushMessage.info.host)
        return PendingIntent.getBroadcast(context, pushMessage.notificationId.toInt(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getContentIntent(context: Context, notificationId: Int, pushMessage: PushMessage, grouped: Boolean = false): PendingIntent {
        val notificationIntent = context.changeServerIntent(pushMessage.info.host)
        // TODO - add support to go directly to the chatroom
        /*if (!grouped) {
            notificationIntent.putExtra(EXTRA_ROOM_ID, pushMessage.info.roomId)
        }*/
        return PendingIntent.getActivity(context, randomizer.nextInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // CharSequence extensions
    private fun CharSequence.fromHtml(): Spanned {
        return Html.fromHtml(this as String)
    }

    // NotificationCompat.Builder extensions
    private fun NotificationCompat.Builder.addReplyAction(pushMessage: PushMessage): NotificationCompat.Builder {
        val replyTextHint = context.getText(R.string.notif_action_reply_hint)
        val replyRemoteInput = RemoteInput.Builder(REMOTE_INPUT_REPLY)
                .setLabel(replyTextHint)
                .build()
        val pendingIntent = getReplyPendingIntent(pushMessage)
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_action_message_reply_24dp, replyTextHint, pendingIntent)
                .addRemoteInput(replyRemoteInput)
                .setAllowGeneratedReplies(true)
                .build()

        this.addAction(replyAction)
        return this
    }

    private fun getReplyIntent(pushMessage: PushMessage): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(context, DirectReplyReceiver::class.java)
        } else {
            Intent(context, MainActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }.also {
            it.action = ACTION_REPLY
            it.putExtra(EXTRA_PUSH_MESSAGE, pushMessage)
        }
    }

    private fun getReplyPendingIntent(pushMessage: PushMessage): PendingIntent {
        val replyIntent = getReplyIntent(pushMessage)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PendingIntent.getBroadcast(
                    context,
                    randomizer.nextInt(),
                    replyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                    context,
                    randomizer.nextInt(),
                    replyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun NotificationCompat.Builder.setMessageNotification(): NotificationCompat.Builder {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val res = context.resources
        val smallIcon = res.getIdentifier(
                "rocket_chat_notification", "drawable", context.packageName)
        with(this, {
            setAutoCancel(true)
            setShowWhen(true)
            color = context.resources.getColor(R.color.colorPrimary)
            setDefaults(Notification.DEFAULT_ALL)
            setSmallIcon(smallIcon)
            setSound(alarmSound)
        })
        return this
    }
}

data class PushMessage(
        val title: String,
        val message: String,
        val info: PushInfo,
        val image: String? = null,
        val count: String? = null,
        val notificationId: String,
        val summaryText: String? = null,
        val style: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(PushMessage::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeParcelable(info, flags)
        parcel.writeString(image)
        parcel.writeString(count)
        parcel.writeString(notificationId)
        parcel.writeString(summaryText)
        parcel.writeString(style)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushMessage> {
        override fun createFromParcel(parcel: Parcel): PushMessage {
            return PushMessage(parcel)
        }

        override fun newArray(size: Int): Array<PushMessage?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonSerializable
data class PushInfo @KotshiConstructor constructor(
        @Json(name = "host") val hostname: String,
        @Json(name = "rid") val roomId: String,
        val type: RoomType,
        val name: String?,
        val sender: PushSender?
) : Parcelable {
    val createdAt: Long
        get() = System.currentTimeMillis()
    val host by lazy {
        sanitizeUrl(hostname)
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            roomTypeOf(parcel.readString()),
            parcel.readString(),
            parcel.readParcelable(PushInfo::class.java.classLoader))

    private fun sanitizeUrl(baseUrl: String): String {
        var url = baseUrl.trim()
        while (url.endsWith('/')) {
            url = url.dropLast(1)
        }

        return url
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hostname)
        parcel.writeString(roomId)
        parcel.writeString(type.toString())
        parcel.writeString(name)
        parcel.writeParcelable(sender, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushInfo> {
        override fun createFromParcel(parcel: Parcel): PushInfo {
            return PushInfo(parcel)
        }

        override fun newArray(size: Int): Array<PushInfo?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonSerializable
data class PushSender @KotshiConstructor constructor(
        @Json(name = "_id") val id: String,
        val username: String?,
        val name: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushSender> {
        override fun createFromParcel(parcel: Parcel): PushSender {
            return PushSender(parcel)
        }

        override fun newArray(size: Int): Array<PushSender?> {
            return arrayOfNulls(size)
        }
    }
}

const val EXTRA_NOT_ID = "com.medicnet.android.EXTRA_NOT_ID"
const val EXTRA_HOSTNAME = "com.medicnet.android.EXTRA_HOSTNAME"
const val EXTRA_PUSH_MESSAGE = "com.medicnet.android.EXTRA_PUSH_MESSAGE"
const val EXTRA_ROOM_ID = "com.medicnet.android.EXTRA_ROOM_ID"
const val ACTION_REPLY = "com.medicnet.android.ACTION_REPLY"
const val REMOTE_INPUT_REPLY = "REMOTE_INPUT_REPLY"
