package services;

import data.*;
import exceptions.InvalidPairingArgsException;
import exceptions.PMVNotAvailException;
import exceptions.PairingNotFoundException;
import jdk.jfr.Percentage;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;

public class ServerDoubleFail implements Server {
    @Override
    public void checkPMVAvail(VehicleID vhID) throws PMVNotAvailException {
        throw new PMVNotAvailException("Pmv not avail");
    }
    @Override
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                                GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{
        if(user==null || veh==null || st==null || loc==null || date==null){
            throw new InvalidPairingArgsException("Invalid arguments");
        }
        setPairing(user,veh,st,loc,date);
        registerLocation(veh,st);
    }
    @Override
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException, PairingNotFoundException {
        if(user==null || veh==null || st==null || loc==null || date==null || avSp==0.0 || dist==0.0 || dur==0.0 || imp==null){
            throw new InvalidPairingArgsException("Invalid pairing arguments");
        }

        unPairRegisterService(user,veh,st,loc,date);
    }
    @Override
    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                           GeographicPoint loc, LocalDateTime date){
    }
    @Override
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{
        throw  new PairingNotFoundException("Pairing not found");
    }
    @Override
    public void registerLocation(VehicleID veh, StationID st){

    }

    @Override
    public void registerPayment(ServiceID serviceID, UserAccount userAccount, BigDecimal imp, char payMeth) {

    }

}
