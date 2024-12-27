package micromobility;

import data.*;
import exceptions.*;
import micromobility.payment.*;
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
    private ServiceID serviceID;
    private Wallet wallet;
    private BigDecimal impAmount;
    private final GeographicPoint geographicPoint; //Localitzacio del PMVehicle

    public JourneyRealizeHandler(QRDecoder qrDecoder, UnbondedBTSignal btSignal, Server server, UserAccount userAccount, ArduinoMicroController arduinoMicroController, GeographicPoint geographicPoint, BufferedImage bufferedImage, ServiceID serviceID) {
        this.qrDecoder = qrDecoder;
        this.unbondedBTSignal = btSignal;
        this.server = server;
        this.userAccount = userAccount;
        this.arduinoMicroController = arduinoMicroController;
        this.geographicPoint = geographicPoint;
        this.bufferedImage = bufferedImage;
        this.serviceID= serviceID;
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

        addValuesToNewJourneyService(date, loc, date.getHour(), userAccount, vehicleID); //Extract method

        server.registerPairing(userAccount, vehicleID, originStationID, loc, date);
    }




    public void unPairVehicle() throws ConnectException, InvalidPairingArgsException, PairingNotFoundException, ProceduralException {
        StationID endStationID = this.stationID;

        if (endStationID == null || pmVehicle.getState() != PMVState.UnderWay || !journeyService.isInProgress()) {
            throw new ProceduralException("Procedural exception");
        }
        GeographicPoint endPoint = pmVehicle.getGeographicPoint();
        LocalDateTime endDate = LocalDateTime.now();

        int duration = calculateDuration(journeyService.getInitDate(), endDate);
        float distance = geographicPoint.calculateDistance(journeyService.getOriginPoint(), endPoint);
        float avSpeed = distance / (float) duration;

        this.impAmount = calculateImport(duration, distance);

        addValuesToFinishedJourneyService(endDate, endDate.getHour(), endPoint, distance, duration,avSpeed,impAmount), serviceID; //Extract method

        server.stopPairing(userAccount, vehicleID, stationID, endPoint, endDate, avSpeed, distance, duration, impAmount);

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

    public void selectPaymentMethod(char opt) throws NotEnoughWalletException, ProceduralException {
        if(!pmVehicle.getState().equals(PMVState.Available)){
            throw new ProceduralException("Procedural exception");
        }
        WalletPayment walletPayment = new WalletPayment();
        walletPayment.setImpAmount(this.impAmount);
        realizePayment(this.impAmount);
        server.registerPayment(serviceID, userAccount,impAmount,opt);
    }

    public void realizePayment(BigDecimal imp) throws NotEnoughWalletException {
        wallet.deduct(imp);
    }

    public void undoBtConnection(){
        arduinoMicroController.undoBTconnection();
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


    private void addValuesToNewJourneyService(LocalDateTime date, GeographicPoint loc, int hour, UserAccount userAccount, VehicleID vehicleID) {
        journeyService.setInitDate(date);
        journeyService.setOriginPoint(loc);
        journeyService.setInitHour(hour);
        journeyService.setUserAccount(userAccount);
        journeyService.setVehicleID(vehicleID);
    }
    private void addValuesToFinishedJourneyService(LocalDateTime endDate, int hour, GeographicPoint endPoint, float distance, int duration, float avSpeed, BigDecimal imp, ServiceID serviceID) {
        journeyService.setEndDate(endDate);
        journeyService.setEndHour(hour);
        journeyService.setEndPoint(endPoint);
        journeyService.setDistance(distance);
        journeyService.setDuration(duration);
        journeyService.setAvgSpeed(avSpeed);
        journeyService.setImportAmount(imp);
        journeyService.setServiceID(serviceID);
    }

}


















