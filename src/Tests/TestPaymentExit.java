package Tests;

import data.*;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVState;
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
import java.security.Provider;

import static org.junit.jupiter.api.Assertions.*;


public class TestPaymentExit {
    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    StationID initStation;
    StationID endStation;
    Wallet wallet;
    BigDecimal impAmount;
    ServiceID serviceID;
    Character option;
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
        Server server = new ServerDouble();
        UnbondedBTSignal unbondedBTSignal = new UnbondedBTSignalDoubleExit();

        pmVehicle = new PMVehicle(vehicleID, geographicPoint);
        wallet = new Wallet(new BigDecimal(200));

        impAmount = new BigDecimal(50);



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

        journeyRealizeHandler.setImpAmount(impAmount);
        option = 0;

    }

    @Test
    public void testPaymentInstanceIsCreated() throws NotEnoughWalletException, ProceduralException {
        journeyRealizeHandler.selectPaymentMethod(option);
        assertNotNull(journeyRealizeHandler.getWalletPayment());
    }

    @Test
    public void testPaymentImportIsModified() throws NotEnoughWalletException, ProceduralException {
        journeyRealizeHandler.selectPaymentMethod(option);
        WalletPayment walletPayment = journeyRealizeHandler.getWalletPayment();
        assertEquals(impAmount,walletPayment.getImpAmount());
    }

    @Test
    public void testPaymentIsVinculatedWithJourneyService() throws NotEnoughWalletException, ProceduralException {
        journeyRealizeHandler.selectPaymentMethod(option);
        WalletPayment walletPayment = journeyRealizeHandler.getWalletPayment();
        assertEquals(serviceID,walletPayment.getServiceID());
    }

    @Test
    public void testWalletBalanceIsModified() throws NotEnoughWalletException, ProceduralException {
        BigDecimal expectedBalance=new BigDecimal(150);
        journeyRealizeHandler.selectPaymentMethod(option);
        assertEquals(expectedBalance,wallet.getBalance());
    }
}
