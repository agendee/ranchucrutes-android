package br.com.agendee.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by wagner on 25/07/15.
 */
public class ProfissionalBasicoVo implements Parcelable,Serializable {
    private Long id;
    private String nome;
    private String numeroRegistro;
    private String espec;
    private Double latitude;
    private Double longitude;
    private String endereco;
    private String telefone;
    private Boolean temAgenda;
    private Integer idProfissao;
    private String nomeProfissao;
    private Integer idParceiro;
    private Long idClinicaAtual;
    private Boolean aceitaParticular;
    private Boolean aceitaPlano;
    private String foto;
    private boolean favorito = false;

    public ProfissionalBasicoVo() {

    }


    public ProfissionalBasicoVo(Parcel source) {
        this.id = source.readLong();
        this.nome = source.readString();
        this.numeroRegistro = source.readString();
        this.espec = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.endereco = source.readString();
        this.telefone = source.readString();
        this.temAgenda = new Boolean(source.readString());
        this.idProfissao = source.readInt();
        this.nomeProfissao = source.readString();
        this.idClinicaAtual = source.readLong();
        this.aceitaParticular = new Boolean(source.readString());
        this.aceitaPlano = new Boolean(source.readString());
        this.foto = source.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.nome);
        dest.writeString(this.numeroRegistro);
        dest.writeString(this.espec);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.endereco);
        dest.writeString(this.telefone);
        dest.writeString(this.temAgenda == null ? "false" : this.temAgenda.toString());
        dest.writeInt(this.idProfissao);
        dest.writeString(this.nomeProfissao);
        if (this.idClinicaAtual != null){
            dest.writeLong(this.idClinicaAtual);
        }
        dest.writeString(this.aceitaParticular != null ? this.aceitaParticular.toString() : "false");
        dest.writeString(this.aceitaPlano != null ? this.aceitaPlano.toString() : "false");
        dest.writeString(this.foto);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getTemAgenda() {
        return temAgenda == null ? false : temAgenda;
    }

    public void setTemAgenda(Boolean temAgenda) {
        this.temAgenda = temAgenda;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ProfissionalBasicoVo> CREATOR = new Parcelable.Creator<ProfissionalBasicoVo>(){
        @Override
        public ProfissionalBasicoVo createFromParcel(Parcel source) {
            return new ProfissionalBasicoVo(source);
        }
        @Override
        public ProfissionalBasicoVo[] newArray(int size) {
            return new ProfissionalBasicoVo[size];
        }
    };

    public Integer getIdProfissao() {
        return idProfissao;
    }

    public void setIdProfissao(Integer idProfissao) {
        this.idProfissao = idProfissao;
    }

    public String getNomeProfissao() {
        return nomeProfissao;
    }

    public void setNomeProfissao(String nomeProfissao) {
        this.nomeProfissao = nomeProfissao;
    }

    public Integer getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(Integer idParceiro) {
        this.idParceiro = idParceiro;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Long getIdClinicaAtual() {
        return idClinicaAtual;
    }

    public void setIdClinicaAtual(Long idClinicaAtual) {
        this.idClinicaAtual = idClinicaAtual;
    }

    public Boolean getAceitaParticular() {
        return aceitaParticular;
    }

    public void setAceitaParticular(Boolean aceitaParticular) {
        this.aceitaParticular = aceitaParticular;
    }

    public Boolean getAceitaPlano() {
        return aceitaPlano;
    }

    public void setAceitaPlano(Boolean aceitaPlano) {
        this.aceitaPlano = aceitaPlano;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
