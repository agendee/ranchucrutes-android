package br.com.wjaa.ranchucrutes.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.wjaa.ranchucrutes.dao.DatabaseManager;
import br.com.wjaa.ranchucrutes.dao.PersistenceBean;
import br.com.wjaa.ranchucrutes.utils.CollectionUtils;
import br.com.wjaa.ranchucrutes.utils.NumberUtils;


public class DataServiceImpl implements DataService {

    private DatabaseManager db;

    public DataServiceImpl( Context context ) {
        db = new DatabaseManager( context );
        criaBase();
    }

    private void criaBase() {
        try {
            if( db.checkDataBase()) {
                return;
            }
        } catch( Throwable e ) {
            Log.e( TAG, e.getMessage(), e);
        }

        recriarBase();
    }


    public void recriarBase() {
        try {
            db.createDataBase();
        } catch (IOException e) {
            Log.e( TAG, e.getMessage(), e);
        }
    }

    public <T extends PersistenceBean> Integer getSequence( Class<T> clazz ) {
        T bean;
        Cursor cr = null;
        try {
            bean = clazz.newInstance();

            db.openDataBase();
            cr = db.getDatabase().query( bean.getTabela(), bean.getColunas(), null, null, null, null, "id DESC", "1" );
            if( cr.getCount() > 0 ) {
                cr.moveToFirst();
                bean.setBean( cr );
                return bean.getId() + 1;
            } else {
                return 1;
            }
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            this.closeCursor(cr);
            db.close();
        }
        return -1;
    }

    public <T extends PersistenceBean> T getById( Class<T> clazz, Integer id ) {
        Cursor cr = null;
        try {
            T bean = clazz.newInstance();

            db.openDataBase();
            cr = db.getDatabase().query( bean.getTabela(), bean.getColunas(), "id=?", new String[] { id.toString() }, null, null, null );
            if( cr.getCount() > 0 ) {
                cr.moveToFirst();
                bean.setBean( cr );
                return bean;
            }
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            this.closeCursor(cr);
            db.close();
        }
        return null;
    }

    private void closeCursor(Cursor cr) {
        if (cr != null && !cr.isClosed()){
            cr.close();
        }
    }

    public <T extends PersistenceBean> List<T> getList( Class<T> clazz, String orderBy ) {
        return getList( clazz, null, null, orderBy );
    }

    public <T extends PersistenceBean> List<T> getList( Class<T> clazz, String selection, String[] args, String orderBy ) {
        T bean;
        List<T> lista = new ArrayList<T>();
        Cursor cr = null;
        try {
            bean = clazz.newInstance();

            db.openDataBase();
            cr = db.getDatabase().query( bean.getTabela(), bean.getColunas(), selection, args, null, null, orderBy );
            while( cr.moveToNext() ) {
                bean = clazz.newInstance();
                bean.setBean( cr );
                lista.add( bean );
            }
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            this.closeCursor(cr);
            db.close();
        }
        return lista;
    }

    public <T extends PersistenceBean> List<T> getList( Class<T> clazz, String selection, String[] args ) {
        T bean;
        List<T> lista = new ArrayList<T>();
        Cursor cr = null;
        try {
            bean = clazz.newInstance();

            db.openDataBase();
            cr = db.getDatabase().query( bean.getTabela(), bean.getColunas(), selection, args, null, null, null );
            while( cr.moveToNext() ) {
                bean = clazz.newInstance();
                bean.setBean( cr );
                lista.add( bean );
            }
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            this.closeCursor(cr);
            db.close();
        }
        return lista;
    }

    public <T extends PersistenceBean> List<T> getList( Class<T> clazz ) {
        List<T> lista = new ArrayList<T>();
        Cursor cr = null;
        try {
            db.openDataBase();

            T bean = clazz.newInstance();
            cr = db.getDatabase().query( bean.getTabela(), bean.getColunas(), null, null, null, null, null );
            while( cr.moveToNext() ) {
                bean = clazz.newInstance();
                bean.setBean( cr );
                lista.add( bean );
            }
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            this.closeCursor(cr);
            db.close();
        }
        return lista;
    }

