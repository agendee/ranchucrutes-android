package br.com.wjaa.ranchucrutes.vo;

import java.io.Serializable;

import br.com.wjaa.ranchucrutes.commons.AuthType;
import br.com.wjaa.ranchucrutes.form.LoginForm;

/**
 * Created by wagner on 10/09/15.
 */
public class PacienteVo implements Serializable {

    private static final long serialVersionUID = -8738431158110410130L;
    private Long id;
    private String email;
    private String nome;
    private String telefone;
    private String senha;
    private String keySocial;
    private AuthType authType;
    private Integer idCategoria;
    private String keyDeviceGcm;
    private String urlFoto;

    public PacienteVo(){}

    public PacienteVo(String nome, String email, String telefone, String keySocial, AuthType authType) {
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

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getKeyDeviceGcm() {
        return keyDeviceGcm;
    }

    public void setKeyDeviceGcm(String keyDeviceGcm) {
        this.keyDeviceGcm = keyDeviceGcm;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}
