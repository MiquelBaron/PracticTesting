package micromobility;
import data.*;

/**
 * Internal classes involved in the use of the service
 */
public class PMVehicle {

    private VehicleID veh;
    private PMVState state;
    private GeographicPoint geographicPoint;
    private UserAccount userAccount;
    public PMVehicle(VehicleID veh, GeographicPoint geographicPoint){
        this.geographicPoint=geographicPoint;
        this.veh=veh;
        this.state=PMVState.Available;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    // All the getter methods
    public PMVState getState(){
        return this.state;
    }

    public GeographicPoint getGeographicPoint() {
        return geographicPoint;
    }



    // The setter methods to be used are only the following ones
    public void setLocation (GeographicPoint gP) {
        this.geographicPoint=gP;
    }
    public void setNotAvailb () {
        this.state = PMVState.NotAvailable;
    }
    public void setUnderWay () {
        this.state = PMVState.UnderWay;
    }
    public void setAvailb () {
        this.state = PMVState.Available;
    }
}