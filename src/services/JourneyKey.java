package services;

import data.UserAccount;
import data.VehicleID;

public class JourneyKey {
    private UserAccount userAccount;
    private VehicleID vehicleID;
    public JourneyKey(UserAccount userAccount, VehicleID vehicleID){
        if(userAccount==null || vehicleID==null){
            throw new IllegalArgumentException();
        }
        this.userAccount=userAccount;
        this.vehicleID=vehicleID;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Es el mismo objeto en memoria
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Comparación con un objeto nulo o de una clase diferente
        }
        JourneyKey other = (JourneyKey) obj;
        return (userAccount != null ? userAccount.equals(other.userAccount) : other.userAccount == null) &&
                (vehicleID != null ? vehicleID.equals(other.vehicleID) : other.vehicleID == null);
    }

    public VehicleID getVehicleID() {
        return vehicleID;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }
}
