package services;

import data.*;
import exceptions.*;
import micromobility.JourneyService;

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
    private Map<StationID, GeographicPoint> stations; //Registered stations with its localization
    private Map<ServiceID, Character> paymentType;
    private Map<ServiceID, JourneyService> journeys;
    private Map<UserAccount, ServiceID> users;
    private final boolean serverError; //If true, throws new ConnectException

    public ServerDouble(boolean serverError){

        //Simulating values that are already registered in the server
        this.vehicles = new HashMap<>();
        this.vehicles.put(new VehicleID("1"),true);
        this.vehicles.put(new VehicleID("2"),true);
        this.vehicles.put(new VehicleID("3"),false); //Simulates a vehicle that is being used (for the multi-user tests)

        this.pairings = new HashMap<>();
        pairings.put(new UserAccount("1"),null);
        pairings.put(new UserAccount("2"),null);
        pairings.put(new UserAccount("3"),null);

        this.stations=new HashMap<>();
        stations.put(new StationID("1", new GeographicPoint(10,10)), new GeographicPoint(10,10));
        stations.put(new StationID("2", new GeographicPoint(20,20)), new GeographicPoint(20,20));
        stations.put(new StationID("3", new GeographicPoint(30,30)), new GeographicPoint(30,30));

        paymentType= new HashMap<>();
        journeys = new HashMap<>();
        users=new HashMap<>();

        this.vehicleLocation = new HashMap<>();

        this.serverError=serverError;
    }

    public void checkPMVAvail(VehicleID vhID)
            throws PMVNotAvailException, ConnectException{
        if(serverError){throw new ConnectException();}
        if(!vehicles.containsKey(vhID) || !vehicles.get(vhID)){
            throw new PMVNotAvailException("Vehicle with id "+vhID.getId()+" is not avaliable");
        }
    }
    public void registerPairing(UserAccount user, VehicleID veh, StationID st,
                         GeographicPoint loc, LocalDateTime date)
            throws InvalidPairingArgsException, ConnectException{

        if(serverError){throw new ConnectException("Connect exception");}

        //Check null values
        if(user==null || veh==null || st==null || loc==null || date==null){
            throw new InvalidPairingArgsException("Invalid arguments");
        }
        //Check if user & vehicle & station are registered in the server
        if(!vehicles.containsKey(veh) || !pairings.containsKey(user) || !stations.containsKey(st) || !st.getLoc().equals(stations.get(st))){
            throw new InvalidPairingArgsException("Values are not registered in the server");
        }
        setPairing(user,veh,st,loc,date);
    }
    public void stopPairing(UserAccount user, VehicleID veh, StationID st,
                            GeographicPoint loc, LocalDateTime date, float avSp, float dist,
                            int dur, BigDecimal imp)
            throws InvalidPairingArgsException, ConnectException, PairingNotFoundException {
        if(serverError){throw new ConnectException();}

        if(user==null || veh==null || st==null || st.getLoc()==null|| loc==null || date==null || avSp<=0.0 || dist<=0.0 || imp==null){
            throw new InvalidPairingArgsException("Invalid pairing arguments");
        }
        if(!vehicles.containsKey(veh) || !pairings.containsKey(user) || !pairings.get(user).equals(veh)){
            throw new PairingNotFoundException("Pairing not found");
        }
        ServiceID serviceID = users.get(user);
        JourneyService journeyService = journeys.get(serviceID);
        journeyService.completeJourneyService(date, date.getHour(), dur,dist, avSp, loc, imp);

        unPairRegisterService(journeyService);
        registerLocation(veh,st);
    }

    @Override
    public void registerPayment(ServiceID serviceID, UserAccount userAccount, BigDecimal imp, char payMeth) throws InvalidPaymentArgsException {
        if(userAccount==null || !pairings.containsKey(userAccount) || imp==null || payMeth =='\u0000'){
            throw new InvalidPaymentArgsException("Invalid paymentType args");
        }
        paymentType.put(serviceID,payMeth);
    }


    // Internal operations
    public void setPairing(UserAccount user, VehicleID veh, StationID st,
                    GeographicPoint loc, LocalDateTime date){
        JourneyService journeyService = new JourneyService(date,date.getHour(),loc,veh,user);

        //Save journey
        journeys.put(journeyService.getServiceID(), journeyService);

        //Save user-serviceID
        users.put(user,journeyService.getServiceID());

        //Save pairing
        pairings.replace(user,veh);

        //Update vehicle state
        vehicles.replace(veh,false);
    }
    public void unPairRegisterService(JourneyService s) throws PairingNotFoundException{
        ServiceID serviceID = s.getServiceID();
        UserAccount userAccount = s.getUserAccount();
        VehicleID vehicleID = s.getVehicleID();

        //Update initial journey service to ended journey service
        journeys.replace(serviceID,s);

        //Unpair user from journeyService & vehicle
        users.replace(userAccount,null);
        pairings.replace(userAccount,null);

        //Set vehicle availability true
        vehicles.replace(vehicleID, true);
    }
    public void registerLocation(VehicleID veh, StationID st){

        //Update station location
        stations.replace(st,st.getLoc());

        //Update vehicle location
        vehicleLocation.put(veh,st);
    }


}




















