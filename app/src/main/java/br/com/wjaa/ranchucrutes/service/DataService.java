package br.com.wjaa.ranchucrutes.service;

import java.util.List;

import br.com.wjaa.ranchucrutes.dao.PersistenceBean;

/**
 *
 */
public interface DataService {

    String TAG = "DATABASE_SERVICE";

    void recriarBase() ;

    <T extends PersistenceBean> Integer getSequence( Class<T> clazz );

    <T extends PersistenceBean> T getById( Class<T> clazz, Integer id ) ;

    <T extends PersistenceBean> List<T> getList( Class<T> clazz ) ;

    <T extends PersistenceBean> List<T> getList( Class<T> clazz, String selection, String[] args );

    /**
     * Procura um Objeto e retorna apenas um resultado.
     * @param clazz Class
     * @param selection Where condition
     * @param args param values
     * @return Objecto encontrado.
     */
    <T extends PersistenceBean> T findUniqueResult( Class<T> clazz, String selection, String[] args );

    /**
     * @param clazz
     * @param orderBy - String "nome da coluna" "tipo (ASC ou DESC)"
     * @return
     */
    <T extends PersistenceBean> List<T> getList( Class<T> clazz, String orderBy );

    /**
     *
     * @param clazz
     * @param selection - String "nome da coluna" "condicao('<' '>' '=')" "interrogacao"
     * @param args - array de String valores que substituiram as interroga��es do selecion
     * @param orderBy - String "nome da coluna" "tipo (ASC ou DESC)"
     * @return
     */
    <T extends PersistenceBean> List<T> getList( Class<T> clazz, String selection, String[] args, String orderBy );

    <T extends PersistenceBean> void insert( T bean );

    <T extends PersistenceBean> void insert( List<T> beans ) ;

    <T extends PersistenceBean> void delete( Class<T> clazz );

    <T extends PersistenceBean> void deleteById( T bean ) ;

    <T extends PersistenceBean> void deleteById( T ... beans ) ;

    <T extends PersistenceBean> void deleteById( List<T> beans );

    <T extends PersistenceBean> void updateById( T bean );

    <T extends PersistenceBean> void updateById( T ... beans );

    <T extends PersistenceBean> void updateById( List<T> beans );

    <T extends PersistenceBean> void update( T bean, String selection, String[] args );

    /**
     * Caso o objeto nao tenha um id ele sera inserido na base, cao contrario sera atualizado pelo seu id.
     * @param bean Objeto a ser persistido.
     * @return Objeto persistido.
     */
    <T extends PersistenceBean> T insertOrUpdate( T bean );

    /**
     * Executa um comando sql
     * @param sql Query
     * @param args parametros
     * @return Linha contendo todas as colunas da consulta
     */
    List<Object[]> executeSQL(String sql, String [] args);
}
