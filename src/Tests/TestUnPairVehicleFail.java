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
import services.ServerDoubleFail;
import services.smartfeatures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.ConnectException;

public class TestUnPairVehicleFail {
    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    StationID initStation;
    StationID endStation;

    @BeforeEach
    public void setUp() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, PMVPhisicalException {
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
        journeyRealizeHandler.setServer(new ServerDoubleFail());
    }

    @Test
    public void testProceduralException(){
        assertThrows(ProceduralException.class, ()-> journeyRealizeHandler.unPairVehicle());
    }

    @Test
    public void testInvalidPairingArgsException() throws ProceduralException, ConnectException, PMVPhisicalException {
        journeyRealizeHandler.broadcastStationID(endStation);
        journeyRealizeHandler.stopDriving();
        journeyRealizeHandler.setUserAccount(null);
        assertThrows(InvalidPairingArgsException.class, ()-> journeyRealizeHandler.unPairVehicle());
    }

    @Test
    public void testPairingNotFoundException() throws ConnectException, ProceduralException, PMVPhisicalException {
        journeyRealizeHandler.broadcastStationID(endStation);
        journeyRealizeHandler.stopDriving();
        journeyRealizeHandler.setUserAccount(new UserAccount("2"));
        assertThrows(PairingNotFoundException.class,()-> journeyRealizeHandler.unPairVehicle());
    }

    @Test
    public void testConnectException() throws ConnectException, ProceduralException, PMVPhisicalException {
        journeyRealizeHandler.broadcastStationID(endStation);
        journeyRealizeHandler.stopDriving();
        journeyRealizeHandler.setServer(new ServerDoubleFail());
        assertThrows(ConnectException.class, ()-> journeyRealizeHandler.unPairVehicle());
    }




















}
