package Tests;

import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.JourneyService;
import micromobility.PMVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.ConnectException;

public class TestStopDrivingFail {
    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    StationID initStation;
    StationID endStation;

    @BeforeEach
    public void setUp() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, PMVPhisicalException {
        initStation = new StationID("1", new GeographicPoint(10, 10));
        endStation = new StationID("2", new GeographicPoint(20, 20));

        vehicleID = new VehicleID("1"); //Vehicle que ja tenim emmagatzemat a servidor
        geographicPoint = new GeographicPoint(10, 10);
        userAccount = new UserAccount("1");

        ArduinoMicroController arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);
        Server server = new ServerDouble(false);
        UnbondedBTSignal unbondedBTSignal = new UnbondedBTSignalDoubleExit();

        pmVehicle = new PMVehicle(vehicleID, geographicPoint);


        this.journeyRealizeHandler = new JourneyRealizeHandler();
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);
        journeyRealizeHandler.setServer(server);
        journeyRealizeHandler.setArduinoMicroController(arduinoMicroController);
        journeyRealizeHandler.setQrDecoder(qrDecoder);
        journeyRealizeHandler.setPmVehicle(pmVehicle);
        journeyRealizeHandler.setUserAccount(userAccount);

        journeyRealizeHandler.broadcastStationID(initStation);
        journeyRealizeHandler.scanQR();
        journeyRealizeHandler.startDriving();
        journeyRealizeHandler.broadcastStationID(endStation);

        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(false,false));
    }


    @Test
    public void testJourneyServiceNotInProgressThrowsProceduralException() {
        JourneyService journeyService=journeyRealizeHandler.getLocalJourneyService();
        journeyService.setInProgress(false);
        journeyRealizeHandler.setLocalJourneyService(journeyService);

        assertThrows(ProceduralException.class, ()->journeyRealizeHandler.stopDriving());
    }

    @Test
    public void testPMVehicleNotUnderWayThrowsProceduralException(){
        pmVehicle.setNotAvailb();
        journeyRealizeHandler.setPmVehicle(pmVehicle);

        assertThrows(ProceduralException.class,()->journeyRealizeHandler.stopDriving());
    }
    @Test
    public void testStopDrivingConnectExeption(){
        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(true,false));
        assertThrows(ConnectException.class,()-> journeyRealizeHandler.stopDriving());
    }

    @Test
    public void testStopDrivingThrowsPMVhisicalException(){
        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(false,true));
        assertThrows(PMVPhisicalException.class,()-> journeyRealizeHandler.stopDriving());
    }

    @Test
    public void testStopDrivingThrowsProceduralException(){
        assertThrows(ProceduralException.class,()-> journeyRealizeHandler.stopDriving());
    }




}
