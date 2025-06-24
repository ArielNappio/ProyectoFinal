package com.example.proyectofinal.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proyectofinal.R
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.mail.domain.model.MessageModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val ktorClient: HttpClient by inject(HttpClient::class.java)

    override fun doWork(): Result {
        val userId = inputData.getString("userId") ?: return Result.failure()

        Log.d("NotificationWorker", "User ID: $userId")
        val backendUrl = ApiUrls.MESSAGES_INBOX_BY_ID.replace("{userId}", userId)

        val workerResult = runBlocking {
            try {
                val messages: List<MessageModel> = ktorClient.get(backendUrl).body()

                val inboxMessagesIds = messages.filter { it.userToId == userId }.map { it.id }
                val newData = inboxMessagesIds.toString()
                Log.d("NotificationWorker", "New Inbox messages IDs: $newData")

                val sharedPreferences = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val lastData = sharedPreferences.getString("last_data", null)
                Log.d("NotificationWorker", "Old Inbox messages IDs: $lastData")

                if (lastData != null && newData != lastData) {
                    Log.d("NotificationWorker", "Sending notification for new messages: $newData")
                    sharedPreferences.edit { putString("last_data", newData) }
                    sendNotification()
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("NotificationWorker", "Error fetching new inbox messages: ${e.message}", e)
                Result.failure()
            }
        }
        // Reschedule the worker
        scheduleOneTimeWorker(applicationContext, userId)

        return workerResult
    }

    private fun sendNotification() {
        val channelId = "updates_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Updates", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Tenés un nuevo mensaje")
            .setContentText("Revisá tu bandeja de entrada para verlo.")
            .setSmallIcon(R.drawable.wirin50)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationUUID = UUID.randomUUID().hashCode()

        notificationManager.notify(notificationUUID, notification)
    }
}