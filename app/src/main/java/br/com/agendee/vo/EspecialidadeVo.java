package br.com.agendee.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import br.com.agendee.view.SearchingListModel;

public class EspecialidadeVo implements Serializable, SearchingListModel {


    private static final long serialVersionUID = -1602319229739751782L;
    private Integer id;
    private String nome;
    private Boolean popular;
    private ProfissaoVo profissao;

    public EspecialidadeVo(Parcel parcel) {
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

    public static final Parcelable.Creator<EspecialidadeVo> CREATOR = new Parcelable.Creator<EspecialidadeVo>(){
        @Override
        public EspecialidadeVo createFromParcel(Parcel source) {
            return new EspecialidadeVo(source);
        }
        @Override
        public EspecialidadeVo[] newArray(int size) {
            return new EspecialidadeVo[size];
        }
    };

    public ProfissaoVo getProfissao() {
        return profissao;
    }

    public void setProfissao(ProfissaoVo profissao) {
        this.profissao = profissao;
    }
}
