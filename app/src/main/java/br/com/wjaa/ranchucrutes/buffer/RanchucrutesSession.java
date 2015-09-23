package br.com.wjaa.ranchucrutes.buffer;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import br.com.wjaa.ranchucrutes.commons.AuthType;
import br.com.wjaa.ranchucrutes.entity.UsuarioEntity;
import br.com.wjaa.ranchucrutes.form.LoginForm;
import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;

/**
 * Created by wagner on 17/09/15.
 */
public class RanchucrutesSession {

    private static UsuarioEntity usuario;
    private static List<SessionChangedListener> sessionChangedListenerList;

    public static UsuarioEntity getUsuario() {
        return usuario;
    }

    public static void setUsuario(UsuarioEntity usuario) {
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
        if (AuthType.AUTH_FACEBOOK.equals(usuario.getAuthType())){
            LoginManager.getInstance().logOut();
        }
        setUsuario(null);
    }
}
