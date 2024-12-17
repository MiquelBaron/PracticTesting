package services.smartfeatures;

import data.VehicleID;
import exceptions.CorruptedImgException;

import java.awt.image.BufferedImage;

public class QRDecoderDoubleFail implements QRDecoder{

    @Override
    public VehicleID getVehicleID(BufferedImage QRImg) throws CorruptedImgException {
        throw new CorruptedImgException("Corrupted image");
    }
}
