package it.unimib.travelhub.util;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public class JSONParserUtil {

    private static final String TAG = JSONParserUtil.class.getSimpleName();

    private final Context context;

    public JSONParserUtil(Application application) {
        this.context = application.getApplicationContext();
    }
    public TravelsResponse parseJSONFileWithGSon(String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

        TravelsResponse travelsResponseUnordered = gson.fromJson(bufferedReader, TravelsResponse.class);
        List<Travels> travelsList = travelsResponseUnordered.getTravelsList();
        Collections.sort(travelsList);

        Date currentDate = new Date();
        for (Travels travels : travelsList) {
            if (travels.getStartDate().before(currentDate) && travels.getEndDate().after(currentDate)) {
                travels.setStatus(Travels.Status.ONGOING);
            } else if (travels.getEndDate().before(currentDate)) {
                travels.setStatus(Travels.Status.DONE);
            } else if (travels.getStartDate().after(currentDate)) {
                travels.setStatus(Travels.Status.FUTURE);
            }
        }

        return new TravelsResponse(
                travelsResponseUnordered.getTravelsCount(),
                travelsList
        );
    }
}
