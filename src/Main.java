import micromobility.JourneyRealizeHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Main  {
    JourneyRealizeHandler journeyRealizeHandler;
    File qrFile = new File("QrImage.png");
    BufferedImage bufferedImage;
    Scanner scanner = new Scanner(System.in);
    String idUsuari;
    String idVehicle;
    String idEstacio;


    public Main() throws IOException {
        this.bufferedImage= ImageIO.read(qrFile);

        //this.journeyRealizeHandler=new JourneyRealizeHandler();
    }
    public void run() {
        System.out.print("Introdueix el teu identificador d'usuari ");
        this.idUsuari = scanner.nextLine();
    }
}
