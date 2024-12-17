package micromobility;

import com.sun.net.httpserver.Authenticator;
import data.*;
import exceptions.*;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import services.*;
import services.smartfeatures.*;

public class JourneyRealizeHandler {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private Server server;
    private UserAccount userAccount;
    private QRDecoder qrDecoder;
    private UnbondedBTSignal unbondedBTSignal;
    private JourneyService journeyService;
    private ArduinoMicroController arduinoMicroController;
    private PMVehicle pmVehicle;
    private VehicleID vehicleID;
    private BufferedImage bufferedImage;
    private StationID stationID;
    private boolean delimitedZone;

    public JourneyRealizeHandler(StationID stationId, QRDecoder qrDecoder, UnbondedBTSignal btSignal, Server server, UserAccount userAccount) {
        this.stationID = stationId;
        this.qrDecoder = qrDecoder;
        this.unbondedBTSignal = btSignal;
        this.server = server;
        this.delimitedZone = true;
        this.userAccount=userAccount;
    }

    private void setDelimitedZone(Boolean aux){ this.delimitedZone=aux}

    public void scanQR()
            throws ConnectException, InvalidPairingArgsException, CorruptedImgException, PMVNotAvailException, ProceduralException {

        if(stationID==null || !delimitedZone){
            throw new ProceduralException("Procedural exception");
        }

        this.vehicleID = qrDecoder.getVehicleID(this.bufferedImage); //VehicleId
        server.checkPMVAvail(vehicleID);

        this.journeyService = new JourneyService();

        arduinoMicroController.setBTconnection();

        this.pmVehicle=new PMVehicle(vehicleID);
        GeographicPoint loc=pmVehicle.getGeographicPoint();

        LocalDateTime date = LocalDateTime.now();

        pmVehicle.setNotAvailb();

        journeyService.setInitDate(date);
        journeyService.setOriginPoint(loc);
        journeyService.setInitHour(date.getHour());

        server.registerPairing(userAccount, vehicleID, stationID, loc, date);
        //FALTA ASSOCIAR LA INSTANCIA DE JourneyService AMB VEHICLE I USUARI
    }




    public void unPairVehicle () throws ConnectException, InvalidPairingArgsException, PairingNotFoundException, ProceduralException{
        if(!this.journeyService.getEndStation().equals(stationID) || !delimitedZone || !journeyService.isInProgress() || !pmVehicle.getState().equals(pmvState.UnderWay)){
            throw new ProceduralException("Procedural exception");
        }

        PMVehicle pmVehicle1 = new PMVehicle(this.vehicleID);
        GeographicPoint loc = pmVehicle1.getGeographicPoint();
        server.stopPairing(this.userAccount, this.vehicleID, this.stationID,loc,  LocalDateTime.now(), avSpeed, dist, duration, imp);


    }
    public void broadcastStationID (StationID stID) throws ConnectException{
        unbondedBTSignal.BTbroadcast();
        this.stationID=stID;
    }

    // Input events from the Arduino microcontroller channel
    public void startDriving ()
            throws ConnectException, ProceduralException {
        try{
            arduinoMicroController.startDriving();
        } catch (PMVPhisicalException e){
            throw new ConnectException("Connect exception");
        }
        this.pmVehicle.setUnderWay();

    }
    public void stopDriving ()
            throws ConnectException, ProceduralException{

        try{
            arduinoMicroController.stopDriving();
        } catch (PMVPhisicalException e){
            throw new ConnectException("Connect exception");
        }
        this.pmVehicle.setAvailb();
    }
    // Internal operations


    private Float calculateDistance(GeographicPoint point1, GeographicPoint point2){
        double lat1Rad = Math.toRadians(point1.getLatitude());
        double lon1Rad = Math.toRadians(point1.getLongitude());
        double lat2Rad = Math.toRadians(point2.getLatitude());
        double lon2Rad = Math.toRadians(point2.getLongitude());

        // Diferencias de latitud y longitud
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distancia final
        double distance = EARTH_RADIUS_KM * c;

        return (float) distance;
    }

    private int calculateDuration(LocalDateTime startTime, LocalDateTime endTime){
        return (int) ChronoUnit.MINUTES.between(startTime, endTime);
    }
}


















