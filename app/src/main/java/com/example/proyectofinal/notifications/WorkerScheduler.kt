package com.example.proyectofinal.notifications

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

fun scheduleOneTimeWorker(context: Context, userId: String) {
    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInputData(workDataOf("userId" to userId))
        .setInitialDelay(1, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}