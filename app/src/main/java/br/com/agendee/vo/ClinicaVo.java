package br.com.agendee.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.agendee.utils.CollectionUtils;

/**
 * Created by wagner on 18/10/15.
 */
public class ClinicaVo implements Parcelable, Serializable{
    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    private Double latitude;
    private Double longitude;
    private List<ProfissionalBasicoVo> profissionais;
    private MapTipoLocalidade mapTipoLocalidade = MapTipoLocalidade.PARTICULAR;


    public ClinicaVo(){

    }

    public ClinicaVo(Parcel source) {
        this.id = source.readLong();
        this.nome = source.readString();
        this.endereco = source.readString();
        this.telefone = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.profissionais = new ArrayList<>();
        try{
            source.readList(this.profissionais,ProfissionalBasicoVo.class.getClassLoader());
        }catch(Exception ex){
            ex.printStackTrace();
        }
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
        dest.writeString(this.endereco);
        dest.writeString(this.telefone);
        dest.writeDouble(this.latitude == null ? null : this.latitude);
        dest.writeDouble(this.longitude == null ? null : this.longitude);
        dest.writeList(this.profissionais);
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

    public MapTipoLocalidade getMapTipoLocalidade() {
        return mapTipoLocalidade;
    }

    public void setMapTipoLocalidade(MapTipoLocalidade mapTipoLocalidade) {
        this.mapTipoLocalidade = mapTipoLocalidade;
    }

    public List<ProfissionalBasicoVo> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<ProfissionalBasicoVo> profissionais) {
        this.profissionais = profissionais;
    }

    public boolean getTemAgenda() {
        if (CollectionUtils.isEmpty(profissionais)) {
            return false;
        }

        for (ProfissionalBasicoVo p : profissionais){
            if (p.getTemAgenda()){
                return true;
            }
        }

        return false;
    }
}
