package it.unimib.travelhub.data.source;

import java.util.List;

import it.unimib.travelhub.data.database.TravelsDao;
import it.unimib.travelhub.data.database.TravelsRoomDatabase;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public class TravelsLocalDataSource extends BaseTravelsLocalDataSource {
    private final TravelsDao travelsDao;

    public TravelsLocalDataSource(TravelsRoomDatabase travelsRoomDatabase) {
        this.travelsDao = travelsRoomDatabase.travelsDao();
    }

    @Override
    public void getTravels() {
        TravelsRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<Travels> travelsList = travelsDao.getAll();
                if (travelsList.isEmpty()) {
                    travelsCallback.onFailureFromLocal(new Exception("No travels found"));
                } else {
                    travelsCallback.onSuccessFromLocal(new TravelsResponse(travelsList));
                }
            } catch (Exception e) {
                travelsCallback.onFailureFromLocal(e);
            }
        });
    }

    @Override
    public void updateTravel(Travels travels) {
        TravelsRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                int updated = travelsDao.updateSingleTravel(travels);
                if (updated == 0) {
                    travelsCallback.onFailureFromLocal(new Exception("No travels updated"));
                } else {
                    travelsCallback.onSuccessSynchronization();
                }
            } catch (Exception e) {
                travelsCallback.onFailureFromLocal(e);
            }
        });
    }

    @Override
    public void insertTravels(List<Travels> travelsList) {
        TravelsRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<Long> inserted = travelsDao.insertTravelsList(travelsList);
                if (inserted.isEmpty()) {
                    travelsCallback.onFailureFromLocal(new Exception("No travels inserted"));
                } else {
                    travelsCallback.onSuccessFromLocal(new TravelsResponse(travelsList));
                }
            } catch (Exception e) {
                travelsCallback.onFailureFromLocal(e);
            }
        });
    }

    @Override
    public void deleteAll() {
        TravelsRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                int deleted = travelsDao.deleteAll();
                if (deleted == 0) {
                    travelsCallback.onFailureFromLocal(new Exception("No travels deleted"));
                } else {
                    travelsCallback.onSuccessDeletion();
                }
            } catch (Exception e) {
                travelsCallback.onFailureFromLocal(e);
            }
        });
    }

}
