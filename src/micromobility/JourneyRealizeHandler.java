package micromobility;

import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import exceptions.*;
import services.Server;
import services.smartfeatures.ArduinoMicroController;
import services.smartfeatures.QRDecoder;
import services.smartfeatures.UnbondedBTSignal;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class JourneyRealizeHandler {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private final Server server;
    private final QRDecoder qrDecoder;
    private final UnbondedBTSignal unbondedBTSignal;
    private UserAccount userAccount;
    private JourneyService journeyService;
    private final ArduinoMicroController arduinoMicroController;
    private PMVehicle pmVehicle;
    private VehicleID vehicleID;
    private final BufferedImage bufferedImage;
    private StationID stationID;
    private final GeographicPoint geographicPoint; //Localitzacio del PMVehicle

    public JourneyRealizeHandler(QRDecoder qrDecoder, UnbondedBTSignal btSignal, Server server, UserAccount userAccount, ArduinoMicroController arduinoMicroController, GeographicPoint geographicPoint, BufferedImage bufferedImage) {
        this.qrDecoder = qrDecoder;
        this.unbondedBTSignal = btSignal;
        this.server = server;
        this.userAccount = userAccount;
        this.arduinoMicroController = arduinoMicroController;
        this.geographicPoint = geographicPoint;
        this.bufferedImage = bufferedImage;
        journeyService = null;
    }


    public void scanQR()
            throws ConnectException, InvalidPairingArgsException, CorruptedImgException, PMVNotAvailException, ProceduralException {
        StationID originStationID = this.stationID;
        this.stationID = null; //El tornem a posar a null, ja que haurà de guardar el valor de "endStationID".
        if (originStationID == null) {
            throw new ProceduralException("Procedural exception");
        }

        this.vehicleID = qrDecoder.getVehicleID(this.bufferedImage); //VehicleId
        server.checkPMVAvail(vehicleID);

        this.journeyService = new JourneyService();

        arduinoMicroController.setBTconnection();

        this.pmVehicle = new PMVehicle(vehicleID, geographicPoint);
        GeographicPoint loc = pmVehicle.getGeographicPoint();

        LocalDateTime date = LocalDateTime.now();


        pmVehicle.setNotAvailb();

        journeyService.setInitDate(date);
        journeyService.setOriginPoint(loc);
        journeyService.setInitHour(date.getHour());

        //Associem l'usuari i el vehicle
        journeyService.setUserAccount(userAccount);
        journeyService.setVehicleID(vehicleID);

        server.registerPairing(userAccount, vehicleID, originStationID, loc, date);
    }


    public void unPairVehicle() throws ConnectException, InvalidPairingArgsException, PairingNotFoundException, ProceduralException {
        StationID endStationID = this.stationID;

        if (endStationID == null || pmVehicle.getState() != PMVState.UnderWay || !journeyService.isInProgress()) {
            throw new ProceduralException("Procedural exception");
        }
        GeographicPoint endPoint = pmVehicle.getGeographicPoint();
        LocalDateTime endDate = LocalDateTime.now();

        journeyService.setEndDate(endDate);
        journeyService.setEndHour(endDate.getHour());
        journeyService.setEndPoint(endPoint);

        int duration = calculateDuration(journeyService.getInitDate(), endDate);
        float distance = geographicPoint.calculateDistance(journeyService.getOriginPoint(), endPoint);
        float avSpeed = distance / (float) duration;

        journeyService.setDistance(distance);
        journeyService.setDuration(duration);
        journeyService.setAvgSpeed(avSpeed);

        BigDecimal imp = calculateImport(duration, distance);

        journeyService.setImportAmount(imp);

        server.stopPairing(userAccount, vehicleID, stationID, endPoint, endDate, avSpeed, distance, duration, imp);

        pmVehicle.setAvailb();
        pmVehicle.setLocation(endPoint);

        journeyService.setInProgress(false);
        journeyService = null;
        arduinoMicroController.undoBTconnection();
    }

    public void broadcastStationID(StationID stID) throws ConnectException {
        unbondedBTSignal.BTbroadcast();
        this.stationID = stID;
    }

    // Input events from the Arduino microcontroller channel
    public void startDriving()
            throws ConnectException, ProceduralException {
        if (pmVehicle.getState() != PMVState.NotAvailable || journeyService == null) {
            throw new ProceduralException("Procedural exception");
        }

        try {
            arduinoMicroController.startDriving();
        } catch (PMVPhisicalException e) {
            throw new ConnectException("Connect exception");
        }
        pmVehicle.setUnderWay();
        journeyService.setInProgress(true);
    }

    public void stopDriving()
            throws ConnectException, ProceduralException {
        if (pmVehicle.getState() != PMVState.UnderWay || !journeyService.isInProgress()) {
            throw new ProceduralException("Procedural exception");
        }

        try {
            arduinoMicroController.stopDriving();
        } catch (PMVPhisicalException e) {
            throw new ConnectException("Connect exception");
        }
    }


    // Internal operations


    private int calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        return (int) ChronoUnit.MINUTES.between(startTime, endTime); //Durada en minuts
    }

    private BigDecimal calculateImport(int duration, float distance) {
        BigDecimal durationPrice = BigDecimal.valueOf(duration);
        BigDecimal distancePrice = BigDecimal.valueOf(distance);
        return durationPrice.add(distancePrice); //L'import és la suma de la durada i la distància
    }

    //Getters i setters pels tests

    public StationID getStationID() {
        return this.stationID;
    }

    public VehicleID getVehicleID() {
        return vehicleID;
    }

    public PMVState pmvState() {
        return this.pmVehicle.getState();
    }

    public JourneyService getJourneyService() {
        return this.journeyService;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }


}


















