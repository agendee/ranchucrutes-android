package br.com.agendee.service;

/**
 * Created by wagner on 10/09/15.
 */
public class RanchucrutesConstants {
     public static final String WS_HOST = "rest.agendee.com.br";

    // public static final String WS_HOST = "localhost:9191/ranchucrutes-ws";


    public static final String END_POINT_PROCURAR_PROFISSIONAL = "profissional/search";
    public static final String END_POINT_PROCURAR_CLINICAS = "profissional/clinica/search";
    public static final String END_POINT_CRIAR_PACIENTE = "paciente/save";
    public static final String END_POINT_AUTH_PACIENTE = "auth/paciente";
    public static final String END_POINT_ATUALIZAR_PACIENTE = "paciente/update";
    public static final String END_POINT_RECUPERAR_SENHA_PACIENTE = "paciente/recuperarsenha";
    public static final String END_POINT_CRIAR_AGENDAMENTO = "agendamento/criar";
    public static final String END_POINT_AGENDAMENTO = "agendamento";
    public static final String END_POINT_CONFIRMAR_SOLICITACAO = "agendamento/confirmarSolicitacao";
    public static final String END_POINT_CANCELAR_CONSULTA = "agendamento/cancelarConsulta";
    public static final String END_POINT_CONFIRMAR_CONSULTA = "agendamento/confirmarConsulta";
    public static final String END_POINT_GET_PROFISSIONAL = "profissional/basico";
    public static final String END_POINT_GET_AGENDAMENTOS = "agendamento/list";
    public static final String END_POINT_GET_AGENDAMENTO = "agendamento";
    public static final String END_POINT_REGISTRO_GCM = "gcm/register";


    public static final String PARAM_LIST_SEARCH = "listSearch";
    public static final String PARAM_RESULT_SEARCH = "resultSearch";
    public static final String PARAM_PROFISSIONAL = "profissionalParam";
    public static final String PARAM_CLINICA = "clinicaParam";
    public static final String PARAM_CONFIMAR_AGENDAMENTO = "confirmarAgendamentoParam";
    public static final String PARAM_OPEN_FRAGMENT_MAIN_ACTIVITY = "openFragmentMainActivity";
    public static final String PARAM_AGENDAMENTO = "agendamentoParam";
    public static final String PARAM_QUERY_TEXT = "queryText";
    public static final String PARAM_TITLE = "titleParam";

    /*CONSTANTES PARA CONTROLE DE ACTIVITYS*/
    public static final int FINISH_TO_OPEN_HOME = 1000;

    public static final int FINISH_MY_LOCATION = 50;
    public static final int FINISH_CONFIRME_AGENDAMENTO = 51;
    public static final int FINISH_CONFIRME_AGENDAMENTO_OPEN_LIST = 52;


}
