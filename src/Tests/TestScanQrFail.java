package Tests;

import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import exceptions.CorruptedImgException;
import exceptions.InvalidPairingArgsException;
import exceptions.PMVNotAvailException;
import exceptions.ProceduralException;
import micromobility.JourneyRealizeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Server;
import services.ServerDouble;
import services.ServerDoubleFail;
import services.smartfeatures.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class TestScanQrFail {
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
    QRDecoder qrDecoderFail;
    StationID st;
    GeographicPoint geographicPoint;

    @BeforeEach
    public void setUp() throws IOException {
        this.idSt = "100";
        this.idUs = "10";
        this.idVeh = "1";
        this.geographicPoint = new GeographicPoint(10, 10);

        File qrFile = new File("QrImage.png");
        this.bufferedImage = ImageIO.read(qrFile);
        this.server = new ServerDoubleFail();

        this.st = new StationID(idSt);
        this.vehicleID = new VehicleID(idVeh);
        this.userAccount = new UserAccount(idUs);

        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.unbondedBTSignal = new UnbondedBTSignalDoubleExit();
        this.arduinoMicroController = new ArduinoMicroControllerDoubleExit();

    }


    @Test
    public void testScanQrThrowsCorruptedImageException() throws CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException, ConnectException {
        this.qrDecoderFail = new QRDecoderDoubleFail();
        this.journeyRealizeHandler = new JourneyRealizeHandler(qrDecoderFail, unbondedBTSignal, server, userAccount, arduinoMicroController, geographicPoint, bufferedImage);
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(CorruptedImgException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testScanQrThrowsPMVNotAvailException() throws ConnectException {
        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        this.server = new ServerDoubleFail();
        this.journeyRealizeHandler = new JourneyRealizeHandler(qrDecoderExit, unbondedBTSignal, server, userAccount, arduinoMicroController, geographicPoint, bufferedImage);
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(PMVNotAvailException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testScanQrThrowsConnectException() throws ConnectException, CorruptedImgException, InvalidPairingArgsException, ProceduralException, PMVNotAvailException {
        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.arduinoMicroController = new ArduinoMicroControllerDoubleFail();
        this.server = new ServerDouble();
        this.journeyRealizeHandler = new JourneyRealizeHandler(qrDecoderExit, unbondedBTSignal, server, userAccount, arduinoMicroController, geographicPoint, bufferedImage);
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(ConnectException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testScanQrThrowsInvalidPairingArgsException() throws ConnectException {
        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        this.server = new ServerDouble();
        this.journeyRealizeHandler = new JourneyRealizeHandler(qrDecoderExit, unbondedBTSignal, server, userAccount, arduinoMicroController, geographicPoint, bufferedImage);
        journeyRealizeHandler.setUserAccount(null);
        journeyRealizeHandler.broadcastStationID(st);

        assertThrows(InvalidPairingArgsException.class, () -> journeyRealizeHandler.scanQR());
    }

    @Test
    public void testScanQrThrowsPairingProceduralException() throws ConnectException {
        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        this.server = new ServerDouble();
        this.journeyRealizeHandler = new JourneyRealizeHandler(qrDecoderExit, unbondedBTSignal, server, userAccount, arduinoMicroController, geographicPoint, bufferedImage);

        assertThrows(ProceduralException.class, () -> journeyRealizeHandler.scanQR());
    }

}


