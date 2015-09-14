package br.com.wjaa.ranchucrutes.form;

/**
 * Created by wagner on 10/09/15.
 */
public class LoginForm {

    public enum AuthType{
        AUTH_FACEBOOK,
        AUTH_GPLUS,
        AUTH_RANCHUCRUTES
    }


    private String login;
    private String senha;
    private String keySocial;
    private AuthType type;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public AuthType getType() {
        return type;
    }

    public void setType(AuthType type) {
        this.type = type;
    }
}
