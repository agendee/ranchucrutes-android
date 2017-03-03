package br.com.agendee.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import br.com.agendee.view.SearchingListModel;

/**
 * Created by wagner on 12/12/16.
 */
public class ProfissaoVo implements Serializable, SearchingListModel {


    private Integer id;
    private String nome;
    private Boolean popular;

    public ProfissaoVo(Parcel parcel) {
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
    public String getName() {
        return this.getNome();
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

    public static final Parcelable.Creator<ProfissaoVo> CREATOR = new Parcelable.Creator<ProfissaoVo>(){
        @Override
        public ProfissaoVo createFromParcel(Parcel source) {
            return new ProfissaoVo(source);
        }
        @Override
        public ProfissaoVo[] newArray(int size) {
            return new ProfissaoVo[size];
        }
    };
}
