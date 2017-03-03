package br.com.agendee.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

public abstract class PersistenceBean implements Serializable {

    private String tabela = null;
    private String colunas[] = null;

    public PersistenceBean( String tabela, String[] colunas ) {
        this.tabela = tabela;
        this.colunas = colunas;
    }

    public final String getTabela() {
        return tabela;
    }

    public final String[] getColunas() {
        return colunas;
    }

    public abstract ContentValues getContentValues();

    public abstract void setBean( Cursor cr );

    public abstract void setId( Integer id );

    public abstract Integer getId();

    public abstract boolean equals(Object o);

}