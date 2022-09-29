/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */
package com.BiblioLivro.criarlivros.gestores

import androidx.appcompat.app.AppCompatActivity
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.media.AudioAttributes
import androidx.core.app.NotificationManagerCompat
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.BiblioLivro.criarlivros.activities.TelaPesquisar
import com.BiblioLivro.criarlivros.R

class GestorNotification(
    activity: AppCompatActivity?,
    icon: Int,
    title: String?,
    text: String?,
    priority: Int
) {
    //ATRIBUTOS
    private var builder: NotificationCompat.Builder? = null
    private var activity: AppCompatActivity? = null
    private var sound: Uri? = null
    private lateinit var pattern: LongArray


    init {
        if (priority >= -2 && priority <= 2) {
            this.activity = activity
            builder = NotificationCompat.Builder(this.activity!!, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(setPendingIntent())
                .setAutoCancel(true)
                .setPriority(priority)
        }
    }

    /**
     * @param color é utilizado para definir a cor background da notificação
     */
    fun setColor(color: Int) {
        builder!!.color = color
    }

    /**
     * @param pattern é utilizado para determinar a duração da vibração:
     * posição:
     * #0  tempo de espera em mills antes de iniciar a vibração
     * #1  a duração da vibração em mills
     * #2  a pausa entre uma vibração e a outra
     * #n  repetição das outras posições: #1 e #2
     */
    fun setDurationVibrate(pattern: LongArray) {
        this.pattern = pattern
        builder!!.setVibrate(pattern)
    }

    /**
     * @param sound é utilizado para executar um som durante a notificação
     */
    fun setSound(sound: Uri?) {
        this.sound = sound
        builder!!.setSound(sound)
    }

    /**
     * Este método é utilizado só para as API 26
     *
     * @param channelName    nome do canal
     * @param description    descrição do canal
     * @param systemServices NotificationManager
     */
    fun createChannelId(
        channelName: CharSequence?,
        description: String?,
        systemServices: NotificationManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            channel.description = description
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            channel.setSound(sound, attributes)
            channel.vibrationPattern = pattern
            channel.lightColor = Color.GREEN
            channel.enableLights(true)
            channel.enableVibration(true)
            systemServices.createNotificationChannel(channel)
        }
    }

    /**
     * Método utilizado para imprimir a notificação
     */
    fun printNotification() {
        val notificationManager = NotificationManagerCompat.from(activity!!)
        notificationManager.notify(0, builder!!.build())
    }

    /**
     * Método utilizado caso o usuário clique na notificação
     * será exibido a TelaPesquisar
     */
    private fun setPendingIntent(): PendingIntent {
        val intent = Intent(activity, TelaPesquisar::class.java)
        intent.putExtra("tipo", R.id.rbPesquisarPorTodos)
        intent.putExtra("chave", "")
        val stackBuilder = TaskStackBuilder.create(activity)
        stackBuilder.addNextIntentWithParentStack(intent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        private const val CHANNEL_ID = "DEFAULT CHANNEL"
    }
}