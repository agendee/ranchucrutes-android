package br.com.agendee.entity;

import android.content.ContentValues;
import android.database.Cursor;

import br.com.agendee.dao.PersistenceBean;
import br.com.agendee.vo.ConvenioCategoriaVo;

/**
 * Created by wagner on 20/09/15.
 */
public class PacienteConvenioEntity extends PersistenceBean {


    private Integer id;
    private Integer idPaciente;
    private Integer idCategoria;
    private String nomeCategoria;
    private String nomeConvenio;



    public PacienteConvenioEntity() {
        super( "paciente_convenio", new String[] { "id","id_paciente","id_categoria","nome_categoria","nome_convenio"} );
    }

    public PacienteConvenioEntity(Integer idPaciente,ConvenioCategoriaVo convenioCategoriaVo){
        this();
        this.idPaciente = idPaciente;
        this.idCategoria = convenioCategoriaVo.getId();
        this.nomeCategoria = convenioCategoriaVo.getNome();
        if ( convenioCategoriaVo.getConvenioVo() != null ){
            this.nomeConvenio = convenioCategoriaVo.getConvenioVo().getNome();
        }
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
        val.put("id_paciente", this.getIdPaciente());
        val.put("id_categoria", this.getIdCategoria());
        val.put("nome_categoria", this.getNomeCategoria());
        val.put("nome_convenio", this.getNomeConvenio());
        return val;
    }

    public void setBean(Cursor cr) {
        this.setId(cr.getInt(0));
        this.setIdPaciente(cr.getInt(1));
        this.setIdCategoria(cr.getInt(2));
        this.setNomeCategoria(cr.getString(3));
        this.setNomeConvenio(cr.getString(4));
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }


    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }


    public String getNomeConvenio() {
        return nomeConvenio;
    }

    public void setNomeConvenio(String nomeConvenio) {
        this.nomeConvenio = nomeConvenio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PacienteConvenioEntity that = (PacienteConvenioEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (idPaciente != null ? !idPaciente.equals(that.idPaciente) : that.idPaciente != null)
            return false;
        return idCategoria != null ? idCategoria.equals(that.idCategoria) : that.idCategoria == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (idPaciente != null ? idPaciente.hashCode() : 0);
        result = 31 * result + (idCategoria != null ? idCategoria.hashCode() : 0);
        return result;
    }
}
