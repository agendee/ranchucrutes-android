package br.com.wjaa.ranchucrutes.vo;

/**
 * Created by wagner on 18/08/15.
 */

public class ResultadoLoginVo {

    public enum StatusLogin{
        SUCESSO("Logado"),
        ERRO("Usúario ou senha inválido!"),
        ERRO_SOCIAL("Usuário de rede social não cadastrado!"),
        ACESSO_NAO_CONFIRMADO("Seu acesso não foi confirmado!");

        private String msg;
        private StatusLogin(String msg){
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }


    private MedicoBasicoVo medico;
    private PacienteVo paciente;
    private StatusLogin status;

    public MedicoBasicoVo getMedico() {
        return medico;
    }

    public void setMedico(MedicoBasicoVo medico) {
        this.medico = medico;
    }

    public StatusLogin getStatus() {
        return status;
    }

    public void setStatus(StatusLogin status) {
        this.status = status;
    }

    public Boolean isSucesso(){
        return StatusLogin.SUCESSO.equals(this.status);
    }


    public Boolean isErro(){
        return StatusLogin.ERRO.equals(this.status);
    }


    public Boolean isAcessoNaoConfirmado(){
        return StatusLogin.ACESSO_NAO_CONFIRMADO.equals(this.status);
    }

    public PacienteVo getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteVo paciente) {
        this.paciente = paciente;
    }
}
