package micromobility;

import data.GeographicPoint;
import data.ServiceID;
import data.UserAccount;
import data.VehicleID;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JourneyService {

    // Atributos de la clase
    private UserAccount userAccount;
    private VehicleID vehicleID;
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
    private ServiceID serviceID;


    public JourneyService() {
        this.initDate = null;
        this.endDate = null;
        this.initHour = 0;
        this.endHour = 0;
        this.duration = 0;
        this.distance = 0.0f;
        this.avgSpeed = 0.0f;
        this.originPoint = null;
        this.endPoint = null;
        this.importAmount = BigDecimal.ZERO;
        this.inProgress = false;
        this.serviceID=null;
    }

    //Injectar dependències
    public void setUserAccount(UserAccount userAccount){
        this.userAccount=userAccount;
    }
    public void setVehicleID(VehicleID vehicleID){
        this.vehicleID=vehicleID;
    }


    public UserAccount getUserAccount(){
        return this.userAccount;
    }
    public VehicleID getVehicleID(){
        return this.vehicleID;
    }

    public LocalDateTime getInitDate() {
        return initDate;
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

    public BigDecimal getImportAmount() {
        return importAmount;
    }

    public void setImportAmount(BigDecimal importAmount) {
        this.importAmount = importAmount;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }
    public void setServiceID(ServiceID serviceID){ this.serviceID=serviceID;}

    public ServiceID getServiceID() {
        return serviceID;
    }
}
