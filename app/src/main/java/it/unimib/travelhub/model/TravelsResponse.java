package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public class TravelsResponse implements Parcelable {
    private List<Travels> travelsList;

    public TravelsResponse() {}

    public TravelsResponse(List<Travels> travelsList) {
        this.travelsList = travelsList;
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

    @NonNull
    @Override
    public String toString() {
        return "TravelsResponse{" +
                "Travels List=" + travelsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.travelsList);
    }

    public void readFromParcel(Parcel source) {
        this.travelsList = source.createTypedArrayList(Travels.CREATOR);
    }

    protected TravelsResponse(Parcel in) {
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
