package br.com.agendee.service;

import android.util.Log;

import java.util.Date;

import br.com.agendee.exception.AgendamentoServiceException;
import br.com.agendee.exception.RestResponseUnsatisfiedException;
import br.com.agendee.form.AgendamentoForm;
import br.com.agendee.rest.RestUtils;
import br.com.agendee.utils.ObjectUtils;
import br.com.agendee.vo.AgendaVo;
import br.com.agendee.vo.AgendamentoVo;
import br.com.agendee.vo.ConfirmarAgendamentoVo;
import br.com.agendee.exception.RestException;
import br.com.agendee.exception.RestRequestUnstable;

/**
 * Created by wagner on 20/10/15.
 */
public class AgendamentoServiceImpl implements AgendamentoService {

    private static final String TAG = AgendamentoServiceImpl.class.getName();

    @Override
    public AgendaVo getAgendamentoByIdProfissional(Long idProfissional, Long idClinica) throws AgendamentoServiceException {
        try {
            return RestUtils.getJsonWithParamPath(AgendaVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_AGENDAMENTO, idProfissional.toString(), idClinica.toString());
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao buscar o agendamento do profissional ", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG, "Erro ao buscar o agendamento do profissional ", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }
    }

    @Override
    public ConfirmarAgendamentoVo criarAgendamento(Long idProfissional, Long idClinica, Long idPaciente,
                                                   Date date, boolean particular) throws AgendamentoServiceException {
        AgendamentoForm form = new AgendamentoForm(idProfissional,idClinica,idPaciente,date, particular);
        String json = ObjectUtils.toJson(form);
        try {
            return RestUtils.postJson(ConfirmarAgendamentoVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CRIAR_AGENDAMENTO, json);
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao buscar o agendamento do profissional ", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG, "Erro ao buscar o agendamento do profissional ", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }

    }

    @Override
    public AgendamentoVo confirmarSolicitacao(Long idAgendamento, String codigo) throws AgendamentoServiceException {
        try {
            return RestUtils.getJsonWithParamPath(AgendamentoVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CONFIRMAR_SOLICITACAO, idAgendamento.toString(), codigo);
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao confirmar o agendamento ", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG, "Erro ao confirmar o agendamento ", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }
    }


    @Override
    public AgendamentoVo[] getAgendamentos(Long idPaciente) throws AgendamentoServiceException {
        try {
            return RestUtils.getJsonWithParamPath(AgendamentoVo[].class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_GET_AGENDAMENTOS, idPaciente.toString() );
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao listar os agendamentos", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG,"Erro ao listar os agendamentos", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }
    }

    @Override
    public AgendamentoVo cancelarAgendamento(Long id) throws AgendamentoServiceException {
        try {
            return RestUtils.getJsonWithParamPath(AgendamentoVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CANCELAR_CONSULTA, id.toString() );
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao cancelar o agendamento", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG,"Erro ao cancelar o agendamento", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }
    }

    @Override
    public AgendamentoVo confirmarAgendamento(Long id) throws AgendamentoServiceException {
        try {
            return RestUtils.getJsonWithParamPath(AgendamentoVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_CONFIRMAR_CONSULTA, id.toString() );
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao confirmar o agendamento", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG,"Erro ao confirmar o agendamento", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }
    }

    @Override
    public AgendamentoVo getAgendamentoById(Long id) throws AgendamentoServiceException {
        try {
            return RestUtils.getJsonWithParamPath(AgendamentoVo.class, RanchucrutesConstants.WS_HOST,
                    RanchucrutesConstants.END_POINT_GET_AGENDAMENTO, id.toString() );
        } catch (RestResponseUnsatisfiedException | RestRequestUnstable e) {
            Log.e(TAG,"Erro ao buscar o agendamento", e);
            throw new AgendamentoServiceException("Erro na comunicação com o servidor.");
        } catch (RestException e) {
            Log.e(TAG,"Erro ao buscar o agendamento", e);
            throw new AgendamentoServiceException(e.getErrorMessage().getErrorMessage());
        }
    }
}
