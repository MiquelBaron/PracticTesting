package Tests;
import data.*;
import exceptions.*;
import micromobility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.*;
import services.smartfeatures.*;

import java.net.ConnectException;


import static org.junit.jupiter.api.Assertions.*;

public class TestScanQrExit {

    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    @BeforeEach
    public void setUp() throws ConnectException {
        StationID st = new StationID("1", new GeographicPoint(10,10));
        vehicleID = new VehicleID("1"); //Vehicle que ja tenim emmagatzemat a servidor
        geographicPoint = new GeographicPoint(10,10);
        userAccount = new UserAccount("1");

        ArduinoMicroController arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);
        Server server = new ServerDouble(false);
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleExit();

        pmVehicle=new PMVehicle(vehicleID,geographicPoint);


        this.journeyRealizeHandler= new JourneyRealizeHandler();
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);
        journeyRealizeHandler.setServer(server);
        journeyRealizeHandler.setArduinoMicroController(arduinoMicroController);
        journeyRealizeHandler.setQrDecoder(qrDecoder);
        journeyRealizeHandler.setPmVehicle(pmVehicle);
        journeyRealizeHandler.setUserAccount(userAccount);

        journeyRealizeHandler.broadcastStationID(st);

    }

    @Test
    public void testScanQrDoesNotThrowException(){
        assertDoesNotThrow(()-> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testGetVehicleID() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();

        assertEquals(journeyRealizeHandler.getVehicleID(),this.vehicleID);
    }

    @Test
    public void testJourneyServiceNotNull() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();

        assertNotNull(journeyRealizeHandler.getLocalJourneyService());
    }

    @Test
    public void testPMVehicleIsNotAvailable() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();

        assertEquals(PMVState.NotAvailable,pmVehicle.getState());
    }

    @Test
    public void testJourneyServiceOriginPoint() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();
        JourneyService journeyService=journeyRealizeHandler.getLocalJourneyService();

        assertEquals(geographicPoint, journeyService.getOriginPoint());
    }
    @Test
    public void testJourneyServiceIsAssociated() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();
        JourneyService journeyService=journeyRealizeHandler.getLocalJourneyService();

        assertEquals(userAccount,journeyService.getUserAccount());
        assertEquals(vehicleID,journeyService.getVehicleID());
    }

    @Test
    public void testPMVehicleAssociatedWithUser() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();
        assertEquals(userAccount, pmVehicle.getUserAccount());
    }



}
