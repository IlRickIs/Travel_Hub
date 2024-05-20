package it.unimib.travelhub.data.source;

public abstract class BaseTravelsRemoteDataSource {
    protected TravelsCallback travelsCallback;

    public void setTravelsCallback(TravelsCallback travelsCallback) {
        this.travelsCallback = travelsCallback;
    }

    public abstract void getTravels();
}
