package br.com.wjaa.ranchucrutes.commons;

/**
 * Created by wagner on 21/09/15.
 */
public enum AuthType{
    AUTH_FACEBOOK,
    AUTH_GPLUS,
    AUTH_RANCHUCRUTES;

    public static AuthType getByOrdinal(int o) {
        for (AuthType a : AuthType.values()){
            if (a.ordinal() == o){
                return a;
            }
        }
        return null;
    }
}

