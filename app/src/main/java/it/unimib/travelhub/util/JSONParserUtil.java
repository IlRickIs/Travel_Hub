package it.unimib.travelhub.util;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        return gson.fromJson(bufferedReader, TravelsResponse.class);
    }
}
