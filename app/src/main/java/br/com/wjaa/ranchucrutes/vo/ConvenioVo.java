package br.com.wjaa.ranchucrutes.vo;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 30/09/15.
 */
public class ConvenioVo implements SearchingListModel {


    private Integer id;
    private String nome;
    private Boolean popular;

    public ConvenioVo() {}

    public ConvenioVo(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ConvenioVo(Parcel parcel) {
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

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }


    @Override
    public String toString() {
        return "ConvenioVo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", popular=" + popular +
                '}';
    }

    @Override
    public String getName() {
        return this.nome;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getNome());
    }

    public static final Parcelable.Creator<ConvenioVo> CREATOR = new Parcelable.Creator<ConvenioVo>(){
        @Override
        public ConvenioVo createFromParcel(Parcel source) {
            return new ConvenioVo(source);
        }
        @Override
        public ConvenioVo[] newArray(int size) {
            return new ConvenioVo[size];
        }
    };
}
