package br.com.wjaa.ranchucrutes.vo;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.wjaa.ranchucrutes.view.SearchingListModel;

/**
 * Created by wagner on 15/03/16.
 */
public class PlacesVo implements SearchingListModel {
    private String name;
    private String placeId;


    public PlacesVo(String placeId,String name) {
        this.name = name;
        this.placeId = placeId;
    }

    public PlacesVo(Parcel parcel) {
        setPlaceId(parcel.readString());
        setName(parcel.readString());
    }

    public PlacesVo() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPlaceId());
        dest.writeString(getName());
    }

    public static final Parcelable.Creator<PlacesVo> CREATOR = new Parcelable.Creator<PlacesVo>(){
        @Override
        public PlacesVo createFromParcel(Parcel source) {
            return new PlacesVo(source);
        }
        @Override
        public PlacesVo[] newArray(int size) {
            return new PlacesVo[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
