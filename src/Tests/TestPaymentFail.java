package Tests;

import data.*;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVehicle;
import micromobility.payment.Wallet;
import micromobility.payment.WalletPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import java.math.BigDecimal;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.*;

public class TestPaymentFail {
    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    StationID initStation;
    StationID endStation;
    Wallet wallet;
    BigDecimal correctImpAmount;
    BigDecimal incorrectImpAmount;
    Character option;
    ServiceID serviceID;

    @BeforeEach
    public void setUp() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, PMVPhisicalException, PairingNotFoundException {
        initStation = new StationID("1", new GeographicPoint(10, 10));
        endStation = new StationID("2", new GeographicPoint(20, 20));

        vehicleID = new VehicleID("1"); //Vehicle que ja tenim emmagatzemat a servidor
        geographicPoint = new GeographicPoint(10, 10);
        userAccount = new UserAccount("1");
        serviceID=new ServiceID("1");

        ArduinoMicroController arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);
        Server server = new ServerDouble(false);
        UnbondedBTSignal unbondedBTSignal = new UnbondedBTSignalDoubleExit();

        pmVehicle = new PMVehicle(vehicleID, geographicPoint);
        wallet = new Wallet(new BigDecimal(200));

        incorrectImpAmount = new BigDecimal(250);
        correctImpAmount = new BigDecimal(50);



        this.journeyRealizeHandler = new JourneyRealizeHandler();
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);
        journeyRealizeHandler.setServer(server);
        journeyRealizeHandler.setArduinoMicroController(arduinoMicroController);
        journeyRealizeHandler.setQrDecoder(qrDecoder);
        journeyRealizeHandler.setPmVehicle(pmVehicle);
        journeyRealizeHandler.setUserAccount(userAccount);
        journeyRealizeHandler.setWallet(wallet);
        journeyRealizeHandler.setServiceID(serviceID);

        journeyRealizeHandler.broadcastStationID(initStation);
        journeyRealizeHandler.scanQR();
        journeyRealizeHandler.startDriving();
        journeyRealizeHandler.broadcastStationID(endStation);
        journeyRealizeHandler.unPairVehicle();


        option=0;

    }

    @Test
    public void testRealizePaymentThrowsProceduralException() {
        pmVehicle.setNotAvailb();
        journeyRealizeHandler.setPmVehicle(pmVehicle);
        assertThrows(ProceduralException.class,()->journeyRealizeHandler.selectPaymentMethod(option));
    }

    @Test
    public void testRealizePaymentThrowsNotEnoughWalletException(){
        journeyRealizeHandler.setImpAmount(incorrectImpAmount);
        assertThrows(NotEnoughWalletException.class,()->journeyRealizeHandler.selectPaymentMethod(option));
    }



























}
