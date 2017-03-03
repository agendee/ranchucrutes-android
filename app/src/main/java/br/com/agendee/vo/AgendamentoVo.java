package br.com.agendee.vo;

import android.os.Parcel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wagner on 20/10/15.
 */
public class AgendamentoVo implements Serializable {

    private Long id;
    private ProfissionalBasicoVo profissional;
    private PacienteVo paciente;
    private Date dataAgendamento;
    private Date dataCriacao;
    private Date dataConfirmacao;
    private Date dataConfirmacaoProfissional;
    private String codigoConfirmacao;
    private Boolean cancelado;
    private Boolean finalizado;

    public AgendamentoVo(){}

    public AgendamentoVo(Parcel source) {

    }

   /* @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.profissional, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(this.paciente, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeSerializable(dataAgendamento);
        dest.writeSerializable(dataCriacao);
        dest.writeSerializable(dataConfirmacao);
        dest.writeSerializable(dataConfirmacaoProfissional);
        dest.writeString(codigoConfirmacao);
        dest.writeSerializable(cancelado);
        dest.writeSerializable(finalizado);
    }
*/


    public Date getDataConfirmacaoConsulta() {
        return dataConfirmacaoConsulta;
    }

    public void setDataConfirmacaoConsulta(Date dataConfirmacaoConsulta) {
        this.dataConfirmacaoConsulta = dataConfirmacaoConsulta;
    }

    public Boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }

    public void setCodigoConfirmacao(String codigoConfirmacao) {
        this.codigoConfirmacao = codigoConfirmacao;
    }

    public Date getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(Date dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    private Date dataConfirmacaoConsulta;

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

    public Date getDataConfirmacaoProfissional() {
        return dataConfirmacaoProfissional;
    }

    public void setDataConfirmacaoProfissional(Date dataConfirmacaoProfissional) {
        this.dataConfirmacaoProfissional = dataConfirmacaoProfissional;
    }

    public Boolean getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Boolean finalizado) {
        this.finalizado = finalizado;
    }

   /* @Override
    public int describeContents() {
        return 0;
    }



    public static final Parcelable.Creator<AgendamentoVo> CREATOR = new Parcelable.Creator<AgendamentoVo>(){
        @Override
        public AgendamentoVo createFromParcel(Parcel source) {
            return new AgendamentoVo(source);
        }
        @Override
        public AgendamentoVo[] newArray(int size) {
            return new AgendamentoVo[size];
        }
    };*/
}
