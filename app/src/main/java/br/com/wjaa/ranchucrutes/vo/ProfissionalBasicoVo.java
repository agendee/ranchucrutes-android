package br.com.wjaa.ranchucrutes.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wagner on 25/07/15.
 */
public class ProfissionalBasicoVo implements Parcelable {
    private Long id;
    private String nome;
    private Integer crm;
    private String espec;
    private Double latitude;
    private Double longitude;
    private String endereco;
    private String telefone;
    private ClinicaVo[] clinicas;

    public ProfissionalBasicoVo() {

    }


    public ProfissionalBasicoVo(Parcel source) {
        this.id = source.readLong();
        this.nome = source.readString();
        this.crm = source.readInt();
        this.espec = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.endereco = source.readString();
        this.telefone = source.readString();

        Parcelable[] parcelableArray =
                source.readParcelableArray(ClinicaVo.class.getClassLoader());

        if (parcelableArray != null) {
            clinicas = Arrays.copyOf(parcelableArray, parcelableArray.length, ClinicaVo[].class);
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.nome);
        if (this.crm != null){
            dest.writeInt(this.crm);
        }
        dest.writeString(this.espec);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.endereco);
        dest.writeString(this.telefone);
        dest.writeParcelableArray(clinicas, PARCELABLE_WRITE_RETURN_VALUE);
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

    public Integer getCrm() {
        return crm;
    }

    public void setCrm(Integer crm) {
        this.crm = crm;
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

    public ClinicaVo[] getClinicas() {
        return clinicas;
    }

    public void setClinicas(ClinicaVo[] clinicas) {
        this.clinicas = clinicas;
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
}
