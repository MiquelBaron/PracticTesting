import data.GeographicPoint;
import data.StationID;
import data.UserAccount;
import data.VehicleID;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVehicle;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Main  {


    public Main() throws IOException {

        JourneyRealizeHandler journeyRealizeHandler = init();

        try {
            StationID originStation = new StationID("1");
            StationID endStation = new StationID("2");

            journeyRealizeHandler.broadcastStationID(originStation);
            journeyRealizeHandler.scanQR();
            journeyRealizeHandler.startDriving();
            journeyRealizeHandler.broadcastStationID(endStation);
            journeyRealizeHandler.stopDriving();
            journeyRealizeHandler.unPairVehicle();

        }catch(Exception e){
            System.out.println("Error durant l'execucio");
        }
    }

    private static JourneyRealizeHandler init() throws IOException {

        UserAccount userAccount = new UserAccount("1");
        GeographicPoint geographicPoint = new GeographicPoint(10,10);
        VehicleID vehicleID = new VehicleID("10");

        ArduinoMicroController arduinoMicroController= new ArduinoMicroControllerDoubleExit();
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleExit();
        Server server=new ServerDouble();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);

        File qrFile = new File("QrImage.png");
        BufferedImage bufferedImage= ImageIO.read(qrFile);

        return new JourneyRealizeHandler(qrDecoder,unbondedBTSignal, server,userAccount,arduinoMicroController,geographicPoint,bufferedImage);
    }

}
