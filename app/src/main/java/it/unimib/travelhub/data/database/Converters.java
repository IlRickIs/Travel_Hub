package it.unimib.travelhub.data.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.TravelSegment;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<TravelMember> jsonToTravelMember (String json) {
        Type type = new TypeToken<List<TravelMember>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    @TypeConverter
    public static String travelMemberToJson(List<TravelMember> travelMembers) {
        return new Gson().toJson(travelMembers);
    }

    @TypeConverter
    public static List<TravelSegment> jsonToTravelSegment (String json) {
        Type type = new TypeToken<List<TravelSegment>>() {}.getType();
        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String travelSegmentToJson(List<TravelSegment> travelSegments) {
        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
        return gson.toJson(travelSegments);
    }
}
