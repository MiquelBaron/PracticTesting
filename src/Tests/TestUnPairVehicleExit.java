package Tests;

import data.*;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.JourneyService;
import micromobility.PMVState;
import micromobility.PMVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import services.*;
import services.smartfeatures.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

public class TestUnPairVehicleExit {

    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    StationID initStation;
    StationID endStation;

    @BeforeEach
    public void setUp() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException {
         initStation = new StationID("1", new GeographicPoint(10,10));
         endStation =new StationID("2", new GeographicPoint(20,20));

        vehicleID = new VehicleID("1"); //Vehicle que ja tenim emmagatzemat a servidor
        geographicPoint = new GeographicPoint(10,10);
        userAccount = new UserAccount("1");

        ArduinoMicroController arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);
        Server server = new ServerDouble();
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleExit();

        pmVehicle=new PMVehicle(vehicleID,geographicPoint);


        this.journeyRealizeHandler= new JourneyRealizeHandler();
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
        journeyRealizeHandler.stopDriving();


    }

    @Test
    public void testUnPairVehicleDoesNotThrowException() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, PairingNotFoundException {
        assertDoesNotThrow(()->journeyRealizeHandler.unPairVehicle());
    }

    @Test
    public void testJourneyServiceEndPoint() throws ConnectException, PairingNotFoundException, InvalidPairingArgsException, ProceduralException {
        JourneyService journeyService=journeyRealizeHandler.getJourneyService();
        journeyRealizeHandler.unPairVehicle();

        assertEquals(endStation.getLoc(),journeyService.getEndPoint());
    }

    @Test
    public void testPMVehicleIsNotAvailable() throws PairingNotFoundException, InvalidPairingArgsException, ProceduralException, ConnectException {
        journeyRealizeHandler.unPairVehicle();

        assertEquals(PMVState.Available, pmVehicle.getState());
    }

    @Test
    public void testPMVehicleUbicationHasBeenUpdated() throws PairingNotFoundException, InvalidPairingArgsException, ProceduralException, ConnectException {
        journeyRealizeHandler.unPairVehicle();
        assertEquals(endStation.getLoc(), pmVehicle.getGeographicPoint());
    }

    @Test
    public void testPMVehicleVinculationHasBeenDeleted() throws PairingNotFoundException, InvalidPairingArgsException, ProceduralException, ConnectException {
        journeyRealizeHandler.unPairVehicle();
        assertNull(pmVehicle.getUserAccount());
    }

    @Test
    public void testJourneyServiceHasBeenDeleted() throws PairingNotFoundException, InvalidPairingArgsException, ProceduralException, ConnectException {
        journeyRealizeHandler.unPairVehicle();
        assertNull(journeyRealizeHandler.getJourneyService());
    }






}

































