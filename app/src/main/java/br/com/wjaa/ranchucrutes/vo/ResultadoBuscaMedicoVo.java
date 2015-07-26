package br.com.wjaa.ranchucrutes.vo;

import java.util.List;

/**
 * Created by wagner on 25/07/15.
 */
public class ResultadoBuscaMedicoVo {

    private List<MedicoBasicoVo> medicos;
    private Double latitude;
    private Double longitude;

    public List<MedicoBasicoVo> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<MedicoBasicoVo> medicos) {
        this.medicos = medicos;
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
