package services;

import data.*;
import exceptions.*;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ServerDouble implements Server{
    private Map<VehicleID, Boolean> disponibility; //True = disponible - False = no disponible
    private Map<UserAccount, VehicleID> pairings;
    private Map<VehicleID, StationID> vehicleLocation;

    public ServerDouble(){
        this.disponibility = new HashMap<>();
        this.pairings = new HashMap<>();
        this.vehicleLocation = new HashMap<>();
    }

    public void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException{
        if(!disponibility.get(vhID)){
            throw new PMVNotAvailException("Vehicle with id "+vhID.getId()+" is not avaliable");
        }
    }
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{
        if(user==null || veh==null || st==null || loc==null || date==null){
            throw new InvalidPairingArgsException("Invalid arguments");
        }
        setPairing(user,veh,st,loc,date);
        registerLocation(veh,st);
    }
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException, PairingNotFoundException {
        if(user==null || veh==null || st==null || loc==null || date==null || avSp==0.0 || dist==0.0 || dur==0.0 || imp==null){
            throw new InvalidPairingArgsException("Invalid pairing arguments");
        }
        if(!pairings.containsKey(user)){
            throw new PairingNotFoundException("Pairing not found");
        }

        unPairRegisterService(user,veh,st,loc,date);
        registerLocation(veh,st);

    }
    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime date){
        pairings.put(user,veh);
    }
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{
        pairings.remove(user);
        disponibility.replace(veh,true);
    }
    public void registerLocation(VehicleID veh, StationID st){
        vehicleLocation.put(veh,st);
    }

    //Mètodes pels tests

    private void setDisponibility(VehicleID vehicleID, Boolean disponible){
        disponibility.put(vehicleID,disponible);
    }
}




















