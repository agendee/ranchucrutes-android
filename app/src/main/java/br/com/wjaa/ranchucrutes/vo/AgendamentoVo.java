package br.com.wjaa.ranchucrutes.vo;

import java.util.Date;

/**
 * Created by wagner on 20/10/15.
 */
public class AgendamentoVo {

    private Long id;
    private ProfissionalBasicoVo profissional;
    private PacienteVo paciente;
    private Date dataAgendamento;

    public ProfissionalBasicoVo getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalBasicoVo profissional) {
        this.profissional = profissional;
    }

    public PacienteVo getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteVo paciente) {
        this.paciente = paciente;
    }

    public Date getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(Date dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
