package br.com.wjaa.ranchucrutes.service;

import java.util.Date;

import br.com.wjaa.ranchucrutes.exception.AgendamentoServiceException;
import br.com.wjaa.ranchucrutes.vo.AgendaVo;
import br.com.wjaa.ranchucrutes.vo.AgendamentoVo;
import br.com.wjaa.ranchucrutes.vo.ConfirmarAgendamentoVo;

/**
 * Created by wagner on 20/10/15.
 */
public interface AgendamentoService {

    AgendaVo getAgendamentoByIdProfissional(Long idProfissional, Long idClinica) throws AgendamentoServiceException;

    ConfirmarAgendamentoVo criarAgendamento(Long idProfissional,Long idClinica, Long idPaciente, Date date, boolean particular) throws AgendamentoServiceException;

    AgendamentoVo confirmarAgendamento(Long idAgendamento, String codigo) throws AgendamentoServiceException;

    AgendamentoVo[] getAgendamentos(Long idPaciente) throws AgendamentoServiceException;
}
