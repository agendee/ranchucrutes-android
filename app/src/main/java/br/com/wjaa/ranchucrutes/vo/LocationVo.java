package br.com.wjaa.ranchucrutes.vo;

/**
 * Created by wagner on 26/07/15.
 */
public class LocationVo {

    private Double latitude;
    private Double longitude;

    public LocationVo(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
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
