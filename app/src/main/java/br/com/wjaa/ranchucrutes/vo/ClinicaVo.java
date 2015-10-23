package br.com.wjaa.ranchucrutes.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wagner on 18/10/15.
 */
public class ClinicaVo implements Parcelable{
    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    private Double latitude;
    private Double longitude;


    public ClinicaVo(){

    }

    public ClinicaVo(Parcel source) {
        this.id = source.readLong();
        this.nome = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.endereco = source.readString();
        this.telefone = source.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id == null ? null : this.id);
        dest.writeString(this.nome);
        dest.writeDouble(this.latitude == null ? null : this.latitude);
        dest.writeDouble(this.longitude == null ? null : this.longitude);
        dest.writeString(this.endereco);
        dest.writeString(this.telefone);
    }

    public static final Parcelable.Creator<ClinicaVo> CREATOR = new Parcelable.Creator<ClinicaVo>(){
        @Override
        public ClinicaVo createFromParcel(Parcel source) {
            return new ClinicaVo(source);
        }
        @Override
        public ClinicaVo[] newArray(int size) {
            return new ClinicaVo[size];
        }
    };
}
