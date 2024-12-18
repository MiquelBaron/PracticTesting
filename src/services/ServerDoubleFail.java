package services;

import data.*;
import exceptions.*;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;

public class ServerDoubleFail implements Server{
    private boolean avail = false;
    private boolean connectException = false;

    public void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException{
        if(!avail){
            throw new PMVNotAvailException("Vehicle with id "+vhID.getId()+" not available");
        }
        throw new ConnectException();
    }
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{
        if (user == null || veh == null || st == null || loc == null || date == null) {
            throw new InvalidPairingArgsException("Invalid arguments");
        }
        setPairing(user,veh,st,loc,date);
        throw new ConnectException("Connect exception");

    }
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp) throws InvalidPairingArgsException, ConnectException{
        if (user == null || veh == null || st == null || loc == null || date == null || imp==null) {
            throw new InvalidPairingArgsException("Invalid arguments");
        }
        if(connectException) {
            throw new ConnectException("Connect exception");
        }
        try {
            unPairRegisterService(user, veh, st, loc, date);
        }catch(PairingNotFoundException e){
            throw new InvalidPairingArgsException("Exception");
        }
    }
    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime date){
    }
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{
        throw new PairingNotFoundException("Pairing not found between user "+user.getId()+" and vehicle "+veh.getId());
    }
    public void registerLocation(VehicleID veh, StationID st){

    }
    private void setAvail(Boolean avail){
        this.avail=avail;
    }
    private void setConnectException(Boolean connect){this.connectException=connect;}
}










