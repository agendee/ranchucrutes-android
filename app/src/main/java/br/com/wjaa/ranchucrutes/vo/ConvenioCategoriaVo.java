package br.com.wjaa.ranchucrutes.vo;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 30/09/15.
 */
public class ConvenioCategoriaVo implements SearchingListModel {

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
}
