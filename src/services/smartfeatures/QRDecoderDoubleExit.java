package services.smartfeatures;

import data.VehicleID;
import exceptions.CorruptedImgException;

import java.awt.image.BufferedImage;

public class QRDecoderDoubleExit implements QRDecoder{
    private VehicleID vehicleID;

    public QRDecoderDoubleExit(VehicleID vehicleID){
        this.vehicleID= vehicleID;

    }
    @Override
    public VehicleID getVehicleID(BufferedImage QRImg) throws CorruptedImgException {
        return this.vehicleID;
    }

    public void setVehicleID(VehicleID vehicleID){
        this.vehicleID=vehicleID;
    }
}
