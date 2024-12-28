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
import static org.junit.jupiter.api.Assertions.*;

import java.net.ConnectException;

public class TestStartDrivingFail {
    JourneyRealizeHandler journeyRealizeHandler;
    VehicleID vehicleID;
    PMVehicle pmVehicle;
    UserAccount userAccount;
    GeographicPoint geographicPoint;

    StationID initStation;
    StationID endStation;

    @BeforeEach
    public void setUp() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException {
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
    }

    @Test
    public void testThrowsProceduralExeption(){
        pmVehicle.setAvailb();
        assertThrows(ProceduralException.class, ()-> journeyRealizeHandler.unPairVehicle());
        pmVehicle.setNotAvailb();
        journeyRealizeHandler.setLocalJourneyService(null);
        assertThrows(ProceduralException.class, ()-> journeyRealizeHandler.unPairVehicle());
        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(false,false));
        assertThrows(ProceduralException.class, ()->journeyRealizeHandler.startDriving());
    }

    @Test
    public void testThrowsConnectException(){
        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(true,false));
        assertThrows(ConnectException.class,()->journeyRealizeHandler.startDriving());
    }

    @Test
    public void testThrowsPMVPhisicalException(){
        journeyRealizeHandler.setArduinoMicroController(new ArduinoMicroControllerDoubleFail(false,true));
        assertThrows(PMVPhisicalException.class,()-> journeyRealizeHandler.startDriving());
    }
}


























