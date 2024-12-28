package Tests;

import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import java.net.ConnectException;
import static org.junit.jupiter.api.Assertions.*;
public class TestMultipleUsers {
    JourneyRealizeHandler handler1;
    JourneyRealizeHandler handler2;

    VehicleID vehicleID1;
    VehicleID vehicleID2;
    PMVehicle pmVehicle1;
    PMVehicle pmVehicle2;
    UserAccount user1;
    UserAccount user2;
    GeographicPoint geographicPoint;
    StationID initStation;
    StationID endStation1;
    StationID endStation2;
    QRDecoder qrDecoder2;
    QRDecoder qrDecoder;


    @BeforeEach
    public void setUp() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, PMVPhisicalException {
        initStation = new StationID("1", new GeographicPoint(10, 10));
        endStation1 = new StationID("2", new GeographicPoint(20, 20));
        endStation2 = new StationID("3", new GeographicPoint(30, 30));

        vehicleID1 = new VehicleID("1");
        vehicleID2 = new VehicleID("2");

        user1 = new UserAccount("1");
        user2 = new UserAccount("2");

        geographicPoint = new GeographicPoint(10, 10);


        ArduinoMicroController arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        qrDecoder = new QRDecoderDoubleExit(vehicleID1);
        qrDecoder2 = new QRDecoderDoubleExit(vehicleID2);
        Server server = new ServerDouble(false);
        UnbondedBTSignal unbondedBTSignal = new UnbondedBTSignalDoubleExit();

        pmVehicle1 = new PMVehicle(vehicleID1, geographicPoint);
        pmVehicle2 = new PMVehicle(vehicleID2, geographicPoint);

        this.handler1 = new JourneyRealizeHandler();

        handler1.setUnbondedBTSignal(unbondedBTSignal);
        handler1.setServer(server);
        handler1.setArduinoMicroController(arduinoMicroController);
        handler1.setQrDecoder(qrDecoder);
        handler1.setPmVehicle(pmVehicle1);
        handler1.setUserAccount(user1);

        handler1.broadcastStationID(initStation);


        this.handler2 = new JourneyRealizeHandler();

        handler2.setUnbondedBTSignal(unbondedBTSignal);
        handler2.setServer(server);
        handler2.setArduinoMicroController(arduinoMicroController);
        handler2.setQrDecoder(qrDecoder);
        handler2.setPmVehicle(pmVehicle1);
        handler2.setUserAccount(user2);

        handler2.broadcastStationID(initStation);


    }

    @Test
    public void testAnotherUserIsUsingVehicle() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        handler1.scanQR();

        assertEquals(vehicleID1, handler1.getVehicleID());
        assertThrows(PMVNotAvailException.class, () -> handler2.scanQR());
    }

    @Test
    public void testSameEndStation() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException, PMVPhisicalException, PairingNotFoundException {
        handler2.setPmVehicle(pmVehicle2);
        handler2.setQrDecoder(qrDecoder2);

        handler1.scanQR();
        handler2.scanQR();
        handler1.startDriving();
        handler2.startDriving();
        handler1.broadcastStationID(endStation1);
        handler2.broadcastStationID(endStation1);
        handler1.stopDriving();
        handler2.stopDriving();
        handler1.unPairVehicle();
        handler2.unPairVehicle();

        assertEquals(handler1.getStationID(), handler2.getStationID());
    }

    @Test
    public void testDiferentEndStation() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException, PMVPhisicalException, PairingNotFoundException {
        handler2.setPmVehicle(pmVehicle2);
        handler2.setQrDecoder(qrDecoder2);

        handler1.scanQR();
        handler2.scanQR();
        handler1.startDriving();
        handler2.startDriving();
        handler1.broadcastStationID(endStation1);
        handler2.broadcastStationID(endStation2);
        handler1.stopDriving();
        handler2.stopDriving();
        handler1.unPairVehicle();
        handler2.unPairVehicle();

        assertNotEquals(handler1.getStationID(), handler2.getStationID());
    }

    @Test
    public void testMoreDistance() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException, PMVPhisicalException, PairingNotFoundException {
        handler2.setPmVehicle(pmVehicle2);
        handler2.setQrDecoder(qrDecoder2);

        handler1.scanQR();
        handler2.scanQR();
        handler1.startDriving();
        handler2.startDriving();
        handler1.broadcastStationID(endStation1);
        handler2.broadcastStationID(endStation2);
        handler1.stopDriving();
        handler2.stopDriving();
        handler1.unPairVehicle();
        handler2.unPairVehicle();

        float distance1=handler1.getLocalJourneyService().getDistance();
        float distance2=handler2.getLocalJourneyService().getDistance();
        assertTrue(distance2>distance1);
    }




}























