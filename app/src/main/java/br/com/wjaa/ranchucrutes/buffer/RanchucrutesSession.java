package br.com.wjaa.ranchucrutes.buffer;

import java.util.ArrayList;
import java.util.List;

import br.com.wjaa.ranchucrutes.listener.SessionChangedListener;
import br.com.wjaa.ranchucrutes.vo.PacienteVo;

/**
 * Created by wagner on 17/09/15.
 */
public class RanchucrutesSession {

    private static PacienteVo paciente;
    private static List<SessionChangedListener> sessionChangedListenerList;

    public static PacienteVo getPaciente() {
        return paciente;
    }

    public static void setPaciente(PacienteVo paciente) {
        RanchucrutesSession.paciente = paciente;
        //disparando o evento
        if (sessionChangedListenerList != null){
            for (SessionChangedListener listener: sessionChangedListenerList) {
                listener.pacienteChange(paciente);
            }
        }
    }

    public static void addSessionChangedListener (SessionChangedListener listener){
        if ( sessionChangedListenerList == null ){
            sessionChangedListenerList = new ArrayList<>();
        }
        sessionChangedListenerList.add(listener);
    }
}
