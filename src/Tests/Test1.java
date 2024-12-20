package Tests;
import data.*;
import exceptions.*;
import micromobility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.*;
import services.smartfeatures.*;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Test1 {
    String idVeh;
    String idUs;
    String idSt;
    JourneyRealizeHandler journeyRealizeHandler;
    Server server;
    UserAccount userAccount;
    ArduinoMicroController arduinoMicroController;
    QRDecoder qrDecoder;
    UnbondedBTSignal unbondedBTSignal;
    VehicleID vehicleID;
    StationID st;
    GeographicPoint geographicPoint;
    @BeforeEach
    public void setUp(){
        this.idSt="100";
        this.idUs="10";
        this.idVeh="1";
        this.geographicPoint=new GeographicPoint(10,10);

        this.st=new StationID(idSt);
        this.vehicleID=new VehicleID(idVeh);
        this.userAccount=new UserAccount(idUs);

        this.qrDecoder = new QRDecoderDoubleExit(this.vehicleID);
        this.unbondedBTSignal = new UnbondedBTSignalDoubleExit();
        this.server=new ServerDouble();
        this.arduinoMicroController=new ArduinoMicroControllerDoubleExit();
        this.journeyRealizeHandler=new JourneyRealizeHandler(qrDecoder,unbondedBTSignal,server,userAccount,arduinoMicroController,geographicPoint);

    }

    @Test
    public void testBroadcastStationID() throws ConnectException {
        journeyRealizeHandler.broadcastStationID(st);
        assertEquals(journeyRealizeHandler.getStationID(),st);
    }

    @Test
    public void testScanQr() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.broadcastStationID(st);
        journeyRealizeHandler.scanQR();
        assertEquals(journeyRealizeHandler.getVehicleID(),this.vehicleID);
        assertEquals(journeyRealizeHandler.pmvState(),PMVState.NotAvailable);
    }




}
