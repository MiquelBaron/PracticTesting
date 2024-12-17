package micromobility;

import data.UserAccount;
import data.VehicleID;

public class Association {
    private UserAccount userAccount;
    private VehicleID vehicleID;
    private JourneyService journeyService;
    public Association(UserAccount userAccount, VehicleID vehicleID, JourneyService journeyService){
        this.userAccount=userAccount;
        this.vehicleID=vehicleID;
        this.journeyService=journeyService;
    }

    public VehicleID getVehicleID() {
        return vehicleID;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public JourneyService getJourneyService() {
        return journeyService;
    }

    public void setJourneyService(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setVehicleID(VehicleID vehicleID) {
        this.vehicleID = vehicleID;
    }
}
