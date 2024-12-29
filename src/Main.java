import data.*;
import exceptions.*;
import micromobility.JourneyRealizeHandler;
import micromobility.PMVehicle;
import micromobility.payment.Wallet;
import micromobility.payment.WalletPayment;
import services.Server;
import services.ServerDouble;
import services.smartfeatures.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;

public class Main {

    JourneyRealizeHandler journeyRealizeHandler;

    public Main() throws IOException {
        init();
        setupGUI();
    }

    public void setupGUI() {
        JFrame frame = new JFrame("Micromobilidad compartida");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new GridLayout(6, 1));

        JButton scanQRButton = new JButton("Escanear QR");
        JButton startDrivingButton = new JButton("Iniciar Conducción");
        JButton stopDrivingButton = new JButton("Detener Conducción");
        JButton unpairVehicleButton = new JButton("Desemparejar Vehículo");
        JButton selectPaymentButton = new JButton("Seleccionar Método de Pago");
        JButton exitButton = new JButton("Salir");

        boolean[] actionsCompleted = new boolean[5];

        scanQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    StationID originStation = new StationID("1", new GeographicPoint(10, 10));
                    journeyRealizeHandler.broadcastStationID(originStation);
                    journeyRealizeHandler.scanQR();

                    StationID endStation = new StationID("2", new GeographicPoint(20, 20));
                    journeyRealizeHandler.broadcastStationID(endStation);

                    actionsCompleted[0] = true;
                    JOptionPane.showMessageDialog(frame, "QR escaneado y estaciones configuradas automáticamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error al escanear QR: " + ex.getMessage());
                }
            }
        });

        startDrivingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actionsCompleted[0]) {
                    JOptionPane.showMessageDialog(frame, "Debe escanear el QR antes de iniciar conducción.");
                } else {
                    try {
                        journeyRealizeHandler.startDriving();
                    } catch (ConnectException | ProceduralException | PMVPhisicalException ex) {
                        throw new RuntimeException(ex);
                    }
                    actionsCompleted[1] = true;
                    JOptionPane.showMessageDialog(frame, "Conducción iniciada.");
                }
            }
        });

        stopDrivingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actionsCompleted[1]) {
                    JOptionPane.showMessageDialog(frame, "Debe iniciar conducción antes de detenerla.");
                } else {
                    try {
                        journeyRealizeHandler.stopDriving();
                    } catch (ConnectException | ProceduralException | PMVPhisicalException ex) {
                        throw new RuntimeException(ex);
                    }
                    actionsCompleted[2] = true;
                    JOptionPane.showMessageDialog(frame, "Conducción detenida.");
                }
            }
        });

        unpairVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actionsCompleted[2]) {
                    JOptionPane.showMessageDialog(frame, "Debe detener la conducción antes de desemparejar el vehículo.");
                } else {
                    try {
                        journeyRealizeHandler.unPairVehicle();
                    } catch (ConnectException | InvalidPairingArgsException | PairingNotFoundException |
                             ProceduralException ex) {
                        throw new RuntimeException(ex);
                    }
                    actionsCompleted[3] = true;
                    JOptionPane.showMessageDialog(frame, "Vehículo desemparejado.");
                }
            }
        });

        selectPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actionsCompleted[3]) {
                    JOptionPane.showMessageDialog(frame, "Debe desemparejar el vehículo antes de seleccionar el método de pago.");
                } else {
                    String paymentMethod = JOptionPane.showInputDialog(frame, "Seleccione el método de pago (W para Wallet):");
                    if (paymentMethod != null && !paymentMethod.isEmpty()) {
                        try {
                            journeyRealizeHandler.selectPaymentMethod(paymentMethod.charAt(0));
                        } catch (NotEnoughWalletException | ProceduralException | InvalidPaymentArgsException ex) {
                            throw new RuntimeException(ex);
                        }
                        actionsCompleted[4] = true;

                        // Simular el cálculo del importe del pago
                        BigDecimal paymentAmount = journeyRealizeHandler.getImpAmount();
                        BigDecimal roundedAmount = paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP); //Només agafem 2 decimals

                        JOptionPane.showMessageDialog(frame, "Pago realizado. Importe: " + roundedAmount + "unidades monetarias.");
                    }
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Saliendo del programa...");
                System.exit(0);
            }
        });

        frame.add(scanQRButton);
        frame.add(startDrivingButton);
        frame.add(stopDrivingButton);
        frame.add(unpairVehicleButton);
        frame.add(selectPaymentButton);
        frame.add(exitButton);

        frame.setVisible(true);
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
        journeyRealizeHandler.setServiceID(serviceID);
        journeyRealizeHandler.setQrDecoder(qrDecoder);
        journeyRealizeHandler.setArduinoMicroController(arduinoMicroController);
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
