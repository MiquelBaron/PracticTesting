package services.smartfeatures;
import exceptions.CorruptedImgException;
import data.*;
import java.awt.image.BufferedImage;

public interface QRDecoder { // Decodes QR codes from an image
    VehicleID getVehicleID(BufferedImage QRImg) throws CorruptedImgException;
}
