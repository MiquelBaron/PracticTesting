
package Tests;

import data.*;
import exceptions.CorruptedImgException;
import exceptions.InvalidPairingArgsException;
import exceptions.PMVNotAvailException;
import exceptions.ProceduralException;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestScanQrFail {
    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;
    StationID st;

    @BeforeEach
    public void setUp() throws ConnectException {
        st = new StationID("1", new GeographicPoint(10,10));
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
    }

    @Test
    public void testScanQrThrowsProceduralException() {
        assertThrows(ProceduralException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testScanQrThrowsCorruptedImageException() throws ConnectException {
        journeyRealizeHandler.setQrDecoder(new QRDecoderDoubleFail());
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(CorruptedImgException.class, () -> journeyRealizeHandler.scanQR());
    }


    @Test
    public void testUnAvailableVehicleThrowsPMVNotAvailException() throws ConnectException {
        VehicleID vehicleID1 = new VehicleID("3"); //Registered in server as not available
        PMVehicle pmVehicle1 = new PMVehicle(vehicleID1,geographicPoint);

        journeyRealizeHandler.setPmVehicle(pmVehicle1);
        journeyRealizeHandler.setQrDecoder(new QRDecoderDoubleExit(vehicleID1));
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(PMVNotAvailException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testUnRegisteredVehicleThrowsPMVNotAvailException() throws ConnectException {
        VehicleID vehicleID2 = new VehicleID("4"); //Not registered in server
        PMVehicle pmVehicle2 = new PMVehicle(vehicleID2, geographicPoint);

        journeyRealizeHandler.setPmVehicle(pmVehicle2);
        journeyRealizeHandler.setQrDecoder(new QRDecoderDoubleExit(vehicleID2));
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(PMVNotAvailException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testCheckPMVAvailThrowsConnectException() throws ConnectException {
        journeyRealizeHandler.setServer(new ServerDouble(true));
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(ConnectException.class,()->journeyRealizeHandler.scanQR());
    }


    @Test
    public void testScanQrArduinoThrowsConnectException() throws ConnectException{
        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(true,false));
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(ConnectException.class, () -> journeyRealizeHandler.scanQR());
    }


    @Test
    public void testScanQrThrowsInvalidPairingArgsException() throws ConnectException {
        journeyRealizeHandler.setUserAccount(null);
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(InvalidPairingArgsException.class, () -> journeyRealizeHandler.scanQR());
    }



}


