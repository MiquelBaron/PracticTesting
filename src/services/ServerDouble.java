package services;

import data.*;
import exceptions.*;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerDouble implements Server{
    private Map<VehicleID, Boolean> vehicles; //True = disponible - False = no disponible
    private Map<UserAccount, VehicleID> pairings; //Registered users with vehicle associated
    private Map<VehicleID, StationID> vehicleLocation;
    private final List<StationID> stations; //Registered stations
    private final boolean connectException; //If true, throws new ConnectException

    public ServerDouble(boolean connectException){
        this.vehicles = new HashMap<>();
        this.vehicles.put(new VehicleID("1"),true);
        this.vehicles.put(new VehicleID("2"),true);

        this.pairings = new HashMap<>();
        pairings.put(new UserAccount("1"),null);
        pairings.put(new UserAccount("2"),null);
        pairings.put(new UserAccount("3"),null);

        this.stations=new ArrayList<>();
        stations.add(new StationID("1", new GeographicPoint(10,10)));
        stations.add(new StationID("2", new GeographicPoint(20,20)));
        stations.add(new StationID("3", new GeographicPoint(30,30)));


        this.vehicleLocation = new HashMap<>();

        this.connectException=connectException;
    }

    public void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException{
        if(connectException){throw new ConnectException();}
        if(!vehicles.get(vhID) || !vehicles.containsKey(vhID)){
            throw new PMVNotAvailException("Vehicle with id "+vhID.getId()+" is not avaliable");
        }
    }
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{

        if(connectException){throw new ConnectException("Connect exception");}

        //Check null values
        if(user==null || veh==null || st==null || loc==null || date==null || st.getLoc()==null){
            throw new InvalidPairingArgsException("Invalid arguments");
        }
        //Check if user & vehicle & station are registered in the server
        if(!vehicles.containsKey(veh) || !pairings.containsKey(user) || !stations.contains(st)){
            throw new InvalidPairingArgsException("Values are not registered in the server");
        }
        setPairing(user,veh,st,loc,date);
    }
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException, PairingNotFoundException {
        if(connectException){throw new ConnectException();}

        if(user==null || veh==null || st==null || st.getLoc()==null|| loc==null || date==null || avSp<=0.0 || dist<=0.0 || imp==null){
            throw new InvalidPairingArgsException("Invalid pairing arguments");
        }
        if(!vehicles.containsKey(veh) || !pairings.containsKey(user) || !pairings.get(user).equals(veh)){
            throw new InvalidPairingArgsException("Vehicle does not exists");
        }

        unPairRegisterService(user,veh,st,loc,date);
        registerLocation(veh,st);
    }

    @Override
    public void registerPayment(ServiceID serviceID, UserAccount userAccount, BigDecimal imp, char payMeth) {

    }


    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime date){
        pairings.replace(user,veh);
        vehicles.replace(veh,false);
    }
    public void unPairRegisterService(UserAccount user, VehicleID veh, StationID st, GeographicPoint loc, LocalDateTime date) throws PairingNotFoundException{
        pairings.replace(user,null);
        vehicles.replace(veh,true);
    }
    public void registerLocation(VehicleID veh, StationID st){
        vehicleLocation.put(veh,st);
    }


}




















