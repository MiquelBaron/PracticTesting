package micromobility;
import data.*;

/**
 * Internal classes involved in the use of the service
 */
public class PMVehicle {

    private VehicleID veh;
    private PMVState state;
    private GeographicPoint geographicPoint;
    public PMVehicle(VehicleID veh){
        this.veh=veh;
        this.state=PMVState.Available;
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