package br.com.agendee.service;

import java.util.Date;

import br.com.agendee.exception.AgendamentoServiceException;
import br.com.agendee.vo.AgendaVo;
import br.com.agendee.vo.AgendamentoVo;
import br.com.agendee.vo.ConfirmarAgendamentoVo;

/**
 * Created by wagner on 20/10/15.
 */
public interface AgendamentoService {

    AgendaVo getAgendamentoByIdProfissional(Long idProfissional, Long idClinica) throws AgendamentoServiceException;

    ConfirmarAgendamentoVo criarAgendamento(Long idProfissional, Long idClinica, Long idPaciente, Date date, boolean particular) throws AgendamentoServiceException;

    AgendamentoVo confirmarSolicitacao(Long idAgendamento, String codigo) throws AgendamentoServiceException;

    AgendamentoVo[] getAgendamentos(Long idPaciente) throws AgendamentoServiceException;

    AgendamentoVo cancelarAgendamento(Long id) throws AgendamentoServiceException;

    AgendamentoVo confirmarAgendamento(Long id) throws AgendamentoServiceException;

    AgendamentoVo getAgendamentoById(Long id) throws AgendamentoServiceException;
}
