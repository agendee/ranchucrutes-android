package br.com.wjaa.ranchucrutes.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner on 4/12/16.
 */
public class ResultadoBuscaClinicaVo {


    private List<ClinicaVo> clinicas;
    private Double latitude;
    private Double longitude;
    private ClinicaVo clinicaMaisProxima;
    private Double distanceInKm;



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


    public List<ClinicaVo> getClinicas() {
        return clinicas;
    }

    public void setClinicas(List<ClinicaVo> clinicas) {
        this.clinicas = clinicas;
    }

    public void addClinica(ClinicaVo clinicaVo) {
        if (clinicas == null){
            clinicas = new ArrayList<>();
        }
        clinicas.add(clinicaVo);
    }

    public ClinicaVo getClinicaMaisProxima() {
        return clinicaMaisProxima;
    }

    public void setClinicaMaisProxima(ClinicaVo clinicaMaisProxima) {
        this.clinicaMaisProxima = clinicaMaisProxima;
    }

    public Double getDistanceInKm() {
        return distanceInKm == null ? 0.0 : distanceInKm ;
    }

    public void setDistanceInKm(Double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
}