    public <T extends PersistenceBean> void insert( List<T> beans ) {
        try {
            db.openDataBase();
            for( PersistenceBean bean : beans ) {
                db.getDatabase().insert( bean.getTabela(), null, bean.getContentValues() );
            }
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    public <T extends PersistenceBean> void insert( T bean ) {
        insert( getList( bean ) );
    }

    public <T extends PersistenceBean> void insert( T ... beans ) {
        insert( Arrays.asList( beans ) );
    }

    public <T extends PersistenceBean> void delete( Class<T> clazz ) {
        try {
            T bean = clazz.newInstance();
            db.openDataBase();
            db.getDatabase().delete( bean.getTabela(), null, null );
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    public <T extends PersistenceBean> void deleteById( T bean ) {
        deleteById( getList( bean ) );
    }

    public <T extends PersistenceBean> void deleteById( T ... beans ) {
        deleteById( Arrays.asList( beans ) );
    }

    public <T extends PersistenceBean> void deleteById( List<T> beans ) {
        try {
            db.openDataBase();
            for( PersistenceBean bean : beans ) {
                db.getDatabase().delete( bean.getTabela(), "id=?", new String[] { bean.getId().toString() } );
            }
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    public <T extends PersistenceBean> void updateById( T bean ) {
        updateById( getList( bean ) );
    }

    public <T extends PersistenceBean> void updateById( T ... beans ) {
        updateById( Arrays.asList( beans ) );
    }

    public <T extends PersistenceBean> void updateById( List<T> beans ) {
        try {
            db.openDataBase();
            for( PersistenceBean bean : beans ) {
                db.getDatabase().update( bean.getTabela(), bean.getContentValues(), "id=?", new String[] { bean.getId().toString() } );
            }
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    private <T extends PersistenceBean> List<T> getList( T bean ) {
        List<T> lista = new ArrayList<T>();
        lista.add( bean );
        return lista;
    }

    @Override
    public <T extends PersistenceBean> void update(T bean, String selection, String[] args) {
        try {
            db.openDataBase();
            db.getDatabase().update( bean.getTabela(), bean.getContentValues(), selection, args );
        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PersistenceBean> T findUniqueResult(Class<T> clazz,
                                                          String selection, String[] args) {
        List<T> result = this.getList(clazz, selection, args);

        if (CollectionUtils.isNotEmpty(result)){
            return result.get(0);
        }else{
            Log.d(TAG, "Objeto nao encontrado para condicao [" + selection + "]");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PersistenceBean> T insertOrUpdate(T bean) {
        T result = null;
        try {
            Integer id = null;
            if (NumberUtils.isPositive(bean.getId())){
                db.openDataBase();
                db.getDatabase().update( bean.getTabela(), bean.getContentValues(), "id=?", new String[] { bean.getId().toString() } );
                id = bean.getId();
            }else{
                id = this.getSequence(bean.getClass());
                //abrindo uma nova conexao apos pegar a sequence
                db.openDataBase();
                bean.setId(id);
                db.getDatabase().insert( bean.getTabela(), null, bean.getContentValues() );
            }
            //recarregando o objeto novamente.
            result = (T)getById(bean.getClass(), id);

        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> executeSQL(String sql, String[] args) {
        List<Object[]> result = Collections.EMPTY_LIST;
        try {
            db.openDataBase();
            SQLiteCursor c = (SQLiteCursor) db.getDatabase().rawQuery(sql, args);
            result = new ArrayList<Object[]>(c.getCount());

            //percorrendo as linhas
            while( c.moveToNext() ) {

                Object dados [] = new Object[c.getColumnCount()];
                //percorrendo as colunas
                for (int i = 0; i < c.getColumnCount(); i ++){
                    Object value = this.getValue(c, i);
                    dados[i] = value;
                }
                //adicionando uma nova linha.
                result.add(dados);

            }
            this.closeCursor(c);

        } catch ( Throwable e ) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            db.close();
        }

        return result;
    }

    private Object getValue(SQLiteCursor c, int index) {
        switch (c.getType(index)) {
            case SQLiteCursor.FIELD_TYPE_BLOB:
                return c.getBlob(index);

            case SQLiteCursor.FIELD_TYPE_FLOAT:
                return c.getFloat(index);

            case SQLiteCursor.FIELD_TYPE_INTEGER:
                return c.getInt(index);

            case SQLiteCursor.FIELD_TYPE_STRING:
                return c.getString(index);

        }

        return null;
    }
}

