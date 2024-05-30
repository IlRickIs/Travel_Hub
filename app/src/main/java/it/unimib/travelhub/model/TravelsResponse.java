package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import it.unimib.travelhub.data.source.TravelsCallback;

public class TravelsResponse implements Parcelable {
    private static final String TAG = "TravelsResponse";
    private List<Travels> travelsList;
    private TravelsCallback travelsCallback;
    private Integer length;

    public TravelsResponse(List<Travels> travelsList) {
        this.travelsList = travelsList;
        this.travelsCallback = null;
        this.length = travelsList.size();
    }

    public TravelsResponse(Integer length, TravelsCallback travelsCallback) {
        this.travelsList = new java.util.ArrayList<>(length);
        this.travelsCallback = travelsCallback;
        this.length = length;
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

    public void addTravel(Travels travel) {
        travelsList.add(travel);
        if (travelsList.size() == length) {
            Log.d(TAG, "Travels list size: " + travelsList.size());
            travelsCallback.onSuccessFromRemote(this, System.currentTimeMillis());
        }
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
