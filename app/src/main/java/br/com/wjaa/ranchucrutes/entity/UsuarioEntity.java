package br.com.wjaa.ranchucrutes.entity;

import android.content.ContentValues;
import android.database.Cursor;

import br.com.wjaa.ranchucrutes.commons.AuthType;
import br.com.wjaa.ranchucrutes.dao.PersistenceBean;
import br.com.wjaa.ranchucrutes.vo.ConvenioCategoriaVo;

/**
 * Created by wagner on 20/09/15.
 */
public class UsuarioEntity extends PersistenceBean {

    private static final long serialVersionUID = 3338854478316950156L;
    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private AuthType authType;
    private Integer idCategoria;
    private String deviceKey;
    private ConvenioCategoriaVo categoriaVo;


    public UsuarioEntity() {
        super( "usuario", new String[] { "id","nome","email","telefone","auth_type","id_categoria","device_key"} );
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
        val.put("telefone", this.getTelefone());
        val.put("auth_type", this.getAuthType() != null ? this.getAuthType().ordinal() : null);
        val.put("id_categoria", this.getIdCategoria());
        val.put("device_key", this.getDeviceKey());
        return val;
    }

    public void setBean(Cursor cr) {
        this.setId(cr.getInt(0));
        this.setNome(cr.getString(1));
        this.setEmail(cr.getString(2));
        this.setTelefone(cr.getString(3));
        this.setAuthType(AuthType.getByOrdinal(cr.getInt(4)));
        this.setIdCategoria(cr.getInt(5));
        this.setDeviceKey(cr.getString(6));
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
    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }


    public ConvenioCategoriaVo getCategoriaVo() {
        return categoriaVo;
    }

    public void setCategoriaVo(ConvenioCategoriaVo categoriaVo) {
        this.categoriaVo = categoriaVo;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
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
