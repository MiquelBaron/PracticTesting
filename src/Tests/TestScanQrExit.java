package Tests;
import com.sun.tools.javac.Main;
import data.*;
import exceptions.*;
import micromobility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.*;
import services.smartfeatures.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;


import static org.junit.jupiter.api.Assertions.*;

public class TestScanQrExit {
    String idVeh;
    String idUs;
    String idSt;
    JourneyRealizeHandler journeyRealizeHandler;
    Server server;
    UserAccount userAccount;
    ArduinoMicroController arduinoMicroController;
    QRDecoder qrDecoderExit;
    QRDecoder getQrDecoderFail;
    UnbondedBTSignal unbondedBTSignal;
    VehicleID vehicleID;
    BufferedImage bufferedImage;
    StationID st;
    GeographicPoint geographicPoint;
     
    @BeforeEach
    public void setUp() throws IOException {

        this.idSt="100";
        this.idUs="10";
        this.idVeh="1";
        this.geographicPoint=new GeographicPoint(10,10);

        File qrFile = new File("QrImage.png");
        this.bufferedImage= ImageIO.read(qrFile);

        this.st=new StationID(idSt);
        this.vehicleID=new VehicleID(idVeh);
        this.userAccount=new UserAccount(idUs);

        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.unbondedBTSignal = new UnbondedBTSignalDoubleExit();
        this.server=new ServerDouble();
        this.arduinoMicroController=new ArduinoMicroControllerDoubleExit();
        this.journeyRealizeHandler=new JourneyRealizeHandler(qrDecoderExit,unbondedBTSignal,server,userAccount,arduinoMicroController,geographicPoint,bufferedImage );
        journeyRealizeHandler.broadcastStationID(st);

    }

    @Test
    public void testScanQr() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        journeyRealizeHandler.scanQR();
        assertEquals(journeyRealizeHandler.getVehicleID(),this.vehicleID);
        assertEquals(journeyRealizeHandler.pmvState(),PMVState.NotAvailable);
        assertNotNull(journeyRealizeHandler.getJourneyService());
    }










}
