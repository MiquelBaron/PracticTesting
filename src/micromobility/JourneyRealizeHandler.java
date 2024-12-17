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
    ArduinoMicroController arduinoMicroController;
    QRDecoder qrDecoder;
    Server server;
    UnbondedBTSignal unbondedBTSignal;
    JourneyService journeyService;
    BufferedImage bufferedImage;
    private float avSpeed;
    private float dist;
    private int duration;
    private BigDecimal imp;
    private UserAccount userAccount;
    private VehicleID vehicleID;
    private GeographicPoint geographicPoint;
    private StationID stationID;
    private PMVehicle pmVehicle;

    //Setters
    public void setArduinoMicroController(ArduinoMicroController arduinoMicroController){
        this.arduinoMicroController = arduinoMicroController;
    }

    public void setQrDecoder(QRDecoder qrDecoder){
        this.qrDecoder = qrDecoder;
    }
    public void setServer(Server server){
        this.server = server;
    }
    public void setUnbondedBTSignal(UnbondedBTSignal unbondedBTSignal){
        this.unbondedBTSignal=unbondedBTSignal;
    }
    public void setBufferedImage(BufferedImage bufferedImage){ this.bufferedImage=bufferedImage;}



    public void scanQR()
            throws ConnectException, InvalidPairingArgsException, CorruptedImgException, PMVNotAvailException, ProceduralException {

        this.vehicleID = qrDecoder.getVehicleID(this.bufferedImage);

        server.checkPMVAvail(vehicleID);

        PMVehicle pmVehicle1 = new PMVehicle(vehicleID);
        GeographicPoint loc=pmVehicle1.getGeographicPoint();

        LocalDateTime time = LocalDateTime.now();

        server.registerPairing(userAccount, vehicleID, this.stationID, loc, time);
        pmVehicle1.setNotAvailb();
    }




    public void unPairVehicle ()
            throws ConnectException, InvalidPairingArgsException,
            PairingNotFoundException, ProceduralException{
        //calculateValues(journeyService.getOriginPoint(),journeyService.getEndDate());
        //calculateImport(journeyService.getDistance(), journeyService.getDuration(), journeyService.getAvgSpeed(), journeyService.getEndDate());
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
    private void calculateValues (GeographicPoint gP, LocalDateTime date){
        GeographicPoint puntInici = journeyService.getOriginPoint();
        LocalDateTime dataInici = journeyService.getInitDate();
        int duration = calculateDuration(dataInici, date);
        Float distance = calculateDistance(puntInici, gP);
    }
    private void calculateImport (float dis, int dur, float avSp,
                                  LocalDateTime date){

    }

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


















