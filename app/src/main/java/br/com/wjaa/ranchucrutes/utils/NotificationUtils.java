package br.com.wjaa.ranchucrutes.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import br.com.wjaa.ranchucrutes.R;

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

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icone)
                .setTicker(mensagemBarraStatus)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(p)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .build();

        // Flag utilizada para remover a notificacao da barra de status
        // quando o usuario clicar nela
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Espera 100ms e vibra por 250ms, espera por mais 100ms e vibra por 500ms
        notification.vibrate = new long[] { 100, 250, 100, 500 };

        // id da notificacao
        nm.notify(R.string.app_name, notification);
    }

}