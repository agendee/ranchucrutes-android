package br.com.wjaa.ranchucrutes.service;

import android.util.Log;

import java.util.Date;
import java.util.List;

import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.exception.RestException;
import br.com.wjaa.ranchucrutes.exception.RestRequestUnstable;
import br.com.wjaa.ranchucrutes.exception.RestResponseUnsatisfiedException;
import br.com.wjaa.ranchucrutes.form.AgendamentoForm;
import br.com.wjaa.ranchucrutes.rest.RestUtils;
import br.com.wjaa.ranchucrutes.utils.ObjectUtils;
import br.com.wjaa.ranchucrutes.vo.AgendaVo;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ConfirmarAgendamentoVo;

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
    public ConfirmarAgendamentoVo criarAgendamento(Long idProfissional,Long idClinica, Long idPaciente,
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
    public AgendamentoVo confirmarAgendamento(Long idAgendamento, String codigo) throws AgendamentoServiceException {
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
}
