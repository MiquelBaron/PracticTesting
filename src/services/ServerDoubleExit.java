package services;

import data.*;
import exceptions.*;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;

public class ServerDoubleExit implements Server{
    private boolean avail = false;


    public void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException{

    }
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{

    }
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException{
        this.avail=true;

    }
    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime
                            date){

    }
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{

    }
    public void registerLocation(VehicleID veh, StationID st){

    }
    private void setAvail(Boolean avail){
        this.avail=avail;
    }
}




















