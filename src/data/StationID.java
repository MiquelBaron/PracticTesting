package data;

import java.util.zip.GZIPOutputStream;

public class StationID {
    private String id;
    private GeographicPoint loc;

    public StationID(String id, GeographicPoint loc) {
        if (id == null || loc==null || id.isEmpty()) {
            throw new IllegalArgumentException("StationID cannot be null or empty");
        }
        this.id = id;
        this.loc=loc;
    }

    public String getId() {
        return this.id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationID stationID = (StationID) o;
        return id.equals(stationID.id) && loc.equals(stationID.loc);
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "StationID{" + "id='" + id + '\'' + '}';
    }

    public GeographicPoint getLoc() {
        return loc;
    }
}
