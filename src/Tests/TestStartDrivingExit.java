package Tests;

import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.JourneyService;
import micromobility.PMVState;
import micromobility.PMVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.ConnectException;

public class TestStartDrivingExit {
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
        Server server = new ServerDouble();
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
    }

    @Test
    public void testStartDrivingDoesNotThrowException() throws ProceduralException, PMVPhisicalException, ConnectException {
        assertDoesNotThrow(()->journeyRealizeHandler.startDriving());
    }

    @Test
    public void testPMVehicleIsUnderWay() throws ProceduralException, PMVPhisicalException, ConnectException {
        journeyRealizeHandler.startDriving();

        assertEquals(PMVState.UnderWay, pmVehicle.getState());
    }

    @Test
    public void testJourneyServiceIsInProgress() throws ProceduralException, PMVPhisicalException, ConnectException {
        JourneyService journeyService=journeyRealizeHandler.getJourneyService();
        journeyRealizeHandler.startDriving();

        assertTrue(journeyService.isInProgress());
    }

}
























