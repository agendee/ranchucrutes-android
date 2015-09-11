package br.com.wjaa.ranchucrutes.vo;

import br.com.wjaa.ranchucrutes.form.LoginForm;

/**
 * Created by wagner on 10/09/15.
 */
public class PacienteVo {

    private String id;
    private String email;
    private String nome;
    private String telefone;
    private String senha;
    private String keySocial;
    private LoginForm.AuthType authType;

    public PacienteVo(String nome, String email, String telefone, String keySocial, LoginForm.AuthType authType) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.keySocial = keySocial;
        this.authType = authType;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getKeySocial() {
        return keySocial;
    }

    public void setKeySocial(String keySocial) {
        this.keySocial = keySocial;
    }

    public LoginForm.AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(LoginForm.AuthType authType) {
        this.authType = authType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
