import data.*;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVehicle;
import micromobility.payment.Wallet;
import micromobility.payment.WalletPayment;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;


public class Main {

    JourneyRealizeHandler journeyRealizeHandler;
    public Main() throws IOException {

        init();

        try {
            StationID originStation = new StationID("1", new GeographicPoint(10,10));
            StationID endStation = new StationID("2", new GeographicPoint(20,20));

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
        ServiceID serviceID = new ServiceID("1");

        PMVehicle pmVehicle = new PMVehicle(vehicleID,geographicPoint);
        Wallet wallet = new Wallet(new BigDecimal(1000));

        ArduinoMicroController arduinoMicroController= new ArduinoMicroControllerDoubleExit();
        UnbondedBTSignal unbondedBTSignal=new UnbondedBTSignalDoubleExit();
        Server server=new ServerDouble();
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);

        File qrFile = new File("QrImage.png");
        BufferedImage bufferedImage= ImageIO.read(qrFile);

        this.journeyRealizeHandler= new JourneyRealizeHandler();
        journeyRealizeHandler.setServer(server);
        journeyRealizeHandler.setUserAccount(userAccount);
        journeyRealizeHandler.setBufferedImage(bufferedImage);
        journeyRealizeHandler.setPmVehicle(pmVehicle);
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);
        journeyRealizeHandler.setServiceID(serviceID);
        journeyRealizeHandler.setQrDecoder(qrDecoder);
        journeyRealizeHandler.setArduinoMicroController(arduinoMicroController);
        journeyRealizeHandler.setWallet(wallet);
    }

}
