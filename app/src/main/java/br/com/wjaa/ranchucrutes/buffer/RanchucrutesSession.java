package br.com.wjaa.ranchucrutes.buffer;

import java.util.ArrayList;
import java.util.List;

import br.com.wjaa.ranchucrutes.entity.PacienteEntity;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;

/**
 * Created by wagner on 17/09/15.
 */
public class RanchucrutesSession {

    private static PacienteEntity usuario;
    private static List<SessionChangedListener> sessionChangedListenerList;

    public static PacienteEntity getUsuario() {
        return usuario;
    }

    public static void setUsuario(PacienteEntity usuario) {
        RanchucrutesSession.usuario = usuario;
        //disparando o evento
        if (sessionChangedListenerList != null){
            for (SessionChangedListener listener: sessionChangedListenerList) {
                listener.usuarioChange(usuario);
            }
        }
    }

    public static void addSessionChangedListener (SessionChangedListener listener){
        if ( sessionChangedListenerList == null ){
            sessionChangedListenerList = new ArrayList<>();
        }
        sessionChangedListenerList.add(listener);
    }

    public static boolean isUsuarioLogado() {
        return usuario != null;
    }

    public static void logoff() {
        setUsuario(null);
    }
}
