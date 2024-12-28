package services;

import data.*;
import exceptions.InvalidPairingArgsException;
import exceptions.PMVNotAvailException;
import exceptions.PairingNotFoundException;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ServerDoubleFail implements Server {

    private UserAccount userAccount;
    private VehicleID vehicleID;
    public ServerDoubleFail(){
        this.userAccount=new UserAccount("1");
        this.vehicleID=new VehicleID("1");
    }
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
        if(!user.equals(this.userAccount)){
            throw new PairingNotFoundException("Pairing not found");
        }
        throw new ConnectException("Connect exception");
    }
    @Override
    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                           GeographicPoint loc, LocalDateTime date){
    }
    @Override
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{
    }
    @Override
    public void registerLocation(VehicleID veh, StationID st){

    }

    @Override
    public void registerPayment(ServiceID serviceID, UserAccount userAccount, BigDecimal imp, char payMeth) {

    }

}
