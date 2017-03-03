package br.com.agendee.vo;

import java.util.List;

/**
 * Created by wagner on 25/07/15.
 */
public class ResultadoBuscaProfissionalVo {

    private List<ProfissionalBasicoVo> profissionais;
    private Double latitude;
    private Double longitude;

    public List<ProfissionalBasicoVo> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<ProfissionalBasicoVo> profissionais) {
        this.profissionais = profissionais;
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

}
