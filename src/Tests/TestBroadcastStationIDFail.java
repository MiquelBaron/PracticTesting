package Tests;

import Tests.Interfaces.BroadcastStationIDInterface;
import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import micromobility.JourneyRealizeHandler;
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


public class TestBroadcastStationIDFail {

    String idVeh;
    String idUs;
    String idSt;
    JourneyRealizeHandler journeyRealizeHandler;
    Server server;
    UserAccount userAccount;
    ArduinoMicroController arduinoMicroController;
    QRDecoder qrDecoderExit;
    UnbondedBTSignal unbondedBTSignal;
    VehicleID vehicleID;
    BufferedImage bufferedImage;
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

        this.st = new StationID(idSt);
        this.vehicleID = new VehicleID(idVeh);
        this.userAccount = new UserAccount(idUs);

        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.unbondedBTSignal = new UnbondedBTSignalDoubleFail();
        this.server = new ServerDouble();
        this.arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        this.journeyRealizeHandler = new JourneyRealizeHandler(qrDecoderExit, unbondedBTSignal, server, userAccount, arduinoMicroController, geographicPoint, bufferedImage);
    }

    @Test
    public void testBroadcastStationID() throws ConnectException {
        assertThrows(ConnectException.class, () -> journeyRealizeHandler.broadcastStationID(this.st));
    }
}
