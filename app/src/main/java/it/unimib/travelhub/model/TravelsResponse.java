package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Class to represent the API response of NewsAPI.org (https://newsapi.org)
 * associated with the endpoint "Top headlines" - /v2/top-headlines.
 */
public class TravelsResponse implements Parcelable {
    private int travelsCount;
    private List<Travels> travelsList;

    public TravelsResponse() {}

    public TravelsResponse(int totalResults, List<Travels> travelsList) {
        this.travelsCount = totalResults;
        this.travelsList = travelsList;
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

    public Travels getOnGoingTravel() {
        for (Travels travels : travelsList) {
            if (travels.getStatus() == Travels.Status.ONGOING) {
                return travels;
            } else if (travels.getStatus() == Travels.Status.FUTURE) {
                return travels;
            }
        }
        return null;
    }

    public Travels getFutureTravel() {
        return getFutureTravelsList().isEmpty() ? null : getFutureTravelsList().get(0);
    }

    public Travels getDoneTravel() {
        return getDoneTravelsList().isEmpty() ? null : getDoneTravelsList().get(0);
    }

    public List<Travels> getFutureTravelsList() {
        List<Travels> futureTravelsList = new java.util.ArrayList<>();
        for (Travels travels : travelsList) {
            if (travels.getStatus() == Travels.Status.FUTURE) {
                futureTravelsList.add(travels);
            }
        }
        return futureTravelsList;
    }

    public List<Travels> getDoneTravelsList() {
        List<Travels> doneTravelsList = new java.util.ArrayList<>();
        for (Travels travels : travelsList) {
            if (travels.getStatus() == Travels.Status.DONE) {
                doneTravelsList.add(travels);
            }
        }
        Collections.reverse(doneTravelsList);
        return doneTravelsList;
    }

    public void setTravelsList(List<Travels> travelsList) {
        this.travelsList = travelsList;
    }

    @Override
    public String toString() {
        return "NewsApiResponse{" +
                "totalResults=" + travelsCount +
                ", articles=" + travelsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.travelsCount);
        dest.writeTypedList(this.travelsList);
    }

    public void readFromParcel(Parcel source) {
        this.travelsCount = source.readInt();
        this.travelsList = source.createTypedArrayList(Travels.CREATOR);
    }

    protected TravelsResponse(Parcel in) {
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
