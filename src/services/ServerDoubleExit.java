package services;

import data.*;
import exceptions.*;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;

public class ServerDoubleExit implements Server{

    public void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException{
    }
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{
        setPairing(user,veh,st,loc,date);
        registerLocation(veh,st);
    }
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException{
        try{
            unPairRegisterService(user, veh, st, loc, date);
        }catch(PairingNotFoundException ignored){

        }

    }
    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime date){

    }
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{

    }
    public void registerLocation(VehicleID veh, StationID st){

    }
}




















