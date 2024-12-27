package Tests;

import data.*;
import exceptions.InvalidPairingArgsException;
import exceptions.PairingNotFoundException;
import exceptions.ProceduralException;
import micromobility.JourneyRealizeHandler;
import micromobility.JourneyService;
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
    JourneyService journeyService;
    ServiceID serviceID;
    @BeforeEach
    public void setUp() throws IOException, ProceduralException {
        this.idSt="100";
        this.idUs="10";
        this.idVeh="1";
        this.geographicPoint=new GeographicPoint(10,10);

        File qrFile = new File("QrImage.png");
        this.bufferedImage= ImageIO.read(qrFile);
        this.journeyService=new JourneyService();

        this.st=new StationID(idSt);
        this.vehicleID=new VehicleID(idVeh);
        this.userAccount=new UserAccount(idUs);
        this.serviceID=new ServiceID("1");



        this.qrDecoderExit = new QRDecoderDoubleExit(this.vehicleID);
        this.unbondedBTSignal = new UnbondedBTSignalDoubleExit();
        this.server=new ServerDouble();
        this.arduinoMicroController=new ArduinoMicroControllerDoubleExit();
        this.journeyRealizeHandler=new JourneyRealizeHandler(qrDecoderExit,unbondedBTSignal,server,userAccount,arduinoMicroController,geographicPoint,bufferedImage,serviceID, journeyService );
        this.journeyRealizeHandler.broadcastStationID(st);
        this.journeyRealizeHandler.startDriving();
        this.journeyRealizeHandler.stopDriving();

    }

    @Test
    public void testUnNotNull() throws ConnectException, PairingNotFoundException, InvalidPairingArgsException, ProceduralException {
        journeyRealizeHandler.unPairVehicle();

        assertAll((assertNotNull(journeyService.getEndPoint()),
                assertNotNull(journeyService.getEndDate()),
                assertNotNull(journeyService.getEndHour()),
                assertNotNull(journeyService.getDuration()),
                assertNotNull(journeyService.getDistance()),
                assertNotNull(journeyService.getAvgSpeed())
        );
    }

































}
