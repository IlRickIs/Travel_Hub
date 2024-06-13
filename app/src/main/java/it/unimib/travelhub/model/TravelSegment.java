package it.unimib.travelhub.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TravelSegment implements Serializable, Comparable<TravelSegment> {

    private String location;
    private Date dateFrom;
    private Date dateTo;
    private String description;

    public TravelSegment() {
        this.location = null;
        this.dateFrom = null;
        this.dateTo = null;
        this.description = null;
    }

    public TravelSegment(String location){
        this.location = location;
        this.dateFrom = null;
        this.dateTo = null;
        this.description = null;
    }

    public TravelSegment(String location, Date dateFrom, Date dateTo, String description) {
        this.location = location;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public String getDescription() {
        return description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int compareTo(TravelSegment travels) {
        if (this.dateFrom.compareTo(travels.dateFrom) == 0)
            return this.dateTo.compareTo(travels.dateTo);
        else
            return this.dateFrom.compareTo(travels.dateFrom);
    }

    @Override
    public String toString() {
        return "TravelSegment{" +
                "location='" + location + '\'' +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("dateFrom", dateFrom);
        map.put("dateTo", dateTo);
        map.put("description", description);
        return map;

    }
}
