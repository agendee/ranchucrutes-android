package br.com.wjaa.ranchucrutes.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import br.com.wjaa.ranchucrutes.entity.PacienteConvenioEntity;
import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 30/09/15.
 */
public class ConvenioCategoriaVo implements SearchingListModel, Serializable {

    private static final long serialVersionUID = -5079975446508058767L;

    private Integer id;
    private String nome;
    private Integer idConvenio;
    private ConvenioVo convenioVo;


    public ConvenioCategoriaVo() {}

    public ConvenioCategoriaVo(int id, String nome) {
        this.id= id;
        this.nome = nome;
    }

    public ConvenioCategoriaVo(Parcel parcel) {
        setId(parcel.readInt());
        setNome(parcel.readString());
        setIdConvenio(parcel.readInt());
        setConvenioVo((ConvenioVo) parcel.readParcelable(ConvenioVo.class.getClassLoader()));
    }

    public ConvenioCategoriaVo(PacienteConvenioEntity pc) {
        this.id = pc.getIdCategoria();
        this.nome = pc.getNomeCategoria();
        this.convenioVo = new ConvenioVo(null,pc.getNomeConvenio());
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Integer idConvenio) {
        this.idConvenio = idConvenio;
    }

    @Override
    public String getName() {
        return this.nome;
    }

    public ConvenioVo getConvenioVo() {
        return convenioVo;
    }

    public void setConvenioVo(ConvenioVo convenioVo) {
        this.convenioVo = convenioVo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getNome());
        if (getIdConvenio() != null){
            dest.writeInt(getIdConvenio());
        }
        dest.writeParcelable(convenioVo,PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Parcelable.Creator<ConvenioCategoriaVo> CREATOR = new Parcelable.Creator<ConvenioCategoriaVo>(){
        @Override
        public ConvenioCategoriaVo createFromParcel(Parcel source) {
            return new ConvenioCategoriaVo(source);
        }
        @Override
        public ConvenioCategoriaVo[] newArray(int size) {
            return new ConvenioCategoriaVo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConvenioCategoriaVo that = (ConvenioCategoriaVo) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
