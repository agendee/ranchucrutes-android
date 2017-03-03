package br.com.agendee.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import br.com.agendee.R;

/**
 * Created by wagner on 19/11/15.
 */
public class NotificationUtils {


    // Exibe a notificacao
    public static void criarNotificacao(Context context, CharSequence mensagemBarraStatus,
                                        CharSequence titulo, CharSequence mensagem, Class<?> activity) {
        // Recupera o servico do NotificationManager
        NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        // PendingIntent para executar a Activity se o usuario selecionar a notificacao
        PendingIntent p = PendingIntent.getActivity(context, 0, new Intent(context, activity), 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icone)
                .setTicker(mensagemBarraStatus)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(p)
                .setContentTitle(titulo)
                .setSound(sound)
                .setContentText(mensagem);

        Notification notification = builder.build();

        if(Build.VERSION.SDK_INT >= 21) {
            notification.sound = sound;
            notification.category = Notification.CATEGORY_ALARM;

            AudioAttributes.Builder attrs = new AudioAttributes.Builder();
            attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attrs.setUsage(AudioAttributes.USAGE_ALARM);
            notification.audioAttributes = attrs.build();
        }


        // Flag utilizada para remover a notificacao da barra de status
        // quando o usuario clicar nela
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Espera 100ms e vibra por 250ms, espera por mais 100ms e vibra por 500ms
        notification.vibrate = new long[] { 200, 700, 200, 700 };

        // id da notificacao
        nm.notify(R.string.app_name, notification);

    }

}