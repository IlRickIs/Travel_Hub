package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Travels implements Parcelable, Comparable<Travels> {
    //@SerializedName("publishedAt")
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    private List<TravelSegment> destinations;
    private List<TravelMember> members;

    private Date startDate;

    private Date endDate;
    private Status status;
    public enum Status {NULL, ONGOING, DONE, FUTURE}

    public Travels() {}

    public Travels(long id, String title, String description, Date startDate, Date endDate,
                   List<TravelMember> members, List<TravelSegment> destinations) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = Status.NULL;
        this.destinations = destinations;
        this.members = members;
    }

    public List<TravelSegment> getDestinations() {
        return destinations;
    }

    public List<TravelMember> getMembers() {
        return members;
    }

    public void setDestinations(List<TravelSegment> destinations) {
        this.destinations = destinations;
    }

    public void setMembers(List<TravelMember> members) {
        this.members = members;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public Date getStartDate() {return startDate;}

    public void setStartDate(Date startDate) {this.startDate = startDate;}

    public Date getEndDate() {return endDate;}

    public void setEndDate(Date endDate) {this.endDate = endDate;}

    public Status getStatus() {return status;}

    public void setStatus(Status status) {this.status = status;}

//    public void setStatus(String status) {
//        switch (status) {
//            case "ONGOING":
//                this.status = Status.ONGOING;
//                break;
//            case "DONE":
//                this.status = Status.DONE;
//                break;
//            case "FUTURE":
//                this.status = Status.FUTURE;
//                break;
//            default:
//                this.status = Status.NULL;
//        }
//    }

    public int compareTo(Travels travels) {
        return this.startDate.compareTo(travels.startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Travels travels = (Travels) o;
        return id == travels.id && Objects.equals(title, travels.title) &&
                Objects.equals(description, travels.description) &&
                Objects.equals(startDate, travels.startDate) && Objects.equals(endDate, travels.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, startDate, endDate);
    }

    @NonNull
    @Override
    public String toString() {
        return "Travels{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", destinations=" + destinations +
                ", members=" + members +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeSerializable(this.startDate);
        dest.writeSerializable(this.endDate);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.title = source.readString();
        this.description = source.readString();
        this.startDate = (Date) source.readSerializable();
        this.endDate = (Date) source.readSerializable();
    }

    protected Travels(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.description = in.readString();
        this.startDate = (Date) in.readSerializable();
        this.endDate = (Date) in.readSerializable();
    }

    public static final Parcelable.Creator<Travels> CREATOR = new Parcelable.Creator<Travels>() {
        @Override
        public Travels createFromParcel(Parcel source) {
            return new Travels(source);
        }

        @Override
        public Travels[] newArray(int size) {
            return new Travels[size];
        }
    };

}
