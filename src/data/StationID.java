package data;

import java.util.zip.GZIPOutputStream;

public class StationID {
    private String id;
    private GeographicPoint loc;
    public StationID(String id, GeographicPoint loc){
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("StationID cannot be null or empty");
        }
        this.id = id;
    }
    public String getId(){
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationID stationID = (StationID) o;
        return id.equals(stationID.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "StationID{" + "id='" + id + '\'' + '}';
    }
    GeographicPoint getLoc { return this.loc} ;
}
