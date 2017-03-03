package br.com.agendee.utils;

import java.util.Collection;

/**
 *
 * Utilitario para Collection
 *
 * @author wagner
 *
 */
public class CollectionUtils {


    /**
     * Verifica se uma colecao NAO esta vazia.
     * @param c Collection
     * @return true caso a colecao tenha ao menus um objeto e false caso contrario.
     */
    public static boolean isNotEmpty(Collection c){
        return c != null && !c.isEmpty();
    }


    /**
     * Verifica se uma colecao ESTA vazia.
     * @param c Collection
     * @return true caso esteja vazia e false caso contrario.
     */
    public static boolean isEmpty(Collection c){
        return !isNotEmpty(c);
    }
}