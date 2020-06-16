/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.gestores;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.activities.TelaPesquisar;


public class GestorNotification {

    //ATRIBUTOS
    private NotificationCompat.Builder builder;
    private AppCompatActivity activity;
    private Uri Sound;
    private long[] Pattern;

    private static String CHANNEL_ID = "DEFAULT CHANNEL";


    /**
     * @param Activity utilizado para indicar em qual "tela" será imprimida a notificação
     * @param icon     utilizado para indicar qual será o ícone da notificação utilizando a classe R.icon
     * @param title    utilizado para indicar o título da notificação
     * @param text     utilizado para indicar o texto da notificação. Por padrão aparecerá em uma linha apenas.
     *                 Fazer a extenção da classe caso queira modificar esse parâmentro.
     * @param priority utilizado para indicar a prioridade da notificação:
     *                 Onde:
     *                 -2 é a menor prioridade. Só aparecerá no menú das notificações
     *                 2 é a maior prioridade. Aparecerá em destaque na tela "Activity" que escolheu,
     *                 e também no menu das notificações
     */
    public GestorNotification(AppCompatActivity Activity, int icon, String title, String text, int priority) {
        if (priority >= -2 && priority <= 2) {
            activity = Activity;

            builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(setPendingIntent())
                    .setAutoCancel(true)
                    .setPriority(priority);

        }
    }

    /**
     * @param color é utilizado para definir a cor background da notificação
     */
    public void setColor(int color) {
        builder.setColor(color);
    }

    /**
     * @param pattern é utilizado para determinar a duração da vibração:
     *                posição:
     *                #0  tempo de espera em mills antes de iniciar a vibração
     *                #1  a duração da vibração em mills
     *                #2  a pausa entre uma vibração e a outra
     *                #n  repetição das outras posições: #1 e #2
     */
    public void setDurationVibrate(long[] pattern) {
        Pattern = pattern;
        builder.setVibrate(pattern);
    }


    /**
     * @param sound é utilizado para executar um som durante a notificação
     */
    public void setSound(Uri sound) {
        Sound = sound;
        builder.setSound(sound);
    }


    /**
     * Este método é utilizado só para as API 26
     *
     * @param channelName    nome do canal
     * @param description    descrição do canal
     * @param systemServices NotificationManager
     */
    public void createChannelId(CharSequence channelName, String description, NotificationManager systemServices) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(description);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel.setSound(Sound, attributes);
            channel.setVibrationPattern(Pattern);
            channel.setLightColor(Color.GREEN);
            channel.enableLights(true);
            channel.enableVibration(true);
            systemServices.createNotificationChannel(channel);

        }
    }

    /**
     * Método utilizado para imprimir a notificação
     */
    public void printNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
        notificationManager.notify(0, builder.build());
    }

    /**
     * Método utilizado caso o usuário clique na notificação
     * será exibido a TelaPesquisar
     */
    private PendingIntent setPendingIntent() {
        Intent intent = new Intent(activity, TelaPesquisar.class);
        intent.putExtra("tipo", R.id.rbPesquisarPorTodos);
        intent.putExtra("chave", "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
