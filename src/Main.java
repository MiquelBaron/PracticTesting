import data.*;
import micromobility.JourneyRealizeHandler;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class Main {

    JourneyRealizeHandler journeyRealizeHandler;
    public Main() throws IOException {

        init();

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

    public void  init() throws IOException {

        UserAccount userAccount = new UserAccount("1");
        GeographicPoint geographicPoint = new GeographicPoint(10,10);
        VehicleID vehicleID = new VehicleID("10");

        ArduinoMicroController arduinoMicroController= new ArduinoMicroControllerDoubleExit();
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleExit();
        Server server=new ServerDouble();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);
        ServiceID serviceID = new ServiceID("1");

        File qrFile = new File("QrImage.png");
        BufferedImage bufferedImage= ImageIO.read(qrFile);

        this.journeyRealizeHandler= new JourneyRealizeHandler(qrDecoder,unbondedBTSignal, server,userAccount,arduinoMicroController,geographicPoint,bufferedImage,serviceID);
    }

}
