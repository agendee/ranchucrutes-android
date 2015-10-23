package br.com.wjaa.ranchucrutes.vo;

/**
 * Created by wagner on 20/10/15.
 */
public class ConfirmarAgendamentoVo {

    private String codigoConfirmacao;
    private AgendamentoVo agendamentoVo;

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }

    public void setCodigoConfirmacao(String codigoConfirmacao) {
        this.codigoConfirmacao = codigoConfirmacao;
    }

    public AgendamentoVo getAgendamentoVo() {
        return agendamentoVo;
    }

    public void setAgendamentoVo(AgendamentoVo agendamentoVo) {
        this.agendamentoVo = agendamentoVo;
    }
}
