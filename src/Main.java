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
import java.util.Scanner;

public class Main {

    JourneyRealizeHandler journeyRealizeHandler;

    public Main() throws IOException {

        init();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;
            boolean[] actionsCompleted = new boolean[6]; // Track completed actions


            while (!exit) {
                System.out.println("\nSeleccione una acción:");
                System.out.println("1. Escanear QR");
                System.out.println("2. Iniciar conducción");
                System.out.println("3. Detener conducción");
                System.out.println("4. Desemparejar vehículo");
                System.out.println("5. Seleccionar método de pago");
                System.out.println("6. Salir");
                System.out.print("Opción: ");

                int option = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                switch (option) {
                    case 1:
                        StationID originStation = new StationID("1", new GeographicPoint(10, 10));
                        journeyRealizeHandler.broadcastStationID(originStation);
                        System.out.println("ID de estación de origen recibida automáticamente.");
                        journeyRealizeHandler.scanQR();


                        actionsCompleted[0] = true;
                        System.out.println("QR escaneado.");
                        break;
                    case 2:
                        if (!actionsCompleted[0]) {
                            System.out.println("Debe completar la acción 1 antes de continuar.");
                        } else {
                            journeyRealizeHandler.startDriving();
                            actionsCompleted[1] = true;
                            System.out.println("Conducción iniciada.");
                        }
                        break;
                    case 3:
                        if (!actionsCompleted[1]) {
                            System.out.println("Debe completar la acción 2 antes de continuar.");
                        } else {
                            StationID endStation = new StationID("5", new GeographicPoint(10.00000001f,10.0000001f));
                            journeyRealizeHandler.broadcastStationID(endStation);
                            System.out.println("ID de estación de destino recibida automáticamente.");
                            journeyRealizeHandler.stopDriving();
                            actionsCompleted[2] = true;
                            System.out.println("Conducción detenida.");
                        }
                        break;
                    case 4:
                        if (!actionsCompleted[2]) {
                            System.out.println("Debe completar la acción 3 antes de continuar.");
                        } else {
                            journeyRealizeHandler.unPairVehicle();
                            actionsCompleted[3] = true;
                            System.out.println("Vehículo desemparejado.");
                        }
                        break;
                    case 5:
                        if (!actionsCompleted[3]) {
                            System.out.println("Debe completar la acción 4 antes de continuar.");
                        } else {
                            System.out.print("Seleccione el método de pago (W para Wallet): ");
                            char paymentMethod = scanner.nextLine().charAt(0);
                            System.out.println("Desea proceder al pago?");
                            String response = scanner.nextLine();

                            while(!response.equalsIgnoreCase("si")){
                                System.out.println("Debe realizar el pago para poder completar el proceso! Quiere proceder al pago?");
                                response = scanner.nextLine();
                            }
                            journeyRealizeHandler.selectPaymentMethod(paymentMethod);
                            actionsCompleted[4] = true;
                            System.out.println("Pago realizado. Se le ha reducido de su balance "+journeyRealizeHandler.getImpAmount()+" unidades monetarias");
                            journeyRealizeHandler.undoBtConnection();
                        }
                        break;

                    case 6:
                        exit = true;
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción no válida, intente nuevamente.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error durante la ejecución: " + e.getMessage());
        }
    }

    public void init() throws IOException {

        UserAccount userAccount = new UserAccount("1");
        GeographicPoint geographicPoint = new GeographicPoint(10, 10);
        VehicleID vehicleID = new VehicleID("1");
        ServiceID serviceID = new ServiceID("1");

        PMVehicle pmVehicle = new PMVehicle(vehicleID, geographicPoint);
        Wallet wallet = new Wallet(new BigDecimal(1000000));

        ArduinoMicroController arduinoMicroController = new ArduinoMicroControllerDoubleExit();
        UnbondedBTSignal unbondedBTSignal = new UnbondedBTSignalDoubleExit();
        Server server = new ServerDouble(false);
        QRDecoder qrDecoder = new QRDecoderDoubleExit(vehicleID);

        File qrFile = new File("QrImage.png");
        BufferedImage bufferedImage = ImageIO.read(qrFile);

        this.journeyRealizeHandler = new JourneyRealizeHandler();
        journeyRealizeHandler.setServer(server);
        journeyRealizeHandler.setUserAccount(userAccount);
        journeyRealizeHandler.setBufferedImage(bufferedImage);
        journeyRealizeHandler.setPmVehicle(pmVehicle);
        journeyRealizeHandler.setUnbondedBTSignal(unbondedBTSignal);
        journeyRealizeHandler.setQrDecoder(qrDecoder);
        journeyRealizeHandler.setArduinoMicroController(arduinoMicroController);
        journeyRealizeHandler.setServiceID(serviceID);
        journeyRealizeHandler.setWallet(wallet);
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (IOException e) {
            System.out.println("Error al inicializar el programa: " + e.getMessage());
        }
    }
}
