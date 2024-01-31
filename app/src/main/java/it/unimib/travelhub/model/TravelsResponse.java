package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Class to represent the API response of NewsAPI.org (https://newsapi.org)
 * associated with the endpoint "Top headlines" - /v2/top-headlines.
 */
public class TravelsResponse implements Parcelable {
    private String status;
    private int travelsCount;
    private List<Travels> travelsList;

    public TravelsResponse() {}

    public TravelsResponse(String status, int totalResults, List<Travels> travelsList) {
        this.status = status;
        this.travelsCount = totalResults;
        this.travelsList = travelsList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTravelsCount() {
        return travelsCount;
    }

    public void setTravelsCount(int travelsCount) {
        this.travelsCount = travelsCount;
    }

    public List<Travels> getTravelsList() {
        return travelsList;
    }

    public List<Travels> getRunningTravelsList() {
        List<Travels> runningTravelsList = new java.util.ArrayList<>();
        for (Travels travels : travelsList) {
            if (travels.getEndDate().after(new Date())) {
                runningTravelsList.add(travels);
            }
        }
        return runningTravelsList;
    }

    public List<Travels> getDoneTravelsList() {
        List<Travels> doneTravelsList = new java.util.ArrayList<>();
        for (Travels travels : travelsList) {
            if (travels.getEndDate().before(new Date())) {
                doneTravelsList.add(travels);
            }
        }
        return doneTravelsList;
    }

    public void setTravelsList(List<Travels> travelsList) {
        this.travelsList = travelsList;
    }

    @Override
    public String toString() {
        return "NewsApiResponse{" +
                "status='" + status + '\'' +
                ", totalResults=" + travelsCount +
                ", articles=" + travelsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeInt(this.travelsCount);
        dest.writeTypedList(this.travelsList);
    }

    public void readFromParcel(Parcel source) {
        this.status = source.readString();
        this.travelsCount = source.readInt();
        this.travelsList = source.createTypedArrayList(Travels.CREATOR);
    }

    protected TravelsResponse(Parcel in) {
        this.status = in.readString();
        this.travelsCount = in.readInt();
        this.travelsList = in.createTypedArrayList(Travels.CREATOR);
    }

    public static final Parcelable.Creator<TravelsResponse> CREATOR = new Parcelable.Creator<TravelsResponse>() {
        @Override
        public TravelsResponse createFromParcel(Parcel source) {
            return new TravelsResponse(source);
        }

        @Override
        public TravelsResponse[] newArray(int size) {
            return new TravelsResponse[size];
        }
    };
}
