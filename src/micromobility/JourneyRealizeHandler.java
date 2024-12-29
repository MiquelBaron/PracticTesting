package micromobility;

import data.*;
import exceptions.*;
import micromobility.calculators.DistanceCalculator;
import micromobility.calculators.ImportAmountCalculator;
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
import java.time.DayOfWeek;

public class JourneyRealizeHandler {
    private static final double EARTH_RADIUS_KM = 6371.0; //Value to transform latitud & longitud to distance
    private static final BigDecimal PRICEDISTANCE = new BigDecimal(5);
    private static final BigDecimal PRICETIME = new BigDecimal(10);
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.10"); // 10% discount percentage on weekend
    private static final BigDecimal FINE_PERCENTAGE = new BigDecimal("0.20"); // 20% fine if speed limit is overpassed
    private static final int SPEED_LIMIT = 40; //


    private  BufferedImage bufferedImage;


    //Package data
    private ServiceID serviceID;
    private UserAccount userAccount;
    private VehicleID vehicleID;
    private StationID stationID;

    //Package services
    private Server server;
    private QRDecoder qrDecoder;
    private  UnbondedBTSignal unbondedBTSignal;
    private ArduinoMicroController arduinoMicroController;


    //Package micromobility
    private JourneyService localJourneyService;
    private PMVehicle pmVehicle;

    //Package payment
    private Wallet wallet;
    private BigDecimal impAmount;
    private WalletPayment walletPayment;


    public JourneyRealizeHandler() {
        localJourneyService =null;
    }

    public void scanQR()
            throws ConnectException, InvalidPairingArgsException, CorruptedImgException, PMVNotAvailException, ProceduralException {
        StationID originStationID = this.stationID;
        this.stationID = null; //El tornem a posar a null, ja que haurà de guardar el valor de "endStationID".

        if (originStationID == null) {
            throw new ProceduralException("Station has null value");
        }

        this.vehicleID = qrDecoder.getVehicleID(this.bufferedImage); //VehicleId
        server.checkPMVAvail(vehicleID);

        arduinoMicroController.setBTconnection();

        GeographicPoint loc = pmVehicle.getGeographicPoint();

        LocalDateTime date = LocalDateTime.now();

        pmVehicle.setNotAvailb();
        pmVehicle.setUserAccount(userAccount); //Pas de vinculacio entre vehicle i usuari

        this.localJourneyService=new JourneyService(date,date.getHour(), loc, vehicleID,userAccount);

        server.registerPairing(userAccount, vehicleID, originStationID, loc, date);
    }




    public void unPairVehicle() throws ConnectException, InvalidPairingArgsException, PairingNotFoundException, ProceduralException {
        StationID endStationID = this.stationID;

        if (endStationID == null || pmVehicle.getState() != PMVState.UnderWay || !localJourneyService.isInProgress()) {
            throw new ProceduralException("Procedural exception");
        }

        GeographicPoint endPoint = endStationID.getLoc();

        LocalDateTime endDate = LocalDateTime.now();

        int duration = calculateDuration(localJourneyService.getInitDate(), endDate); //Refactoring extract method
        float distance = DistanceCalculator.calculateDistance(localJourneyService.getOriginPoint(), endStationID.getLoc()); //Refactoring extract class
        float avSpeed = distance / (float) duration;

        this.impAmount = ImportAmountCalculator.calculateImport(duration, distance, avSpeed); //Refactoring extract class
        completeJourneyService(endDate, endDate.getHour(), endPoint, distance, duration,avSpeed,impAmount, serviceID);

        server.stopPairing(userAccount, vehicleID, stationID, endPoint, endDate, avSpeed, distance, duration, impAmount);

        pmVehicle.setAvailb();
        pmVehicle.setLocation(endPoint);
        pmVehicle.setUserAccount(null);

        localJourneyService.setInProgress(false);
    }

    public void broadcastStationID(StationID stID) throws ConnectException {
        unbondedBTSignal.BTbroadcast();
        this.stationID = stID;
    }

    // Input events from the Arduino microcontroller channel
    public void startDriving()
            throws ConnectException, ProceduralException, PMVPhisicalException {
        if (pmVehicle.getState() != PMVState.NotAvailable || localJourneyService == null) {
            throw new ProceduralException("Procedural exception");
        }
        arduinoMicroController.startDriving();
        pmVehicle.setUnderWay();
        localJourneyService.setInProgress(true);
    }

    public void stopDriving()
            throws ConnectException, ProceduralException, PMVPhisicalException {
        if (pmVehicle.getState() != PMVState.UnderWay || !localJourneyService.isInProgress()) {
            throw new ProceduralException("Procedural exception");
        }
        arduinoMicroController.stopDriving();
    }

    public void selectPaymentMethod(char opt) throws NotEnoughWalletException, ProceduralException, InvalidPaymentArgsException {
        if(!pmVehicle.getState().equals(PMVState.Available)){
            throw new ProceduralException("Procedural exception");
        }
        realizePayment(this.impAmount);
        server.registerPayment(serviceID, userAccount, impAmount, opt);

    }

    public void realizePayment(BigDecimal imp) throws NotEnoughWalletException {
        walletPayment=new WalletPayment(wallet);
        walletPayment.setImpAmount(imp);
        walletPayment.realizeWalletPayment();
        walletPayment.setServiceID(serviceID);
    }

    public void undoBtConnection(){
        arduinoMicroController.undoBTconnection();
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

    public JourneyService getLocalJourneyService() {
        return this.localJourneyService;
    }

    public void setLocalJourneyService(JourneyService localJourneyService){
        this.localJourneyService = localJourneyService;
    }

    public void setImpAmount(BigDecimal impAmount){
        this.impAmount=impAmount;
    }
    public WalletPayment getWalletPayment(){ return this.walletPayment;}
    public BigDecimal getImpAmount(){
        return this.impAmount;
    }





    //Internal operations

    private void completeJourneyService(LocalDateTime endDate, int hour, GeographicPoint endPoint, float distance, int duration, float avSpeed, BigDecimal imp, ServiceID serviceID) {
        localJourneyService.setEndDate(endDate);
        localJourneyService.setEndHour(hour);
        localJourneyService.setEndPoint(endPoint);
        localJourneyService.setDistance(distance);
        localJourneyService.setDuration(duration);
        localJourneyService.setAvgSpeed(avSpeed);
        localJourneyService.setImportAmount(imp);
        localJourneyService.setServiceID(serviceID);
    }
    private int calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        return (int) ChronoUnit.MILLIS.between(startTime, endTime); //Ho fem amb milisegons pq si la unitat son segons (o alguna més gran), serà 0 en temps d'execucio, per tant, saltarà invalidPairingArgsException
    }





    //Setters for injecting dependencies
    public void setQrDecoder(QRDecoder qrDecoder) {
        this.qrDecoder = qrDecoder;
    }

    public void setUnbondedBTSignal(UnbondedBTSignal unbondedBTSignal) {
        this.unbondedBTSignal = unbondedBTSignal;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setArduinoMicroController(ArduinoMicroController arduinoMicroController) {
        this.arduinoMicroController = arduinoMicroController;
    }
    public void setPmVehicle(PMVehicle pmVehicle) {
        this.pmVehicle = pmVehicle;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public void setServiceID(ServiceID serviceID) {
        this.serviceID = serviceID;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}


















