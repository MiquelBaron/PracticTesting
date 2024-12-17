package micromobility;

import data.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JourneyService {

    private LocalDateTime initDate;
    private LocalDateTime endDate;
    private int initHour;
    private int endHour;
    private int duration;
    private float distance;
    private float avgSpeed;
    private GeographicPoint originPoint;
    private GeographicPoint endPoint;
    private BigDecimal importAmount;
    private boolean inProgress;
    private StationID originStation;
    private StationID endStation;


    // Constructor
    public JourneyService(LocalDateTime initDate, int initHour, GeographicPoint originPoint) {
        this.initDate = initDate;
        this.initHour = initHour;
        this.originPoint = originPoint;
        this.inProgress = true;
    }

    // Getters y setters
    public LocalDateTime getInitDate() {
        return initDate;
    }

    public void setEndStation(StationID endStation) {
        this.endStation = endStation;
    }

    public StationID getEndStation() {
        return endStation;
    }
    public StationID getOriginStation(){
        return originStation;
    }
    public StationID setOriginStation(StationID originStation){ this.originStation = originStation }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInitDate(LocalDateTime initDate) {
        this.initDate = initDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getInitHour() {
        return initHour;
    }

    public void setInitHour(int initHour) {
        this.initHour = initHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public GeographicPoint getOriginPoint() {
        return originPoint;
    }

    public void setOriginPoint(GeographicPoint originPoint) {
        this.originPoint = originPoint;
    }

    public GeographicPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(GeographicPoint endPoint) {
        this.endPoint = endPoint;
    }
    public void setInProgress(Boolean progress){
        this.inProgress=progress;
    }
    public void setImportAmount(BigDecimal amount){
        this.importAmount=amount;
    }
}
