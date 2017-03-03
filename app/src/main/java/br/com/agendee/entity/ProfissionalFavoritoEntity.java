package br.com.agendee.entity;

import android.content.ContentValues;
import android.database.Cursor;

import br.com.agendee.dao.PersistenceBean;

/**
 * Created by wagner on 20/09/15.
 */
public class ProfissionalFavoritoEntity extends PersistenceBean {


    private static final long serialVersionUID = -6468517205842930918L;

    private Integer id;
    private Integer idProfissional;
    private Integer idClinica;
    private String nome;
    private String espec;

    public ProfissionalFavoritoEntity() {
        super( "profissional_favorito", new String[] { "id","id_profissional","id_clinica","nome","espec"} );
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
        val.put("id_profissional", this.getIdProfissional());
        val.put("id_clinica", this.getIdClinica());
        val.put("nome", this.getNome());
        val.put("espec", this.getEspec());

        return val;
    }

    public void setBean(Cursor cr) {
        this.setId(cr.getInt(0));
        this.setIdProfissional(cr.getInt(1));
        this.setIdClinica(cr.getInt(2));
        this.setNome(cr.getString(3));
        this.setEspec(cr.getString(4));

    }

    public Integer getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(Integer idProfissional) {
        this.idProfissional = idProfissional;
    }

    public Integer getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Integer idClinica) {
        this.idClinica = idClinica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspec() {
        return espec;
    }

    public void setEspec(String espec) {
        this.espec = espec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfissionalFavoritoEntity that = (ProfissionalFavoritoEntity) o;

        if (!id.equals(that.id)) return false;
        if (!idProfissional.equals(that.idProfissional)) return false;
        return idClinica.equals(that.idClinica);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + idProfissional.hashCode();
        result = 31 * result + idClinica.hashCode();
        return result;
    }
}
