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
    private static final double EARTH_RADIUS_KM = 6371.0; //Per calcular la distancia a partir de longitud i latitud

    private Server server;
    private QRDecoder qrDecoder;
    private  UnbondedBTSignal unbondedBTSignal;
    private UserAccount userAccount;
    private JourneyService journeyService;
    private ArduinoMicroController arduinoMicroController;
    private PMVehicle pmVehicle;
    private VehicleID vehicleID;
    private  BufferedImage bufferedImage;
    private StationID stationID;
    private ServiceID serviceID;
    private Wallet wallet;
    private BigDecimal impAmount;


    public JourneyRealizeHandler() {
        journeyService=null;
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

        this.journeyService = new JourneyService();

        arduinoMicroController.setBTconnection();

        GeographicPoint loc = pmVehicle.getGeographicPoint();

        LocalDateTime date = LocalDateTime.now();

        pmVehicle.setNotAvailb();
        pmVehicle.setUserAccount(userAccount); //Pas de vinculacio entre vehicle i usuari

        addValuesToNewJourneyService(date, loc, date.getHour(), userAccount, vehicleID); //Extract method

        server.registerPairing(userAccount, vehicleID, originStationID, loc, date);
    }




    public void unPairVehicle() throws ConnectException, InvalidPairingArgsException, PairingNotFoundException, ProceduralException {
        StationID endStationID = this.stationID;

        if (endStationID == null || pmVehicle.getState() != PMVState.UnderWay || !journeyService.isInProgress()) {
            throw new ProceduralException("Procedural exception");
        }

        GeographicPoint endPoint = endStationID.getLoc();

        LocalDateTime endDate = LocalDateTime.now();

        int duration = calculateDuration(journeyService.getInitDate(), endDate);
        float distance = calculateDistance(journeyService.getOriginPoint(), endStationID.getLoc());
        float avSpeed = distance / (float) duration;

        impAmount = calculateImport(duration, distance);

        addValuesToFinishedJourneyService(endDate, endDate.getHour(), endPoint, distance, duration,avSpeed,impAmount, serviceID); //Extract method

        server.stopPairing(userAccount, vehicleID, stationID, endPoint, endDate, avSpeed, distance, duration, impAmount);

        pmVehicle.setAvailb();
        pmVehicle.setLocation(endPoint);
        pmVehicle.setUserAccount(null);

        journeyService.setInProgress(false);
        journeyService = null;
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
        realizePayment(this.impAmount);
        server.registerPayment(serviceID, userAccount,impAmount,opt);
    }

    public void realizePayment(BigDecimal imp) throws NotEnoughWalletException {
        WalletPayment walletPayment=new WalletPayment(wallet);
        walletPayment.setImpAmount(imp);
        walletPayment.realizeWalletPayment();
        walletPayment.setJourneyService(journeyService);
    }

    public void undoBtConnection(){
        arduinoMicroController.undoBTconnection();
    }


    //Getters pels tests

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




    //Internal operations

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
    private int calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        return (int) ChronoUnit.MILLIS.between(startTime, endTime); //Ho fem amb milisegons pq si la unitat son segons (o alguna més gran), serà 0 en temps d'execucio, per tant, saltarà invalidPairingArgsException
    }

    private BigDecimal calculateImport(int duration, float distance) {
        BigDecimal durationPrice = BigDecimal.valueOf(duration);
        BigDecimal distancePrice = BigDecimal.valueOf(distance);
        return durationPrice.add(distancePrice); //Per simplificar, l'import és la suma de la durada i la distància
    }

    public  Float calculateDistance(GeographicPoint point1, GeographicPoint point2) {
        double lat1Rad = Math.toRadians(point1.getLatitude());
        double lon1Rad = Math.toRadians(point1.getLongitude());
        double lat2Rad = Math.toRadians(point2.getLatitude());
        double lon2Rad = Math.toRadians(point2.getLongitude());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Fórmula de Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        return (float) distance;
    }

    //Injeccio de dependències
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


















