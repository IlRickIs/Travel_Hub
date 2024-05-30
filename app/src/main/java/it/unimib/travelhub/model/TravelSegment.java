package it.unimib.travelhub.model;

import java.util.Date;
public class TravelSegment {
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
}
