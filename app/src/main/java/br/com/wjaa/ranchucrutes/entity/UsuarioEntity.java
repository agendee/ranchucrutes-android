package br.com.wjaa.ranchucrutes.entity;

import android.content.ContentValues;
import android.database.Cursor;

import br.com.wjaa.ranchucrutes.dao.PersistenceBean;

/**
 * Created by wagner on 20/09/15.
 */
public class UsuarioEntity extends PersistenceBean {

    /**
     *
     */
    private static final long serialVersionUID = 7443650836071061174L;


    private Integer id;
    private String nome;
    private String email;




    public UsuarioEntity() {
        super( "usuario", new String[] { "id", "nome","email"} );

    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public ContentValues getContentValues() {
        ContentValues val = new ContentValues();
        val.put("id", this.getId());
        val.put("nome", this.getNome());
        val.put("email", this.getEmail());
        return val;
    }

    public void setBean(Cursor cr) {
        this.setId(cr.getInt(0));
        this.setNome(cr.getString(1));
        this.setEmail(cr.getString(2));
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioEntity that = (UsuarioEntity) o;

        if (!id.equals(that.id)) return false;
        if (!nome.equals(that.nome)) return false;
        return email.equals(that.email);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
